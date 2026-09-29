package com.example.mishkat.service

import android.util.Log
import com.example.mishkat.guardrails.MishkatGuardrails
import com.example.mishkat.model.MishkatRequest
import com.example.mishkat.model.MishkatResponse
import com.example.mishkat.model.TutorExplanationMode
import com.example.mishkat.model.TutorLevel
import com.example.mishkat.rag.BookKnowledgeChunk
import com.example.mishkat.rag.MishkatKnowledgeBase
import com.example.mishkat.rag.MishkatRagRetriever

/**
 * التطبيق الأساسي لخدمة مدرس الذكاء الاصطناعي "مشكاة"
 * يدمج RAG، Guardrails، Firebase AI SDK، ومولد Gemini API المباشر مع دعم كامل للأوفلاين.
 */
class MishkatTutorServiceImpl(
    private val ragRetriever: MishkatRagRetriever = MishkatRagRetriever(),
    private val firebaseAiService: FirebaseAiMishkatService = FirebaseAiMishkatService(),
    private val restGeminiService: RestGeminiMishkatService = RestGeminiMishkatService()
) : MishkatTutorService {

    companion object {
        private const val TAG = "MishkatTutorService"
    }

    override suspend fun askMishkat(request: MishkatRequest): MishkatResponse {
        val query = request.userQuery.trim()
        val isEnglish = request.language == com.example.mishkat.localization.MishkatLanguage.ENGLISH ||
            (query.count { it in 'a'..'z' || it in 'A'..'Z' } > query.count { it.code in 0x0600..0x06FF } && query.length > 5)

        // 1. الفحص الصارم لحدود المحتوى ومحاولات الاختراق (Guardrails)
        if (MishkatGuardrails.isClearlyOutOfScope(query) || MishkatGuardrails.isPromptInjection(query)) {
            return MishkatResponse(
                replyText = MishkatGuardrails.getOutOfScopeMessage(isEnglish),
                citedReferences = emptyList(),
                citedPages = emptyList(),
                isOutOfScope = true,
                sourceEngine = "Guardrails Gatekeeper"
            )
        }

        // 2. استرجاع الجذاذات المعرفية الموثقة (RAG Retrieval)
        val relevantChunks = ragRetriever.retrieveRelevantContext(
            query = query,
            currentLessonSlug = request.currentLessonSlug,
            maxChunks = 3
        )

        val ragFormattedContext = ragRetriever.formatContextForPrompt(relevantChunks)
        val citedPages = relevantChunks.flatMap { (it.pageStart..it.pageEnd).toList() }.distinct().sorted()
        val citedRefs = if (isEnglish) {
            relevantChunks.map { "${it.getLocalizedLessonTitle(true)} (pp. ${it.pageStart} - ${it.pageEnd})" }
        } else {
            relevantChunks.map { "${it.lessonTitle} (ص ${it.pageStart} - ص ${it.pageEnd})" }
        }

        // 3. بناء موجه النظام والموجه المعزز (Prompt Augmentation)
        val systemPrompt = buildSystemInstructions(request.tutorLevel, isEnglish)
        val augmentedPrompt = buildAugmentedPrompt(request, ragFormattedContext, relevantChunks, isEnglish)

        // 4. محاولة التوليد عبر Firebase AI SDK أولاً
        var generatedResult: Result<String>? = null
        var engineUsed = "Firebase AI (Gemini 2.5 Flash)"

        if (firebaseAiService.isAvailable()) {
            val result = firebaseAiService.generateWithRag(augmentedPrompt)
            if (result.isSuccess) {
                generatedResult = result
            } else {
                try {
                    Log.w(TAG, "Firebase AI failed, falling back to RestGeminiService: ${result.exceptionOrNull()?.message}")
                } catch (_: Throwable) {}
            }
        }

        // 5. في حال تعذر Firebase AI، اللجوء لـ Gemini REST API كبديل مباشر
        if (generatedResult == null || generatedResult.isFailure) {
            val restResult = restGeminiService.generateContent(augmentedPrompt, systemPrompt)
            if (restResult.isSuccess) {
                generatedResult = restResult
                engineUsed = "Gemini Direct API (REST)"
            } else {
                try {
                    Log.w(TAG, "Rest Gemini failed, falling back to Local RAG Synthesizer: ${restResult.exceptionOrNull()?.message}")
                } catch (_: Throwable) {}
            }
        }

        // 6. في حال انقطاع الشبكة، توليد الرد محلياً من قاعدة معارف الكتاب (Offline Fallback)
        val finalReplyText: String = if (generatedResult != null && generatedResult.isSuccess) {
            val raw = generatedResult.getOrNull() ?: ""
            MishkatGuardrails.sanitizeOutput(raw)
        } else {
            engineUsed = "مستودع المعرفة الموثقة (الروض الناضر)"
            synthesizeLocalResponse(request, relevantChunks)
        }

        // التحقق من خروج السؤال عن المحتوى حتى لو أنتج النموذج ذلك
        if (finalReplyText.contains("هذه المعلومة إضافية") || finalReplyText.contains("ليست واردة في الكتاب")) {
            return MishkatResponse(
                replyText = MishkatGuardrails.OUT_OF_SCOPE_EXACT_MESSAGE,
                citedReferences = emptyList(),
                citedPages = emptyList(),
                isOutOfScope = true,
                sourceEngine = engineUsed
            )
        }

        // ضمان وجود الإحالة وتنسيق السؤال التحققي
        val verifiedReply = MishkatGuardrails.ensureCitationAppended(
            finalReplyText,
            "المرجع: كتاب الروض الناضر في تحريرات عاصم من طيبة النشر - " + citedRefs.joinToString(" ، ")
        )

        return MishkatResponse(
            replyText = verifiedReply,
            citedReferences = citedRefs,
            citedPages = citedPages,
            verificationQuestion = extractVerificationQuestion(verifiedReply),
            hintStep = request.hintStep,
            isOutOfScope = false,
            sourceEngine = engineUsed
        )
    }

    override suspend fun explainConcept(
        concept: String,
        mode: TutorExplanationMode,
        level: TutorLevel
    ): MishkatResponse {
        val request = MishkatRequest(
            userQuery = "اشرح مفهوم ($concept) وفق أسلوب: ${mode.labelArabic}",
            explanationMode = mode,
            tutorLevel = level
        )
        return askMishkat(request)
    }

    override suspend fun getNextHint(
        problemOrConcept: String,
        currentStep: Int
    ): MishkatResponse {
        val nextStep = (currentStep + 1).coerceIn(1, 3)
        val request = MishkatRequest(
            userQuery = "أعطني التلميح رقم ($nextStep) لمسألة ($problemOrConcept) دون إعطائي الحل النهائي المباشر",
            hintStep = nextStep
        )
        return askMishkat(request)
    }

    override suspend fun generateRevisionPlan(studentErrors: List<String>): MishkatResponse {
        val errorSummary = if (studentErrors.isNotEmpty()) {
            studentErrors.joinToString(" ، ")
        } else {
            "الخلط بين السكت على قصر المنفصل، والغنة في اللام والراء مع التوسط، ووجه الإدغام في ألم نخلقكم"
        }

        val request = MishkatRequest(
            userQuery = "اقترح خطة مراجعة مخصصة مع دروس وتمارين بناءً على مواضع الضعف التالية:\n$errorSummary",
            isRevisionPlanRequest = true
        )
        return askMishkat(request)
    }

    override suspend fun generatePracticeExercises(
        topic: String,
        count: Int,
        level: TutorLevel
    ): MishkatResponse {
        val request = MishkatRequest(
            userQuery = "أنشئ عدد ($count) تمارين تطبيقية متدرجة الصعوبة (مبتدئ، متوسط، متقدم) حول باب: $topic مستنداً لكتاب الروض الناضر حصراً",
            isExerciseRequest = true,
            tutorLevel = level
        )
        return askMishkat(request)
    }

    override suspend fun evaluateRecitation(
        recitationText: String,
        targetReaderOrRule: String?,
        level: TutorLevel
    ): MishkatResponse {
        val targetInfo = if (!targetReaderOrRule.isNullOrBlank()) " لرواية أو طريق: $targetReaderOrRule" else ""
        val prompt = """
            [جلسة تصحيح وتحرير تلاوة الطالب]:
            قام الطالب بتلاوة النص القرآني التالي صوتياً$targetInfo:
            «$recitationText»
            
            المطلوب من مشكاة:
            1. فحص التلاوة والأوجه التحريرية المقروء بها بدقة أصولية وفق نصوص كتاب «الروض الناضر».
            2. بيان هل هذا الوجه جائز أم ممتنع لرواية حفص أو شعبة من طريق طيبة النشر؟
            3. بيان التركيب التحريري (مثال: إذا كان هناك مد منفصل مع سكت أو غنة أو إمالة، فهل يصح اجتماعهما في طريق واحد مع عزو الكتب الـ 26 المعتمدة كالتيسير والمستنير والكامل).
            4. تقديم إرشاد ونصح توجيهي مباشر للطالب لتثبيت الوجه الصحيح.
        """.trimIndent()

        val request = MishkatRequest(
            userQuery = prompt,
            tutorLevel = level,
            isRecitationCorrectionRequest = true,
            targetReader = targetReaderOrRule
        )
        val response = askMishkat(request)
        return response.copy(isRecitationCorrection = true)
    }

    private fun buildSystemInstructions(level: TutorLevel, isEnglish: Boolean = false): String {
        if (isEnglish) {
            return """
                You are "Mishkat", the dedicated academic AI Tutor for "Ar-Rawdat An-Nadir fi Tahrirat Asim min Tayyibat An-Nashr", prepared by Sheikh Abdul-Hamid Zaid under the supervision of Shaykhah Samah bint As-Sayyid Ahmad Al-Bandari (Academy of Qira'at Sciences).
                
                [Identity & Pedagogical Tone]:
                - Tone: Respectful, encouraging, inspiring, and academically rigorous in clear modern English.
                - Sole Reference: Strictly the classical texts and transmission chains of "Ar-Rawdat An-Nadir" provided in the context.
                - Current academic level: ${level.labelEnglish} (${level.descriptionEnglish}).
                - Transliteration & Terminology: Explain concepts in fluent English while providing the canonical Quranic Arabic technical terms in brackets (e.g., Al-Munfasil [المنفصل], Qasr [قصر = 2 counts], Tawassut [توسط = 4 counts], Sakt [سكت = breathless pause], Ghunnah [غنة]).
                
                [Strict Academic Guardrails]:
                1. If the question relates to the book's content: Answer with high precision and provide citations (Unit / Chapter / Lesson / Pages).
                2. If the question is outside the scope of the book (such as fiqh, non-tahrirat tafsir, secular sciences, or other Qira'at not analyzed in the book):
                   You MUST respond immediately and verbatim with the exact phrase:
                   "${MishkatGuardrails.OUT_OF_SCOPE_EXACT_MESSAGE_EN}"
                3. Strictly NEVER fabricate any facet, chain, or citation not contained in the provided book texts.
                4. Strictly NEVER reveal internal reasoning or thinking tags. Provide only the final polished explanation.
                5. If the student asks for a hint, provide progressive pedagogical hints (Hint 1, 2, 3) leading the student to deduce the answer.
                6. Always conclude with a short "Comprehension Check Question" to test the student's understanding.
            """.trimIndent()
        }
        return """
            أنت "مشكاة"، معلم الذكاء الاصطناعي الأكاديمي الحصري لكتاب "الروض الناضر في تحريرات عاصم من طيبة النشر" من إعداد الشيخ عبد الحميد زيد وإشراف الشيخة المقرئة سماح بنت السيد أحمد البنداري (أكاديمية علوم القراءات).
            
            [الهوية والسلوك الأكاديمي]:
            - النبرة: ودودة، محفزة، وقورة، أكاديمية، وباللغة العربية الفصحى المبسطة الدقيقة.
            - المرجع الوحيد: نصوص ومصادر كتاب "الروض الناضر" المرفقة حصراً.
            - مستوى التلقي المطلوب حالياً: ${level.labelArabic} (${level.descriptionArabic}).
            
            [الضوابط والحواجز الصارمة (Guardrails)]:
            1. إذا كان السؤال متعلقاً بمحتوى الكتاب: أجب بدقة وعزو، واذكر دائماً (الوحدة/الفصل/الدرس/الصفحات).
            2. إذا كان السؤال خارج محتوى الكتاب (مثل الفقه، التفسير غير التحريري، العلوم العامة، أو قراءات أخرى لم يحررها الكتاب):
               يجب عليك الرد فوراً ودون أي تفكير أو إضافة بالصيغة الإلزامية التالية بالحرف:
               "${MishkatGuardrails.OUT_OF_SCOPE_EXACT_MESSAGE}"
            3. يُمنع منعاً باتاً اختلاق أي وجه أو إسناد أو نسبة معلومة لم ترد في نصوص الكتاب المرفقة.
            4. يُمنع منعاً باتاً كشف خطوات التفكير الداخلي (Chain of Thought). قدّم الشرح النهائي المنقح فقط.
            5. إذا طلب الطالب حلاً لمسألة، قدم تلميحات تدريجية (Hint 1, Hint 2, Hint 3) تحفزه على الاستنباط بدلاً من تقديم الإجابة الجاهزة فوراً.
            6. اختم دائماً بـ "سؤال تحقق من الفهم" قصير ومحدد لاختبار استيعاب الطالب.
        """.trimIndent()
    }

    private fun buildAugmentedPrompt(
        request: MishkatRequest,
        ragFormattedContext: String,
        chunks: List<BookKnowledgeChunk>,
        isEnglish: Boolean = false
    ): String {
        val sb = StringBuilder()
        sb.append(ragFormattedContext).append("\n\n")

        if (isEnglish) {
            sb.append("[Student Request Guidance]:\n")
            sb.append("- Explanation Mode: ${request.explanationMode.labelEnglish}\n")
            sb.append("- Academic Level: ${request.tutorLevel.labelEnglish}\n")
            sb.append("- Language: English (with Quranic Arabic terms cited in brackets)\n")

            if (request.hintStep > 0) {
                sb.append("- This is progressive hint #${request.hintStep} of 3. Do not give away the entire direct solution, but guide the student's attention to the core rule or classical matn citation.\n")
            }
            if (request.isRevisionPlanRequest) {
                sb.append("- Task: Analyze weaknesses and suggest a numbered revision schedule with: (Target lessons + Page numbers + Guided exercises).\n")
            }
            if (request.isExerciseRequest) {
                sb.append("- Task: Create practical exercises including transmission paths, rules of Waqf and Madd, with true/false and permissible/prohibited facets questions.\n")
            }

            sb.append("\n[Student Inquiry]:\n")
            sb.append(request.userQuery)
        } else {
            sb.append("[تعليمات إضافية خاصة بطلب الطالب]:\n")
            sb.append("- نمط الشرح المفضل: ${request.explanationMode.labelArabic}\n")
            sb.append("- المستوى الأكاديمي: ${request.tutorLevel.labelArabic}\n")

            if (request.hintStep > 0) {
                sb.append("- هذا طلب تلميح تدريجي رقم (${request.hintStep}) من أصل 3. لا تقدم الحل كاملاً بل وجه انتباه الطالب للرابط الأصولي أو المتن فقط.\n")
            }

            if (request.isRevisionPlanRequest) {
                sb.append("- المطلوب: تحليل مواضع الضعف واقتراح جدول مراجعة مرقم يتضمن: (الدروس المستهدفة + أرقام الصفحات + تمارين موجهة).\n")
            }

            if (request.isExerciseRequest) {
                sb.append("- المطلوب: صياغة تمارين عملية تتضمن أوجه الرواية وتحريرات الطرق وعلامات الضبط مع أسئلة (ضع علامة صح أو خطأ مع التعليل، واستخرج الأوجه الجائزة والممتنعة).\n")
            }

            sb.append("\n[استفسار الطالب]:\n")
            sb.append(request.userQuery)
        }

        return sb.toString()
    }

    /**
     * توليد محلي عالي الدقة في حال عدم توفر اتصال بالإنترنت
     */
    private fun synthesizeLocalResponse(
        request: MishkatRequest,
        chunks: List<BookKnowledgeChunk>
    ): String {
        val primaryChunk = chunks.firstOrNull() ?: MishkatKnowledgeBase.chunks.first()
        val sb = StringBuilder()

        sb.append("أهلاً بك يا بني في محراب علم التحريرات المبارك. بناءً على ما جاء في كتابنا المعتمد **«الروض الناضر في تحريرات عاصم من طيبة النشر»**:\n\n")

        when (request.explanationMode) {
            TutorExplanationMode.DIRECT -> {
                sb.append("📌 **التحرير والتأصيل الأكاديمي**:\n")
                sb.append(primaryChunk.content).append("\n\n")
            }
            TutorExplanationMode.METAPHOR -> {
                sb.append("💡 **تقريب المفهوم وتشبيهه**:\n")
                sb.append("تحريرات القراءات كالمسارات المحكمة في شبكة الطرق؛ فكل طريق له ضوابطه ومحطاته الخاصة التي لا يجوز الخلط بينها، حتى يصل القارئ بسند صحيح نقي.\n\n")
                sb.append(primaryChunk.content).append("\n\n")
            }
            TutorExplanationMode.APPLIED_EXAMPLE -> {
                sb.append("📖 **المثال القرآني التطبيقي**:\n")
                sb.append(primaryChunk.content).append("\n\n")
            }
        }

        if (primaryChunk.matnQuotes.isNotEmpty()) {
            sb.append("📜 **الشواهد من المنظومة والتنقيح**:\n")
            primaryChunk.matnQuotes.forEach { quote ->
                sb.append("• «$quote»\n")
            }
            sb.append("\n")
        }

        if (primaryChunk.allowedRules.isNotEmpty()) {
            sb.append("✅ **الأوجه الجائزة**:\n")
            primaryChunk.allowedRules.forEach { sb.append("• $it\n") }
            sb.append("\n")
        }

        if (primaryChunk.forbiddenRules.isNotEmpty()) {
            sb.append("❌ **الموانع والامتناعات**:\n")
            primaryChunk.forbiddenRules.forEach { sb.append("• $it\n") }
            sb.append("\n")
        }

        sb.append("📚 **الإحالة المصدرية**: كتاب الروض الناضر، ${primaryChunk.unitTitle}، ${primaryChunk.lessonTitle} (ص ${primaryChunk.pageStart} - ص ${primaryChunk.pageEnd}).\n\n")

        sb.append("❓ **سؤال تحقق من الفهم**: استناداً لما سبق، هل يجوز الجمع بين قصر المنفصل والسكت العام لحفص؟ ولماذا؟")

        return sb.toString()
    }

    private fun extractVerificationQuestion(text: String): String? {
        val questionPrefixes = listOf("سؤال تحقق من الفهم", "سؤال للتدريب", "سؤال تطبيقي", "❓")
        val lines = text.lines()
        for (i in lines.indices) {
            val line = lines[i]
            if (questionPrefixes.any { line.contains(it) }) {
                return lines.drop(i).joinToString("\n").trim()
            }
        }
        return null
    }
}

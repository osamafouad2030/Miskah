package com.example.mishkat.service

import android.util.Log
import com.example.mishkat.guardrails.MishkatGuardrails
import com.example.mishkat.rag.BookKnowledgeChunk
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig

/**
 * تطبيق خدمة المعلم الذكي مشكاة باستخدام حزمة Firebase AI SDK
 * يدير استدعاء نماذج Gemini السحابية عبر بنية Firebase AI Logic المعتمدة.
 */
class FirebaseAiMishkatService {

    companion object {
        private const val TAG = "FirebaseAiMishkat"
        // النموذج المعتمد للمهام النصية المعقدة والاستدلال القرائي
        private const val MODEL_NAME = "gemini-2.5-flash"
    }

    private var generativeModel: GenerativeModel? = null

    init {
        try {
            generativeModel = Firebase.ai.generativeModel(
                modelName = MODEL_NAME,
                generationConfig = generationConfig {
                    temperature = 0.1f // درجة منخفضة جداً لمنع الهلوسة والالتزام بالنص
                    topP = 0.95f
                },
                systemInstruction = content {
                    text(buildSystemPrompt())
                }
            )
        } catch (e: Throwable) {
            try {
                Log.w(TAG, "Firebase AI not initialized or FirebaseApp not ready: ${e.message}")
            } catch (_: Throwable) {}
        }
    }

    fun isAvailable(): Boolean = generativeModel != null

    /**
     * استدعاء توليد النص عبر Firebase AI SDK مع نصوص RAG المرفقة
     */
    suspend fun generateWithRag(
        augmentedPrompt: String
    ): Result<String> {
        val model = generativeModel ?: return Result.failure(IllegalStateException("Firebase AI is not initialized"))
        return try {
            val response = model.generateContent(augmentedPrompt)
            val text = response.text ?: ""
            Result.success(MishkatGuardrails.sanitizeOutput(text))
        } catch (e: Throwable) {
            try {
                Log.e(TAG, "Firebase AI generation failed", e)
            } catch (_: Throwable) {}
            Result.failure(Exception(e.message ?: "Firebase AI generation failed"))
        }
    }

    private fun buildSystemPrompt(): String {
        return """
            أنت "مشكاة"، المعلم والموجّه الافتراضي الذكي الحصري لمنصة "الروض الناضر في تحريرات عاصم من طيبة النشر" التابعة لأكاديمية الشيخة سماح البنداري لعلوم القراءات.
            
            [قواعد صارمة لا تقبل الاستثناء]
            1. مرجعيتك الوحيدة والمطلقة هي كتاب "الروض الناضر في تحريرات عاصم من طيبة النشر" ونصوص RAG المرفقة حصراً.
            2. يمنع منعاً باتاً اختلاق أي وجه أو طريق أو نسبة أي تحرير لعاصم أو شعبة أو حفص لم يرد في النصوص المرفقة.
            3. إذا كان السؤال عن أي موضوع خارج محتوى الكتاب (مثل الفقه العام، العلوم الدنيوية، لغات برمجة، أو قراءات أخرى لم تُذكر في سياق مقارنة في الكتاب)، يجب عليك الرد حرفياً ودون أي زيادة بالعبارة التالية:
            "${MishkatGuardrails.OUT_OF_SCOPE_EXACT_MESSAGE}"
            4. لا تخرج خطوات التفكير الداخلي أو وسوم التفكير (No Chain of Thought) إطلاقاً.
            5. اذكر دائماً الإحالة المصدرية: (الوحدة، الفصل، الدرس، أرقام الصفحات في كتاب الروض الناضر).
            6. اختم شرحك بسؤال تدريبي أو تحققي من الفهم للطالب.
        """.trimIndent()
    }
}

package com.example.mishkat.rag

/**
 * محرك الاسترجاع المعزز بالتوليد (RAG Retriever) لمدرس الذكاء الاصطناعي "مشكاة"
 * يحلل استفسارات الطالب ويسترجع أدق الجذاذات المعرفية الموثقة من كتاب "الروض الناضر".
 */
class MishkatRagRetriever {

    /**
     * استرجاع أهم المقاطع المطابقة لاستفسار الطالب
     */
    fun retrieveRelevantContext(query: String, currentLessonSlug: String? = null, maxChunks: Int = 3): List<BookKnowledgeChunk> {
        val normalizedQuery = normalizeArabic(query)
        val words = normalizedQuery.split("\\s+".toRegex()).filter { it.length > 2 }

        val scored = MishkatKnowledgeBase.chunks.map { chunk ->
            var score = 0

            // فحص الكلمات المفتاحية
            for (kw in chunk.keywords) {
                val normKw = normalizeArabic(kw)
                if (normalizedQuery.contains(normKw)) {
                    score += 15
                }
                for (w in words) {
                    if (normKw.contains(w) || w.contains(normKw)) {
                        score += 8
                    }
                }
            }

            // فحص محتوى الجذاذة
            val normContent = normalizeArabic(chunk.content)
            for (w in words) {
                if (normContent.contains(w)) {
                    score += 2
                }
            }

            // مطابقة الدرس الحالي
            if (currentLessonSlug != null && chunk.id.contains(currentLessonSlug, ignoreCase = true)) {
                score += 10
            }

            chunk to score
        }

        val topChunks = scored.filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(maxChunks)

        return if (topChunks.isNotEmpty()) topChunks else MishkatKnowledgeBase.chunks.take(2)
    }

    /**
     * صياغة سياق RAG مُهيكل جاهز للحقن في الـ Prompt
     */
    fun formatContextForPrompt(chunks: List<BookKnowledgeChunk>): String {
        val sb = StringBuilder()
        sb.append("=== نصوص موثقة حصرياً من كتاب 'الروض الناضر في تحريرات عاصم من طيبة النشر' ===\n")
        chunks.forEachIndexed { index, chunk ->
            sb.append("\n[المصدر ${index + 1}]:\n")
            sb.append("- الوحدة: ${chunk.unitTitle}\n")
            sb.append("- الفصل: ${chunk.chapterTitle}\n")
            sb.append("- الدرس: ${chunk.lessonTitle}\n")
            sb.append("- الصفحات: ص ${chunk.pageStart} - ص ${chunk.pageEnd}\n")
            sb.append("- الموضوع: ${chunk.topic}\n")
            sb.append("- النص التحريري المعتمد:\n${chunk.content}\n")
            if (chunk.matnQuotes.isNotEmpty()) {
                sb.append("- الشواهد من المنظومات:\n")
                chunk.matnQuotes.forEach { sb.append("  * $it\n") }
            }
            if (chunk.allowedRules.isNotEmpty()) {
                sb.append("- الأوجه الجائزة: ${chunk.allowedRules.joinToString(" ، ")}\n")
            }
            if (chunk.forbiddenRules.isNotEmpty()) {
                sb.append("- الأوجه الممتنعة: ${chunk.forbiddenRules.joinToString(" ، ")}\n")
            }
        }
        sb.append("\n=== نهاية النصوص الموثقة من الكتاب ===\n")
        return sb.toString()
    }

    /**
     * توحيد الحروف العربية وتجريد التشكيل لتحسين البحث
     */
    fun normalizeArabic(text: String): String {
        return text
            .replace("[\\u064B-\\u065F\\u0670]".toRegex(), "") // إزالة التشكيل
            .replace("[إأآا]".toRegex(), "ا")
            .replace("ى", "ي")
            .replace("ة", "ه")
            .replace("ؤ", "و")
            .replace("ئ", "ي")
            .trim()
    }
}

package com.example.mishkat.rag

/**
 * جذاذة معرفية تمثل مقطعاً موثقاً من كتاب "الروض الناضر في تحريرات عاصم من طيبة النشر"
 */
data class BookKnowledgeChunk(
    val id: String,
    val unitTitle: String,
    val chapterTitle: String,
    val lessonTitle: String,
    val pageStart: Int,
    val pageEnd: Int,
    val topic: String,
    val content: String,
    val keywords: List<String>,
    val matnQuotes: List<String> = emptyList(),
    val allowedRules: List<String> = emptyList(),
    val forbiddenRules: List<String> = emptyList(),
    val unitTitleEn: String? = null,
    val chapterTitleEn: String? = null,
    val lessonTitleEn: String? = null,
    val topicEn: String? = null
) {
    fun getLocalizedLessonTitle(isEnglish: Boolean): String =
        if (isEnglish && !lessonTitleEn.isNullOrBlank()) lessonTitleEn else lessonTitle

    fun getLocalizedUnitTitle(isEnglish: Boolean): String =
        if (isEnglish && !unitTitleEn.isNullOrBlank()) unitTitleEn else unitTitle

    fun getLocalizedChapterTitle(isEnglish: Boolean): String =
        if (isEnglish && !chapterTitleEn.isNullOrBlank()) chapterTitleEn else chapterTitle

    fun getLocalizedTopic(isEnglish: Boolean): String =
        if (isEnglish && !topicEn.isNullOrBlank()) topicEn else topic
}

package com.example.mishkat.model

/**
 * تصنيفات الأوسمة والإنجازات لطلاب تحريرات القراءات في تطبيق مشكاة
 */
enum class BadgeCategory(val titleArabic: String, val titleEnglish: String) {
    ALL("الكل", "All"),
    LESSONS("إنجاز الدروس", "Lessons"),
    PERFECT_SCORE("الدرجات الكاملة", "Perfect Scores"),
    QUIZ_MASTERY("إتقان الاختبارات", "Quizzes"),
    DILIGENCE("الاجتهاد والملاحظات", "Diligence");

    fun localizedTitle(isEn: Boolean): String = if (isEn) titleEnglish else titleArabic
}

/**
 * تمثيل وسام إنجاز أكاديمي/قرآني للطالب
 */
data class MishkatBadge(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: BadgeCategory,
    val targetCount: Int,
    val currentCount: Int,
    val isUnlocked: Boolean,
    val unlockedAtTimestamp: Long? = null,
    val iconType: BadgeIconType,
    val titleEn: String = "",
    val subtitleEn: String = "",
    val descriptionEn: String = ""
) {
    val progressRatio: Float
        get() = if (targetCount > 0) (currentCount.toFloat() / targetCount).coerceIn(0f, 1f) else 0f

    val progressPercentage: Int
        get() = (progressRatio * 100).toInt()

    fun localizedTitle(isEn: Boolean): String =
        if (isEn && titleEn.isNotBlank()) titleEn else title

    fun localizedSubtitle(isEn: Boolean): String =
        if (isEn && subtitleEn.isNotBlank()) subtitleEn else subtitle

    fun localizedDescription(isEn: Boolean): String =
        if (isEn && descriptionEn.isNotBlank()) descriptionEn else description
}

/**
 * أنواع أيقونات الأوسمة لسهولة العرض الرسومي
 */
enum class BadgeIconType {
    FIRST_LESSON,      // فاتحة الباب
    STUDENT_PROGRESS,  // طالب متقدم
    SCHOLAR_PROGRESS,  // جامع المسائل
    COMPLETION_CROWN,  // خاتم المنهج
    STAR_PERFECT,      // أول درجة كاملة
    TRIPLE_PERFECT,    // عَلَم الإتقان (3 درجات كاملة)
    MASTER_PERFECT,    // المقرئ الدقيق (5 درجات كاملة)
    QUIZ_PASSED,       // فارس الاختبارات
    NOTE_TAKER         // مُدوّن الفوائد
}

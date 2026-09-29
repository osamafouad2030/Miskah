package com.example.mishkat.model

import com.example.mishkat.localization.MishkatLanguage

/**
 * مستويات الشرح والتبسيط لمعلم الذكاء الاصطناعي "مشكاة"
 */
enum class TutorLevel(
    val labelArabic: String,
    val descriptionArabic: String,
    val labelEnglish: String = labelArabic,
    val descriptionEnglish: String = descriptionArabic
) {
    BEGINNER(
        labelArabic = "مبتدئ",
        descriptionArabic = "تبسيط المحتوى، لغة ميسرة، أمثلة توضيحية خالية من التعقيد",
        labelEnglish = "Beginner",
        descriptionEnglish = "Simplified rules, accessible language, introductory examples"
    ),
    INTERMEDIATE(
        labelArabic = "متوسط",
        descriptionArabic = "تقعيد أصولي، عزو للطرق والرواة، مقارنة بين الشاطبية والطيبة",
        labelEnglish = "Intermediate",
        descriptionEnglish = "Core principles, attribution of paths, comparison between Shatibiyyah and Tayyiba"
    ),
    ADVANCED(
        labelArabic = "متقدم",
        descriptionArabic = "دقائق الأسانيد، نصوص المحررين والكتب الأصلية، وعلل الامتناع والأوجه المركبة",
        labelEnglish = "Advanced",
        descriptionEnglish = "In-depth chains of transmission, classical references, and precise prohibitions"
    );

    fun getLabel(isEnglish: Boolean): String = if (isEnglish) labelEnglish else labelArabic
    fun getDescription(isEnglish: Boolean): String = if (isEnglish) descriptionEnglish else descriptionArabic
}

/**
 * طرق الشرح الثلاث المطلوبة في مواصفات مشكاة
 */
enum class TutorExplanationMode(
    val labelArabic: String,
    val labelEnglish: String = labelArabic
) {
    DIRECT("تعريف مباشر وتأصيل أكاديمي", "Direct Definition & Academic Grounding"),
    METAPHOR("تشبيه عملي ومثال من الحياة اليومية", "Conceptual Analogy & Everyday Example"),
    APPLIED_EXAMPLE("مثال تطبيقي قرآني محلول خطوة بخطوة", "Step-by-Step Quranic Applied Example");

    fun getLabel(isEnglish: Boolean): String = if (isEnglish) labelEnglish else labelArabic
}

/**
 * طلب المحادثة مع مشكاة
 */
data class MishkatRequest(
    val userQuery: String,
    val conversationHistory: List<MishkatMessage> = emptyList(),
    val currentLessonSlug: String? = null,
    val tutorLevel: TutorLevel = TutorLevel.INTERMEDIATE,
    val explanationMode: TutorExplanationMode = TutorExplanationMode.DIRECT,
    val hintStep: Int = 0, // 0 = no hint, 1 = Hint 1, 2 = Hint 2, 3 = Hint 3
    val isExerciseRequest: Boolean = false,
    val isRevisionPlanRequest: Boolean = false,
    val isRecitationCorrectionRequest: Boolean = false,
    val targetReader: String? = "حفص أو شعبة",
    val studentErrors: List<String> = emptyList(),
    val language: MishkatLanguage = MishkatLanguage.ARABIC
)

/**
 * رسالة محادثة في جلسة مشكاة
 */
data class MishkatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val citedReferences: List<String> = emptyList(),
    val isOutOfScope: Boolean = false,
    val hintStep: Int = 0,
    val isVoiceRecitation: Boolean = false,
    val isRecitationCorrection: Boolean = false
)

enum class MessageSender {
    STUDENT,
    MISHKAT
}

/**
 * استجابة مشكاة المدعومة بالتحريرات والمصادر
 */
data class MishkatResponse(
    val replyText: String,
    val citedReferences: List<String> = emptyList(),
    val citedPages: List<Int> = emptyList(),
    val verificationQuestion: String? = null,
    val hintStep: Int = 0,
    val isOutOfScope: Boolean = false,
    val isRecitationCorrection: Boolean = false,
    val sourceEngine: String = "Firebase AI / Gemini"
)

package com.example.mishkat.service

import com.example.mishkat.model.MishkatRequest
import com.example.mishkat.model.MishkatResponse
import com.example.mishkat.model.TutorExplanationMode
import com.example.mishkat.model.TutorLevel

/**
 * الواجهة البرمجية لخدمة مدرس الذكاء الاصطناعي "مشكاة"
 * تدير التفاعل الأكاديمي، الشرح المتعدد الأنماط، التلميحات التدريجية، واقتراح خطط المراجعة.
 */
interface MishkatTutorService {

    /**
     * إرسال استفسار عام إلى مشكاة مع تفعيل RAG وGuardrails
     */
    suspend fun askMishkat(request: MishkatRequest): MishkatResponse

    /**
     * شرح مفهوم قرائي محدد بإحدى الطرق الثلاث (مباشر / تشبيه / مثال تطبيقي) وفق المستوى
     */
    suspend fun explainConcept(
        concept: String,
        mode: TutorExplanationMode = TutorExplanationMode.DIRECT,
        level: TutorLevel = TutorLevel.INTERMEDIATE
    ): MishkatResponse

    /**
     * تقديم تلميح تدريجي خطوة بخطوة (Hint 1, Hint 2, Hint 3)
     */
    suspend fun getNextHint(
        problemOrConcept: String,
        currentStep: Int
    ): MishkatResponse

    /**
     * إنشاء خطة مراجعة مخصصة وموجهة بناءً على أنماط أخطاء الطالب
     */
    suspend fun generateRevisionPlan(
        studentErrors: List<String>
    ): MishkatResponse

    /**
     * توليد تمارين إضافية متدرجة الصعوبة حول باب تحريري محدد
     */
    suspend fun generatePracticeExercises(
        topic: String,
        count: Int = 3,
        level: TutorLevel = TutorLevel.INTERMEDIATE
    ): MishkatResponse

    /**
     * تقييم وتصحيح فوري لتلاوة الطالب وتحرير الأوجه المقروء بها وفق كتاب الروض الناضر
     */
    suspend fun evaluateRecitation(
        recitationText: String,
        targetReaderOrRule: String? = null,
        level: TutorLevel = TutorLevel.INTERMEDIATE
    ): MishkatResponse
}

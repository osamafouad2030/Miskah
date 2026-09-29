package com.example.mishkat.data

import android.content.Context
import android.content.SharedPreferences
import com.example.mishkat.model.BadgeCategory
import com.example.mishkat.model.BadgeIconType
import com.example.mishkat.model.MishkatBadge
import org.json.JSONObject

/**
 * مدير نظام إنجازات وأوسمة الطالب وحفظ التقدم في تطبيق مشكاة
 */
object MishkatAchievementsManager {

    private const val PREFS_NAME = "mishkat_student_achievements_v1"
    private const val KEY_COMPLETED_LESSONS = "completed_lesson_ids"
    private const val KEY_QUIZ_SCORES_JSON = "quiz_scores_json"
    private const val KEY_UNLOCKED_BADGE_PREFIX = "badge_unlocked_time_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * جلب معرفات الدروس المكتملة
     */
    fun getCompletedLessonIds(context: Context): Set<String> {
        val prefs = getPrefs(context)
        return prefs.getStringSet(KEY_COMPLETED_LESSONS, emptySet()) ?: emptySet()
    }

    /**
     * تبديل حالة إكمال درس
     */
    fun toggleLessonCompleted(context: Context, lessonId: String): Pair<Set<String>, List<MishkatBadge>> {
        val current = getCompletedLessonIds(context).toMutableSet()
        val isNowCompleted = if (current.contains(lessonId)) {
            current.remove(lessonId)
            false
        } else {
            current.add(lessonId)
            true
        }
        getPrefs(context).edit().putStringSet(KEY_COMPLETED_LESSONS, current).apply()

        // فحص الأوسمة الجديدة إذا اكتمل درس
        val newlyUnlocked = if (isNowCompleted) {
            checkAndUnlockBadges(context)
        } else emptyList()

        return Pair(current, newlyUnlocked)
    }

    /**
     * تعيين درس كمكتمل
     */
    fun markLessonCompleted(context: Context, lessonId: String): List<MishkatBadge> {
        val current = getCompletedLessonIds(context).toMutableSet()
        if (!current.contains(lessonId)) {
            current.add(lessonId)
            getPrefs(context).edit().putStringSet(KEY_COMPLETED_LESSONS, current).apply()
            return checkAndUnlockBadges(context)
        }
        return emptyList()
    }

    /**
     * جلب درجات الاختبارات لكل درس (الدرجة المحققة، الدرجة الإجمالية)
     */
    fun getQuizScores(context: Context): Map<String, Pair<Int, Int>> {
        val prefs = getPrefs(context)
        val jsonStr = prefs.getString(KEY_QUIZ_SCORES_JSON, "{}") ?: "{}"
        val result = mutableMapOf<String, Pair<Int, Int>>()
        try {
            val json = JSONObject(jsonStr)
            val keys = json.keys()
            while (keys.hasNext()) {
                val lessonId = keys.next()
                val scoreObj = json.getJSONObject(lessonId)
                val score = scoreObj.getInt("score")
                val total = scoreObj.getInt("total")
                result[lessonId] = Pair(score, total)
            }
        } catch (e: Exception) {
            // تجاهل الخطأ والبدء بخريطة فارغة
        }
        return result
    }

    /**
     * تسجيل نتيجة اختبار وتحديث الأوسمة إن استحق الطالب
     */
    fun recordQuizScore(context: Context, lessonId: String, score: Int, total: Int): List<MishkatBadge> {
        val currentScores = getQuizScores(context).toMutableMap()
        val previous = currentScores[lessonId]

        // نحتفظ بأعلى نتيجة حققها الطالب
        val shouldUpdate = if (previous == null) {
            true
        } else {
            val prevRatio = previous.first.toFloat() / previous.second
            val newRatio = score.toFloat() / total
            newRatio >= prevRatio
        }

        if (shouldUpdate) {
            currentScores[lessonId] = Pair(score, total)
            val json = JSONObject()
            for ((key, value) in currentScores) {
                val obj = JSONObject()
                obj.put("score", value.first)
                obj.put("total", value.second)
                json.put(key, obj)
            }
            getPrefs(context).edit().putString(KEY_QUIZ_SCORES_JSON, json.toString()).apply()
        }

        // إذا نجح الطالب (60% أو أعلى)، يعلّم الدرس تلقائياً كمكتمل إن لم يكن معلماً
        if (score.toFloat() / total >= 0.6f) {
            val completed = getCompletedLessonIds(context).toMutableSet()
            if (!completed.contains(lessonId)) {
                completed.add(lessonId)
                getPrefs(context).edit().putStringSet(KEY_COMPLETED_LESSONS, completed).apply()
            }
        }

        return checkAndUnlockBadges(context)
    }

    /**
     * فحص جميع الأوسمة وتسجيل الأوسمة الجديدة التي استوفت شروطها
     */
    fun checkAndUnlockBadges(
        context: Context,
        notesCount: Int = 0,
        totalLessonsInCurriculum: Int = 12
    ): List<MishkatBadge> {
        val completedIds = getCompletedLessonIds(context)
        val quizScores = getQuizScores(context)
        val prefs = getPrefs(context)

        val completedCount = completedIds.size
        val perfectScoresCount = quizScores.values.count { it.second > 0 && it.first == it.second }
        val passedQuizzesCount = quizScores.values.count { it.second > 0 && (it.first.toFloat() / it.second) >= 0.7f }

        val definitions = getBadgeDefinitions(
            completedCount = completedCount,
            perfectScoresCount = perfectScoresCount,
            passedQuizzesCount = passedQuizzesCount,
            notesCount = notesCount,
            totalLessonsCount = totalLessonsInCurriculum
        )

        val newlyUnlocked = mutableListOf<MishkatBadge>()
        val editor = prefs.edit()

        for (def in definitions) {
            val key = KEY_UNLOCKED_BADGE_PREFIX + def.id
            val wasUnlocked = prefs.contains(key)
            if (def.isUnlocked && !wasUnlocked) {
                val timestamp = System.currentTimeMillis()
                editor.putLong(key, timestamp)
                newlyUnlocked.add(def.copy(unlockedAtTimestamp = timestamp))
            }
        }
        editor.apply()

        return newlyUnlocked
    }

    /**
     * جلب قائمة الأوسمة الحالية مع حالتها
     */
    fun getAllBadges(
        context: Context,
        notesCount: Int = 0,
        totalLessonsInCurriculum: Int = 12
    ): List<MishkatBadge> {
        val completedIds = getCompletedLessonIds(context)
        val quizScores = getQuizScores(context)
        val prefs = getPrefs(context)

        val completedCount = completedIds.size
        val perfectScoresCount = quizScores.values.count { it.second > 0 && it.first == it.second }
        val passedQuizzesCount = quizScores.values.count { it.second > 0 && (it.first.toFloat() / it.second) >= 0.7f }

        val definitions = getBadgeDefinitions(
            completedCount = completedCount,
            perfectScoresCount = perfectScoresCount,
            passedQuizzesCount = passedQuizzesCount,
            notesCount = notesCount,
            totalLessonsCount = totalLessonsInCurriculum
        )

        return definitions.map { badge ->
            val key = KEY_UNLOCKED_BADGE_PREFIX + badge.id
            val unlockedTimestamp = if (prefs.contains(key)) prefs.getLong(key, 0L) else null
            badge.copy(
                isUnlocked = badge.isUnlocked || unlockedTimestamp != null,
                unlockedAtTimestamp = unlockedTimestamp
            )
        }
    }

    /**
     * تعريفات منظومة الأوسمة في تطبيق مشكاة
     */
    private fun getBadgeDefinitions(
        completedCount: Int,
        perfectScoresCount: Int,
        passedQuizzesCount: Int,
        notesCount: Int,
        totalLessonsCount: Int
    ): List<MishkatBadge> {
        return listOf(
            // 1. فاتحة الباب
            MishkatBadge(
                id = "first_lesson",
                title = "فاتحة الباب",
                subtitle = "الخطوة التحريرية الأولى",
                description = "إتمام أول درس تحريري من كتاب «الروض الناضر» بنجاح",
                category = BadgeCategory.LESSONS,
                targetCount = 1,
                currentCount = completedCount,
                isUnlocked = completedCount >= 1,
                iconType = BadgeIconType.FIRST_LESSON,
                titleEn = "Gateway to Tahrirat",
                subtitleEn = "First Scholarly Step",
                descriptionEn = "Successfully complete your first lesson from Ar-Rawdat An-Nadir"
            ),
            // 2. طالب مجتهد
            MishkatBadge(
                id = "three_lessons",
                title = "طالب مجتهد",
                subtitle = "المثابرة والانتظام",
                description = "إتمام 3 دروس تحريرية متتالية في روايتي حفص وشعبة",
                category = BadgeCategory.LESSONS,
                targetCount = 3,
                currentCount = completedCount,
                isUnlocked = completedCount >= 3,
                iconType = BadgeIconType.STUDENT_PROGRESS,
                titleEn = "Diligent Student",
                subtitleEn = "Perseverance & Dedication",
                descriptionEn = "Complete 3 consecutive lessons covering Hafs and Shu'bah Tahrirat"
            ),
            // 3. جامع المسائل
            MishkatBadge(
                id = "six_lessons",
                title = "جامع المسائل",
                subtitle = "إحاطة بالأصول والفرش",
                description = "إتمام 6 دروس مقررة من أبواب طيبة النشر",
                category = BadgeCategory.LESSONS,
                targetCount = 6,
                currentCount = completedCount,
                isUnlocked = completedCount >= 6,
                iconType = BadgeIconType.SCHOLAR_PROGRESS,
                titleEn = "Compiler of Chapters",
                subtitleEn = "Mastering Usul & Farsh",
                descriptionEn = "Complete 6 curriculum lessons across major chapters of Tayyibat An-Nashr"
            ),
            // 4. خاتم المنهج
            MishkatBadge(
                id = "curriculum_completed",
                title = "تاج التحريرات",
                subtitle = "إتمام مقرر الروض الناضر",
                description = "إنهاء دراسة جميع الدروس المقررة في المنهج التحريري",
                category = BadgeCategory.LESSONS,
                targetCount = totalLessonsCount,
                currentCount = completedCount,
                isUnlocked = completedCount >= totalLessonsCount && totalLessonsCount > 0,
                iconType = BadgeIconType.COMPLETION_CROWN,
                titleEn = "Crown of Tahrirat",
                subtitleEn = "Curriculum Completion",
                descriptionEn = "Finish all prescribed lessons in Ar-Rawdat An-Nadir curriculum"
            ),
            // 5. الدرجة الكاملة الأولى
            MishkatBadge(
                id = "first_perfect_score",
                title = "نجم الإتقان",
                subtitle = "أول علامة كاملة 100%",
                description = "الحصول على الدرجة الكاملة (100%) في اختبار أحد الدروس",
                category = BadgeCategory.PERFECT_SCORE,
                targetCount = 1,
                currentCount = perfectScoresCount,
                isUnlocked = perfectScoresCount >= 1,
                iconType = BadgeIconType.STAR_PERFECT,
                titleEn = "Star of Mastery",
                subtitleEn = "First 100% Score",
                descriptionEn = "Achieve a perfect 100% score on any lesson mastery quiz"
            ),
            // 6. ثلاثية الإتقان
            MishkatBadge(
                id = "triple_perfect_scores",
                title = "عَلَم التحرير",
                subtitle = "ثلاث درجات كاملة",
                description = "الحصول على الدرجة الكاملة في 3 اختبارات تحريرية مختلفة",
                category = BadgeCategory.PERFECT_SCORE,
                targetCount = 3,
                currentCount = perfectScoresCount,
                isUnlocked = perfectScoresCount >= 3,
                iconType = BadgeIconType.TRIPLE_PERFECT,
                titleEn = "Standard of Precision",
                subtitleEn = "Triple Perfect Scores",
                descriptionEn = "Score 100% on 3 different lesson quizzes"
            ),
            // 7. خبير الممتنعات
            MishkatBadge(
                id = "master_perfect_scores",
                title = "المقرئ الدقيق",
                subtitle = "خمس درجات كاملة",
                description = "التميز بالدرجة الكاملة (100%) في 5 اختبارات تحريرية",
                category = BadgeCategory.PERFECT_SCORE,
                targetCount = 5,
                currentCount = perfectScoresCount,
                isUnlocked = perfectScoresCount >= 5,
                iconType = BadgeIconType.MASTER_PERFECT,
                titleEn = "Precision Reciter",
                subtitleEn = "Five Perfect Scores",
                descriptionEn = "Achieve 100% mastery on 5 distinct Tahrirat quizzes"
            ),
            // 8. فارس الاختبارات
            MishkatBadge(
                id = "quiz_master",
                title = "فارس الاختبارات",
                subtitle = "اجتياز الاختبارات بامتياز",
                description = "اجتياز 4 اختبارات بنسبة إتقان تزيد عن 70%",
                category = BadgeCategory.QUIZ_MASTERY,
                targetCount = 4,
                currentCount = passedQuizzesCount,
                isUnlocked = passedQuizzesCount >= 4,
                iconType = BadgeIconType.QUIZ_PASSED,
                titleEn = "Quiz Knight",
                subtitleEn = "Excellence in Exams",
                descriptionEn = "Pass 4 quizzes with a score higher than 70%"
            ),
            // 9. مُدوّن الفوائد
            MishkatBadge(
                id = "scholar_notes",
                title = "مُدوّن الفوائد",
                subtitle = "تقييد العلم بالكتابة",
                description = "تسجيل ملحوظات وفوائد دراسية خاصة في دروس التحريرات",
                category = BadgeCategory.DILIGENCE,
                targetCount = 1,
                currentCount = notesCount,
                isUnlocked = notesCount >= 1,
                iconType = BadgeIconType.NOTE_TAKER,
                titleEn = "Scholar's Scribe",
                subtitleEn = "Preserving Knowledge",
                descriptionEn = "Record personal study notes on Tahrirat lessons"
            )
        )
    }

    /**
     * لقب ورتبة الطالب التحفيزية وفق عدد الأوسمة المحققة
     */
    fun getStudentRank(unlockedCount: Int, isEn: Boolean = false): Pair<String, String> {
        return when {
            unlockedCount >= 8 -> if (isEn)
                Pair("Certified Master Reciter", "Attained top-tier precision in Tayyibat An-Nashr Tahrirat")
            else
                Pair("شيخ مقرئ مجاز", "بلغت مرتبة التحرير والإتقان العالي في طيبة النشر")
            unlockedCount >= 6 -> if (isEn)
                Pair("Precise Editor", "Mastered distinguishing permissible vs. prohibited facets")
            else
                Pair("محرر متقن", "تستحضر الفروق والممتنعات بدقة وبصيرة")
            unlockedCount >= 4 -> if (isEn)
                Pair("Tayyiba Scholar", "Advanced significantly through Usul and Farsh chapters")
            else
                Pair("باحث في طيبة النشر", "قطعت شوطاً مباركاً في أصول التحرير والفرش")
            unlockedCount >= 2 -> if (isEn)
                Pair("Aspiring Seeker", "Steady foundational progress and continuous study")
            else
                Pair("طالب هميم", "بداية راسخة ومثابرة متواصلة في المدارسة")
            unlockedCount >= 1 -> if (isEn)
                Pair("Initiate in Mastery", "Started your canonical Quranic Tahrirat journey")
            else
                Pair("سائر في مدارج الإتقان", "بدأت أولى خطوات التحرير القرآني المعتمد")
            else -> if (isEn)
                Pair("New Learner", "Start lessons and quizzes to earn scholarship badges")
            else
                Pair("مستهلّ المسار", "انطلق في دراسة الدروس وخوض الاختبارات لكسب الأوسمة")
        }
    }
}

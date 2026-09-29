package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.mishkat.data.MishkatAchievementsManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MishkatAchievementsManagerTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        // تنظيف تفضيلات الإنجازات قبل كل اختبار
        context.getSharedPreferences("mishkat_student_achievements_v1", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun firstLessonCompletion_unlocksFirstStepBadge() {
        val (completedLessons, newBadges) = MishkatAchievementsManager.toggleLessonCompleted(context, "lesson_1")
        assertTrue(completedLessons.contains("lesson_1"))
        assertTrue("يجب فتح وسام المستفتح القرآني عند إنجاز أول درس", newBadges.any { it.id == "first_lesson" })

        // التحقق من الرتبة بعد الإنجاز
        val (rankTitle, rankDesc) = MishkatAchievementsManager.getStudentRank(1)
        assertEquals("طالب مبتدئ", rankTitle)
    }

    @Test
    fun perfectQuizScore_unlocksPerfectScoreBadge() {
        val newBadges = MishkatAchievementsManager.recordQuizScore(
            context = context,
            lessonId = "lesson_tajweed_1",
            score = 5,
            total = 5
        )

        assertTrue(
            "يجب فتح وسام نجم الإتقان عند تحقيق علامة كاملة في الاختبار",
            newBadges.any { it.id == "perfect_quiz_score" }
        )

        // التحقق من حفظ نتيجة الاختبار
        val scores = MishkatAchievementsManager.getQuizScores(context)
        assertEquals(Pair(5, 5), scores["lesson_tajweed_1"])
    }

    @Test
    fun fiveLessonsCompletion_unlocksFiveLessonsBadge() {
        listOf("l1", "l2", "l3", "l4").forEach { id ->
            MishkatAchievementsManager.toggleLessonCompleted(context, id)
        }
        val (_, newBadges) = MishkatAchievementsManager.toggleLessonCompleted(context, "l5")
        assertTrue("يجب فتح وسام السالك المجد عند إتمام 5 دروس", newBadges.any { it.id == "five_lessons" })
    }

    @Test
    fun studentRank_progressesWithEarnedBadges() {
        assertEquals("طالب مبتدئ", MishkatAchievementsManager.getStudentRank(1).first)
        assertEquals("مُحصِّل مجتهد", MishkatAchievementsManager.getStudentRank(3).first)
        assertEquals("باحث في طيبة النشر", MishkatAchievementsManager.getStudentRank(5).first)
        assertEquals("مُتقن التحريرات", MishkatAchievementsManager.getStudentRank(7).first)
        assertEquals("عَلَم التحرير ومقرئ الإتقان", MishkatAchievementsManager.getStudentRank(9).first)
    }
}

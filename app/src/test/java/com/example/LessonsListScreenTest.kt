package com.example

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.mishkat.ui.LessonsListScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LessonsListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLessonsListScreen_elementsDisplayedAndSearchable() {
        var selectedTitle: String? = null
        var selectedTopic: String? = null

        composeTestRule.setContent {
            MyApplicationTheme {
                LessonsListScreen(
                    onSelectLessonForTutor = { title, topic ->
                        selectedTitle = title
                        selectedTopic = topic
                    }
                )
            }
        }

        // التحقق من ظهور بطاقة التقدم وشريط الإنجاز والإحصائيات
        composeTestRule.onNodeWithTag("lessons_progress_card").assertExists()
        composeTestRule.onNodeWithTag("lessons_progress_bar").assertExists()
        composeTestRule.onNodeWithTag("lessons_progress_stats_text").assertExists()
        composeTestRule.onNodeWithTag("lessons_progress_percentage_text").assertExists()
        composeTestRule.onNodeWithTag("lessons_progress_stats_text").assertTextContains("0", substring = true)
        composeTestRule.onNodeWithTag("lessons_progress_percentage_text").assertTextContains("0%", substring = true)

        // التحقق من ظهور شريط البحث وقائمة الدروس
        composeTestRule.onNodeWithTag("lessons_search_input").assertExists()
        composeTestRule.onNodeWithTag("lessons_lazy_column").assertExists()

        // التحقق من العنصر الأول في القائمة (sanad_general)
        composeTestRule.onNodeWithTag("lesson_card_sanad_general").assertExists()
        composeTestRule.onNodeWithTag("ask_tutor_lesson_sanad_general").assertExists()
        composeTestRule.onNodeWithTag("toggle_complete_lesson_sanad_general").assertExists()

        // تعليم الدرس كمكتمل والتحقق من تحديث الإحصائيات ونسبة التقدم
        composeTestRule.onNodeWithTag("toggle_complete_lesson_sanad_general").performClick()
        composeTestRule.onNodeWithTag("lessons_progress_stats_text").assertTextContains("1", substring = true)

        // إدخال نص للبحث
        composeTestRule.onNodeWithTag("lessons_search_input").performTextInput("السكت")
    }
}

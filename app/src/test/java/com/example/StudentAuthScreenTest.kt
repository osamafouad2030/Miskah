package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.example.mishkat.ui.StudentAuthScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StudentAuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testStudentAuthScreen_fieldsDisplayedAndAcceptInput() {
        composeTestRule.setContent {
            MyApplicationTheme {
                StudentAuthScreen()
            }
        }

        // التحقق من وجود حقول إدخال اسم الطالب والبريد الإلكتروني وكلمة المرور
        composeTestRule.onNodeWithTag("student_name_input").assertExists()
        composeTestRule.onNodeWithTag("student_email_input").assertExists()
        composeTestRule.onNodeWithTag("student_password_input").assertExists()
        composeTestRule.onNodeWithTag("auth_submit_button").assertExists()

        // إدخال بيانات تجريبية
        composeTestRule.onNodeWithTag("student_name_input").performTextInput("عبد الله محمد")
        composeTestRule.onNodeWithTag("student_email_input").performTextInput("student@example.com")
        composeTestRule.onNodeWithTag("student_password_input").performTextInput("secret123")
    }
}

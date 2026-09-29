package com.example.mishkat.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.example.mishkat.data.MishkatAchievementsManager
import com.example.mishkat.localization.MishkatStrings
import com.example.mishkat.ui.theme.MishkatGoldContainer
import com.example.mishkat.ui.theme.MishkatOnGoldContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mishkat.quiz.MishkatQuizRepository
import com.example.mishkat.quiz.QuizQuestion
import com.example.mishkat.rag.BookKnowledgeChunk
import com.example.mishkat.ui.theme.MishkatBorder
import com.example.mishkat.ui.theme.MishkatEmeraldContainer
import com.example.mishkat.ui.theme.MishkatEmeraldDark
import com.example.mishkat.ui.theme.MishkatEmeraldLight
import com.example.mishkat.ui.theme.MishkatEmeraldPrimary
import com.example.mishkat.ui.theme.MishkatGoldAccent
import com.example.mishkat.ui.theme.MishkatManuscriptSand
import com.example.mishkat.ui.theme.MishkatTextPrimary
import com.example.mishkat.ui.theme.MishkatTextSecondary

/**
 * نافذة حوارية تفاعلية لاختبار استيعاب الطالب لتحريرات الدرس
 * تعرض أسئلة اختيار من متعدد، وتتحقق فورياً مع الشرح التحريري وعرض النتيجة النهائية.
 */
@Composable
fun LessonQuizDialog(
    chunk: BookKnowledgeChunk,
    onDismiss: () -> Unit,
    isEn: Boolean = false,
    onQuizCompleted: (score: Int, total: Int) -> Unit = { _, _ -> },
    onAskMishkatForClarification: (String) -> Unit = {}
) {
    val questions = remember(chunk.id) {
        MishkatQuizRepository.getQuizForLesson(chunk.id, chunk.lessonTitle)
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } // questionIndex -> selectedOptionIndex
    var isSubmitted by remember { mutableStateOf(false) }

    val totalQuestions = questions.size
    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    // حساب النتيجة النهائية
    val correctAnswersCount = remember(isSubmitted) {
        if (!isSubmitted) 0
        else {
            questions.indices.count { idx ->
                userAnswers[idx] == questions[idx].correctOptionIndex
            }
        }
    }

    val scorePercentage = if (totalQuestions > 0) ((correctAnswersCount.toFloat() / totalQuestions) * 100).toInt() else 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val layoutDirection = if (isEn) LayoutDirection.Ltr else LayoutDirection.Rtl
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.96f)
                    .testTag("lesson_quiz_dialog"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MishkatBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // شريط العنوان العلوي
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MishkatEmeraldContainer,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Quiz,
                                        contentDescription = null,
                                        tint = MishkatEmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = MishkatStrings.quizDialogHeader(isEn),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatTextPrimary
                                )
                                Text(
                                    text = chunk.lessonTitle,
                                    fontSize = 11.5.sp,
                                    color = MishkatTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_quiz_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = MishkatStrings.closeQuiz(isEn),
                                tint = MishkatTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isSubmitted) {
                        // شاشة عرض النتيجة والتقييم النهائي
                        QuizResultView(
                            chunk = chunk,
                            score = correctAnswersCount,
                            total = totalQuestions,
                            percentage = scorePercentage,
                            questions = questions,
                            userAnswers = userAnswers,
                            isEn = isEn,
                            onRetry = {
                                userAnswers.clear()
                                currentQuestionIndex = 0
                                isSubmitted = false
                            },
                            onFinish = {
                                onQuizCompleted(correctAnswersCount, totalQuestions)
                                onDismiss()
                            },
                            onAskMishkatForClarification = onAskMishkatForClarification
                        )
                    } else if (currentQuestion != null) {
                        // شاشة خوض الاختبار التفاعلي
                        val progress by animateFloatAsState(
                            targetValue = (currentQuestionIndex + 1).toFloat() / totalQuestions,
                            label = "QuizProgress"
                        )

                        // مؤشر التقدم
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = MishkatStrings.questionCountFormat(isEn, currentQuestionIndex + 1, totalQuestions),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatEmeraldPrimary
                                )
                                Text(
                                    text = "${((currentQuestionIndex + 1).toFloat() / totalQuestions * 100).toInt()}%",
                                    fontSize = 11.5.sp,
                                    color = MishkatTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MishkatEmeraldPrimary,
                                trackColor = MishkatManuscriptSand
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // نص السؤال
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MishkatManuscriptSand.copy(alpha = 0.5f),
                                border = BorderStroke(0.5.dp, MishkatBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = null,
                                        tint = MishkatEmeraldPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = currentQuestion.questionText,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MishkatTextPrimary,
                                        lineHeight = 22.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // خيارات الإجابة
                            val selectedOption = userAnswers[currentQuestionIndex]

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                currentQuestion.options.forEachIndexed { optIndex, optionText ->
                                    val isSelected = selectedOption == optIndex
                                    Surface(
                                        onClick = {
                                            userAnswers[currentQuestionIndex] = optIndex
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) MishkatEmeraldContainer else Color.White,
                                        border = BorderStroke(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) MishkatEmeraldPrimary else MishkatBorder
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("quiz_option_${currentQuestionIndex}_$optIndex")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) MishkatEmeraldPrimary else MishkatManuscriptSand,
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${optIndex + 1}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else MishkatTextSecondary
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Text(
                                                text = optionText,
                                                fontSize = 12.5.sp,
                                                color = if (isSelected) MishkatEmeraldDark else MishkatTextPrimary,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                modifier = Modifier.weight(1f),
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // أزرار التنقل وتسليم الاختبار
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentQuestionIndex > 0) {
                                OutlinedButton(
                                    onClick = { currentQuestionIndex-- },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, MishkatBorder)
                                ) {
                                    Icon(
                                        imageVector = if (isEn) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(MishkatStrings.previousQuestion(isEn), fontSize = 12.sp)
                                }
                            } else {
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            if (currentQuestionIndex < totalQuestions - 1) {
                                Button(
                                    onClick = { currentQuestionIndex++ },
                                    enabled = userAnswers.containsKey(currentQuestionIndex),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MishkatEmeraldPrimary),
                                    modifier = Modifier.testTag("quiz_next_button")
                                ) {
                                    Text(MishkatStrings.nextQuestion(isEn), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = if (isEn) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        isSubmitted = true
                                        onQuizCompleted(
                                            questions.indices.count { idx -> userAnswers[idx] == questions[idx].correctOptionIndex },
                                            totalQuestions
                                        )
                                    },
                                    enabled = userAnswers.size == totalQuestions,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MishkatEmeraldPrimary,
                                        disabledContainerColor = MishkatBorder
                                    ),
                                    modifier = Modifier.testTag("submit_quiz_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TaskAlt,
                                        contentDescription = null,
                                        tint = MishkatGoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = MishkatStrings.submitQuizAnswers(isEn),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * شاشة عرض النتيجة الفورية ومراجعة إجابات الطالب والشروح التحريرية
 */
@Composable
private fun QuizResultView(
    chunk: BookKnowledgeChunk,
    score: Int,
    total: Int,
    percentage: Int,
    questions: List<QuizQuestion>,
    userAnswers: Map<Int, Int>,
    isEn: Boolean = false,
    onRetry: () -> Unit,
    onFinish: () -> Unit,
    onAskMishkatForClarification: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val isSuccess = percentage >= 70
    val resultTitle = MishkatStrings.quizResultSummaryTitle(isEn, percentage)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        // بطاقة ملخص النتيجة الفورية
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSuccess) MishkatEmeraldContainer else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
            border = BorderStroke(
                1.dp,
                if (isSuccess) MishkatEmeraldPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth().testTag("quiz_result_summary_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null,
                    tint = if (isSuccess) MishkatEmeraldPrimary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(42.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = resultTitle,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSuccess) MishkatEmeraldDark else MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = MishkatStrings.finalScoreText(isEn, score, total, percentage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MishkatTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = MishkatStrings.quizFeedbackText(isEn, isSuccess),
                    fontSize = 11.5.sp,
                    color = MishkatTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // زر مشاركة نتيجة الاختبار عبر تطبيقات التواصل
                Button(
                    onClick = {
                        val shareText = if (isEn) {
                            """
                                🏆 Redaction Achievement in «Mishkat» app - Asim's Tahrirat from Tayyibat An-Nashr:
                                📖 Lesson: ${chunk.lessonTitle}
                                📚 Chapter: ${chunk.unitTitle}
                                ✨ Score: $score of $total ($percentage%)
                                💡 $resultTitle
                                
                                According to «Ar-Rawdat An-Nadir» supervised by Sheikha Samah Al-Bandari.
                                #Mishkat #TayyibatAnNashr #Asim
                            """.trimIndent()
                        } else {
                            """
                                🏆 إنجاز تحريري في تطبيق «مشكاة» - تحريرات الإمام عاصم من طيبة النشر:
                                📖 الدرس: ${chunk.lessonTitle}
                                📚 الباب: ${chunk.unitTitle}
                                ✨ النتيجة: $score من أصل $total ($percentage%)
                                💡 $resultTitle
                                
                                وفق كتاب «الروض الناضر في تحرير أوجه عاصم» بإشراف الشيخة المقرئة سماح البنداري.
                                #مشكاة #طيبة_النشر #قراءات #عاصم
                            """.trimIndent()
                        }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, if (isEn) "Share Quiz Result" else "مشاركة نتيجة الاختبار القرآني")
                        context.startActivity(shareIntent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MishkatGoldAccent),
                    modifier = Modifier.testTag("share_quiz_result_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MishkatEmeraldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = MishkatStrings.shareQuizResultButton(isEn),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MishkatEmeraldDark
                    )
                }

                if (percentage == 100) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MishkatGoldContainer,
                        border = BorderStroke(1.dp, MishkatGoldAccent),
                        modifier = Modifier.fillMaxWidth().testTag("perfect_score_achievement_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MishkatGoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MishkatEmeraldDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = MishkatStrings.perfectScoreBannerTitle(isEn),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatOnGoldContainer
                                )
                                Text(
                                    text = MishkatStrings.perfectScoreBannerDesc(isEn),
                                    fontSize = 10.5.sp,
                                    color = MishkatOnGoldContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = MishkatStrings.quizReviewDetailsHeader(isEn),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MishkatTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // مراجعة كل سؤال وشرحه
        questions.forEachIndexed { index, question ->
            val userAnswerIndex = userAnswers[index]
            val isCorrect = userAnswerIndex == question.correctOptionIndex

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isCorrect) MishkatEmeraldContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                border = BorderStroke(
                    0.5.dp,
                    if (isCorrect) MishkatEmeraldPrimary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isCorrect) MishkatEmeraldPrimary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${index + 1}. ${question.questionText}",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MishkatTextPrimary,
                            lineHeight = 19.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val selectedAnswerText = userAnswerIndex?.let { question.options.getOrNull(it) } ?: MishkatStrings.didNotAnswer(isEn)
                    Text(
                        text = MishkatStrings.yourAnswer(isEn, selectedAnswerText),
                        fontSize = 11.5.sp,
                        color = if (isCorrect) MishkatEmeraldDark else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (!isCorrect) {
                        Text(
                            text = MishkatStrings.correctAnswer(isEn, question.options[question.correctOptionIndex]),
                            fontSize = 11.5.sp,
                            color = MishkatEmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // الشرح التحريري
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.8f),
                        border = BorderStroke(0.5.dp, MishkatBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = MishkatStrings.approvedRedactionHeader(isEn),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = question.explanation,
                                fontSize = 11.5.sp,
                                color = MishkatTextPrimary,
                                lineHeight = 17.sp
                            )
                            question.sourceMatnQuote?.let { quote ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "«$quote»",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MishkatEmeraldDark
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // خيارات الإجراءات بعد النتيجة
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("retry_quiz_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MishkatBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MishkatEmeraldPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(MishkatStrings.retryQuiz(isEn), fontSize = 11.5.sp, color = MishkatTextPrimary)
            }

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("finish_quiz_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MishkatEmeraldPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.TaskAlt,
                    contentDescription = null,
                    tint = MishkatGoldAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(MishkatStrings.understoodDone(isEn), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // زر استفسار فوري لمشكاة حول أسئلة الاختبار
        Surface(
            onClick = {
                val query = if (isEn) {
                    "I want to review the tahrirat for lesson: «${chunk.lessonTitle}» after taking the quiz, and clarify the subtle differences between the transmission paths."
                } else {
                    "أريد مراجعة تحريرات درس: «${chunk.lessonTitle}» بعد خوض الاختبار القصير وتوضيح الفروق الدقيقة بين أوجه الطرق."
                }
                onAskMishkatForClarification(query)
                onFinish()
            },
            shape = RoundedCornerShape(12.dp),
            color = MishkatManuscriptSand,
            border = BorderStroke(0.5.dp, MishkatBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MishkatEmeraldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = MishkatStrings.askMishkatAboutQuiz(isEn),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MishkatEmeraldPrimary
                )
            }
        }
    }
}

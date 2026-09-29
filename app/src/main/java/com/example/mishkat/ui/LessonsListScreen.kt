package com.example.mishkat.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.Language
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mishkat.data.MishkatAchievementsManager
import com.example.mishkat.model.MishkatBadge
import com.example.mishkat.data.notes.LessonNoteEntity
import com.example.mishkat.localization.LocalMishkatLanguage
import com.example.mishkat.localization.MishkatLanguage
import com.example.mishkat.localization.MishkatLanguageManager
import com.example.mishkat.localization.MishkatStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mishkat.rag.BookKnowledgeChunk
import com.example.mishkat.rag.MishkatKnowledgeBase
import com.example.mishkat.ui.theme.MishkatBorder
import com.example.mishkat.ui.theme.MishkatEmeraldContainer
import com.example.mishkat.ui.theme.MishkatEmeraldDark
import com.example.mishkat.ui.theme.MishkatEmeraldLight
import com.example.mishkat.ui.theme.MishkatEmeraldPrimary
import com.example.mishkat.ui.theme.MishkatGoldAccent
import com.example.mishkat.ui.theme.MishkatGoldContainer
import com.example.mishkat.ui.theme.MishkatGoldWarm
import com.example.mishkat.ui.theme.MishkatManuscriptSand
import com.example.mishkat.ui.theme.MishkatOnEmeraldContainer
import com.example.mishkat.ui.theme.MishkatOnGoldContainer
import com.example.mishkat.ui.theme.MishkatTextMuted
import com.example.mishkat.ui.theme.MishkatTextPrimary

/**
 * واجهة تصفح قائمة دروس وفصول تحريرات الإمام عاصم من طيبة النشر
 * وفق كتاب «الروض الناضر» بإشراف الشيخة سماح البنداري
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsListScreen(
    onNavigateBack: (() -> Unit)? = null,
    onSelectLessonForTutor: ((lessonTitle: String, topic: String) -> Unit)? = null,
    isEmbeddedInSplitPane: Boolean = false,
    onCloseSplitPane: (() -> Unit)? = null,
    notesViewModel: LessonNotesViewModel = viewModel(),
    initialUnitFilter: String? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUnitFilter by remember { mutableStateOf<String?>(initialUnitFilter) }
    var selectedLessonForDetail by remember { mutableStateOf<BookKnowledgeChunk?>(null) }
    var selectedLessonForQuiz by remember { mutableStateOf<BookKnowledgeChunk?>(null) }

    val context = LocalContext.current

    var completedLessonIds by remember {
        mutableStateOf(MishkatAchievementsManager.getCompletedLessonIds(context))
    }
    var quizScoresByLessonId by remember {
        mutableStateOf(MishkatAchievementsManager.getQuizScores(context))
    }
    var showBadgesDialog by remember { mutableStateOf(false) }
    var newlyUnlockedBadges by remember { mutableStateOf<List<MishkatBadge>>(emptyList()) }

    val allNotes by notesViewModel.allNotes.collectAsStateWithLifecycle()
    val notesCountByLessonId = remember(allNotes) {
        allNotes.groupBy { it.lessonId }.mapValues { it.value.size }
    }

    val allLessons = remember { MishkatKnowledgeBase.chunks }
    val totalLessonsCount = allLessons.size
    val completedCount = completedLessonIds.size
    val completionRatio = if (totalLessonsCount > 0) completedCount.toFloat() / totalLessonsCount else 0f
    val completionPercentage = (completionRatio * 100).toInt()

    val studentBadges = remember(completedLessonIds, quizScoresByLessonId, allNotes.size, totalLessonsCount) {
        MishkatAchievementsManager.getAllBadges(
            context = context,
            notesCount = allNotes.size,
            totalLessonsInCurriculum = totalLessonsCount
        )
    }
    val unlockedBadgesCount = remember(studentBadges) {
        studentBadges.count { it.isUnlocked }
    }

    val currentLanguage = LocalMishkatLanguage.current
    val isEn = currentLanguage == MishkatLanguage.ENGLISH
    val layoutDirection = if (isEn) LayoutDirection.Ltr else LayoutDirection.Rtl

    val (studentRankTitle, _) = remember(unlockedBadgesCount, isEn) {
        MishkatAchievementsManager.getStudentRank(unlockedBadgesCount, isEn)
    }

    val filterAllLabel = MishkatStrings.filterAll(isEn)
    val notesFilterPrefix = if (isEn) "📝 My Notes" else "📝 ملاحظاتي الخاصة"
    val unitsList = remember(allNotes, isEn) {
        val base = listOf(filterAllLabel)
        val withNotes = if (allNotes.isNotEmpty()) listOf("$notesFilterPrefix (${allNotes.size})") else emptyList()
        base + withNotes + allLessons.map { it.unitTitle }.distinct()
    }

    val filteredLessons by remember(searchQuery, selectedUnitFilter, allNotes, filterAllLabel, notesFilterPrefix) {
        derivedStateOf {
            allLessons.filter { lesson ->
                val matchesUnit = when {
                    selectedUnitFilter == null || selectedUnitFilter == filterAllLabel -> true
                    selectedUnitFilter!!.startsWith(notesFilterPrefix) -> notesCountByLessonId.containsKey(lesson.id)
                    else -> lesson.unitTitle == selectedUnitFilter
                }
                val q = searchQuery.trim().lowercase()
                val matchesSearch = q.isEmpty() ||
                        lesson.lessonTitle.lowercase().contains(q) ||
                        lesson.chapterTitle.lowercase().contains(q) ||
                        lesson.topic.lowercase().contains(q) ||
                        lesson.keywords.any { it.lowercase().contains(q) } ||
                        lesson.content.lowercase().contains(q) ||
                        allNotes.any { it.lessonId == lesson.id && it.noteContent.lowercase().contains(q) }

                matchesUnit && matchesSearch
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = MishkatStrings.lessonsTitle(isEn),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldPrimary
                            )
                            Text(
                                text = MishkatStrings.lessonsSubtitle(isEn),
                                fontSize = 11.sp,
                                color = MishkatTextMuted
                            )
                        }
                    },
                    navigationIcon = {
                        if (isEmbeddedInSplitPane && onCloseSplitPane != null) {
                            IconButton(
                                onClick = onCloseSplitPane,
                                modifier = Modifier.testTag("lessons_close_split_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = MishkatStrings.splitViewClose(isEn),
                                    tint = MishkatEmeraldPrimary
                                )
                            }
                        } else if (onNavigateBack != null) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.testTag("lessons_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = MishkatStrings.navBack(isEn),
                                    tint = MishkatEmeraldPrimary
                                )
                            }
                        }
                    },
                    actions = {
                        // زر التبديل بين العربية والإنجليزية
                        Surface(
                            onClick = {
                                MishkatLanguageManager.toggleLanguage(context)
                            },
                            color = MishkatEmeraldContainer,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(0.5.dp, MishkatEmeraldPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .testTag("lessons_language_switch_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = MishkatStrings.topBarSwitchLanguage(isEn),
                                    tint = MishkatEmeraldPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = MishkatStrings.topBarSwitchLanguage(isEn),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatEmeraldDark
                                )
                            }
                        }

                        Surface(
                            onClick = { showBadgesDialog = true },
                            color = MishkatGoldContainer,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(0.5.dp, MishkatGoldAccent),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .testTag("top_badges_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = MishkatStrings.badgesLabel(isEn),
                                    tint = MishkatOnGoldContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${MishkatStrings.badgesLabel(isEn)} ($unlockedBadgesCount/${studentBadges.size})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatOnGoldContainer
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MishkatManuscriptSand
                    )
                )
            },
            containerColor = MishkatManuscriptSand,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 1100.dp)
                ) {
                // شريط إحصائيات ونسبة إنجاز الدروس بهوية مشكاة
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("lessons_progress_card"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MishkatBorder),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MishkatEmeraldContainer,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MishkatEmeraldPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = MishkatStrings.lessonsProgressTitle(isEn),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MishkatEmeraldDark
                                    )
                                    Text(
                                        text = MishkatStrings.lessonsProgressSubtitle(isEn, completedCount, totalLessonsCount),
                                        fontSize = 11.sp,
                                        color = MishkatTextMuted,
                                        modifier = Modifier.testTag("lessons_progress_stats_text")
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // زر مشاركة الإنجاز الدراسي
                                Surface(
                                    onClick = {
                                        val shareText = if (isEn) {
                                            """
                                                🏆 My Achievement in Asim's Tahrirat from Tayyibat An-Nashr via «Mishkat» app:
                                                ✨ Completed $completedCount of $totalLessonsCount prescribed lessons ($completionPercentage%)
                                                📖 In accordance with «Ar-Rawdat An-Nadir» supervised by Sheikha Samah Al-Bandari.
                                                
                                                Join me in studying the Holy Quran with scholarly precision!
                                                #Mishkat #TayyibatAnNashr #QuranReadings #Asim
                                            """.trimIndent()
                                        } else {
                                            """
                                                🏆 إنجازي في مسار تحريرات الإمام عاصم من طيبة النشر عبر تطبيق «مشكاة»:
                                                ✨ أنجزت $completedCount من أصل $totalLessonsCount درسًا مقررًا ($completionPercentage%)
                                                📖 وفق ضوابط كتاب «الروض الناضر» وإشراف الشيخة المقرئة سماح البنداري.
                                                
                                                شاركوني التدارس القرآني والتنافس في مدارج الإتقان!
                                                #مشكاة #طيبة_النشر #قراءات_القرآن #عاصم
                                            """.trimIndent()
                                        }
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, if (isEn) "Share Progress" else "مشاركة التقدم القرآني")
                                        context.startActivity(shareIntent)
                                    },
                                    color = MishkatGoldContainer,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(0.5.dp, MishkatGoldAccent),
                                    modifier = Modifier.testTag("share_course_progress_button")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = MishkatStrings.share(isEn),
                                            tint = MishkatOnGoldContainer,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = MishkatStrings.share(isEn),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MishkatOnGoldContainer
                                        )
                                    }
                                }

                                // شارة النسبة المئوية
                                Surface(
                                    color = if (completionPercentage == 100) MishkatEmeraldPrimary else MishkatGoldContainer,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(0.5.dp, if (completionPercentage == 100) MishkatEmeraldPrimary else MishkatGoldAccent.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "$completionPercentage%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (completionPercentage == 100) Color.White else MishkatOnGoldContainer,
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                            .testTag("lessons_progress_percentage_text")
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // شريط التقدم الفعلي المنسق
                        LinearProgressIndicator(
                            progress = { completionRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .testTag("lessons_progress_bar"),
                            color = MishkatEmeraldPrimary,
                            trackColor = MishkatBorder
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MishkatBorder.copy(alpha = 0.6f), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // شريط الرتبة والأوسمة المحققة
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
                                    color = MishkatGoldContainer,
                                    border = BorderStroke(0.5.dp, MishkatGoldAccent),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = MishkatOnGoldContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${MishkatStrings.studentRankPrefix(isEn)}$studentRankTitle",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MishkatEmeraldPrimary
                                    )
                                    Text(
                                        text = MishkatStrings.badgesUnlockedCount(isEn, unlockedBadgesCount, studentBadges.size),
                                        fontSize = 10.5.sp,
                                        color = MishkatTextMuted
                                    )
                                }
                            }

                            Surface(
                                onClick = { showBadgesDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                color = MishkatGoldAccent,
                                modifier = Modifier.testTag("open_badges_from_progress_card")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = MishkatEmeraldDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = MishkatStrings.badgesAndAwards(isEn),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MishkatEmeraldDark
                                    )
                                }
                            }
                        }
                    }
                }

                // شريط البحث المنسق بهوية مشكاة
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MishkatManuscriptSand)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = MishkatStrings.searchLessonsDetailPlaceholder(isEn),
                                fontSize = 12.sp,
                                color = MishkatTextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = if (isEn) "Search" else "بحث",
                                tint = MishkatEmeraldPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = MishkatStrings.searchClear(isEn),
                                        tint = MishkatTextMuted
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lessons_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MishkatEmeraldPrimary,
                            unfocusedBorderColor = MishkatBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                // شريط تصفية الوحدات الأكاديمية
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    unitsList.forEach { unit ->
                        val isSelected = (selectedUnitFilter == null && unit == filterAllLabel) || selectedUnitFilter == unit
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedUnitFilter = if (unit == filterAllLabel) null else unit
                            },
                            label = {
                                Text(
                                    text = unit,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MishkatEmeraldPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = MishkatEmeraldPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) MishkatEmeraldPrimary else MishkatBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // إحصائية النتائج
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = MishkatStrings.availableLessonsHeader(isEn, filteredLessons.size),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MishkatEmeraldDark
                    )
                    Text(
                        text = MishkatStrings.underSupervision(isEn),
                        fontSize = 11.sp,
                        color = MishkatGoldAccent,
                        fontWeight = FontWeight.Medium
                    )
                }

                // قائمة الدروس التحريرية
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("lessons_lazy_column"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredLessons, key = { it.id }) { lesson ->
                        val isLessonCompleted = completedLessonIds.contains(lesson.id)
                        LessonItemCard(
                            chunk = lesson,
                            isCompleted = isLessonCompleted,
                            quizScore = quizScoresByLessonId[lesson.id],
                            notesCount = notesCountByLessonId[lesson.id] ?: 0,
                            isEn = isEn,
                            onToggleCompleted = {
                                val (newCompleted, newBadges) = MishkatAchievementsManager.toggleLessonCompleted(context, lesson.id)
                                completedLessonIds = newCompleted
                                if (newBadges.isNotEmpty()) {
                                    newlyUnlockedBadges = newBadges
                                }
                            },
                            onCardClick = {
                                selectedLessonForDetail = lesson
                            },
                            onStartQuiz = {
                                selectedLessonForQuiz = lesson
                            },
                            onAskTutorClick = {
                                onSelectLessonForTutor?.invoke(lesson.lessonTitle, lesson.topic)
                            }
                        )
                    }

                    if (filteredLessons.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, MishkatBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MishkatGoldAccent,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = MishkatStrings.noLessonsFound(isEn),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MishkatEmeraldPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = MishkatStrings.noLessonsFoundHint(isEn),
                                        fontSize = 12.sp,
                                        color = MishkatTextMuted,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        }

        // نافذة عرض تفاصيل ومحتوى الدرس والتحريرات الكاملة
        selectedLessonForDetail?.let { chunk ->
            val isLessonCompleted = completedLessonIds.contains(chunk.id)
            val lessonNotes = remember(allNotes, chunk.id) {
                allNotes.filter { it.lessonId == chunk.id }
            }
            LessonDetailDialog(
                chunk = chunk,
                isCompleted = isLessonCompleted,
                quizScore = quizScoresByLessonId[chunk.id],
                notes = lessonNotes,
                isEn = isEn,
                onAddNote = { noteText ->
                    notesViewModel.addNote(chunk.id, chunk.lessonTitle, noteText)
                },
                onUpdateNote = { note, newContent ->
                    notesViewModel.updateNote(note, newContent)
                },
                onDeleteNote = { note ->
                    notesViewModel.deleteNote(note)
                },
                onToggleCompleted = {
                    val (newCompleted, newBadges) = MishkatAchievementsManager.toggleLessonCompleted(context, chunk.id)
                    completedLessonIds = newCompleted
                    if (newBadges.isNotEmpty()) {
                        newlyUnlockedBadges = newBadges
                    }
                },
                onStartQuiz = {
                    selectedLessonForDetail = null
                    selectedLessonForQuiz = chunk
                },
                onDismiss = { selectedLessonForDetail = null },
                onAskMishkat = {
                    val title = chunk.lessonTitle
                    val topic = chunk.topic
                    selectedLessonForDetail = null
                    onSelectLessonForTutor?.invoke(title, topic)
                }
            )
        }

        // نافذة الاختبار التفاعلي للدرس
        selectedLessonForQuiz?.let { chunk ->
            LessonQuizDialog(
                chunk = chunk,
                isEn = isEn,
                onDismiss = { selectedLessonForQuiz = null },
                onQuizCompleted = { score, total ->
                    val newBadges = MishkatAchievementsManager.recordQuizScore(context, chunk.id, score, total)
                    quizScoresByLessonId = MishkatAchievementsManager.getQuizScores(context)
                    completedLessonIds = MishkatAchievementsManager.getCompletedLessonIds(context)
                    if (newBadges.isNotEmpty()) {
                        newlyUnlockedBadges = newBadges
                    }
                }
            )
        }

        // نافذة عرض أوسمة وإنجازات الطالب
        if (showBadgesDialog) {
            StudentBadgesDialog(
                badges = studentBadges,
                isEn = isEn,
                onDismiss = { showBadgesDialog = false }
            )
        }

        // تنبيه واحتفاء بالحصول على وسام جديد
        if (newlyUnlockedBadges.isNotEmpty()) {
            NewBadgeUnlockedDialog(
                badges = newlyUnlockedBadges,
                isEn = isEn,
                onDismiss = { newlyUnlockedBadges = emptyList() },
                onViewAllBadges = {
                    newlyUnlockedBadges = emptyList()
                    showBadgesDialog = true
                }
            )
        }
    }
}

/**
 * بطاقة عرض مختصرة لكل درس في قائمة الدروس
 */
@Composable
fun LessonItemCard(
    chunk: BookKnowledgeChunk,
    isCompleted: Boolean = false,
    quizScore: Pair<Int, Int>? = null,
    notesCount: Int = 0,
    isEn: Boolean = false,
    onToggleCompleted: () -> Unit = {},
    onCardClick: () -> Unit,
    onStartQuiz: () -> Unit = {},
    onAskTutorClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("lesson_card_${chunk.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, if (isCompleted) MishkatEmeraldPrimary.copy(alpha = 0.6f) else MishkatBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // شارة الوحدة ورقم الصفحات وزر اكتمال الدرس
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = MishkatEmeraldContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = chunk.chapterTitle,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MishkatOnEmeraldContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = MishkatGoldContainer,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.5.dp, MishkatGoldAccent.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = MishkatOnGoldContainer,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = MishkatStrings.pageRangePrefix(isEn, chunk.pageStart, chunk.pageEnd),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatOnGoldContainer
                            )
                        }
                    }

                    if (quizScore != null) {
                        Surface(
                            color = MishkatGoldContainer,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, MishkatGoldAccent.copy(alpha = 0.6f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = null,
                                    tint = MishkatOnGoldContainer,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${quizScore.first}/${quizScore.second}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatOnGoldContainer
                                )
                            }
                        }
                    }

                    if (notesCount > 0) {
                        Surface(
                            color = MishkatEmeraldContainer,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, MishkatEmeraldPrimary.copy(alpha = 0.4f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = MishkatEmeraldPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = MishkatStrings.notesCountLabel(isEn, notesCount),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatEmeraldDark
                                )
                            }
                        }
                    }
                }

                // زر تحديد الدرس كمكتمل / غير مكتمل
                Surface(
                    onClick = onToggleCompleted,
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCompleted) MishkatEmeraldContainer else MishkatManuscriptSand,
                    border = BorderStroke(
                        0.5.dp,
                        if (isCompleted) MishkatEmeraldPrimary else MishkatBorder
                    ),
                    modifier = Modifier.testTag("toggle_complete_lesson_${chunk.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (isCompleted) MishkatStrings.completed(isEn) else MishkatStrings.markAsCompleted(isEn),
                            tint = if (isCompleted) MishkatEmeraldPrimary else MishkatTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCompleted) MishkatStrings.completed(isEn) else MishkatStrings.doneQuestion(isEn),
                            fontSize = 10.sp,
                            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCompleted) MishkatEmeraldDark else MishkatTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // عنوان الدرس
            Text(
                text = chunk.lessonTitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MishkatEmeraldPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // موضوع التحرير ومقتطف
            Text(
                text = chunk.content.lineSequence().firstOrNull() ?: chunk.topic,
                fontSize = 12.sp,
                color = MishkatTextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // الكلمات المفتاحية والأزرار
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // وسم الكلمات المفتاحية
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    chunk.keywords.take(2).forEach { tag ->
                        Surface(
                            color = MishkatManuscriptSand,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                fontSize = 10.sp,
                                color = MishkatEmeraldDark,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // زر الاختبار القصير السريع للدرس
                    Surface(
                        onClick = onStartQuiz,
                        color = if (quizScore != null) MishkatGoldContainer else MishkatEmeraldContainer,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(0.5.dp, if (quizScore != null) MishkatGoldAccent else MishkatEmeraldPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("quiz_lesson_${chunk.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = if (quizScore != null) MishkatOnGoldContainer else MishkatEmeraldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = MishkatStrings.quizButtonText(isEn, quizScore != null),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (quizScore != null) MishkatOnGoldContainer else MishkatEmeraldPrimary
                            )
                        }
                    }

                    // زر استشارة المعلم الذكي مشكاة حول الدرس
                    Surface(
                        onClick = onAskTutorClick,
                        color = MishkatEmeraldPrimary,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("ask_tutor_lesson_${chunk.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MishkatGoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = MishkatStrings.discussInMishkat(isEn),
                                fontSize = 11.sp,
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

/**
 * نافذة عرض تفاصيل ومحتوى الدرس والأوجه الجائزة والممتنعة
 */
@Composable
fun LessonDetailDialog(
    chunk: BookKnowledgeChunk,
    isCompleted: Boolean = false,
    quizScore: Pair<Int, Int>? = null,
    notes: List<LessonNoteEntity> = emptyList(),
    isEn: Boolean = false,
    onAddNote: (String) -> Unit = {},
    onUpdateNote: (LessonNoteEntity, String) -> Unit = { _, _ -> },
    onDeleteNote: (LessonNoteEntity) -> Unit = {},
    onToggleCompleted: () -> Unit = {},
    onStartQuiz: () -> Unit = {},
    onDismiss: () -> Unit,
    onAskMishkat: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val layoutDirection = if (isEn) LayoutDirection.Ltr else LayoutDirection.Rtl
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                shape = RoundedCornerShape(24.dp),
                color = MishkatManuscriptSand,
                border = BorderStroke(1.5.dp, MishkatBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // رأس النافذة مع زر الإغلاق
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chunk.unitTitle,
                                fontSize = 11.sp,
                                color = MishkatGoldWarm,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = chunk.lessonTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val shareText = if (isEn) {
                                        """
                                            📖 Studying lesson from «Ar-Rawdat An-Nadir in Asim's Redactions»:
                                            ✨ Chapter: ${chunk.unitTitle}
                                            📌 Lesson: ${chunk.lessonTitle}
                                            📚 Reference: Pages ${chunk.pageStart} to ${chunk.pageEnd}
                                            
                                            💡 Lesson Summary:
                                            ${chunk.topic}
                                            
                                            ${if (quizScore != null) "🏆 My Quiz Score: ${quizScore.first} of ${quizScore.second}" else ""}
                                            
                                            «Mishkat» app for Asim's Tahrirat from Tayyibat An-Nashr - Supervised by Sheikha Samah Al-Bandari.
                                            #Mishkat #TayyibatAnNashr #Quran #Asim
                                        """.trimIndent()
                                    } else {
                                        """
                                            📖 تدارس درس من كتاب «الروض الناضر في تحرير أوجه عاصم»:
                                            ✨ الباب: ${chunk.unitTitle}
                                            📌 الدرس: ${chunk.lessonTitle}
                                            📚 المرجع: من صفحة ${chunk.pageStart} إلى ${chunk.pageEnd}
                                            
                                            💡 خلاصة الدرس:
                                            ${chunk.topic}
                                            
                                            ${if (quizScore != null) "🏆 نتيجتي في اختبار الدرس: ${quizScore.first} من ${quizScore.second}" else ""}
                                            
                                            تطبيق «مشكاة» لتحريرات عاصم من طيبة النشر - إشراف الشيخة المقرئة سماح البنداري.
                                            #مشكاة #طيبة_النشر #قراءات #عاصم
                                        """.trimIndent()
                                    }
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, if (isEn) "Share Lesson Insights" else "مشاركة فوائد الدرس القرآني")
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier.testTag("dialog_share_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = MishkatStrings.share(isEn),
                                    tint = MishkatEmeraldPrimary
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("dialog_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = MishkatStrings.close(isEn),
                                    tint = MishkatEmeraldPrimary
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = MishkatBorder,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // المحتوى القابل للتمرير
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // بطاقة التوثيق المكتبي
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MishkatBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = MishkatEmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = MishkatStrings.sourceBookLabel(isEn),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MishkatEmeraldPrimary
                                    )
                                    Text(
                                        text = MishkatStrings.bookLocationLabel(isEn, chunk.pageStart, chunk.pageEnd, chunk.chapterTitle),
                                        fontSize = 11.sp,
                                        color = MishkatTextMuted
                                    )
                                }
                            }
                        }

                        // نص الشرح والتحرير الأكاديمي
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MishkatBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = MishkatStrings.explanationHeader(isEn),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatEmeraldPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = chunk.content,
                                    fontSize = 13.sp,
                                    lineHeight = 22.sp,
                                    color = MishkatTextPrimary
                                )
                            }
                        }

                        // شواهد المتون والنظم النشرية (إن وجدت)
                        if (chunk.matnQuotes.isNotEmpty()) {
                            Surface(
                                color = MishkatGoldContainer,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MishkatGoldAccent.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = MishkatStrings.matnEvidenceHeader(isEn),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MishkatOnGoldContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    chunk.matnQuotes.forEach { matn ->
                                        Text(
                                            text = "«$matn»",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MishkatEmeraldDark,
                                            lineHeight = 19.sp,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // الأوجه الجائزة والممتنعة
                        if (chunk.allowedRules.isNotEmpty() || chunk.forbiddenRules.isNotEmpty()) {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MishkatBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    if (chunk.allowedRules.isNotEmpty()) {
                                        Text(
                                            text = MishkatStrings.allowedRulesHeader(isEn),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MishkatEmeraldPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        chunk.allowedRules.forEach { rule ->
                                            Text(
                                                text = "✓ $rule",
                                                fontSize = 12.sp,
                                                color = MishkatEmeraldLight,
                                                lineHeight = 18.sp,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            )
                                        }
                                    }

                                    if (chunk.forbiddenRules.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = MishkatStrings.forbiddenRulesHeader(isEn),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        chunk.forbiddenRules.forEach { forbidden ->
                                            Text(
                                                text = "✗ $forbidden",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.error,
                                                lineHeight = 18.sp,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // بطاقة كراسة ملاحظاتي التحريرية الخاصة بهذا الدرس (Room Database)
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MishkatBorder),
                            modifier = Modifier.fillMaxWidth().testTag("lesson_notes_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = MishkatEmeraldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = MishkatStrings.lessonNotesNotebookHeader(isEn),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MishkatEmeraldPrimary
                                        )
                                        Text(
                                            text = MishkatStrings.lessonNotesNotebookSubtitle(isEn),
                                            fontSize = 11.sp,
                                            color = MishkatTextMuted
                                        )
                                    }
                                    if (notes.isNotEmpty()) {
                                        Surface(
                                            color = MishkatEmeraldContainer,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = MishkatStrings.notesCountBadge(isEn, notes.size),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MishkatEmeraldDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                var noteInputText by remember(chunk.id) { mutableStateOf("") }
                                var editingNote by remember(chunk.id) { mutableStateOf<LessonNoteEntity?>(null) }

                                OutlinedTextField(
                                    value = noteInputText,
                                    onValueChange = { noteInputText = it },
                                    placeholder = {
                                        Text(
                                            text = MishkatStrings.noteInputPlaceholder(isEn, editingNote != null),
                                            fontSize = 12.sp,
                                            color = MishkatTextMuted
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("lesson_note_input_field"),
                                    shape = RoundedCornerShape(12.dp),
                                    minLines = 2,
                                    maxLines = 4,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MishkatEmeraldPrimary,
                                        unfocusedBorderColor = MishkatBorder,
                                        focusedContainerColor = MishkatManuscriptSand.copy(alpha = 0.4f),
                                        unfocusedContainerColor = MishkatManuscriptSand.copy(alpha = 0.2f)
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (editingNote != null) {
                                        Surface(
                                            onClick = {
                                                editingNote = null
                                                noteInputText = ""
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            color = MishkatManuscriptSand,
                                            border = BorderStroke(0.5.dp, MishkatBorder),
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text(
                                                text = MishkatStrings.cancelEdit(isEn),
                                                fontSize = 11.sp,
                                                color = MishkatTextMuted,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = {
                                            if (noteInputText.isNotBlank()) {
                                                val currentEditing = editingNote
                                                if (currentEditing != null) {
                                                    onUpdateNote(currentEditing, noteInputText)
                                                    editingNote = null
                                                } else {
                                                    onAddNote(noteInputText)
                                                }
                                                noteInputText = ""
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (noteInputText.isNotBlank()) MishkatEmeraldPrimary else MishkatBorder,
                                        modifier = Modifier.testTag("save_lesson_note_button")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (editingNote != null) Icons.Default.Check else Icons.Default.Add,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = MishkatStrings.saveNoteButton(isEn, editingNote != null),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                if (notes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = MishkatBorder, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        notes.forEach { note ->
                                            Surface(
                                                color = MishkatManuscriptSand.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(0.5.dp, MishkatBorder),
                                                modifier = Modifier.fillMaxWidth().testTag("saved_note_item_${note.id}")
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()).format(Date(note.updatedAt)),
                                                            fontSize = 10.sp,
                                                            color = MishkatTextMuted
                                                        )

                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            IconButton(
                                                                onClick = {
                                                                    editingNote = note
                                                                    noteInputText = note.noteContent
                                                                },
                                                                modifier = Modifier.size(28.dp).testTag("edit_note_${note.id}")
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Edit,
                                                                    contentDescription = if (isEn) "Edit note" else "تعديل الملاحظة",
                                                                    tint = MishkatEmeraldPrimary,
                                                                    modifier = Modifier.size(15.dp)
                                                                )
                                                            }

                                                            IconButton(
                                                                onClick = { onDeleteNote(note) },
                                                                modifier = Modifier.size(28.dp).testTag("delete_note_${note.id}")
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.DeleteOutline,
                                                                    contentDescription = if (isEn) "Delete note" else "حذف الملاحظة",
                                                                    tint = MaterialTheme.colorScheme.error,
                                                                    modifier = Modifier.size(15.dp)
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = note.noteContent,
                                                        fontSize = 12.5.sp,
                                                        color = MishkatTextPrimary,
                                                        lineHeight = 19.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        color = MishkatManuscriptSand.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = MishkatStrings.noNotesEmpty(isEn),
                                            fontSize = 11.sp,
                                            color = MishkatTextMuted,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // بطاقة الاختبار القصير التفاعلي (Quiz) لتقييم الاستيعاب
                        Surface(
                            color = MishkatEmeraldContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MishkatEmeraldPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("lesson_quiz_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MishkatEmeraldPrimary,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Quiz,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = MishkatStrings.lessonQuizCardTitle(isEn),
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MishkatEmeraldDark
                                        )
                                        val quizScoreText = if (quizScore != null) {
                                            val pct = ((quizScore.first.toFloat() / quizScore.second) * 100).toInt()
                                            MishkatStrings.lessonQuizScoreText(isEn, quizScore.first, quizScore.second, pct)
                                        } else {
                                            MishkatStrings.lessonQuizSubtitleDefault(isEn)
                                        }
                                        Text(
                                            text = quizScoreText,
                                            fontSize = 11.sp,
                                            color = if (quizScore != null) MishkatEmeraldPrimary else MishkatTextMuted,
                                            fontWeight = if (quizScore != null) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (quizScore != null) {
                                        Surface(
                                            onClick = {
                                                val pct = ((quizScore.first.toFloat() / quizScore.second) * 100).toInt()
                                                val shareText = if (isEn) {
                                                    """
                                                        🏆 My Quiz Score on «${chunk.lessonTitle}» in Mishkat app:
                                                        ✨ Score: ${quizScore.first} of ${quizScore.second} ($pct%)
                                                        📚 Chapter: ${chunk.unitTitle}
                                                        
                                                        Asim's Tahrirat from Tayyibat An-Nashr - «Ar-Rawdat An-Nadir» supervised by Sheikha Samah Al-Bandari.
                                                        #Mishkat #TayyibatAnNashr #Asim
                                                    """.trimIndent()
                                                } else {
                                                    """
                                                        🏆 نتيجتي في اختبار درس «${chunk.lessonTitle}» في تطبيق مشكاة:
                                                        ✨ الدرجة: ${quizScore.first} من أصل ${quizScore.second} ($pct%)
                                                        📚 الباب: ${chunk.unitTitle}
                                                        
                                                        تحريرات الإمام عاصم من طيبة النشر - كتاب «الروض الناضر» بإشراف الشيخة سماح البنداري.
                                                        #مشكاة #طيبة_النشر #عاصم
                                                    """.trimIndent()
                                                }
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                    type = "text/plain"
                                                }
                                                val shareIntent = Intent.createChooser(sendIntent, if (isEn) "Share Quiz Result" else "مشاركة نتيجة الاختبار")
                                                context.startActivity(shareIntent)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            color = MishkatGoldContainer,
                                            border = BorderStroke(0.5.dp, MishkatGoldAccent),
                                            modifier = Modifier.testTag("share_recorded_quiz_score_button")
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = MishkatStrings.share(isEn),
                                                    tint = MishkatOnGoldContainer,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        onClick = onStartQuiz,
                                        shape = RoundedCornerShape(10.dp),
                                        color = MishkatEmeraldPrimary,
                                        modifier = Modifier.testTag("start_quiz_from_dialog_button")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Quiz,
                                                contentDescription = null,
                                                tint = MishkatGoldAccent,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (quizScore != null) MishkatStrings.retakeQuiz(isEn) else MishkatStrings.startQuiz(isEn),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // أزرار الإجراءات السفلية
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // زر التبديل لحالة إكمال الدرس داخل التفاصيل
                        Surface(
                            onClick = onToggleCompleted,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("dialog_toggle_complete_button"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCompleted) MishkatEmeraldContainer else Color.White,
                            border = BorderStroke(1.dp, if (isCompleted) MishkatEmeraldPrimary else MishkatBorder)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = if (isCompleted) MishkatStrings.completed(isEn) else MishkatStrings.markAsCompleted(isEn),
                                    tint = if (isCompleted) MishkatEmeraldPrimary else MishkatTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isCompleted) MishkatStrings.completed(isEn) else MishkatStrings.markLessonDone(isEn),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isCompleted) MishkatEmeraldDark else MishkatTextMuted
                                )
                            }
                        }

                        Button(
                            onClick = onAskMishkat,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("ask_mishkat_dialog_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MishkatEmeraldPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MishkatGoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = MishkatStrings.askQuestionsToMishkat(isEn),
                                fontSize = 13.sp,
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

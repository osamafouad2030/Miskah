package com.example.mishkat.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ViewSidebar
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.ui.platform.LocalContext
import com.example.mishkat.data.MishkatAchievementsManager
import com.example.mishkat.localization.LocalMishkatLanguage
import com.example.mishkat.localization.MishkatLanguage
import com.example.mishkat.localization.MishkatLanguageManager
import com.example.mishkat.localization.MishkatStrings
import com.example.mishkat.model.DeviceLayoutType
import com.example.mishkat.model.DisplayModeSetting
import com.example.mishkat.ui.theme.MishkatEmeraldDark
import com.example.mishkat.ui.theme.MishkatEmeraldPrimary
import com.example.mishkat.ui.theme.MishkatGoldAccent
import com.example.mishkat.ui.theme.MishkatGoldContainer
import com.example.mishkat.ui.theme.MishkatOnGoldContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mishkat.model.MessageSender
import com.example.mishkat.model.MishkatMessage
import com.example.mishkat.model.TutorExplanationMode
import com.example.mishkat.model.TutorLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MishkatTutorScreen(
    viewModel: MishkatViewModel = viewModel(),
    onOpenAuth: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    // إعدادات وضع العرض والشاشات (تلقائي، هاتف، لوحي، حاسوب)
    var displayModeSetting by remember { mutableStateOf(DisplayModeSetting.AUTO) }
    var showDisplayModeDialog by remember { mutableStateOf(false) }

    // التحكم في العرض المزدوج (Split Pane للدروس + المعلم الذكي)
    var showLessonsSplitPane by remember { mutableStateOf(false) }

    var showAuthDialog by remember { mutableStateOf(false) }
    var showLessonsDialog by remember { mutableStateOf(false) }
    var showVoiceRecitationDialog by remember { mutableStateOf(false) }
    var showDailyReminderDialog by remember { mutableStateOf(false) }
    var showBadgesDialog by remember { mutableStateOf(false) }
    var currentStudentName by remember { mutableStateOf<String?>(null) }

    // التمرير التلقائي لآخر رسالة عند وصول رد جديد
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    val currentLang = LocalMishkatLanguage.current
    val isEn = currentLang == MishkatLanguage.ENGLISH
    val layoutDirection = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidthDp = maxWidth.value.toInt()

            // كشف فئة الجهاز استناداً لمعايير Material 3 Window Size Classes
            val detectedDeviceClass = when {
                maxWidth < 600.dp -> DeviceLayoutType.MOBILE
                maxWidth < 840.dp -> DeviceLayoutType.TABLET
                else -> DeviceLayoutType.DESKTOP
            }

            // فئة التخطيط الفعالة (إما تلقائية أو مثبتة يدوياً بواسطة المستخدم)
            val effectiveLayoutType = when (displayModeSetting) {
                DisplayModeSetting.AUTO -> detectedDeviceClass
                DisplayModeSetting.MOBILE -> DeviceLayoutType.MOBILE
                DisplayModeSetting.TABLET -> DeviceLayoutType.TABLET
                DisplayModeSetting.DESKTOP -> DeviceLayoutType.DESKTOP
            }

            when (effectiveLayoutType) {
                DeviceLayoutType.DESKTOP -> {
                    // ==========================================
                    // 1. نظام عرض الحاسوب المكتبي (Desktop Workspace)
                    // ==========================================
                    DesktopLayoutContent(
                        state = state,
                        viewModel = viewModel,
                        listState = listState,
                        inputText = inputText,
                        onInputTextChange = { inputText = it },
                        showLessonsSplitPane = showLessonsSplitPane,
                        onToggleSplitPane = { showLessonsSplitPane = !showLessonsSplitPane },
                        displayModeSetting = displayModeSetting,
                        effectiveLayoutType = effectiveLayoutType,
                        onOpenDisplayModeDialog = { showDisplayModeDialog = true },
                        currentStudentName = currentStudentName,
                        onOpenAuth = {
                            if (onOpenAuth != null) onOpenAuth() else showAuthDialog = true
                        },
                        onOpenLessons = {
                            // على الحاسوب، النقر على الدروس يفتح أو يغلق العرض المزدوج بسلاسة
                            showLessonsSplitPane = !showLessonsSplitPane
                        },
                        onOpenVoiceRecitation = { showVoiceRecitationDialog = true },
                        onOpenDailyReminder = { showDailyReminderDialog = true },
                        onOpenBadges = { showBadgesDialog = true },
                        isEn = isEn,
                        onSelectLessonForTutor = { lessonTitle, topic ->
                            val prompt = if (isEn) {
                                "Please provide a comprehensive academic explanation of the lesson: «$lessonTitle» on the topic «$topic», citing canonical sources from Ar-Rawdat An-Nadir and highlighting permissible vs. prohibited facets."
                            } else {
                                "أريد شرحاً تحريرياً وتطبيقياً مفصلاً لدرس: «$lessonTitle» في موضوع: «$topic» مع عزو الأوجه للكتب المعتمدة وبيان الممتنع"
                            }
                            viewModel.sendMessage(prompt)
                        }
                    )
                }

                DeviceLayoutType.TABLET -> {
                    // ==========================================
                    // 2. نظام عرض الأجهزة اللوحية (Tablet Layout)
                    // ==========================================
                    TabletLayoutContent(
                        state = state,
                        viewModel = viewModel,
                        listState = listState,
                        inputText = inputText,
                        onInputTextChange = { inputText = it },
                        showLessonsSplitPane = showLessonsSplitPane,
                        onToggleSplitPane = { showLessonsSplitPane = !showLessonsSplitPane },
                        displayModeSetting = displayModeSetting,
                        effectiveLayoutType = effectiveLayoutType,
                        onOpenDisplayModeDialog = { showDisplayModeDialog = true },
                        currentStudentName = currentStudentName,
                        onOpenAuth = {
                            if (onOpenAuth != null) onOpenAuth() else showAuthDialog = true
                        },
                        onOpenLessons = {
                            showLessonsSplitPane = !showLessonsSplitPane
                        },
                        onOpenVoiceRecitation = { showVoiceRecitationDialog = true },
                        onOpenDailyReminder = { showDailyReminderDialog = true },
                        onOpenBadges = { showBadgesDialog = true },
                        isEn = isEn,
                        onSelectLessonForTutor = { lessonTitle, topic ->
                            val prompt = if (isEn) {
                                "Please provide a comprehensive academic explanation of the lesson: «$lessonTitle» on the topic «$topic», citing canonical sources from Ar-Rawdat An-Nadir and highlighting permissible vs. prohibited facets."
                            } else {
                                "أريد شرحاً تحريرياً وتطبيقياً مفصلاً لدرس: «$lessonTitle» في موضوع: «$topic» مع عزو الأوجه للكتب المعتمدة وبيان الممتنع"
                            }
                            viewModel.sendMessage(prompt)
                        }
                    )
                }

                DeviceLayoutType.MOBILE -> {
                    // ==========================================
                    // 3. نظام عرض الهواتف الذكية (Mobile Handheld Layout)
                    // ==========================================
                    MobileLayoutContent(
                        state = state,
                        viewModel = viewModel,
                        listState = listState,
                        inputText = inputText,
                        onInputTextChange = { inputText = it },
                        displayModeSetting = displayModeSetting,
                        effectiveLayoutType = effectiveLayoutType,
                        onOpenDisplayModeDialog = { showDisplayModeDialog = true },
                        currentStudentName = currentStudentName,
                        onOpenAuth = {
                            if (onOpenAuth != null) onOpenAuth() else showAuthDialog = true
                        },
                        onOpenLessons = { showLessonsDialog = true },
                        onOpenVoiceRecitation = { showVoiceRecitationDialog = true },
                        onOpenDailyReminder = { showDailyReminderDialog = true },
                        onOpenBadges = { showBadgesDialog = true },
                        isEn = isEn
                    )
                }
            }

            // نافذة اختيار نمط العرض (Mobile, Tablet, Desktop, Auto)
            if (showDisplayModeDialog) {
                DisplayModeDialog(
                    currentSetting = displayModeSetting,
                    effectiveType = effectiveLayoutType,
                    screenWidthDp = screenWidthDp,
                    isEn = isEn,
                    onSelectSetting = { newSetting ->
                        displayModeSetting = newSetting
                    },
                    onDismiss = { showDisplayModeDialog = false }
                )
            }
        }

        // ==========================================
        // النوافذ المشتركة (Dialogs) المتوافقة مع كافة المقاسات
        // ==========================================

        // نافذة تسجيل الطالب عبر Firebase Auth
        if (showAuthDialog) {
            Dialog(
                onDismissRequest = { showAuthDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .padding(16.dp)
                        .widthIn(max = 520.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.background,
                    shadowElevation = 8.dp
                ) {
                    StudentAuthScreen(
                        onAuthSuccess = { name, _ ->
                            currentStudentName = name
                            showAuthDialog = false
                        },
                        onNavigateBack = { showAuthDialog = false }
                    )
                }
            }
        }

        // نافذة عرض قائمة وفهرس دروس تحريرات عاصم (عند فتحها منفصلة)
        if (showLessonsDialog) {
            Dialog(
                onDismissRequest = { showLessonsDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                LessonsListScreen(
                    onNavigateBack = { showLessonsDialog = false },
                    onSelectLessonForTutor = { lessonTitle, topic ->
                        showLessonsDialog = false
                        val prompt = if (isEn) {
                            "Please provide a comprehensive academic explanation of the lesson: «$lessonTitle» on the topic «$topic», citing canonical sources from Ar-Rawdat An-Nadir and highlighting permissible vs. prohibited facets."
                        } else {
                            "أريد شرحاً تحريرياً وتطبيقياً مفصلاً لدرس: «$lessonTitle» في موضوع: «$topic» مع عزو الأوجه للكتب المعتمدة وبيان الممتنع"
                        }
                        viewModel.sendMessage(prompt)
                    }
                )
            }
        }

        // نافذة التلاوة الصوتية والتصحيح الفوري بواسطة المعلم الذكي Gemini
        if (showVoiceRecitationDialog) {
            VoiceRecitationDialog(
                onDismiss = { showVoiceRecitationDialog = false },
                onSubmitRecitation = { recitationText, targetRuleOrReader ->
                    viewModel.submitVoiceRecitation(recitationText, targetRuleOrReader)
                }
            )
        }

        // نافذة جدولة التنبيهات اليومية لمراجعة الدروس
        if (showDailyReminderDialog) {
            DailyReminderDialog(
                onDismiss = { showDailyReminderDialog = false },
                onReminderSaved = { enabled, hour, minute, topic ->
                    val timeString = String.format("%02d:%02d", hour, minute)
                    val statusText = if (isEn) {
                        if (enabled) "Daily reminder activated at $timeString for revision of: «$topic»."
                        else "Daily revision reminder has been turned off."
                    } else {
                        if (enabled) "تم تفعيل التنبيه اليومي بنجاح عند الساعة $timeString لمراجعة: «$topic»."
                        else "تم إيقاف التنبيه اليومي للمراجعة."
                    }
                    viewModel.sendSystemNotice(statusText)
                }
            )
        }

        // نافذة عرض أوسمة وإنجازات الطالب (Badges)
        if (showBadgesDialog) {
            val studentBadges = remember {
                MishkatAchievementsManager.getAllBadges(context)
            }
            StudentBadgesDialog(
                badges = studentBadges,
                onDismiss = { showBadgesDialog = false },
                isEn = isEn
            )
        }
    }
}

// ==========================================
// مكونات العرض المتخصصة لكل بيئة
// ==========================================

/**
 * تخطيط الحاسوب المكتبي (Desktop): شريط تنقل جانبي متطور + مساحة عمل فسيحة أو تقسيم شاشة مزدوج
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DesktopLayoutContent(
    state: MishkatUiState,
    viewModel: MishkatViewModel,
    listState: androidx.compose.foundation.lazy.LazyListState,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    showLessonsSplitPane: Boolean,
    onToggleSplitPane: () -> Unit,
    displayModeSetting: DisplayModeSetting,
    effectiveLayoutType: DeviceLayoutType,
    onOpenDisplayModeDialog: () -> Unit,
    currentStudentName: String?,
    onOpenAuth: () -> Unit,
    onOpenLessons: () -> Unit,
    onOpenVoiceRecitation: () -> Unit,
    onOpenDailyReminder: () -> Unit,
    onOpenBadges: () -> Unit,
    isEn: Boolean,
    onSelectLessonForTutor: (lessonTitle: String, topic: String) -> Unit
) {
    val context = LocalContext.current

    Row(modifier = Modifier.fillMaxSize()) {
        // شريط التنقل المكتبي الجانبي (Desktop Navigation Rail)
        NavigationRail(
            modifier = Modifier
                .fillMaxHeight()
                .testTag("desktop_navigation_rail"),
            containerColor = MaterialTheme.colorScheme.surface,
            header = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MishkatEmeraldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MishkatGoldAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = MishkatStrings.appTitle(isEn),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MishkatEmeraldDark
                    )
                    Text(
                        text = if (isEn) "Desktop" else "حاسوب",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MishkatGoldAccent
                    )
                }
            }
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            NavigationRailItem(
                selected = !showLessonsSplitPane,
                onClick = { if (showLessonsSplitPane) onToggleSplitPane() },
                icon = { Icon(Icons.Default.School, contentDescription = null) },
                label = { Text(if (isEn) "AI Tutor" else "المعلم", fontSize = 11.sp) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MishkatEmeraldDark,
                    selectedTextColor = MishkatEmeraldDark,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("nav_rail_tutor")
            )

            NavigationRailItem(
                selected = showLessonsSplitPane,
                onClick = onOpenLessons,
                icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                label = { Text(if (isEn) "Lessons" else "الدروس", fontSize = 11.sp) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MishkatEmeraldDark,
                    selectedTextColor = MishkatEmeraldDark,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("nav_rail_lessons")
            )

            NavigationRailItem(
                selected = false,
                onClick = onOpenVoiceRecitation,
                icon = { Icon(Icons.Default.Mic, contentDescription = null) },
                label = { Text(if (isEn) "Recitation" else "التلاوة", fontSize = 11.sp) },
                colors = NavigationRailItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_rail_voice")
            )

            NavigationRailItem(
                selected = false,
                onClick = onOpenDailyReminder,
                icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null) },
                label = { Text(if (isEn) "Reminders" else "التنبيهات", fontSize = 11.sp) },
                colors = NavigationRailItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_rail_reminders")
            )

            NavigationRailItem(
                selected = false,
                onClick = onOpenBadges,
                icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                label = { Text(if (isEn) "Badges" else "الأوسمة", fontSize = 11.sp) },
                colors = NavigationRailItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_rail_badges")
            )

            NavigationRailItem(
                selected = false,
                onClick = onOpenAuth,
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                label = {
                    Text(
                        text = currentStudentName ?: (if (isEn) "Account" else "الطالب"),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_rail_account")
            )

            Spacer(modifier = Modifier.weight(1f))

            // زر تبديل اللغة (عربي / English) على الشريط الجانبي
            IconButton(
                onClick = {
                    MishkatLanguageManager.toggleLanguage(context)
                    val newLang = MishkatLanguageManager.currentLanguage.value
                    viewModel.setLanguage(newLang)
                },
                modifier = Modifier.testTag("desktop_lang_toggle")
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Toggle Language",
                    tint = MishkatEmeraldPrimary
                )
            }

            // زر فئة العرض والواجهة (Display Mode) على الشريط الجانبي
            Surface(
                onClick = onOpenDisplayModeDialog,
                shape = RoundedCornerShape(12.dp),
                color = MishkatGoldContainer,
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .testTag("desktop_display_mode_btn")
            ) {
                Icon(
                    imageVector = displayModeSetting.icon,
                    contentDescription = MishkatStrings.displayModeSelectorTitle(isEn),
                    tint = MishkatOnGoldContainer,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }
        }

        // خط فاصل عمودي أنيق بين شريط التنقل ومنطقة المحتوى
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        )

        // منطقة المحتوى المكتبي الرئيسية
        Box(modifier = Modifier.weight(1f)) {
            if (showLessonsSplitPane) {
                // تقسيم الشاشة المزدوج (Split Pane): الدروس إلى اليمين/اليسار + المعلم الذكي بجانبه
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(0.44f)) {
                        LessonsListScreen(
                            isEmbeddedInSplitPane = true,
                            onCloseSplitPane = onToggleSplitPane,
                            onSelectLessonForTutor = onSelectLessonForTutor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    )

                    Box(modifier = Modifier.weight(0.56f)) {
                        TutorChatWorkspace(
                            state = state,
                            viewModel = viewModel,
                            listState = listState,
                            inputText = inputText,
                            onInputTextChange = onInputTextChange,
                            isEn = isEn,
                            isDesktop = true,
                            isSplitPane = true,
                            showLessonsSplitPane = true,
                            onToggleSplitPane = onToggleSplitPane,
                            displayModeSetting = displayModeSetting,
                            effectiveLayoutType = effectiveLayoutType,
                            onOpenDisplayModeDialog = onOpenDisplayModeDialog,
                            onOpenLessons = onOpenLessons,
                            onOpenVoiceRecitation = onOpenVoiceRecitation,
                            onOpenDailyReminder = onOpenDailyReminder
                        )
                    }
                }
            } else {
                // مساحة عمل المعلم الفسيحة للشاشات الواسعة
                TutorChatWorkspace(
                    state = state,
                    viewModel = viewModel,
                    listState = listState,
                    inputText = inputText,
                    onInputTextChange = onInputTextChange,
                    isEn = isEn,
                    isDesktop = true,
                    isSplitPane = false,
                    showLessonsSplitPane = false,
                    onToggleSplitPane = onToggleSplitPane,
                    displayModeSetting = displayModeSetting,
                    effectiveLayoutType = effectiveLayoutType,
                    onOpenDisplayModeDialog = onOpenDisplayModeDialog,
                    onOpenLessons = onOpenLessons,
                    onOpenVoiceRecitation = onOpenVoiceRecitation,
                    onOpenDailyReminder = onOpenDailyReminder
                )
            }
        }
    }
}

/**
 * تخطيط الجهاز اللوحي (Tablet): شريط علوي تكيفي غني بالأدوات ومساحة قراءة مريحة مع دعم التقسيم
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabletLayoutContent(
    state: MishkatUiState,
    viewModel: MishkatViewModel,
    listState: androidx.compose.foundation.lazy.LazyListState,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    showLessonsSplitPane: Boolean,
    onToggleSplitPane: () -> Unit,
    displayModeSetting: DisplayModeSetting,
    effectiveLayoutType: DeviceLayoutType,
    onOpenDisplayModeDialog: () -> Unit,
    currentStudentName: String?,
    onOpenAuth: () -> Unit,
    onOpenLessons: () -> Unit,
    onOpenVoiceRecitation: () -> Unit,
    onOpenDailyReminder: () -> Unit,
    onOpenBadges: () -> Unit,
    isEn: Boolean,
    onSelectLessonForTutor: (lessonTitle: String, topic: String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = MishkatStrings.appTitle(isEn),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MishkatEmeraldPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (isEn) "Tablet" else "جهاز لوحي",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // زر تفعيل العرض المزدوج (Split View)
                    Surface(
                        onClick = onToggleSplitPane,
                        color = if (showLessonsSplitPane) MishkatEmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("tablet_split_view_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ViewSidebar,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (showLessonsSplitPane) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = MishkatStrings.splitViewToggle(isEn, showLessonsSplitPane),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showLessonsSplitPane) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // زر نمط العرض
                    Surface(
                        onClick = onOpenDisplayModeDialog,
                        color = MishkatGoldContainer,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(0.5.dp, MishkatGoldAccent),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("tablet_display_mode_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = displayModeSetting.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MishkatOnGoldContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = MishkatStrings.displayModeBadge(isEn, displayModeSetting, effectiveLayoutType),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatOnGoldContainer
                            )
                        }
                    }

                    // زر اللغة
                    IconButton(
                        onClick = {
                            MishkatLanguageManager.toggleLanguage(context)
                            val newLang = MishkatLanguageManager.currentLanguage.value
                            viewModel.setLanguage(newLang)
                        },
                        modifier = Modifier.testTag("tablet_language_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Language",
                            tint = MishkatEmeraldPrimary
                        )
                    }

                    // أزرار سريعة
                    IconButton(onClick = onOpenVoiceRecitation) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onOpenDailyReminder) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Reminders", tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = onOpenBadges) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Badges", tint = MishkatGoldAccent)
                    }
                    IconButton(onClick = onOpenAuth) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Account", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showLessonsSplitPane) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(0.45f)) {
                        LessonsListScreen(
                            isEmbeddedInSplitPane = true,
                            onCloseSplitPane = onToggleSplitPane,
                            onSelectLessonForTutor = onSelectLessonForTutor
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    )
                    Box(modifier = Modifier.weight(0.55f)) {
                        TutorChatWorkspace(
                            state = state,
                            viewModel = viewModel,
                            listState = listState,
                            inputText = inputText,
                            onInputTextChange = onInputTextChange,
                            isEn = isEn,
                            isDesktop = false,
                            isSplitPane = true,
                            showLessonsSplitPane = true,
                            onToggleSplitPane = onToggleSplitPane,
                            displayModeSetting = displayModeSetting,
                            effectiveLayoutType = effectiveLayoutType,
                            onOpenDisplayModeDialog = onOpenDisplayModeDialog,
                            onOpenLessons = onOpenLessons,
                            onOpenVoiceRecitation = onOpenVoiceRecitation,
                            onOpenDailyReminder = onOpenDailyReminder
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 760.dp)
                    ) {
                        TutorChatWorkspace(
                            state = state,
                            viewModel = viewModel,
                            listState = listState,
                            inputText = inputText,
                            onInputTextChange = onInputTextChange,
                            isEn = isEn,
                            isDesktop = false,
                            isSplitPane = false,
                            showLessonsSplitPane = false,
                            onToggleSplitPane = onToggleSplitPane,
                            displayModeSetting = displayModeSetting,
                            effectiveLayoutType = effectiveLayoutType,
                            onOpenDisplayModeDialog = onOpenDisplayModeDialog,
                            onOpenLessons = onOpenLessons,
                            onOpenVoiceRecitation = onOpenVoiceRecitation,
                            onOpenDailyReminder = onOpenDailyReminder
                        )
                    }
                }
            }
        }
    }
}

/**
 * تخطيط الهاتف المحمول (Mobile Handheld): واجهة رشيقة مصممة خصيصاً للشاشات الرأسية
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MobileLayoutContent(
    state: MishkatUiState,
    viewModel: MishkatViewModel,
    listState: androidx.compose.foundation.lazy.LazyListState,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    displayModeSetting: DisplayModeSetting,
    effectiveLayoutType: DeviceLayoutType,
    onOpenDisplayModeDialog: () -> Unit,
    currentStudentName: String?,
    onOpenAuth: () -> Unit,
    onOpenLessons: () -> Unit,
    onOpenVoiceRecitation: () -> Unit,
    onOpenDailyReminder: () -> Unit,
    onOpenBadges: () -> Unit,
    isEn: Boolean
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = MishkatStrings.appTitle(isEn),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isEn) "Mobile" else "المعلم",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = MishkatStrings.appSubtitle(isEn),
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // زر اختيار نمط العرض (Mobile/Tablet/Desktop/Auto)
                    Surface(
                        onClick = onOpenDisplayModeDialog,
                        color = MishkatGoldContainer,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(0.5.dp, MishkatGoldAccent),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("mobile_display_mode_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = displayModeSetting.icon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = MishkatOnGoldContainer
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = MishkatStrings.displayModeBadge(isEn, displayModeSetting, effectiveLayoutType),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatOnGoldContainer
                            )
                        }
                    }

                    // زر اللغة
                    IconButton(
                        onClick = {
                            MishkatLanguageManager.toggleLanguage(context)
                            val newLang = MishkatLanguageManager.currentLanguage.value
                            viewModel.setLanguage(newLang)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mobile_language_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Language",
                            tint = MishkatEmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // زر الدروس
                    IconButton(
                        onClick = onOpenLessons,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mobile_lessons_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Lessons",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // زر الأوسمة
                    IconButton(
                        onClick = onOpenBadges,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mobile_badges_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Badges",
                            tint = MishkatGoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // زر الحساب
                    IconButton(
                        onClick = onOpenAuth,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mobile_account_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            // شريط إعدادات المستوى ونمط الشرح
            TutorControlBar(
                currentLevel = state.tutorLevel,
                onLevelChange = { viewModel.setTutorLevel(it) },
                currentMode = state.explanationMode,
                onModeChange = { viewModel.setExplanationMode(it) },
                isEn = isEn
            )

            // شريط الموضوعات السريعة وحالات الاستخدام
            QuickActionChipsRow(
                onQuerySelected = { query ->
                    viewModel.sendMessage(query)
                },
                onOpenLessonsList = onOpenLessons,
                onOpenVoiceRecitation = onOpenVoiceRecitation,
                onOpenDailyReminder = onOpenDailyReminder,
                onHintRequested = {
                    if (isEn) {
                        viewModel.requestNextHint("Hafs shortening of Munfasil and prohibition of Sakt")
                    } else {
                        viewModel.requestNextHint("قصر المنفصل لحفص وامتناع السكت")
                    }
                },
                onRevisionRequested = {
                    viewModel.requestRevisionPlan()
                },
                onOutOfScopeTest = {
                    viewModel.testGuardrailOutOfScope()
                },
                isEn = isEn
            )

            // تيار الرسائل (Chat Stream)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.messages, key = { it.id }) { message ->
                    ChatMessageItem(message = message, isEn = isEn)
                }

                if (state.isLoading) {
                    item {
                        MishkatLoadingIndicator(isEn = isEn)
                    }
                }
            }

            // شريط الإدخال السفلي
            MessageInputBar(
                inputText = inputText,
                onTextChange = onInputTextChange,
                isLoading = state.isLoading,
                onStartVoiceRecitation = onOpenVoiceRecitation,
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        onInputTextChange("")
                    }
                },
                isEn = isEn
            )
        }
    }
}

/**
 * مساحة عمل المحادثة مع المعلم الذكي (مشتركة بين الحاسوب والأجهزة اللوحية)
 */
@Composable
private fun TutorChatWorkspace(
    state: MishkatUiState,
    viewModel: MishkatViewModel,
    listState: androidx.compose.foundation.lazy.LazyListState,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    isEn: Boolean,
    isDesktop: Boolean,
    isSplitPane: Boolean,
    showLessonsSplitPane: Boolean,
    onToggleSplitPane: () -> Unit,
    displayModeSetting: DisplayModeSetting,
    effectiveLayoutType: DeviceLayoutType,
    onOpenDisplayModeDialog: () -> Unit,
    onOpenLessons: () -> Unit,
    onOpenVoiceRecitation: () -> Unit,
    onOpenDailyReminder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // شريط الرأس العلوي لمساحة العمل
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isSplitPane) {
                                if (isEn) "Mishkat AI Tutor & Assistant" else "المعلم الذكي والمساعد الأكاديمي"
                            } else {
                                MishkatStrings.appTitle(isEn) + " • " + (if (isEn) "Desktop Academic Workspace" else "مساحة العمل الأكاديمية")
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MishkatEmeraldDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MishkatEmeraldPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isDesktop) "Desktop" else "Tablet",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isEn) "Verified scholarly dataset based on Ar-Rawdat An-Nadir" else "منهج كتاب «الروض الناضر» بتحريرات طريقي الفيل وزرعان",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // زر تبديل العرض المزدوج (Split View)
                    Surface(
                        onClick = onToggleSplitPane,
                        color = if (showLessonsSplitPane) MishkatEmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("workspace_split_toggle_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ViewSidebar,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (showLessonsSplitPane) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = MishkatStrings.splitViewToggle(isEn, showLessonsSplitPane),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showLessonsSplitPane) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // شارة نظام العرض
                    Surface(
                        onClick = onOpenDisplayModeDialog,
                        shape = RoundedCornerShape(12.dp),
                        color = MishkatGoldContainer,
                        border = BorderStroke(0.5.dp, MishkatGoldAccent),
                        modifier = Modifier.testTag("workspace_display_mode_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = displayModeSetting.icon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = MishkatOnGoldContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = MishkatStrings.displayModeBadge(isEn, displayModeSetting, effectiveLayoutType),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatOnGoldContainer
                            )
                        }
                    }
                }
            }
        }

        // مساحة المحتوى المحددة بقياس قراءة مريح
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = if (isSplitPane) 760.dp else 920.dp)
            ) {
                // شريط إعدادات المستوى ونمط الشرح
                TutorControlBar(
                    currentLevel = state.tutorLevel,
                    onLevelChange = { viewModel.setTutorLevel(it) },
                    currentMode = state.explanationMode,
                    onModeChange = { viewModel.setExplanationMode(it) },
                    isEn = isEn
                )

                // شريط الموضوعات السريعة وحالات الاستخدام
                QuickActionChipsRow(
                    onQuerySelected = { query ->
                        viewModel.sendMessage(query)
                    },
                    onOpenLessonsList = onOpenLessons,
                    onOpenVoiceRecitation = onOpenVoiceRecitation,
                    onOpenDailyReminder = onOpenDailyReminder,
                    onHintRequested = {
                        if (isEn) {
                            viewModel.requestNextHint("Hafs shortening of Munfasil and prohibition of Sakt")
                        } else {
                            viewModel.requestNextHint("قصر المنفصل لحفص وامتناع السكت")
                        }
                    },
                    onRevisionRequested = {
                        viewModel.requestRevisionPlan()
                    },
                    onOutOfScopeTest = {
                        viewModel.testGuardrailOutOfScope()
                    },
                    isEn = isEn
                )

                // تيار الرسائل (Chat Stream)
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        ChatMessageItem(message = message, isEn = isEn)
                    }

                    if (state.isLoading) {
                        item {
                            MishkatLoadingIndicator(isEn = isEn)
                        }
                    }
                }

                // شريط الإدخال المكتبي السفلي مع تلميح لوحة المفاتيح
                MessageInputBar(
                    inputText = inputText,
                    onTextChange = onInputTextChange,
                    isLoading = state.isLoading,
                    onStartVoiceRecitation = onOpenVoiceRecitation,
                    onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            onInputTextChange("")
                        }
                    },
                    isEn = isEn
                )
            }
        }
    }
}

@Composable
fun TutorControlBar(
    currentLevel: TutorLevel,
    onLevelChange: (TutorLevel) -> Unit,
    currentMode: TutorExplanationMode,
    onModeChange: (TutorExplanationMode) -> Unit,
    isEn: Boolean = false
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // صف اختيار المستوى الأكاديمي
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEn) "Level:" else "المستوى:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(if (isEn) 65.dp else 55.dp)
                )

                SingleChoiceSegmentedButtonRow(modifier = Modifier.weight(1f)) {
                    TutorLevel.values().forEachIndexed { index, level ->
                        SegmentedButton(
                            selected = currentLevel == level,
                            onClick = { onLevelChange(level) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = TutorLevel.values().size)
                        ) {
                            Text(text = level.getLabel(isEn), fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // صف اختيار نمط الشرح
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEn) "Mode:" else "الأسلوب:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.width(if (isEn) 65.dp else 55.dp)
                )

                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TutorExplanationMode.values().forEach { mode ->
                        FilterChip(
                            selected = currentMode == mode,
                            onClick = { onModeChange(mode) },
                            label = { Text(mode.getLabel(isEn), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionChipsRow(
    onQuerySelected: (String) -> Unit,
    onOpenLessonsList: () -> Unit,
    onOpenVoiceRecitation: () -> Unit,
    onOpenDailyReminder: () -> Unit,
    onHintRequested: () -> Unit,
    onRevisionRequested: () -> Unit,
    onOutOfScopeTest: () -> Unit,
    isEn: Boolean = false
) {
    val scrollState = rememberScrollState()
    val quickPrompts = remember(isEn) { MishkatStrings.chipQuickPrompts(isEn) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickChip(
            icon = Icons.Default.NotificationsActive,
            label = if (isEn) "⏰ Daily Reminders" else "⏰ جدولة تنبيهات المراجعة",
            onClick = onOpenDailyReminder
        )
        QuickChip(
            icon = Icons.Default.RecordVoiceOver,
            label = if (isEn) "🎙️ Voice Recitation" else "🎙️ تلاوة وتصحيح فوري",
            onClick = onOpenVoiceRecitation
        )
        QuickChip(
            icon = Icons.AutoMirrored.Filled.MenuBook,
            label = if (isEn) "📖 Curriculum Lessons" else "📖 تصفح فهرس الدروس",
            onClick = onOpenLessonsList
        )
        quickPrompts.forEach { (chipLabel, query) ->
            QuickChip(
                icon = Icons.Default.Bookmark,
                label = chipLabel,
                onClick = { onQuerySelected(query) }
            )
        }
        QuickChip(
            icon = Icons.Default.Lightbulb,
            label = if (isEn) "💡 Request Hint" else "💡 طلب تلميح",
            onClick = onHintRequested
        )
        QuickChip(
            icon = Icons.Default.School,
            label = if (isEn) "📋 Revision Plan" else "📋 خطة مراجعة مخصصة",
            onClick = onRevisionRequested
        )
        QuickChip(
            icon = Icons.Default.Security,
            label = if (isEn) "🛡️ Test Guardrail" else "🛡️ اختبار خارج الكتاب (Guardrail)",
            onClick = onOutOfScopeTest
        )
    }
}

@Composable
fun QuickChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChatMessageItem(
    message: MishkatMessage,
    isEn: Boolean = false
) {
    val isStudent = message.sender == MessageSender.STUDENT

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isStudent) Alignment.End else Alignment.Start
    ) {
        // بطاقة الرسالة
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isStudent) 16.dp else 4.dp,
                bottomEnd = if (isStudent) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isStudent -> MaterialTheme.colorScheme.primaryContainer
                    message.isOutOfScope -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isStudent) 1.dp else 2.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .then(
                    if (!isStudent && !message.isOutOfScope) {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else if (message.isOutOfScope) {
                        Modifier.border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else Modifier
                )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // ترويسة تلاوة شفوية للطالب
                if (isStudent && message.isVoiceRecitation) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEn) "Oral Recitation Audio" else "تلاوة شفوية ملتقطة بالصوت",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // ترويسة الرسالة لمشكاة
                if (!isStudent) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Mishkat",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEn) "Mishkat • Academic AI Tutor" else "مشكاة • المعلم الأكاديمي",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (message.isOutOfScope) {
                            Surface(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isEn) "Out of Scope 🛡️" else "خارج نطاق الكتاب 🛡️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else if (message.isRecitationCorrection) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isEn) "Recitation Correction 🎙️" else "تصحيح تحريري فوري 🎙️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // نص الرسالة الأساسي
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = if (isStudent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )

                // إذا كان هناك مراجع موثقة من الكتاب
                if (message.citedReferences.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = MishkatStrings.referencesHeader(isEn),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            message.citedReferences.forEach { ref ->
                                Text(
                                    text = "• $ref",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MishkatLoadingIndicator(isEn: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = MishkatStrings.chatAnalyzing(isEn),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MessageInputBar(
    inputText: String,
    onTextChange: (String) -> Unit,
    isLoading: Boolean,
    onStartVoiceRecitation: () -> Unit,
    onSend: () -> Unit,
    isEn: Boolean = false
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // زر الميكروفون للتلاوة الشفوية
            IconButton(
                onClick = onStartVoiceRecitation,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .testTag("mic_recitation_input_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = if (isEn) "Start Voice Recitation" else "بدء التلاوة الصوتية والتصحيح",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = onTextChange,
                placeholder = {
                    Text(
                        text = MishkatStrings.chatInputPlaceholder(isEn),
                        fontSize = 12.5.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("mishkat_query_input"),
                maxLines = 3,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onSend,
                enabled = inputText.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (inputText.isNotBlank() && !isLoading) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .testTag("send_query_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = MishkatStrings.chatSend(isEn),
                    tint = if (inputText.isNotBlank() && !isLoading) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

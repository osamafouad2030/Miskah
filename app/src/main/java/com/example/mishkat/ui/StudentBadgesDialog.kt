package com.example.mishkat.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mishkat.data.MishkatAchievementsManager
import com.example.mishkat.localization.MishkatStrings
import com.example.mishkat.model.BadgeCategory
import com.example.mishkat.model.BadgeIconType
import com.example.mishkat.model.MishkatBadge
import com.example.mishkat.ui.theme.MishkatBorder
import com.example.mishkat.ui.theme.MishkatCardSurface
import com.example.mishkat.ui.theme.MishkatEmeraldContainer
import com.example.mishkat.ui.theme.MishkatEmeraldDark
import com.example.mishkat.ui.theme.MishkatEmeraldLight
import com.example.mishkat.ui.theme.MishkatEmeraldPrimary
import com.example.mishkat.ui.theme.MishkatGoldAccent
import com.example.mishkat.ui.theme.MishkatGoldContainer
import com.example.mishkat.ui.theme.MishkatGoldLight
import com.example.mishkat.ui.theme.MishkatManuscriptSand
import com.example.mishkat.ui.theme.MishkatOnEmeraldContainer
import com.example.mishkat.ui.theme.MishkatOnGoldContainer
import com.example.mishkat.ui.theme.MishkatTextMuted
import com.example.mishkat.ui.theme.MishkatTextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * نافذة عرض أوسمة وإنجازات الطالب الأكاديمية والقرآنية
 */
@Composable
fun StudentBadgesDialog(
    badges: List<MishkatBadge>,
    onDismiss: () -> Unit,
    isEn: Boolean = false
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(BadgeCategory.ALL) }

    val unlockedCount = badges.count { it.isUnlocked }
    val totalBadges = badges.size
    val (rankTitle, rankSubtitle) = MishkatAchievementsManager.getStudentRank(unlockedCount, isEn = isEn)

    val perfectScoresCount = badges.firstOrNull { it.id == "first_perfect_score" }?.currentCount ?: 0
    val completedLessonsCount = badges.firstOrNull { it.id == "first_lesson" }?.currentCount ?: 0

    val filteredBadges = remember(selectedCategory, badges) {
        if (selectedCategory == BadgeCategory.ALL) {
            badges
        } else {
            badges.filter { it.category == selectedCategory }
        }
    }

    val layoutDirection = if (isEn) LayoutDirection.Ltr else LayoutDirection.Rtl
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("student_badges_dialog_screen"),
                color = MishkatManuscriptSand
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 960.dp)
                    ) {
                    // شريط العنوان العلوي الفخم
                    Surface(
                        color = MishkatCardSurface,
                        shadowElevation = 3.dp,
                        border = BorderStroke(0.5.dp, MishkatBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MishkatGoldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = MishkatOnGoldContainer,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = MishkatStrings.badgesDialogTitle(isEn),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MishkatEmeraldPrimary
                                    )
                                    Text(
                                        text = MishkatStrings.badgesDialogSubtitle(isEn),
                                        fontSize = 11.sp,
                                        color = MishkatTextMuted
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("badges_dialog_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = MishkatStrings.close(isEn),
                                    tint = MishkatEmeraldPrimary
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. بطاقة الرتبة الحالية والرصيد القرآني
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_rank_card"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MishkatEmeraldPrimary),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    MishkatEmeraldPrimary,
                                                    MishkatEmeraldDark
                                                )
                                            )
                                        )
                                        .padding(18.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // شارة اللقب
                                        Surface(
                                            color = MishkatGoldAccent,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.WorkspacePremium,
                                                    contentDescription = null,
                                                    tint = MishkatEmeraldDark,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = rankTitle,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MishkatEmeraldDark
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = rankSubtitle,
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // إحصائيات الإنجاز الثلاثية
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            AchievementStatItem(
                                                label = MishkatStrings.badgesLabel(isEn),
                                                value = "$unlockedCount/$totalBadges",
                                                icon = Icons.Default.EmojiEvents
                                            )
                                            AchievementStatItem(
                                                label = MishkatStrings.completedLessonsLabel(isEn),
                                                value = "$completedLessonsCount",
                                                icon = Icons.AutoMirrored.Filled.MenuBook
                                            )
                                            AchievementStatItem(
                                                label = MishkatStrings.perfectScoresLabel(isEn),
                                                value = "$perfectScoresCount",
                                                icon = Icons.Default.Star
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. فلاتر التصنيفات
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(BadgeCategory.values()) { category ->
                                    val isSelected = selectedCategory == category
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCategory = category },
                                        label = {
                                            Text(
                                                text = category.localizedTitle(isEn),
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MishkatEmeraldPrimary,
                                            selectedLabelColor = Color.White,
                                            containerColor = MishkatCardSurface,
                                            labelColor = MishkatTextPrimary
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isSelected) MishkatEmeraldPrimary else MishkatBorder
                                        ),
                                        modifier = Modifier.testTag("filter_chip_${category.name}")
                                    )
                                }
                            }
                        }

                        // 3. قائمة الأوسمة
                        items(filteredBadges) { badge ->
                            BadgeItemCard(badge = badge, isEn = isEn)
                        }

                        // مسافة سفلية
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    // الشريط السفلي لمشاركة الأوسمة
                    Surface(
                        color = MishkatCardSurface,
                        border = BorderStroke(0.5.dp, MishkatBorder),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val shareText = if (isEn) {
                                        """
                                            🏆 My Achievements & Badges in «Mishkat» App - Asim's Tahrirat from Tayyibat An-Nashr:
                                            🎖️ Quranic Rank: $rankTitle
                                            ✨ Badges Unlocked: $unlockedCount of $totalBadges badges
                                            📖 Lessons Completed: $completedLessonsCount lessons
                                            🌟 100% Perfect Scores: $perfectScoresCount
                                            
                                            Ar-Rawdat An-Nadir curriculum supervised by Sheikha Samah Al-Bandari.
                                            #Mishkat #TayyibatAnNashr #StudentBadges #Asim
                                        """.trimIndent()
                                    } else {
                                        """
                                            🏆 إنجازاتي وأوسمتي في تطبيق «مشكاة» - تحريرات عاصم من طيبة النشر:
                                            🎖️ الرتبة القرآنية: $rankTitle
                                            ✨ الأوسمة المحققة: $unlockedCount من أصل $totalBadges أوسمة
                                            📖 الدروس المنجزة: $completedLessonsCount درسًا
                                            🌟 الاختبارات المحققة للدرجة الكاملة (100%): $perfectScoresCount
                                            
                                            منهج كتاب «الروض الناضر» بإشراف الشيخة المقرئة سماح البنداري.
                                            #مشكاة #طيبة_النشر #أوسمة_الطالب #عاصم
                                        """.trimIndent()
                                    }
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, if (isEn) "Share Badges & Achievements" else "مشاركة الأوسمة والإنجازات")
                                    context.startActivity(shareIntent)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MishkatGoldAccent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("share_all_badges_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = MishkatEmeraldDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = MishkatStrings.shareAllBadges(isEn),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatEmeraldDark
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

@Composable
private fun AchievementStatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MishkatGoldAccent,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = Color.White.copy(alpha = 0.75f)
        )
    }
}

@Composable
fun BadgeItemCard(
    badge: MishkatBadge,
    isEn: Boolean = false
) {
    val isUnlocked = badge.isUnlocked
    val icon = getBadgeIcon(badge.iconType)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("badge_card_${badge.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MishkatCardSurface else Color(0xFFF3F2EE)
        ),
        border = BorderStroke(
            width = if (isUnlocked) 1.2.dp else 0.5.dp,
            color = if (isUnlocked) MishkatGoldAccent else MishkatBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // أيقونة الوسام
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) Brush.radialGradient(
                            listOf(MishkatGoldLight, MishkatGoldAccent)
                        ) else Brush.radialGradient(
                            listOf(Color(0xFFE2E2DE), Color(0xFFD0D0CA))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) icon else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) MishkatEmeraldDark else Color(0xFF8A8A82),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // بيانات الوسام والشرح
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = badge.localizedTitle(isEn),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) MishkatEmeraldPrimary else MishkatTextMuted
                    )

                    // شارة الحالة
                    if (isUnlocked) {
                        Surface(
                            color = MishkatGoldContainer,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, MishkatGoldAccent)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MishkatOnGoldContainer,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = MishkatStrings.achievedBadge(isEn),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatOnGoldContainer
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "${badge.currentCount}/${badge.targetCount}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MishkatTextMuted
                        )
                    }
                }

                Text(
                    text = badge.localizedSubtitle(isEn),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isUnlocked) MishkatGoldAccent else MishkatTextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = badge.localizedDescription(isEn),
                    fontSize = 11.sp,
                    color = MishkatTextMuted,
                    lineHeight = 15.sp
                )

                // شريط التقدم إن لم يكتمل
                if (!isUnlocked) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = { badge.progressRatio },
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MishkatEmeraldLight,
                            trackColor = MishkatBorder
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${badge.progressPercentage}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MishkatTextMuted
                        )
                    }
                } else if (badge.unlockedAtTimestamp != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(badge.unlockedAtTimestamp))
                    Text(
                        text = MishkatStrings.badgeUnlockedOnDate(isEn, dateStr),
                        fontSize = 9.5.sp,
                        color = MishkatEmeraldLight
                    )
                }
            }
        }
    }
}

/**
 * نافذة تنبيه واحتفاء بالحصول على وسام جديد
 */
@Composable
fun NewBadgeUnlockedDialog(
    badges: List<MishkatBadge>,
    onDismiss: () -> Unit,
    onViewAllBadges: () -> Unit,
    isEn: Boolean = false
) {
    if (badges.isEmpty()) return
    val badge = badges.first()
    val icon = getBadgeIcon(badge.iconType)

    val layoutDirection = if (isEn) LayoutDirection.Ltr else LayoutDirection.Rtl
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn()
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MishkatCardSurface,
                    border = BorderStroke(1.5.dp, MishkatGoldAccent),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(0.92f)
                        .testTag("new_badge_unlocked_dialog")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(22.dp)
                    ) {
                        // هالة ذهبية وأيقونة الوسام
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(MishkatGoldLight, MishkatGoldAccent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MishkatEmeraldDark,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = MishkatStrings.newBadgeUnlockedHeader(isEn),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MishkatEmeraldPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "«${badge.localizedTitle(isEn)}»",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MishkatGoldAccent,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = badge.localizedSubtitle(isEn),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MishkatTextMuted,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = MishkatEmeraldContainer,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(0.5.dp, MishkatEmeraldPrimary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = badge.localizedDescription(isEn),
                                fontSize = 12.sp,
                                color = MishkatOnEmeraldContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }

                        if (badges.size > 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = MishkatStrings.moreBadgesUnlocked(isEn, badges.size - 1),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onDismiss()
                                    onViewAllBadges()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MishkatGoldAccent),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("view_badges_from_alert_button")
                            ) {
                                Text(
                                    text = MishkatStrings.viewBadgesButtonAlert(isEn),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatEmeraldDark
                                )
                            }

                            Button(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MishkatEmeraldPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dismiss_badge_alert_button")
                            ) {
                                Text(
                                    text = MishkatStrings.continueStudyingButton(isEn),
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

private fun getBadgeIcon(type: BadgeIconType): ImageVector {
    return when (type) {
        BadgeIconType.FIRST_LESSON -> Icons.Default.School
        BadgeIconType.STUDENT_PROGRESS -> Icons.AutoMirrored.Filled.MenuBook
        BadgeIconType.SCHOLAR_PROGRESS -> Icons.Default.AutoStories
        BadgeIconType.COMPLETION_CROWN -> Icons.Default.WorkspacePremium
        BadgeIconType.STAR_PERFECT -> Icons.Default.Star
        BadgeIconType.TRIPLE_PERFECT -> Icons.Default.MilitaryTech
        BadgeIconType.MASTER_PERFECT -> Icons.Default.Verified
        BadgeIconType.QUIZ_PASSED -> Icons.Default.Quiz
        BadgeIconType.NOTE_TAKER -> Icons.Default.EditNote
    }
}

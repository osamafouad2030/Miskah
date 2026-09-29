package com.example.mishkat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mishkat.data.MishkatAchievementsManager
import com.example.mishkat.localization.MishkatStrings
import com.example.mishkat.rag.MishkatKnowledgeBase
import com.example.mishkat.ui.theme.MishkatBorder
import com.example.mishkat.ui.theme.MishkatEmeraldContainer
import com.example.mishkat.ui.theme.MishkatEmeraldDark
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
 * بيانات واجهة المسارات التعليمية المتاحة
 */
data class LearningPathway(
    val id: String,
    val unitTitle: String,
    val titleAr: String,
    val titleEn: String,
    val descAr: String,
    val descEn: String,
    val emoji: String,
    val pagesRangeAr: String,
    val pagesRangeEn: String,
    val accentColor: Color,
    val promptAr: String,
    val promptEn: String
)

@Composable
fun MishkatPathwaysDashboard(
    isEn: Boolean,
    onBrowsePathwayLessons: (unitTitle: String) -> Unit,
    onStartPathwayTutor: (prompt: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val completedLessonIds = remember {
        MishkatAchievementsManager.getCompletedLessonIds(context)
    }

    val pathways = remember(isEn) {
        listOf(
            LearningPathway(
                id = "pathway_foundations",
                unitTitle = "الوحدة الأولى: مدخل التأسيس والأسانيد النشرية",
                titleAr = "مسار التأسيس والأسانيد القرآنية",
                titleEn = "Foundations & Quranic Asanid Path",
                descAr = "دراسة أسانيد الإمام عاصم بروايتي شعبة وحفص في التيسير وجامع البيان وطرق الطيبة الـ 128.",
                descEn = "Study the chains of transmission of Imam Asim through Shu'bah and Hafs in At-Taysir and Jami' Al-Bayan.",
                emoji = "📜",
                pagesRangeAr = "الصحفات: 5 - 10",
                pagesRangeEn = "Pages: 5 - 10",
                accentColor = MishkatEmeraldPrimary,
                promptAr = "أريد دراسة مسار التأسيس والأسانيد القرآنية من كتاب الروض الناضر. يرجى شرح أسانيد عاصم وطرق الرواة بالتفصيل.",
                promptEn = "I want to study the Foundations and Quranic Asanid Path from Ar-Rawdat An-Nadir. Please explain Imam Asim's chains and narrators' paths."
            ),
            LearningPathway(
                id = "pathway_usul",
                unitTitle = "الوحدة الثانية: تحريرات الأصول الكبرى",
                titleAr = "مسار تحريرات الأصول الكبرى",
                titleEn = "Major Usul Tahrirat Path",
                descAr = "إتقان ضوابط المد والقصر، شروط الأداء وموانع السكت، الغنة في اللام والراء، وتحرير مد التعظيم.",
                descEn = "Master rules of Madd & Qasr, performance conditions, Sakt prohibitions, and Ghunnah in Lam and Ra.",
                emoji = "⚖️",
                pagesRangeAr = "الصحفات: 62 - 107",
                pagesRangeEn = "Pages: 62 - 107",
                accentColor = MishkatGoldWarm,
                promptAr = "أريد دراسة مسار تحريرات الأصول الكبرى من كتاب الروض الناضر. يرجى شرح قواعد قصر المنفصل وموانع السكت والغنة.",
                promptEn = "I want to study the Major Usul Tahrirat Path from Ar-Rawdat An-Nadir. Please explain Qasr of Munfasil, Sakt prohibitions, and Ghunnah."
            ),
            LearningPathway(
                id = "pathway_farsh_first",
                unitTitle = "الوحدة الثالثة: تحريرات فرش الحروف (السبع الطوال إلى الكهف)",
                titleAr = "مسار تحريرات فرش الحروف",
                titleEn = "Farsh al-Huruf Tahrirat Path",
                descAr = "تحرير الأداء العملي لفرش الكلمات كإخفاء (فنعما)، وسين وصاد (ويبصط)، والإشمام والروم في (تأمنا).",
                descEn = "Unlock practical performance of word-specific variations like Fa-ni'imma, Yabsut, and Ta'manna.",
                emoji = "📖",
                pagesRangeAr = "الصحفات: 94 - 215",
                pagesRangeEn = "Pages: 94 - 215",
                accentColor = MishkatEmeraldDark,
                promptAr = "أريد دراسة مسار تحريرات فرش الحروف من كتاب الروض الناضر. يرجى شرح تحريرات البقرة والنساء والأعراف ويوسف بالتفصيل.",
                promptEn = "I want to study the Farsh al-Huruf Tahrirat Path from Ar-Rawdat An-Nadir. Please explain the variations in Al-Baqarah, Al-A'raf, and Yusuf."
            ),
            LearningPathway(
                id = "pathway_second_half",
                unitTitle = "الوحدة الرابعة: تحريرات النصف الثاني والختم",
                titleAr = "مسار النصف الثاني والختم والمفصل",
                titleEn = "Second Half & Conclusion Path",
                descAr = "تحرير السكتات الأربع لحفص، مراتب مد (عين) في مريم والشورى، والوجوه الأدائية في (ألم نخلقكم).",
                descEn = "Learn the 4 mandatory saktat of Hafs, degrees of letter Ayn, and pronunciation facets of Alam Nakhlukkum.",
                emoji = "🏆",
                pagesRangeAr = "الصحفات: 216 - 398",
                pagesRangeEn = "Pages: 216 - 398",
                accentColor = MishkatGoldAccent,
                promptAr = "أريد دراسة مسار النصف الثاني والختم والمفصل من كتاب الروض الناضر. يرجى شرح السكتات الأربع ومراتب عين وإدغام ألم نخلقكم.",
                promptEn = "I want to study the Second Half and Conclusion Path from Ar-Rawdat An-Nadir. Please explain Hafs' 4 saktat, Ayn degrees, and Alam Nakhlukkum."
            )
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pathways_dashboard")
    ) {
        val screenWidth = maxWidth
        val isTabletOrDesktop = screenWidth >= 600.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ترويسة الواجهة والترحيب (Scholarly Hero Header)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pathways_hero_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, MishkatBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MishkatEmeraldContainer.copy(alpha = 0.3f),
                                        Color.White
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MishkatEmeraldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = MishkatEmeraldPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isEn) "Available Educational Pathways" else "المسارات التعليمية المعتمدة",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldDark,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Tayyibat Al-Nashr Academic Curriculum" else "تحريرات الإمام عاصم من طيبة النشر وفق ضوابط كتاب الروض الناضر",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MishkatGoldAccent,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isEn) {
                                    "Supervised by Sheikha Samah Al-Bandari, Al-Mishkat Quranic Academy"
                                } else {
                                    "بإشراف الشيخة المقرئة سماح البنداري - أكاديمية مشكاة لعلوم القراءات"
                                },
                                fontSize = 11.5.sp,
                                color = MishkatTextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // عنوان فرعي للمسارات
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEn) "Select Your Learning Path" else "اختر مسارك القرآني للتعلم:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MishkatEmeraldDark
                    )
                    Surface(
                        color = MishkatEmeraldPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MishkatEmeraldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEn) "4 Main Tracks" else "4 مسارات رئيسية",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldPrimary
                            )
                        }
                    }
                }
            }

            // عرض بطاقات المسارات بشكل مرن
            if (isTabletOrDesktop) {
                // تقسيم البطاقات في جهاز لوحي أو حاسوب إلى صفين، كل صف بطاقتين بجانب بعضهما
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                PathwayCard(
                                    pathway = pathways[0],
                                    completedLessonIds = completedLessonIds,
                                    isEn = isEn,
                                    onBrowse = onBrowsePathwayLessons,
                                    onStartTutor = onStartPathwayTutor
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                PathwayCard(
                                    pathway = pathways[1],
                                    completedLessonIds = completedLessonIds,
                                    isEn = isEn,
                                    onBrowse = onBrowsePathwayLessons,
                                    onStartTutor = onStartPathwayTutor
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                PathwayCard(
                                    pathway = pathways[2],
                                    completedLessonIds = completedLessonIds,
                                    isEn = isEn,
                                    onBrowse = onBrowsePathwayLessons,
                                    onStartTutor = onStartPathwayTutor
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                PathwayCard(
                                    pathway = pathways[3],
                                    completedLessonIds = completedLessonIds,
                                    isEn = isEn,
                                    onBrowse = onBrowsePathwayLessons,
                                    onStartTutor = onStartPathwayTutor
                                )
                            }
                        }
                    }
                }
            } else {
                // في الهواتف الذكية، بطاقات رأسية متتالية
                items(pathways, key = { it.id }) { pathway ->
                    PathwayCard(
                        pathway = pathway,
                        completedLessonIds = completedLessonIds,
                        isEn = isEn,
                        onBrowse = onBrowsePathwayLessons,
                        onStartTutor = onStartPathwayTutor
                    )
                }
            }
        }
    }
}

@Composable
fun PathwayCard(
    pathway: LearningPathway,
    completedLessonIds: Set<String>,
    isEn: Boolean,
    onBrowse: (unitTitle: String) -> Unit,
    onStartTutor: (prompt: String) -> Unit
) {
    // حساب التقدم الفعلي في المسار من خلال ربط دروس قاعدة البيانات
    val allLessonsInUnit = remember(pathway.unitTitle) {
        MishkatKnowledgeBase.chunks.filter { it.unitTitle == pathway.unitTitle }
    }
    val totalLessons = allLessonsInUnit.size
    val completedLessons = allLessonsInUnit.count { completedLessonIds.contains(it.id) }
    val progressRatio = if (totalLessons > 0) completedLessons.toFloat() / totalLessons else 0f
    val progressPercent = (progressRatio * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pathway_card_${pathway.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MishkatBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // الجزء العلوي: الأيقونة، الاسم، ونطاق الصفحات
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // حاوية الأيقونة التعبيرية المميزة
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(pathway.accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pathway.emoji,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEn) pathway.titleEn else pathway.titleAr,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MishkatEmeraldDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isEn) pathway.pagesRangeEn else pathway.pagesRangeAr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MishkatGoldAccent
                    )
                }

                // شارة عدد الدروس في هذا المسار
                Surface(
                    color = MishkatEmeraldContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isEn) "$totalLessons Lessons" else "$totalLessons دروس",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MishkatEmeraldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // الوصف التوضيحي الأكاديمي للمسار
            Text(
                text = if (isEn) pathway.descEn else pathway.descAr,
                fontSize = 12.sp,
                color = MishkatTextMuted,
                lineHeight = 17.sp,
                minLines = 2,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // شريط قياس تقدم الطالب الحقيقي داخل هذا المسار
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEn) "Learning Progress" else "تقدمك في المسار",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MishkatTextMuted
                    )
                    Text(
                        text = "$completedLessons/$totalLessons ($progressPercent%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MishkatEmeraldPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = pathway.accentColor,
                    trackColor = MishkatBorder.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // أزرار التحكم والعمليات (تصفح أو تدارس)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // زر تصفح الدروس مصفاة تلقائياً
                OutlinedButton(
                    onClick = { onBrowse(pathway.unitTitle) },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MishkatEmeraldPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MishkatEmeraldPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("browse_btn_${pathway.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEn) "View Curriculum" else "📖 تصفح الفهرس",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // زر الدراسة مع المعلم الذكي بالتوجيه الفوري
                Button(
                    onClick = { onStartTutor(if (isEn) pathway.promptEn else pathway.promptAr) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MishkatEmeraldPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .testTag("tutor_btn_${pathway.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEn) "Start with AI Tutor" else "🎙️ دراسة مع المعلم",
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

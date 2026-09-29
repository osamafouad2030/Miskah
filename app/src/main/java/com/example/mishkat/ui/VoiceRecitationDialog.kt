package com.example.mishkat.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.mishkat.voice.MishkatVoiceRecognizer
import com.example.mishkat.voice.VoiceRecognitionState

/**
 * حوار تسجيل التلاوة الصوتية مع تصحيح وتحرير فوري بواسطة Gemini المعلم الذكي
 */
@Composable
fun VoiceRecitationDialog(
    onDismiss: () -> Unit,
    onSubmitRecitation: (recitationText: String, targetRuleOrReader: String) -> Unit
) {
    val context = LocalContext.current
    val voiceRecognizer = remember { MishkatVoiceRecognizer(context) }
    val voiceState by voiceRecognizer.state.collectAsState()
    val rmsDb by voiceRecognizer.rmsDb.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            voiceRecognizer.startListening()
        }
    }

    var selectedTarget by remember { mutableStateOf("حفص من طريق الشاطبية والطيبة") }
    var recognizedLiveText by remember { mutableStateOf("") }

    // تنظيف الموارد عند الإغلاق
    DisposableEffect(Unit) {
        onDispose {
            voiceRecognizer.destroy()
        }
    }

    // متابعة نتائج التعرف الصوتي
    LaunchedEffect(voiceState) {
        when (val current = voiceState) {
            is VoiceRecognitionState.PartialResult -> {
                recognizedLiveText = current.text
            }
            is VoiceRecognitionState.Success -> {
                recognizedLiveText = current.recognizedText
            }
            else -> {}
        }
    }

    // تأثير النبض البصري أثناء التسجيل
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Dialog(onDismissRequest = {
        voiceRecognizer.stopListening()
        onDismiss()
    }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("voice_recitation_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // شريط الترويسة
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مختبر التلاوة الصوتية والتحرير",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "اتلُ بالقراءة لتلقي تصحيح تحريري فوري من «مشكاة»",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = {
                        voiceRecognizer.stopListening()
                        onDismiss()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // اختيار الرواية أو الطريق المستهدف للمقارنة
                Text(
                    text = "حدد الرواية أو المسار التحريري للتلاوة:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                val targets = listOf(
                    "حفص بقصر المنفصل",
                    "حفص بتوسط المنفصل",
                    "شعبة عن عاصم",
                    "أوجه الإدغام والسكت"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    targets.take(2).forEach { target ->
                        FilterChip(
                            selected = selectedTarget == target,
                            onClick = { selectedTarget = target },
                            label = { Text(target, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    targets.drop(2).forEach { target ->
                        FilterChip(
                            selected = selectedTarget == target,
                            onClick = { selectedTarget = target },
                            label = { Text(target, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // زر التسجيل المركزي التفاعلي والنبض الصوتي
                val isListening = voiceState is VoiceRecognitionState.Listening ||
                        voiceState is VoiceRecognitionState.ReadyForSpeech ||
                        voiceState is VoiceRecognitionState.PartialResult

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(100.dp)
                ) {
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                        )
                    }

                    Surface(
                        onClick = {
                            if (!hasAudioPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                if (isListening) {
                                    voiceRecognizer.stopListening()
                                } else {
                                    recognizedLiveText = ""
                                    voiceRecognizer.startListening()
                                }
                            }
                        },
                        shape = CircleShape,
                        color = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("voice_recitation_mic_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isListening) "إيقاف التسجيل" else "بدء التلاوة",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // حالة التسجيل الحالية
                val statusText = when (voiceState) {
                    is VoiceRecognitionState.Idle -> "اضغط على الميكروفون وابدأ التلاوة"
                    is VoiceRecognitionState.ReadyForSpeech -> "الميكروفون جاهز... تفضل بالترتيل"
                    is VoiceRecognitionState.Listening -> "مشكاة يستمع لتلاوتك الآن..."
                    is VoiceRecognitionState.PartialResult -> "جاري تمييز الكلمات..."
                    is VoiceRecognitionState.Success -> "تم التقاط التلاوة بنجاح!"
                    is VoiceRecognitionState.Error -> (voiceState as VoiceRecognitionState.Error).errorMessage
                }

                Text(
                    text = statusText,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (voiceState is VoiceRecognitionState.Error) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // بطاقة عرض النص الملتقط فورياً
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (recognizedLiveText.isNotBlank()) {
                            Text(
                                text = "«$recognizedLiveText»",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = "ستظهر تلاوتك القرآنية هنا مباشرة أثناء القراءة...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // أزرار التحكم السفلية: إرسال لمشكاة للتصحيح
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = {
                            voiceRecognizer.stopListening()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "إلغاء",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    Surface(
                        onClick = {
                            if (recognizedLiveText.isNotBlank()) {
                                voiceRecognizer.stopListening()
                                onSubmitRecitation(recognizedLiveText, selectedTarget)
                                onDismiss()
                            }
                        },
                        enabled = recognizedLiveText.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (recognizedLiveText.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .weight(1.8f)
                            .testTag("submit_voice_recitation_button")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                tint = if (recognizedLiveText.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تصحيح التحرير الآن",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (recognizedLiveText.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

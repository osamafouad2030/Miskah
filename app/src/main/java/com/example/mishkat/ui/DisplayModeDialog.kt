package com.example.mishkat.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mishkat.localization.MishkatStrings
import com.example.mishkat.model.DeviceLayoutType
import com.example.mishkat.model.DisplayModeSetting
import com.example.mishkat.ui.theme.MishkatEmeraldDark
import com.example.mishkat.ui.theme.MishkatEmeraldLight
import com.example.mishkat.ui.theme.MishkatEmeraldPrimary
import com.example.mishkat.ui.theme.MishkatGoldAccent
import com.example.mishkat.ui.theme.MishkatGoldContainer
import com.example.mishkat.ui.theme.MishkatOnGoldContainer
import com.example.mishkat.ui.theme.MishkatTextMuted

/**
 * نافذة حوار لاختيار نمط العرض (Mobile, Tablet, Desktop, أو التكيف التلقائي)
 */
@Composable
fun DisplayModeDialog(
    currentSetting: DisplayModeSetting,
    effectiveType: DeviceLayoutType,
    screenWidthDp: Int,
    isEn: Boolean,
    onSelectSetting: (DisplayModeSetting) -> Unit,
    onDismiss: () -> Unit
) {
    val layoutDirection = if (isEn) LayoutDirection.Ltr else LayoutDirection.Rtl

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .padding(12.dp)
                    .testTag("display_mode_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MishkatGoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null,
                                tint = MishkatOnGoldContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = MishkatStrings.displayModeSelectorTitle(isEn),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldDark
                            )
                            Text(
                                text = if (isEn) "Current viewport width: $screenWidthDp dp" else "عرض الشاشة الحالي: $screenWidthDp نقطة (dp)",
                                fontSize = 11.5.sp,
                                color = MishkatTextMuted
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = MishkatStrings.close(isEn),
                                tint = MishkatTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mode Options
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DisplayModeSetting.values().forEach { setting ->
                            val isSelected = currentSetting == setting
                            val isActiveLayout = when (setting) {
                                DisplayModeSetting.AUTO -> true
                                DisplayModeSetting.MOBILE -> effectiveType == DeviceLayoutType.MOBILE
                                DisplayModeSetting.TABLET -> effectiveType == DeviceLayoutType.TABLET
                                DisplayModeSetting.DESKTOP -> effectiveType == DeviceLayoutType.DESKTOP
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                },
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) MishkatEmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        onSelectSetting(setting)
                                        onDismiss()
                                    }
                                    .testTag("display_mode_option_${setting.name.lowercase()}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MishkatEmeraldPrimary else MaterialTheme.colorScheme.surface
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = setting.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = setting.localizedTitle(isEn),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MishkatEmeraldDark else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (setting == DisplayModeSetting.AUTO) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MishkatGoldContainer
                                                ) {
                                                    Text(
                                                        text = if (isEn) "Recommended" else "مُستحسن",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MishkatOnGoldContainer,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = setting.localizedDescription(isEn),
                                            fontSize = 11.sp,
                                            color = MishkatTextMuted,
                                            lineHeight = 15.sp
                                        )
                                    }

                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            onSelectSetting(setting)
                                            onDismiss()
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = MishkatEmeraldPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Informational note
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MishkatEmeraldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) {
                                    "Mobile (<600dp), Tablet (600-840dp), Desktop (≥840dp). Layout seamlessly adapts to foldables and resized windows."
                                } else {
                                    "الهاتف (<600 نقطة)، اللوحي (600-840 نقطة)، الحاسوب (≥840 نقطة). الواجهة تتكيف بمرونة فائقة مع الأجهزة القابلة للطي وتغيير حجم النوافذ."
                                },
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

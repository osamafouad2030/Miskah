package com.example.mishkat.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * إعدادات وضع العرض: تلقائي حسب مقاس الشاشة، أو محاكاة/تثبيت هاتف، لوحي، حاسوب
 */
enum class DisplayModeSetting(
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val icon: ImageVector
) {
    AUTO(
        titleAr = "تلقائي",
        titleEn = "Auto",
        descriptionAr = "يتكيف تلقائياً مع أبعاد النافذة الحالية",
        descriptionEn = "Automatically adapts to current window size",
        icon = Icons.Default.AutoMode
    ),
    MOBILE(
        titleAr = "هاتف",
        titleEn = "Mobile",
        descriptionAr = "عرض الهاتف المدمج والمثالي للأجهزة الذكية المحمولة",
        descriptionEn = "Compact mobile layout optimized for handheld devices",
        icon = Icons.Default.PhoneAndroid
    ),
    TABLET(
        titleAr = "لوحي",
        titleEn = "Tablet",
        descriptionAr = "عرض الأجهزة اللوحية والقابلة للطي بتنسيق متوازن ومريح",
        descriptionEn = "Tablet & foldable layout with balanced medium spacing",
        icon = Icons.Default.TabletAndroid
    ),
    DESKTOP(
        titleAr = "حاسوب",
        titleEn = "Desktop",
        descriptionAr = "عرض الشاشات الواسعة مع شريط تنقل جانبي ومساحة عمل متكاملة",
        descriptionEn = "Desktop & wide screen layout with side navigation rail & split workspace",
        icon = Icons.Default.DesktopWindows
    );

    fun localizedTitle(isEnglish: Boolean): String = if (isEnglish) titleEn else titleAr
    fun localizedDescription(isEnglish: Boolean): String = if (isEnglish) descriptionEn else descriptionAr
}

/**
 * فئة التخطيط الفعالة (الحجم الفعلي الفعال)
 */
enum class DeviceLayoutType {
    MOBILE,
    TABLET,
    DESKTOP;

    val isDesktop: Boolean get() = this == DESKTOP
    val isTablet: Boolean get() = this == TABLET
    val isMobile: Boolean get() = this == MOBILE
}

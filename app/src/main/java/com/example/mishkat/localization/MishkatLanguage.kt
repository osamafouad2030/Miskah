package com.example.mishkat.localization

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * اللغات المدعومة في تطبيق "مشكاة"
 */
enum class MishkatLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val isRtl: Boolean
) {
    ARABIC(
        code = "ar",
        nativeName = "العربية",
        englishName = "Arabic",
        isRtl = true
    ),
    ENGLISH(
        code = "en",
        nativeName = "English",
        englishName = "English",
        isRtl = false
    );

    val opposite: MishkatLanguage
        get() = if (this == ARABIC) ENGLISH else ARABIC
}

/**
 * مدير لغة التطبيق وحفظ تفضيل الطالب
 */
object MishkatLanguageManager {
    private const val PREFS_NAME = "mishkat_app_preferences"
    private const val KEY_LANGUAGE = "app_selected_language"

    private val _currentLanguage = MutableStateFlow(MishkatLanguage.ARABIC)
    val currentLanguage: StateFlow<MishkatLanguage> = _currentLanguage.asStateFlow()

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedCode = prefs.getString(KEY_LANGUAGE, MishkatLanguage.ARABIC.code) ?: MishkatLanguage.ARABIC.code
        val lang = if (savedCode == MishkatLanguage.ENGLISH.code) MishkatLanguage.ENGLISH else MishkatLanguage.ARABIC
        _currentLanguage.value = lang
    }

    fun setLanguage(context: Context, language: MishkatLanguage) {
        _currentLanguage.value = language
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language.code)
            .apply()
    }

    fun toggleLanguage(context: Context): MishkatLanguage {
        val next = _currentLanguage.value.opposite
        setLanguage(context, next)
        return next
    }
}

val LocalMishkatLanguage = compositionLocalOf { MishkatLanguage.ARABIC }

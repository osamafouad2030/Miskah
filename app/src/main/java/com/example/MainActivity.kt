package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.mishkat.localization.LocalMishkatLanguage
import com.example.mishkat.localization.MishkatLanguageManager
import com.example.mishkat.ui.MishkatTutorScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    MishkatLanguageManager.initialize(this)
    setContent {
      val currentLang by MishkatLanguageManager.currentLanguage.collectAsState()
      val layoutDirection = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

      CompositionLocalProvider(
        LocalMishkatLanguage provides currentLang,
        LocalLayoutDirection provides layoutDirection
      ) {
        MyApplicationTheme {
          MishkatTutorScreen()
        }
      }
    }
  }
}

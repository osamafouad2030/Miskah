package com.example.mishkat.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * حالة جلسة التلاوة والتعرف الصوتي
 */
sealed interface VoiceRecognitionState {
    object Idle : VoiceRecognitionState
    object ReadyForSpeech : VoiceRecognitionState
    object Listening : VoiceRecognitionState
    data class PartialResult(val text: String) : VoiceRecognitionState
    data class Success(val recognizedText: String) : VoiceRecognitionState
    data class Error(val errorMessage: String) : VoiceRecognitionState
}

/**
 * مدير التعرف الصوتي لتلاوة القرآن الكريم وتحريرات القراءات
 * يستعمل Android SpeechRecognizer مع دعم كامل للغة العربية الفصحى.
 */
class MishkatVoiceRecognizer(
    private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null

    private val _state = MutableStateFlow<VoiceRecognitionState>(VoiceRecognitionState.Idle)
    val state: StateFlow<VoiceRecognitionState> = _state.asStateFlow()

    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()

    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(language: String = "ar-SA") {
        destroy()

        if (!isRecognitionAvailable()) {
            _state.value = VoiceRecognitionState.Error("خدمة التعرف على الصوت غير مدعومة على هذا الجهاز أو محاكي النظام")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _state.value = VoiceRecognitionState.ReadyForSpeech
                    }

                    override fun onBeginningOfSpeech() {
                        _state.value = VoiceRecognitionState.Listening
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        _rmsDb.value = rmsdB
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        // انتظار نتائج المعالجة
                    }

                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "خطأ في تسجيل الصوت، يرجى التحقق من الميكروفون"
                            SpeechRecognizer.ERROR_CLIENT -> "خطأ في الاتصال بنظام الصوت"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "لم يتم منح إذن استخدام الميكروفون"
                            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "خطأ في الشبكة أثناء التعرف على الصوت"
                            SpeechRecognizer.ERROR_NO_MATCH -> "لم يتم التقاط تلاوة واضحة، حاول مرة أخرى بهدوء وترتيل"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "محرك التعرف على الصوت مشغول حالياً"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "انتهى الوقت دون التقاط صوت التلاوة"
                            else -> "حدث خطأ أثناء الاستماع للتلاوة (رمز: $error)"
                        }
                        _state.value = VoiceRecognitionState.Error(message)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val recognizedText = matches?.firstOrNull()?.trim().orEmpty()
                        if (recognizedText.isNotBlank()) {
                            _state.value = VoiceRecognitionState.Success(recognizedText)
                        } else {
                            _state.value = VoiceRecognitionState.Error("لم يتم تمييز التلاوة، يُرجى التكرار بصوت أوضح")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val partialMatches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val partial = partialMatches?.firstOrNull()?.trim().orEmpty()
                        if (partial.isNotBlank()) {
                            _state.value = VoiceRecognitionState.PartialResult(partial)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "اتلُ الآية الكريمة أو المسألة التحريرية...")
            }

            speechRecognizer?.startListening(intent)
            _state.value = VoiceRecognitionState.Listening
        } catch (e: Exception) {
            _state.value = VoiceRecognitionState.Error("تعذر بدء الاستماع: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
    }

    fun reset() {
        destroy()
        _state.value = VoiceRecognitionState.Idle
        _rmsDb.value = 0f
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
    }
}

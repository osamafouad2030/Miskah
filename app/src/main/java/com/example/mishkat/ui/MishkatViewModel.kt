package com.example.mishkat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mishkat.guardrails.MishkatGuardrails
import com.example.mishkat.localization.MishkatLanguage
import com.example.mishkat.localization.MishkatStrings
import com.example.mishkat.model.MessageSender
import com.example.mishkat.model.MishkatMessage
import com.example.mishkat.model.MishkatRequest
import com.example.mishkat.model.TutorExplanationMode
import com.example.mishkat.model.TutorLevel
import com.example.mishkat.service.MishkatTutorService
import com.example.mishkat.service.MishkatTutorServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MishkatUiState(
    val messages: List<MishkatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val tutorLevel: TutorLevel = TutorLevel.INTERMEDIATE,
    val explanationMode: TutorExplanationMode = TutorExplanationMode.DIRECT,
    val currentHintStep: Int = 0,
    val currentTopic: String = "طرق قصر المنفصل لحفص",
    val errorNotice: String? = null,
    val isRecordingVoice: Boolean = false,
    val voiceRmsDb: Float = 0f,
    val recognizedSpeechText: String? = null,
    val language: MishkatLanguage = MishkatLanguage.ARABIC
)

class MishkatViewModel(
    private val tutorService: MishkatTutorService = MishkatTutorServiceImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MishkatUiState())
    val uiState: StateFlow<MishkatUiState> = _uiState.asStateFlow()

    init {
        resetWelcomeMessage(MishkatLanguage.ARABIC)
    }

    private fun resetWelcomeMessage(lang: MishkatLanguage) {
        val isEn = lang == MishkatLanguage.ENGLISH
        val welcomeMessage = MishkatMessage(
            sender = MessageSender.MISHKAT,
            text = MishkatStrings.welcomeGreeting(isEn),
            citedReferences = listOf(MishkatStrings.welcomeReference(isEn))
        )
        _uiState.update { it.copy(messages = listOf(welcomeMessage), language = lang) }
    }

    fun setLanguage(lang: MishkatLanguage) {
        val current = _uiState.value
        val updatedMessages = if (current.messages.size == 1 && current.messages.first().sender == MessageSender.MISHKAT) {
            val isEn = lang == MishkatLanguage.ENGLISH
            listOf(
                MishkatMessage(
                    sender = MessageSender.MISHKAT,
                    text = MishkatStrings.welcomeGreeting(isEn),
                    citedReferences = listOf(MishkatStrings.welcomeReference(isEn))
                )
            )
        } else {
            current.messages
        }
        _uiState.update { it.copy(language = lang, messages = updatedMessages) }
    }

    fun setTutorLevel(level: TutorLevel) {
        _uiState.update { it.copy(tutorLevel = level) }
    }

    fun setExplanationMode(mode: TutorExplanationMode) {
        _uiState.update { it.copy(explanationMode = mode) }
    }

    fun sendMessage(queryText: String) {
        val trimmed = queryText.trim()
        if (trimmed.isEmpty()) return

        val userMessage = MishkatMessage(
            sender = MessageSender.STUDENT,
            text = trimmed
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isLoading = true,
                errorNotice = null
            )
        }

        viewModelScope.launch {
            try {
                val current = _uiState.value
                val request = MishkatRequest(
                    userQuery = trimmed,
                    conversationHistory = current.messages,
                    tutorLevel = current.tutorLevel,
                    explanationMode = current.explanationMode,
                    hintStep = current.currentHintStep,
                    language = current.language
                )

                val response = tutorService.askMishkat(request)

                val replyMessage = MishkatMessage(
                    sender = MessageSender.MISHKAT,
                    text = response.replyText,
                    citedReferences = response.citedReferences,
                    isOutOfScope = response.isOutOfScope,
                    hintStep = response.hintStep
                )

                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + replyMessage,
                        isLoading = false,
                        currentHintStep = 0
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorNotice = "تعذر إكمال الاستجابة: ${e.message}"
                    )
                }
            }
        }
    }

    fun requestNextHint(concept: String) {
        val nextStep = (_uiState.value.currentHintStep + 1).coerceIn(1, 3)
        _uiState.update { it.copy(currentHintStep = nextStep) }
        sendMessage("أريد التلميح رقم ($nextStep) لمسألة: $concept دون كشف الحل المباشر")
    }

    fun requestRevisionPlan() {
        val errors = listOf(
            "الجمع بين قصر المنفصل والسكت لحفص",
            "إثبات الغنة في اللام والراء مع القصر المطلق",
            "وجه الإدغام الناقص في ألم نخلقكم من طريق التيسير"
        )
        val userPrompt = "اقترح لي خطة مراجعة مخصصة مع دروس وتمارين لعلاج أخطائي في مواضع الامتناعات لحفص وشعبة"
        val userMsg = MishkatMessage(sender = MessageSender.STUDENT, text = userPrompt)

        _uiState.update { it.copy(messages = it.messages + userMsg, isLoading = true) }

        viewModelScope.launch {
            try {
                val response = tutorService.generateRevisionPlan(errors)
                val reply = MishkatMessage(
                    sender = MessageSender.MISHKAT,
                    text = response.replyText,
                    citedReferences = response.citedReferences
                )
                _uiState.update { it.copy(messages = it.messages + reply, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorNotice = e.message) }
            }
        }
    }

    fun requestPracticeExercises(topic: String) {
        val userPrompt = "أنشئ لي تمارين تدريبية متدرجة الصعوبة حول: $topic"
        val userMsg = MishkatMessage(sender = MessageSender.STUDENT, text = userPrompt)

        _uiState.update { it.copy(messages = it.messages + userMsg, isLoading = true) }

        viewModelScope.launch {
            try {
                val response = tutorService.generatePracticeExercises(topic, 3, _uiState.value.tutorLevel)
                val reply = MishkatMessage(
                    sender = MessageSender.MISHKAT,
                    text = response.replyText,
                    citedReferences = response.citedReferences
                )
                _uiState.update { it.copy(messages = it.messages + reply, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorNotice = e.message) }
            }
        }
    }

    fun testGuardrailOutOfScope() {
        sendMessage("ما هي توقعات الطقس غداً وما هي أفضل طريقة لبرمجة تطبيق بايثون؟")
    }

    fun setRecordingVoice(isRecording: Boolean, rmsDb: Float = 0f) {
        _uiState.update { it.copy(isRecordingVoice = isRecording, voiceRmsDb = rmsDb) }
    }

    fun setVoiceRecognizedText(text: String?) {
        _uiState.update { it.copy(recognizedSpeechText = text) }
    }

    /**
     * إرسال التلاوة الملتقطة صوتياً لمعلم الذكاء الاصطناعي "مشكاة" للتقييم والتصحيح الفوري
     */
    fun submitVoiceRecitation(recitationText: String, targetRuleOrReader: String? = "حفص أو شعبة من طريق الطيبة") {
        val trimmed = recitationText.trim()
        if (trimmed.isEmpty()) return

        val userMessage = MishkatMessage(
            sender = MessageSender.STUDENT,
            text = "🎙️ [تلاوة شفوية]: «$trimmed»",
            isVoiceRecitation = true
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isLoading = true,
                isRecordingVoice = false,
                recognizedSpeechText = null,
                errorNotice = null
            )
        }

        viewModelScope.launch {
            try {
                val current = _uiState.value
                val response = tutorService.evaluateRecitation(
                    recitationText = trimmed,
                    targetReaderOrRule = targetRuleOrReader,
                    level = current.tutorLevel
                )

                val replyMessage = MishkatMessage(
                    sender = MessageSender.MISHKAT,
                    text = response.replyText,
                    citedReferences = response.citedReferences,
                    isOutOfScope = response.isOutOfScope,
                    isRecitationCorrection = true
                )

                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + replyMessage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorNotice = "تعذر تصحيح التلاوة: ${e.message}"
                    )
                }
            }
        }
    }

    fun sendSystemNotice(noticeText: String) {
        val noticeMessage = MishkatMessage(
            sender = MessageSender.MISHKAT,
            text = "⏰ $noticeText\nسأقوم بتذكيرك بإذن الله لمراجعة وردك من كتاب «الروض الناضر» بانتظام لضمان تثبيت الرواية وأوجه التحرير بدقة.",
            citedReferences = listOf("نظام التنبيهات والورد اليومي لأكاديمية الشيخة سماح البنداري")
        )
        _uiState.update { state ->
            state.copy(messages = state.messages + noticeMessage)
        }
    }
}

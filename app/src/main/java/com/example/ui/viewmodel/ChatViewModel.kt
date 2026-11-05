package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.GeminiRepository
import com.example.domain.models.AgentDataDefaults
import com.example.domain.models.AgentInfo
import com.example.domain.models.ProductServiceItem
import com.example.domain.prayer.CityLocation
import com.example.domain.prayer.PrayerTimes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiRepo = GeminiRepository()

    // General AI Chat on Home Tab
    private val _homeChatMessages = MutableStateFlow(
        listOf(
            UiChatMessage(
                isUser = false,
                text = "أهلاً 👋\nأنا مساعدك الذكي، كيف أساعدك اليوم؟"
            )
        )
    )
    val homeChatMessages: StateFlow<List<UiChatMessage>> = _homeChatMessages.asStateFlow()

    private val _isHomeGenerating = MutableStateFlow(false)
    val isHomeGenerating: StateFlow<Boolean> = _isHomeGenerating.asStateFlow()

    private val _isVoiceActive = MutableStateFlow(false)
    val isVoiceActive: StateFlow<Boolean> = _isVoiceActive.asStateFlow()

    // Dedicated Agent Chat
    private val _agentChatMessages = MutableStateFlow<Map<String, List<UiChatMessage>>>(emptyMap())
    val agentChatMessages: StateFlow<Map<String, List<UiChatMessage>>> = _agentChatMessages.asStateFlow()

    private val _isAgentGenerating = MutableStateFlow(false)
    val isAgentGenerating: StateFlow<Boolean> = _isAgentGenerating.asStateFlow()

    // Document & Product Generator State
    private val _generatedProductDoc = MutableStateFlow<String?>(null)
    val generatedProductDoc: StateFlow<String?> = _generatedProductDoc.asStateFlow()

    private val _isProductGenerating = MutableStateFlow(false)
    val isProductGenerating: StateFlow<Boolean> = _isProductGenerating.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    private var lastFailedPrompt: Pair<String, PrayerTimes?>? = null

    fun clearError() {
        _chatError.value = null
    }

    fun retryLastPrompt() {
        val failed = lastFailedPrompt
        if (failed != null) {
            _chatError.value = null
            sendHomePrompt(failed.first, failed.second)
        }
    }

    fun sendHomePrompt(prompt: String, currentPrayerTimes: PrayerTimes? = null) {
        if (prompt.isBlank()) return
        val userMsg = UiChatMessage(isUser = true, text = prompt)
        _homeChatMessages.value = _homeChatMessages.value + userMsg
        _isHomeGenerating.value = true
        _chatError.value = null

        viewModelScope.launch {
            try {
                val prayerInfo = if (currentPrayerTimes != null) {
                    "الوقت الحالي بالرياض. القادم: ${currentPrayerTimes.nextPrayerName} الساعة ${currentPrayerTimes.nextPrayerTime}"
                } else ""
                val response = geminiRepo.generateAgentResponse(
                    agentId = "secretary",
                    systemPrompt = "أنت مساعد 'Fahad Ai' الشخصي المتكامل في السعودية. تجمع بين الذكاء التنفيذي والتنظيم ومراعاة العبادات والتقويم.",
                    userPrompt = prompt,
                    contextInfo = prayerInfo
                )
                _homeChatMessages.value = _homeChatMessages.value + UiChatMessage(isUser = false, text = response)
            } catch (e: Exception) {
                lastFailedPrompt = Pair(prompt, currentPrayerTimes)
                _chatError.value = "تعذر الاتصال بـ Gemini API. يرجى إعادة المحاولة."
            } finally {
                _isHomeGenerating.value = false
            }
        }
    }

    fun toggleVoiceChat(currentPrayerTimes: PrayerTimes? = null) {
        _isVoiceActive.value = !_isVoiceActive.value
        if (_isVoiceActive.value) {
            viewModelScope.launch {
                delay(2500)
                if (_isVoiceActive.value) {
                    val prompt = "لخص لي جدول اليوم وتأكد من مواعيد الصلاة"
                    sendHomePrompt(prompt, currentPrayerTimes)
                    _isVoiceActive.value = false
                }
            }
        }
    }

    fun sendAgentPrompt(agent: AgentInfo, prompt: String, city: CityLocation? = null, currentPrayerTimes: PrayerTimes? = null) {
        if (prompt.isBlank()) return
        val currentList = _agentChatMessages.value[agent.id] ?: listOf(
            UiChatMessage(isUser = false, text = "أهلاً بك! أنا ${agent.name} (${agent.roleTitle}). كيف أخدمك اليوم؟")
        )
        val userMsg = UiChatMessage(isUser = true, text = prompt)
        val updated = currentList + userMsg
        _agentChatMessages.value = _agentChatMessages.value + (agent.id to updated)
        _isAgentGenerating.value = true

        viewModelScope.launch {
            val prayerInfo = if (city != null && currentPrayerTimes != null) {
                "المدينة: ${city.nameAr}، الصلاة القادمة: ${currentPrayerTimes.nextPrayerName} ${currentPrayerTimes.nextPrayerTime}"
            } else ""
            val response = geminiRepo.generateAgentResponse(
                agentId = agent.id,
                systemPrompt = agent.systemPrompt,
                userPrompt = prompt,
                contextInfo = prayerInfo
            )
            val withAgentResponse = updated + UiChatMessage(isUser = false, text = response)
            _agentChatMessages.value = _agentChatMessages.value + (agent.id to withAgentResponse)
            _isAgentGenerating.value = false
        }
    }

    fun getMessagesForAgent(agentId: String): List<UiChatMessage> {
        val list = _agentChatMessages.value[agentId]
        if (list != null) return list
        val agent = AgentDataDefaults.AGENTS.firstOrNull { it.id == agentId }
        val initial = listOf(
            UiChatMessage(
                isUser = false,
                text = "مرحباً بك! أنا ${agent?.name ?: "الوكيل الذكي"} (${agent?.roleTitle ?: ""}).\n" +
                        "أنا جاهز لمباشرة مهامك المتخصصة وتقديم الدعم والتحليل الفوري وفق أنظمة المملكة وسوق العمل."
            )
        )
        _agentChatMessages.value = _agentChatMessages.value + (agentId to initial)
        return initial
    }

    fun generateProduct(product: ProductServiceItem, inputs: Map<String, String>) {
        _isProductGenerating.value = true
        _generatedProductDoc.value = null

        val prompt = buildString {
            append("طلب إنشاء وثيقة ذكية: ").append(product.title).append("\n")
            append("الوصف: ").append(product.description).append("\n")
            append("المدخلات والمواصفات:\n")
            inputs.forEach { (key, value) ->
                append("• ").append(key).append(": ").append(value).append("\n")
            }
        }

        viewModelScope.launch {
            val response = geminiRepo.generateAgentResponse(
                agentId = "business",
                systemPrompt = "أنت صانع وثائق ورخص ونماذج أعمال ذكية في السعودية لتطبيق 'Fahad Ai'. ولد وثيقة متكاملة، احترافية، منسقة، صالحة للاستخدام المباشر والتصدير باللغة العربية الفصحى.",
                userPrompt = prompt
            )
            _generatedProductDoc.value = response
            _isProductGenerating.value = false
        }
    }

    fun clearGeneratedProduct() {
        _generatedProductDoc.value = null
    }

    fun cleanup() {
        _isVoiceActive.value = false
        _isHomeGenerating.value = false
        _isAgentGenerating.value = false
        _isProductGenerating.value = false
    }

    override fun onCleared() {
        super.onCleared()
        cleanup()
    }
}

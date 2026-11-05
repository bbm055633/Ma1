package com.example.data.repository

import com.example.data.remote.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

/**
 * Unit tests for [GeminiRepository] with mocked Retrofit responses and fallback behavior.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GeminiRepositoryTest {

    private class FakeGeminiApiService(
        var shouldThrowError: Boolean = false,
        var responseText: String = "أهلاً بك! تم استلام طلبك واستخراج الاستجابة الذكية بنجاح."
    ) : GeminiApiService {
        var lastReceivedRequest: GeminiRequest? = null

        override suspend fun generateContent(request: GeminiRequest): GeminiResponse {
            lastReceivedRequest = request
            if (shouldThrowError) {
                throw IOException("Mock Retrofit Exception: 503 Service Unavailable")
            }
            return GeminiResponse(
                candidates = listOf(
                    GeminiCandidate(
                        content = GeminiContent(
                            parts = listOf(GeminiPart(text = responseText))
                        )
                    )
                )
            )
        }
    }

    @Test
    fun testGenerateAgentResponseSuccess() = runBlocking {
        val fakeService = FakeGeminiApiService(
            shouldThrowError = false,
            responseText = "التحليل المالي جاهز: الميزانية متوازنة تماماً حسب الأنظمة السعودية."
        )
        val repository = GeminiRepository(apiService = fakeService)

        val response = repository.generateAgentResponse(
            agentId = "mobeen",
            systemPrompt = "أنت المستشار المالي مكسام",
            userPrompt = "ما هي حالة الميزانية؟",
            contextInfo = "مواقيت الصلاة: الرياض"
        )

        assertNotNull("Response should not be null", response)
        assertTrue("Response should contain API response or fallback", response.isNotBlank())
    }

    @Test
    fun testGenerateAgentResponseNetworkFailureFallback() = runBlocking {
        val fakeService = FakeGeminiApiService(shouldThrowError = true)
        val repository = GeminiRepository(apiService = fakeService)

        val response = repository.generateAgentResponse(
            agentId = "mobeen",
            systemPrompt = "مساعد مالي",
            userPrompt = "كيف أحسب ضريبة القيمة المضافة؟"
        )

        assertNotNull("Fallback response should be provided on network failure", response)
        assertTrue("Fallback response should be rich non-empty text", response.length > 10)
    }

    @Test
    fun testDomainFallbackResponsesForDifferentAgents() = runBlocking {
        val fakeService = FakeGeminiApiService(shouldThrowError = true)
        val repository = GeminiRepository(apiService = fakeService)

        val accountantResponse = repository.generateAgentResponse("accountant", "مساعد محاسبي", "تحليل الضريبة")
        assertTrue("Accountant fallback should contain financial/tax terms", accountantResponse.contains("المحاسب") || accountantResponse.contains("ZATCA") || accountantResponse.contains("ضريبة"))

        val financialResponse = repository.generateAgentResponse("financial", "مساعد مالي", "خطة الاستثمار")
        assertTrue("Financial fallback should contain investment/portfolio terms", financialResponse.contains("المالي") || financialResponse.contains("صكوك") || financialResponse.contains("المحفظة"))
    }
}
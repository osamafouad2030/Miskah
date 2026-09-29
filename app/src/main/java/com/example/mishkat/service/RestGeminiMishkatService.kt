package com.example.mishkat.service

import android.util.Log
import com.example.BuildConfig
import com.example.mishkat.guardrails.MishkatGuardrails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * خدمة استدعاء Gemini API عبر واجهة REST المباشرة
 * تعمل كبديل عالي الموثوقية (Fallback Engine) ومسرّع للاستجابة.
 */
class RestGeminiMishkatService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
) {

    companion object {
        private const val TAG = "RestGeminiMishkat"
        private const val MODEL_NAME = "gemini-3.5-flash"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    suspend fun generateContent(
        augmentedPrompt: String,
        systemPrompt: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                try {
                    Log.w(TAG, "GEMINI_API_KEY is not set or default placeholder.")
                } catch (_: Throwable) {}
                return@withContext Result.failure(IllegalStateException("GEMINI_API_KEY is not configured in Secrets panel."))
            }

            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                // تعليمات النظام
                put("system_instruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    })
                })

                // المحتوى
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", augmentedPrompt))
                        })
                    })
                })

                // إعدادات التوليد
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.1)
                    put("topP", 0.95)
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                try {
                    Log.e(TAG, "HTTP Error ${response.code}: $responseBody")
                } catch (_: Throwable) {}
                return@withContext Result.failure(Exception("Gemini API HTTP ${response.code}: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("No candidates returned from Gemini API"))
            }

            val content = candidates.getJSONObject(0).optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

            Result.success(MishkatGuardrails.sanitizeOutput(rawText))
        } catch (e: Exception) {
            Log.e(TAG, "Failed in RestGeminiMishkatService", e)
            Result.failure(e)
        }
    }
}

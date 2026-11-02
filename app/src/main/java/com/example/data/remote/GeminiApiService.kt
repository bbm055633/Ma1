package com.example.data.remote

import sa.com.fahada.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Body request: GeminiRequest
    ): GeminiResponse

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/"

        fun create(apiKeyProvider: () -> String = { BuildConfig.GEMINI_API_KEY }): GeminiApiService {
            // الإصلاح الثاني: إرسال مفتاح API عبر Header باسم x-goog-api-key بدلاً من URL
            val apiKeyInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val key = apiKeyProvider()
                val newRequest = if (key.isNotBlank()) {
                    originalRequest.newBuilder()
                        .header("x-goog-api-key", key)
                        .build()
                } else {
                    originalRequest
                }
                chain.proceed(newRequest)
            }

            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(apiKeyInterceptor)

            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }
            clientBuilder.addInterceptor(logging)

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(GeminiApiService::class.java)
        }
    }
}

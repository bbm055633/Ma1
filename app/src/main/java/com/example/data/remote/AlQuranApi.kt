package com.example.data.remote

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

@JsonClass(generateAdapter = true)
data class QuranSurahDto(
    val number: Int,
    val name: String,
    val englishName: String
)

@JsonClass(generateAdapter = true)
data class QuranAyahDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int,
    val page: Int,
    val surah: QuranSurahDto
)

@JsonClass(generateAdapter = true)
data class QuranPageData(
    val number: Int,
    val ayahs: List<QuranAyahDto>,
    val juz: Int
)

@JsonClass(generateAdapter = true)
data class QuranPageResponse(
    val code: Int,
    val status: String,
    val data: QuranPageData
)

interface AlQuranApi {
    @GET("v1/page/{pageNumber}/quran-uthmani")
    suspend fun getQuranPage(@Path("pageNumber") pageNumber: Int): QuranPageResponse

    companion object {
        private const val BASE_URL = "https://api.alquran.cloud/"
        val instance: AlQuranApi by lazy {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(AlQuranApi::class.java)
        }
    }
}

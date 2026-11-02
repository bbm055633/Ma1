package com.example.data.repository

import android.content.Context
import com.example.data.remote.QuranAyahDto
import com.example.data.remote.QuranPageData
import com.example.data.remote.QuranSurahDto
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object LocalQuranRepository {

    private val cachedPages = mutableMapOf<Int, QuranPageData>()
    private var isInitialized = false

    @Synchronized
    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("quran", "raw", context.packageName)
            )
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }
            val rootObj = JSONObject(jsonString)
            val dataObj = rootObj.getJSONObject("data")
            val surahsArr = dataObj.getJSONArray("surahs")

            val pagesAyahList = mutableMapOf<Int, MutableList<QuranAyahDto>>()

            for (i in 0 until surahsArr.length()) {
                val surahObj = surahsArr.getJSONObject(i)
                val surahNum = surahObj.getInt("number")
                val surahName = surahObj.getString("name")
                val surahEnglishName = surahObj.getString("englishName")
                val surahDto = QuranSurahDto(surahNum, surahName, surahEnglishName)

                val ayahsArr = surahObj.getJSONArray("ayahs")
                for (j in 0 until ayahsArr.length()) {
                    val ayahObj = ayahsArr.getJSONObject(j)
                    val text = ayahObj.getString("text")
                    val number = ayahObj.getInt("number")
                    val numberInSurah = ayahObj.getInt("numberInSurah")
                    val juz = ayahObj.getInt("juz")
                    val page = ayahObj.getInt("page")

                    val ayahDto = QuranAyahDto(
                        number = number,
                        text = text,
                        numberInSurah = numberInSurah,
                        juz = juz,
                        page = page,
                        surah = surahDto
                    )

                    if (!pagesAyahList.containsKey(page)) {
                        pagesAyahList[page] = mutableListOf()
                    }
                    pagesAyahList[page]?.add(ayahDto)
                }
            }

            for ((page, ayahs) in pagesAyahList) {
                val juz = ayahs.firstOrNull()?.juz ?: 1
                cachedPages[page] = QuranPageData(page, ayahs, juz)
            }
            isInitialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getQuranPage(context: Context, pageNumber: Int): QuranPageData {
        if (!isInitialized) {
            initialize(context)
        }
        return cachedPages[pageNumber] ?: QuranPageData(
            number = pageNumber,
            ayahs = emptyList(),
            juz = ((pageNumber - 1) / 20) + 1
        )
    }

    fun searchQuranOffline(context: Context, query: String): List<QuranPageData> {
        if (!isInitialized) {
            initialize(context)
        }
        if (query.isBlank()) return emptyList()
        val pageNum = query.toIntOrNull()
        if (pageNum != null && pageNum in 1..604) {
            val page = cachedPages[pageNum]
            return if (page != null) listOf(page) else emptyList()
        }

        val matchingPages = mutableSetOf<Int>()
        for ((page, pageData) in cachedPages) {
            for (ayah in pageData.ayahs) {
                if (ayah.text.contains(query, ignoreCase = true) || 
                    ayah.surah.name.contains(query, ignoreCase = true) ||
                    ayah.surah.englishName.contains(query, ignoreCase = true)) {
                    matchingPages.add(page)
                    break
                }
            }
        }
        return matchingPages.sorted().mapNotNull { cachedPages[it] }
    }
}

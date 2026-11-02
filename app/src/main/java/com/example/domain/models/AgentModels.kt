package com.example.domain.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class AgentInfo(
    val id: String,
    val name: String,
    val roleTitle: String,
    val description: String,
    val badge: String,
    val voiceName: String,
    val toneDescription: String,
    val accentColor: Long, // hex long for Color
    val suggestedPrompts: List<String>,
    val systemPrompt: String
)

data class ProductServiceItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val iconName: String,
    val fields: List<String>, // Field labels for user input
    val defaultPrompt: String
)

data class AdhkarItem(
    val id: Int,
    val text: String,
    val benefit: String,
    val targetCount: Int,
    var currentCount: Int = 0
)

data class QuranKhatmahState(
    val currentJuz: Int = 18,
    val currentHizb: Int = 36,
    val currentPage: Int = 360,
    val targetDays: Int = 30,
    val dailyPagesGoal: Int = 14,
    val pagesReadToday: Int = 14,
    val currentSurah: String = "سورة الفرقان",
    val currentAyah: Int = 21,
    val prayerPortions: Map<String, Int> = mapOf(
        "الفجر" to 3,
        "الظهر" to 3,
        "العصر" to 3,
        "المغرب" to 3,
        "العشاء" to 2
    ),
    val prayerPortionsCompleted: Map<String, Boolean> = mapOf(
        "الفجر" to true,
        "الظهر" to false,
        "العصر" to false,
        "المغرب" to false,
        "العشاء" to false
    )
)

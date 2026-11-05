package com.example.ui.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AppDesignStyle
import com.example.ui.theme.AppThemePalette

enum class SkyColorPreset(
    val titleAr: String,
    val titleEn: String,
    val hexColors: List<String>
) {
    DEFAULT("افتراضي (ليل نجدي)", "Default (Najd Night)", listOf("#0B1325", "#1C2D4B", "#0F192E")),
    AUTO("تلقائي حسب الوقت", "Auto", listOf("#0F172A", "#312E81", "#EA580C")),
    FOLLOW_DAY("حسب النهار/الليل", "Follow Day", listOf("#0F172A", "#1E293B", "#020617")),
    DUSK("غروب داكن", "Dusk", listOf("#1E1B4B", "#4C1D95", "#831843")),
    EMBER("جمر الغضا", "Ember", listOf("#350808", "#661414", "#8B1919")),
    TWILIGHT("غسوق عسيري", "Asir Twilight", listOf("#1A0B2E", "#3B1B54", "#231138")),
    OCEAN("أزرق المرجان", "Ocean", listOf("#082F49", "#0369A1", "#0EA5E9")),
    PINK_SUNSET("شفق وردي", "Pink Sunset", listOf("#581C87", "#9D174D", "#F43F5E")),
    LAVENDER("خزامى نجد", "Lavender", listOf("#312E81", "#4F46E5", "#818CF8")),
    DESERT_DAWN("فجر النفود", "Nafud Dawn", listOf("#2E150B", "#5C2C18", "#381B0F")),
    AURORA("شفق شمالي", "Northern Lights", listOf("#0B2E28", "#185C50", "#0F3831")),
    ROYAL_NAVY("أزرق ملكي", "Royal Navy", listOf("#081226", "#12264D", "#0A1833"))
}

enum class TerrainType(
    val titleAr: String,
    val titleEn: String
) {
    NONE("بدون تضاريس", "No Terrain"),
    HILLS("تلال وهضاب", "Hills"),
    PEAKS("قمم الجبال", "Peaks"),
    DUNES("كثبان رملية (نفود)", "Dunes"),
    FOREST("غابات ونخيل", "Forest"),
    SAND_DUNES("كثبان رملية", "Sand Dunes"),
    MOUNTAIN_RANGE("سلسلة جبال", "Mountain Range"),
    PALM_OASIS("واحة نخيل", "Palm Oasis"),
    MODERN_SKYLINE("أفق المعالم", "Modern Skyline")
}

@Immutable
data class StarryNightSettings(
    val isEnabled: Boolean = false,
    val skyColorPreset: SkyColorPreset = SkyColorPreset.DEFAULT,
    val terrainType: TerrainType = TerrainType.SAND_DUNES,
    val animationSpeedMultiplier: Float = 1.0f,
    val cardTransparency: Float = 0.85f,
    val showStars: Boolean = true,
    val showShootingStars: Boolean = true,
    val showMoon: Boolean = true,
    val showClouds: Boolean = true,
    val showLightning: Boolean = false,
    val showBirds: Boolean = true,
    val showFireflies: Boolean = true,
    val showSnow: Boolean = false,
    val showHorizonGlow: Boolean = true,
    val batterySaverMode: Boolean = false
)

data class AppThemeSettings(
    val isDarkMode: Boolean = false,
    val themePalette: AppThemePalette = AppThemePalette.SAUDI_EMERALD,
    val fontScale: Float = 1.0f,
    val designStyle: AppDesignStyle = AppDesignStyle.STANDARD,
    val densityScale: Float = 1.0f,
    val customWallpaperUri: String? = null,
    val wallpaperDimAlpha: Float = 0.65f
)

package com.example.ui.screens.worship

import com.example.domain.models.AdhkarItem
import com.example.domain.prayer.PrayerTimes
import com.example.ui.viewmodel.PrayerStreaks

/**
 * Data class bundling prayer tracking state and callbacks for FaithJourneyPrayerTrackingCard.
 */
data class PrayerTrackingData(
    val prayerTimes: PrayerTimes,
    val selectedDayIndex: Int = 0,
    val onDayChange: (Int) -> Unit = {},
    val isEnglish: Boolean = false,

    // Fajr
    val fajrAttendance: PrayerStatus = PrayerStatus.NONE,
    val onFajrAttendanceChange: (PrayerStatus) -> Unit = {},
    val fajrSunnahQabliyah: Boolean = false,
    val onToggleFajrSunnahQabliyah: () -> Unit = {},

    // Duha, Adhkar, Qiyam
    val isDuhaDone: Boolean = false,
    val onToggleDuha: () -> Unit = {},
    val isMorningAdhkarDone: Boolean = false,
    val onToggleMorningAdhkar: () -> Unit = {},
    val onOpenMorningAdhkarSheet: () -> Unit = {},
    val morningAdhkarList: List<AdhkarItem> = emptyList(),

    // Dhuhr
    val dhuhrAttendance: PrayerStatus = PrayerStatus.NONE,
    val onDhuhrAttendanceChange: (PrayerStatus) -> Unit = {},
    val dhuhrSunnahQabliyah: Boolean = false,
    val onToggleDhuhrSunnahQabliyah: () -> Unit = {},
    val dhuhrSunnahBadiyah: Boolean = false,
    val onToggleDhuhrSunnahBadiyah: () -> Unit = {},

    // Asr
    val asrAttendance: PrayerStatus = PrayerStatus.NONE,
    val onAsrAttendanceChange: (PrayerStatus) -> Unit = {},

    // Maghrib
    val maghribAttendance: PrayerStatus = PrayerStatus.NONE,
    val onMaghribAttendanceChange: (PrayerStatus) -> Unit = {},
    val maghribSunnahBadiyah: Boolean = false,
    val onToggleMaghribSunnahBadiyah: () -> Unit = {},

    // Isha
    val ishaAttendance: PrayerStatus = PrayerStatus.NONE,
    val onIshaAttendanceChange: (PrayerStatus) -> Unit = {},
    val ishaSunnahBadiyah: Boolean = false,
    val onToggleIshaSunnahBadiyah: () -> Unit = {},

    // Qiyam, Evening Adhkar, Fasting
    val isQiyamDone: Boolean = false,
    val onToggleQiyam: () -> Unit = {},
    val isEveningAdhkarDone: Boolean = false,
    val onToggleEveningAdhkar: () -> Unit = {},
    val onOpenEveningAdhkarSheet: () -> Unit = {},
    val isFastingDone: Boolean = false,
    val onToggleFasting: () -> Unit = {},

    val streaks: PrayerStreaks = PrayerStreaks()
)

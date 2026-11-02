package com.example.ui.viewmodel

import java.time.LocalDate

enum class PrayerStatus {
    NONE,
    IN_MOSQUE,
    AT_HOME,
    LATE
}

sealed interface PrayerUiEvent {
    data class SelectStatus(val prayerId: String, val status: PrayerStatus) : PrayerUiEvent
    data class ToggleSunnah(val prayerId: String, val isQabliyah: Boolean) : PrayerUiEvent
    data class ChangeDate(val targetDate: LocalDate) : PrayerUiEvent
    data object ToggleDuha : PrayerUiEvent
    data object ToggleMorningAdhkar : PrayerUiEvent
    data object ToggleEveningAdhkar : PrayerUiEvent
    data object ToggleQiyam : PrayerUiEvent
}

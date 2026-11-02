package com.example.ui.screens.worship

/**
 * Unified Prayer Status Enum
 * Represents prayer attendance state across all worship screens.
 */
enum class PrayerStatus {
    NONE,
    IN_MOSQUE,  // في المسجد / جماعة
    INDIVIDUAL, // المنفرد / في البيت
    LATE        // متأخر
}

// Backwards compatibility aliases for existing codebase
typealias AttendanceColumn = PrayerStatus
typealias PrayerAttendance = PrayerStatus

/**
 * Unified parser function converting string representation to PrayerStatus.
 */
fun parsePrayerStatus(value: String?): PrayerStatus {
    if (value == null) return PrayerStatus.NONE
    return when (value.uppercase()) {
        "IN_MOSQUE", "JAMAAH" -> PrayerStatus.IN_MOSQUE
        "AT_HOME", "INDIVIDUAL" -> PrayerStatus.INDIVIDUAL
        "LATE" -> PrayerStatus.LATE
        else -> PrayerStatus.NONE
    }
}

package com.example.ui.viewmodel

import com.example.data.local.PrayerEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class PrayerStreaks(
    val fajrQabliyah: Int = 0,
    val fajrMasjid: Int = 0,
    val fajrHome: Int = 0,
    val fajrLate: Int = 0,
    val fajrBadiyah: Int = 0,

    val dhuhrQabliyah: Int = 0,
    val dhuhrMasjid: Int = 0,
    val dhuhrHome: Int = 0,
    val dhuhrLate: Int = 0,
    val dhuhrBadiyah: Int = 0,

    val asrQabliyah: Int = 0,
    val asrMasjid: Int = 0,
    val asrHome: Int = 0,
    val asrLate: Int = 0,
    val asrBadiyah: Int = 0,

    val maghribQabliyah: Int = 0,
    val maghribMasjid: Int = 0,
    val maghribHome: Int = 0,
    val maghribLate: Int = 0,
    val maghribBadiyah: Int = 0,

    val ishaQabliyah: Int = 0,
    val ishaMasjid: Int = 0,
    val ishaHome: Int = 0,
    val ishaLate: Int = 0,
    val ishaBadiyah: Int = 0,

    val duha: Int = 0
)

fun calculateStreakForAction(
    currentDateStr: String,
    allPrayers: List<PrayerEntity>,
    predicate: (PrayerEntity) -> Boolean
): Int {
    if (allPrayers.isEmpty()) return 0
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val sorted = allPrayers.sortedByDescending { it.date }
    val prayerMap = sorted.associateBy { it.date }

    var streak = 0
    val cal = Calendar.getInstance()
    try {
        val parsedDate = dateFormat.parse(currentDateStr) ?: Date()
        cal.time = parsedDate
    } catch (e: Exception) {
        return 0
    }

    val todayKey = dateFormat.format(cal.time)
    val todayEntity = prayerMap[todayKey]
    val startCal = Calendar.getInstance()
    startCal.time = cal.time

    if (todayEntity != null && predicate(todayEntity)) {
        streak++
        startCal.add(Calendar.DAY_OF_YEAR, -1)
    } else {
        startCal.add(Calendar.DAY_OF_YEAR, -1)
    }

    while (true) {
        val key = dateFormat.format(startCal.time)
        val entity = prayerMap[key] ?: break
        if (predicate(entity)) {
            streak++
            startCal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            break
        }
    }
    return streak
}

package com.example.domain.prayer

import java.util.Calendar
import java.util.Locale
import kotlin.math.*

data class CityLocation(
    val nameAr: String,
    val nameEn: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: Double = 3.0 // AST (Arabia Standard Time = UTC+3)
)

data class PrayerTimes(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val minutesUntilNext: Int,
    val isDndActive: Boolean // DND active 5 mins before Iqamah until 25 mins after prayer
)

data class HijriDateInfo(
    val day: Int,
    val monthNameAr: String,
    val year: Int,
    val isWhiteDay: Boolean,
    val isMondayOrThursday: Boolean,
    val fastingTitle: String?
)

data class PrayerScheduleItem(
    val name: String,
    val formattedTime: String,
    val prayerTimeMillis: Long,
    val alert5MinBeforeMillis: Long,
    val silentModeStartMillis: Long,
    val silentModeEndMillis: Long
)

object PrayerTimeCalculator {

    val SAUDI_CITIES = listOf(
        CityLocation("الرياض", "Riyadh", 24.7136, 46.6753),
        CityLocation("مكة المكرمة", "Makkah", 21.3891, 39.8579),
        CityLocation("المدينة المنورة", "Madinah", 24.5247, 39.5692),
        CityLocation("جدة", "Jeddah", 21.5433, 39.1728),
        CityLocation("الدمام", "Dammam", 26.4207, 50.0888),
        CityLocation("أبها", "Abha", 18.2164, 42.5053),
        CityLocation("تبوك", "Tabuk", 28.3835, 36.5662)
    )

    fun calculatePrayerTimes(city: CityLocation = SAUDI_CITIES[0], calendar: Calendar = Calendar.getInstance()): PrayerTimes {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val lat = city.latitude
        val lng = city.longitude
        val timezone = city.timezone

        // Astronomical calculations
        val d = dayOfYear.toDouble()
        val b = 2.0 * Math.PI * (d - 81) / 365.0
        val eot = 9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b) // Equation of time in minutes
        val declination = Math.toRadians(23.45 * sin(Math.toRadians((360.0 / 365.0) * (d - 81)))) // Solar declination

        // Solar noon (Dhuhr)
        val timeCorrection = (timezone * 15.0 - lng) * 4.0 - eot
        val solarNoonMinutes = 12 * 60 + timeCorrection + 4.0 // +4 min after true solar noon for safety

        // Hour angle helper
        fun hourAngle(altitudeDeg: Double): Double {
            val altRad = Math.toRadians(altitudeDeg)
            val latRad = Math.toRadians(lat)
            val cosHA = (sin(altRad) - sin(latRad) * sin(declination)) / (cos(latRad) * cos(declination))
            val clamped = cosHA.coerceIn(-1.0, 1.0)
            return Math.toDegrees(acos(clamped)) * 4.0 // minutes
        }

        // Umm Al-Qura standard: Fajr is 18.5 degrees below horizon
        val fajrHA = hourAngle(-18.5)
        val sunriseHA = hourAngle(-0.833)

        // Asr: shadow length = object + noon shadow (Shafi'i/Hanbali method used in KSA)
        val noonAngle = abs(Math.toRadians(lat) - declination)
        val asrAltitude = Math.toDegrees(atan(1.0 / (1.0 + tan(noonAngle))))
        val asrHA = hourAngle(asrAltitude)

        val fajrMin = (solarNoonMinutes - fajrHA).toInt()
        val sunriseMin = (solarNoonMinutes - sunriseHA).toInt()
        val dhuhrMin = solarNoonMinutes.toInt()
        val asrMin = (solarNoonMinutes + asrHA).toInt()
        val maghribMin = (solarNoonMinutes + sunriseHA).toInt()
        // Umm Al Qura standard: Isha is 90 minutes after Maghrib
        val ishaMin = maghribMin + 90

        fun formatTime(totalMinutes: Int): String {
            val normalized = (totalMinutes % (24 * 60) + (24 * 60)) % (24 * 60)
            val h24 = normalized / 60
            val m = normalized % 60
            val h12 = if (h24 % 12 == 0) 12 else h24 % 12
            val period = if (h24 < 12) "ص" else "م"
            return String.format(Locale.getDefault(), "%02d:%02d %s", h12, m, period)
        }

        val fajrStr = formatTime(fajrMin)
        val sunriseStr = formatTime(sunriseMin)
        val dhuhrStr = formatTime(dhuhrMin)
        val asrStr = formatTime(asrMin)
        val maghribStr = formatTime(maghribMin)
        val ishaStr = formatTime(ishaMin)

        val currentMin = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        val prayerSchedule = listOf(
            Triple("الفجر", fajrMin, fajrStr),
            Triple("الشروق", sunriseMin, sunriseStr),
            Triple("الظهر", dhuhrMin, dhuhrStr),
            Triple("العصر", asrMin, asrStr),
            Triple("المغرب", maghribMin, maghribStr),
            Triple("العشاء", ishaMin, ishaStr)
        )

        var nextName = "الفجر"
        var nextTime = fajrStr
        var minutesUntil = 0
        var foundNext = false

        for (p in prayerSchedule) {
            if (p.second > currentMin) {
                nextName = p.first
                nextTime = p.third
                minutesUntil = p.second - currentMin
                foundNext = true
                break
            }
        }
        if (!foundNext) {
            // Next is Fajr tomorrow
            nextName = "الفجر"
            nextTime = fajrStr
            minutesUntil = (24 * 60 - currentMin) + fajrMin
        }

        // DND active 5 minutes before Iqamah (Iqamah usually 15-20 min after Azan) and during prayer (~25 mins after Azan)
        var isDnd = false
        val mainPrayers = listOf(fajrMin, dhuhrMin, asrMin, maghribMin, ishaMin)
        for (pMin in mainPrayers) {
            // from 10 mins after azan (approx 5 mins before iqamah) to 30 mins after azan
            if (currentMin in (pMin + 10)..(pMin + 30)) {
                isDnd = true
                break
            }
        }

        return PrayerTimes(
            fajr = fajrStr,
            sunrise = sunriseStr,
            dhuhr = dhuhrStr,
            asr = asrStr,
            maghrib = maghribStr,
            isha = ishaStr,
            nextPrayerName = nextName,
            nextPrayerTime = nextTime,
            minutesUntilNext = minutesUntil,
            isDndActive = isDnd
        )
    }

    fun getEstimatedHijriDate(calendar: Calendar = Calendar.getInstance()): HijriDateInfo {
        // Approximate calculation for Umm Al Qura matching current 2026/1448 H
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isMondayOrThursday = (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.THURSDAY)

        // Estimated Hijri day calculation
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hijriDay = ((dayOfMonth + 11) % 30) + 1
        val hijriMonths = listOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الثاني", "جمادى الأولى", "جمادى الآخرة",
            "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        val monthIdx = (calendar.get(Calendar.MONTH) + 2) % 12
        val hijriMonthName = hijriMonths[monthIdx]
        val hijriYear = 1448

        val isWhiteDay = hijriDay in 13..15
        val fastingTitle = when {
            isWhiteDay -> "صيام الأيام البيض (اليوم $hijriDay من $hijriMonthName)"
            isMondayOrThursday -> if (dayOfWeek == Calendar.MONDAY) "سُنّة صيام الاثنين" else "سُنّة صيام الخميس"
            else -> null
        }

        return HijriDateInfo(
            day = hijriDay,
            monthNameAr = hijriMonthName,
            year = hijriYear,
            isWhiteDay = isWhiteDay,
            isMondayOrThursday = isMondayOrThursday,
            fastingTitle = fastingTitle
        )
    }

    /**
     * Check if a meeting/event time conflicts with prayer times (within 25 mins of Azan).
     */
    fun checkPrayerConflict(startTimeMillis: Long, endTimeMillis: Long, city: CityLocation = SAUDI_CITIES[0]): String? {
        val cal = Calendar.getInstance().apply { timeInMillis = startTimeMillis }
        val startMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val endCal = Calendar.getInstance().apply { timeInMillis = endTimeMillis }
        val endMin = endCal.get(Calendar.HOUR_OF_DAY) * 60 + endCal.get(Calendar.MINUTE)

        val times = calculatePrayerTimes(city, cal)
        val prayerPairs = listOf(
            "صلاة الفجر" to times.fajr,
            "صلاة الظهر" to times.dhuhr,
            "صلاة العصر" to times.asr,
            "صلاة المغرب" to times.maghrib,
            "صلاة العشاء" to times.isha
        )

        // Helper to parse time string like "12:15 م" into minutes
        for ((pName, pTimeStr) in prayerPairs) {
            val parts = pTimeStr.split(" ")
            if (parts.isNotEmpty()) {
                val hm = parts[0].split(":")
                if (hm.size == 2) {
                    var h = hm[0].toIntOrNull() ?: continue
                    val m = hm[1].toIntOrNull() ?: continue
                    val isPm = parts.getOrNull(1) == "م"
                    if (isPm && h != 12) h += 12
                    if (!isPm && h == 12) h = 0
                    val prayerMinuteOfDay = h * 60 + m
                    // prayer window: from azan to +25 minutes
                    if (startMin <= (prayerMinuteOfDay + 25) && endMin >= (prayerMinuteOfDay - 5)) {
                        return "يتعارض الموعد مع وقت $pName ($pTimeStr)! يُفضل جدولة الاجتماع بعد انتهاء الصلاة."
                    }
                }
            }
        }
        return null
    }

    /**
     * Compute exact millisecond timestamps for each prayer for a given date and location.
     */
    fun calculatePrayerScheduleForDay(
        city: CityLocation = SAUDI_CITIES[0],
        calendar: Calendar = Calendar.getInstance()
    ): List<PrayerScheduleItem> {
        val baseYear = calendar.get(Calendar.YEAR)
        val baseMonth = calendar.get(Calendar.MONTH)
        val baseDay = calendar.get(Calendar.DAY_OF_MONTH)

        val times = calculatePrayerTimes(city, calendar)
        val prayers = listOf(
            "الفجر" to times.fajr,
            "الظهر" to times.dhuhr,
            "العصر" to times.asr,
            "المغرب" to times.maghrib,
            "العشاء" to times.isha
        )

        return prayers.mapNotNull { (name, timeStr) ->
            val parts = timeStr.split(" ")
            if (parts.size >= 2) {
                val hm = parts[0].split(":")
                if (hm.size == 2) {
                    var h = hm[0].toIntOrNull() ?: return@mapNotNull null
                    val m = hm[1].toIntOrNull() ?: return@mapNotNull null
                    val isPm = parts[1] == "م"
                    if (isPm && h != 12) h += 12
                    if (!isPm && h == 12) h = 0

                    val prayerCal = Calendar.getInstance().apply {
                        set(baseYear, baseMonth, baseDay, h, m, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val prayerTimeMillis = prayerCal.timeInMillis
                    val alert5MinBeforeMillis = prayerTimeMillis - (5 * 60 * 1000L) // Exactly 5 minutes before
                    val silentStartMillis = prayerTimeMillis + (10 * 60 * 1000L) // 5 minutes before Iqamah
                    val silentEndMillis = prayerTimeMillis + (30 * 60 * 1000L) // End of prayer

                    PrayerScheduleItem(
                        name = name,
                        formattedTime = timeStr,
                        prayerTimeMillis = prayerTimeMillis,
                        alert5MinBeforeMillis = alert5MinBeforeMillis,
                        silentModeStartMillis = silentStartMillis,
                        silentModeEndMillis = silentEndMillis
                    )
                } else null
            } else null
        }
    }

    /**
     * Resolve the user's location in Saudi Arabia using device sensors or fallback to city.
     */
    @android.annotation.SuppressLint("MissingPermission")
    fun resolveSaudiLocation(
        context: android.content.Context,
        fallbackCity: CityLocation = SAUDI_CITIES[0]
    ): CityLocation {
        return try {
            val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasFine && !hasCoarse) {
                return fallbackCity
            }

            val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as? android.location.LocationManager
            val providers = listOf(
                android.location.LocationManager.GPS_PROVIDER,
                android.location.LocationManager.NETWORK_PROVIDER
            )
            var bestLocation: android.location.Location? = null
            for (provider in providers) {
                try {
                    val loc = locationManager?.getLastKnownLocation(provider)
                    if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
                        bestLocation = loc
                    }
                } catch (_: SecurityException) {
                }
            }

            if (bestLocation != null) {
                val lat = bestLocation.latitude
                val lng = bestLocation.longitude
                // Check if inside Kingdom of Saudi Arabia bounding box (approx lat 16.0..32.5, lng 34.5..55.5)
                if (lat in 16.0..32.5 && lng in 34.5..55.5) {
                    // Find nearest known city or return dynamic location
                    val nearest = SAUDI_CITIES.minByOrNull {
                        val dLat = it.latitude - lat
                        val dLng = it.longitude - lng
                        dLat * dLat + dLng * dLng
                    }
                    return nearest ?: CityLocation("موقعي (المملكة)", "Current Location", lat, lng)
                }
            }
            fallbackCity
        } catch (_: Exception) {
            fallbackCity
        }
    }
}

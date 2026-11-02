package com.example.domain.calendar

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SaudiGold
import com.example.ui.theme.SaudiGoldDark
import java.util.Calendar

data class SaudiDayLifestyleInfo(
    val gregorianDay: Int,
    val gregorianMonth: Int, // 0-based
    val gregorianYear: Int,
    val hijriDay: Int,
    val hijriMonthName: String,
    val hijriYear: Int,
    val dayOfWeek: Int,
    val isSalaryDay: Boolean,
    val isFriday: Boolean,
    val isMondayOrThursday: Boolean,
    val isWhiteDay: Boolean,
    val nationalHoliday: String?,
    val religiousOccasion: String?,
    val lifestyleNotes: List<String>
)

data class SaudiLifestylePulse(
    val title: String,
    val daysRemaining: Int,
    val dateLabel: String,
    val emoji: String,
    val description: String,
    val accentColor: Color
)

object SaudiCalendarHelper {

    val HIJRI_MONTHS = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الثاني",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )

    val GREGORIAN_MONTHS_AR = listOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    /**
     * Algorithmic conversion from Gregorian to Umm Al-Qura / Saudi Hijri calendar date.
     */
    fun getHijriDate(cal: Calendar): Triple<Int, String, Int> {
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)

        // Julian Day Number calculation
        var a = (14 - m) / 12
        var y1 = y + 4800 - a
        var m1 = m + 12 * a - 3
        var jd = d + (153 * m1 + 2) / 5 + 365 * y1 + y1 / 4 - y1 / 100 + y1 / 400 - 32045

        // Conversion from Julian Day to Hijri
        val l = jd - 1948440 + 10632
        val n = (l - 1) / 10631
        val l1 = l - 10631 * n + 354
        val j = ((10985 - l1) / 5316) * ((50 * l1) / 17719) + (l1 / 5670) * ((43 * l1) / 15238)
        val l2 = l1 - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val month = (24 * l2) / 709
        val day = l2 - (709 * month) / 24
        val year = 30 * n + j - 30

        val validMonthIdx = ((month - 1) % 12).coerceIn(0, 11)
        val monthName = HIJRI_MONTHS[validMonthIdx]

        return Triple(day.coerceIn(1, 30), monthName, year)
    }

    /**
     * Compute comprehensive Saudi lifestyle data for any specific day.
     */
    fun getSaudiDayInfo(cal: Calendar): SaudiDayLifestyleInfo {
        val gDay = cal.get(Calendar.DAY_OF_MONTH)
        val gMonth = cal.get(Calendar.MONTH)
        val gYear = cal.get(Calendar.YEAR)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

        val (hDay, hMonthName, hYear) = getHijriDate(cal)

        // 1. Saudi Salary Day: 27th of every Gregorian month (or nearest work day if on weekend)
        val isSalaryDay = (gDay == 27)

        // 2. Religious & Sunnah Days
        val isFriday = (dayOfWeek == Calendar.FRIDAY)
        val isMondayOrThursday = (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.THURSDAY)
        val isWhiteDay = (hDay in 13..15)

        // 3. Saudi National Holidays
        val nationalHoliday = when {
            gMonth == Calendar.SEPTEMBER && gDay == 23 -> "🇸🇦 اليوم الوطني للمملكة العربية السعودية"
            gMonth == Calendar.FEBRUARY && gDay == 22 -> "🇸🇦 يوم التأسيس السعودي"
            gMonth == Calendar.MARCH && gDay == 11 -> "🇸🇦 يوم العلم السعودي"
            else -> null
        }

        // 4. Religious Occasions
        val religiousOccasion = when {
            hMonthName == "رمضان" && hDay == 1 -> "🌙 غرة شهر رمضان المبارك"
            hMonthName == "رمضان" && hDay in 21..29 && hDay % 2 != 0 -> "✨ ليلة وترية من العشر الأواخر"
            hMonthName == "شوال" && hDay in 1..3 -> "🎈 عيد الفطر المبارك"
            hMonthName == "ذو الحجة" && hDay in 1..8 -> "🕋 عشر ذي الحجة المباركة"
            hMonthName == "ذو الحجة" && hDay == 9 -> "🕋 يوم عرفة (الحج الأكبر)"
            hMonthName == "ذو الحجة" && hDay in 10..13 -> "🐑 عيد الأضحى المبارك وأيام التشريق"
            hMonthName == "محرم" && hDay == 1 -> "🌟 رأس السنة الهجرية"
            hMonthName == "محرم" && hDay == 10 -> "🤲 يوم عاشوراء"
            else -> null
        }

        val notes = mutableListOf<String>()
        if (isSalaryDay) notes.add("💰 موعد إيداع الراتب الحكومي السعودي")
        if (isFriday) notes.add("🕌 يوم الجمعة المبارك: قراءة سورة الكهف وساعة الاستجابة")
        if (isWhiteDay) notes.add("🌙 صيام الأيام البيض المستحب ($hDay $hMonthName)")
        if (isMondayOrThursday && !isWhiteDay) notes.add("🤲 صيام السُّنة النبوية الشريفة")
        if (nationalHoliday != null) notes.add(nationalHoliday)
        if (religiousOccasion != null) notes.add(religiousOccasion)

        return SaudiDayLifestyleInfo(
            gregorianDay = gDay,
            gregorianMonth = gMonth,
            gregorianYear = gYear,
            hijriDay = hDay,
            hijriMonthName = hMonthName,
            hijriYear = hYear,
            dayOfWeek = dayOfWeek,
            isSalaryDay = isSalaryDay,
            isFriday = isFriday,
            isMondayOrThursday = isMondayOrThursday,
            isWhiteDay = isWhiteDay,
            nationalHoliday = nationalHoliday,
            religiousOccasion = religiousOccasion,
            lifestyleNotes = notes
        )
    }

    /**
     * Compute lifestyle pulses & countdowns for the Saudi lifestyle bar.
     */
    fun getLifestylePulses(today: Calendar = Calendar.getInstance()): List<SaudiLifestylePulse> {
        val pulses = mutableListOf<SaudiLifestylePulse>()

        // 1. Salary Countdown (27th of current or next month)
        val currentDay = today.get(Calendar.DAY_OF_MONTH)
        val daysUntilSalary = if (currentDay <= 27) {
            27 - currentDay
        } else {
            val maxDays = today.getActualMaximum(Calendar.DAY_OF_MONTH)
            (maxDays - currentDay) + 27
        }

        pulses.add(
            SaudiLifestylePulse(
                title = "إيداع الرواتب",
                daysRemaining = daysUntilSalary,
                dateLabel = "27 من كل شهر ميلادي",
                emoji = "💰",
                description = if (daysUntilSalary == 0) "اليوم موعد الصرف!" else "متبقي $daysUntilSalary يوماً على الراتب",
                accentColor = SaudiGold
            )
        )

        // 2. Next Friday (سيد الأيام)
        val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
        val daysUntilFriday = (Calendar.FRIDAY - dayOfWeek + 7) % 7
        pulses.add(
            SaudiLifestylePulse(
                title = "الجمعة المباركة",
                daysRemaining = daysUntilFriday,
                dateLabel = if (daysUntilFriday == 0) "اليوم!" else "بعد $daysUntilFriday أيام",
                emoji = "🕌",
                description = if (daysUntilFriday == 0) "سورة الكهف وساعة الإجابة عصراً" else "الاستعداد لصلاة الجمعة وصلة الرحم",
                accentColor = EmeraldPrimary
            )
        )

        // 3. Saudi National Occasion
        val nationalDaysDiff = getDaysUntilNextNationalDay(today)
        pulses.add(
            SaudiLifestylePulse(
                title = nationalDaysDiff.first,
                daysRemaining = nationalDaysDiff.second,
                dateLabel = nationalDaysDiff.third,
                emoji = "🇸🇦",
                description = "احتفاء بمجد وتاريخ المملكة العربية السعودية",
                accentColor = EmeraldPrimary
            )
        )

        // 4. White Days / Spiritual Fasting
        val (hDay, hMonthName, _) = getHijriDate(today)
        val daysUntilWhiteDays = when {
            hDay < 13 -> 13 - hDay
            hDay in 13..15 -> 0
            else -> (30 - hDay) + 13
        }
        pulses.add(
            SaudiLifestylePulse(
                title = "الأيام البيض",
                daysRemaining = daysUntilWhiteDays,
                dateLabel = "13، 14، 15 $hMonthName",
                emoji = "🌙",
                description = if (daysUntilWhiteDays == 0) "اليوم من الأيام البيض المباركة" else "متبقي $daysUntilWhiteDays يوماً على صيام الأيام البيض",
                accentColor = SaudiGoldDark
            )
        )

        return pulses
    }

    private fun getDaysUntilNextNationalDay(today: Calendar): Triple<String, Int, String> {
        val y = today.get(Calendar.YEAR)

        val nationalDayCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, y)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 23)
        }
        if (nationalDayCal.before(today)) {
            nationalDayCal.set(Calendar.YEAR, y + 1)
        }

        val foundationDayCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, y)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.DAY_OF_MONTH, 22)
        }
        if (foundationDayCal.before(today)) {
            foundationDayCal.set(Calendar.YEAR, y + 1)
        }

        val diffNat = ((nationalDayCal.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        val diffFound = ((foundationDayCal.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)

        return if (diffNat <= diffFound) {
            Triple("اليوم الوطني 96", diffNat, "23 سبتمبر")
        } else {
            Triple("يوم التأسيس", diffFound, "22 فبراير")
        }
    }
}

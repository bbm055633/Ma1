package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalendarEventEntity
import com.example.domain.calendar.SaudiCalendarHelper
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.screens.calendar.components.CalendarEventCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Interactive Hour-by-Hour Timeline View (05:00 ص - 11:00 م)
 * seamlessly integrating Saudi Prayer Times and User Meetings with collision alerts.
 */
@Composable
fun CalendarDayTimelineView(
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: Int,
    events: List<CalendarEventEntity>,
    prayerTimes: PrayerTimes,
    hijriDate: HijriDateInfo,
    onSelectDay: (Int) -> Unit,
    onQuickAddEventAtHour: (Int) -> Unit,
    onToggleComplete: (CalendarEventEntity) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit,
    modifier: Modifier = Modifier,
    viewMode: String = "DAY",
    onViewModeChange: (String) -> Unit = {},
    onGoToToday: () -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val arabicMonths = listOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )
    val monthName = arabicMonths.getOrElse(selectedMonth) { "الشهر" }

    // Day of week name
    val dayCal = remember(selectedYear, selectedMonth, selectedDay) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth)
            set(Calendar.DAY_OF_MONTH, selectedDay)
        }
    }
    val dayOfWeekName = when (dayCal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "الأحد"
        Calendar.MONDAY -> "الإثنين"
        Calendar.TUESDAY -> "الثلاثاء"
        Calendar.WEDNESDAY -> "الأربعاء"
        Calendar.THURSDAY -> "الخميس"
        Calendar.FRIDAY -> "الجمعة"
        else -> "السبت"
    }

    val saudiDayInfo = remember(selectedYear, selectedMonth, selectedDay) {
        SaudiCalendarHelper.getSaudiDayInfo(dayCal)
    }

    // Filter events for this exact day
    val dayEvents = remember(events, selectedDay, selectedMonth, selectedYear) {
        events.filter { event ->
            val eventCal = Calendar.getInstance().apply { timeInMillis = event.startTimeMillis }
            eventCal.get(Calendar.DAY_OF_MONTH) == selectedDay &&
                    eventCal.get(Calendar.MONTH) == selectedMonth &&
                    eventCal.get(Calendar.YEAR) == selectedYear
        }
    }

    // Hours from 5 AM to 11 PM (5..23)
    val timelineHours = (5..23).toList()

    // Helper: Map prayers to approximate hour for timeline visualization
    fun parsePrayerHour(timeStr: String): Int {
        // e.g. "4:40 ص" -> 4, "12:15 م" -> 12, "3:40 م" -> 15, "6:10 م" -> 18, "7:40 م" -> 19
        return try {
            val parts = timeStr.trim().split(" ")
            val timeParts = parts[0].split(":")
            var h = timeParts[0].toInt()
            val isPm = parts.getOrNull(1)?.contains("م") == true
            if (isPm && h < 12) h += 12
            if (!isPm && h == 12) h = 0
            h
        } catch (e: Exception) {
            -1
        }
    }

    val fajrHour = parsePrayerHour(prayerTimes.fajr)
    val dhuhrHour = parsePrayerHour(prayerTimes.dhuhr)
    val asrHour = parsePrayerHour(prayerTimes.asr)
    val maghribHour = parsePrayerHour(prayerTimes.maghrib)
    val ishaHour = parsePrayerHour(prayerTimes.isha)

    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.forLanguageTag("ar")) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Day Navigator Card
        item {
            val conflictsCount = dayEvents.count { it.isPrayerOverlapWarning }
            val isStarry = com.example.ui.components.LocalStarryNightActive.current
            val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
            val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarry) cardAlpha else 1.0f)
            val borderColor = if (isStarry) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = surfaceColor,
                shadowElevation = if (isStarry) 0.dp else 3.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    borderColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Top Row: Title, Date, Scope Switcher & Arrows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "$dayOfWeekName، $selectedDay $monthName $selectedYear م",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1C1B1A)
                                )
                                if (dayEvents.isNotEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = primaryColor.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "${dayEvents.size} موعد",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                if (conflictsCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = WarningOrange.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "⚠️ تعارض صلاة",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WarningOrange,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "الموافق ${saudiDayInfo.hijriDay} ${saudiDayInfo.hijriMonthName} ${saudiDayInfo.hijriYear} هـ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SaudiGoldDark
                            )
                        }

                        // Scope Switcher [ اليوم | أسبوع | شهر ] + Arrows
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ) {
                                Row(modifier = Modifier.padding(2.dp)) {
                                    listOf(
                                        "DAY" to "اليوم",
                                        "WEEK" to "الأسبوع",
                                        "MONTH" to "الشهر"
                                    ).forEach { (scope, label) ->
                                        val isSelected = scope == "DAY"
                                        Surface(
                                            onClick = {
                                                if (scope == "DAY") onGoToToday()
                                                onViewModeChange(scope)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) primaryColor else Color.Transparent,
                                            modifier = Modifier.testTag("day_timeline_scope_$scope")
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    if (selectedDay > 1) onSelectDay(selectedDay - 1)
                                },
                                enabled = selectedDay > 1,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "اليوم السابق",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val maxDays = dayCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                                    if (selectedDay < maxDays) onSelectDay(selectedDay + 1)
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "اليوم التالي",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }

                    if (saudiDayInfo.lifestyleNotes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            saudiDayInfo.lifestyleNotes.forEach { note ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (saudiDayInfo.isSalaryDay) SaudiGoldLight.copy(alpha = 0.25f) else primaryColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = note,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (saudiDayInfo.isSalaryDay) SaudiGoldDark else primaryColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Timeline Hours
        items(timelineHours) { hour ->
            val hourFormatted = if (hour == 12) "12:00 م" else if (hour > 12) "${hour - 12}:00 م" else "$hour:00 ص"

            // Events occurring in this hour
            val eventsInThisHour = dayEvents.filter { event ->
                val startCal = Calendar.getInstance().apply { timeInMillis = event.startTimeMillis }
                startCal.get(Calendar.HOUR_OF_DAY) == hour
            }

            // Check if a prayer occurs around this hour
            val prayerInThisHour = when (hour) {
                fajrHour -> "صلاة الفجر" to prayerTimes.fajr
                dhuhrHour -> "صلاة الظهر" to prayerTimes.dhuhr
                asrHour -> "صلاة العصر" to prayerTimes.asr
                maghribHour -> "صلاة المغرب" to prayerTimes.maghrib
                ishaHour -> "صلاة العشاء" to prayerTimes.isha
                else -> null
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Time label on right
                Column(
                    modifier = Modifier.width(62.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = hourFormatted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Content slot on left
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 1. Prayer Banner if present in this hour
                    prayerInThisHour?.let { (pName, pTime) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PrayerPurple.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrayerPurple.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🕌", fontSize = 14.sp)
                                    Text(
                                        text = "$pName ($pTime)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrayerPurple
                                    )
                                }
                                Text("وقت محمي", fontSize = 10.sp, color = PrayerPurple)
                            }
                        }
                    }

                    // 2. Events in this hour
                    if (eventsInThisHour.isNotEmpty()) {
                        eventsInThisHour.forEach { event ->
                            CalendarEventCard(
                                event = event,
                                timeFormat = timeFormat,
                                onToggleComplete = { onToggleComplete(event) },
                                onDelete = { onDeleteEvent(event) }
                            )
                        }
                    } else if (prayerInThisHour == null) {
                        // Empty slot with quick add
                        Surface(
                            onClick = { onQuickAddEventAtHour(hour) },
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "فترة متاحة للاجتماع",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Icon(
                                    Icons.Default.AddCircleOutline,
                                    contentDescription = "إضافة موعد في هذه الساعة",
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalendarEventEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TaskEntity
import com.example.ui.screens.calendar.components.CalendarEventCard
import com.example.ui.screens.calendar.components.NoteCard
import com.example.domain.calendar.SaudiCalendarHelper
import com.example.domain.calendar.SaudiDayLifestyleInfo
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Saudi Lifestyle Calendar Component (مكون تقويم نمط الحياة السعودي)
 * Seamlessly integrates Hijri and Gregorian dates with Saudi lifestyle milestones:
 * - Salary Day (27th of Gregorian month)
 * - Jumu'ah (Friday) blessings & Kahf reminder
 * - White Days (13, 14, 15 Hijri) and Sunnah fasting
 * - Saudi National Days (National Day 23 Sep, Founding Day 22 Feb, Flag Day 11 Mar)
 * - Umm Al-Qura Prayer times synchronization
 */
@Composable
fun CalendarMonthGridView(
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: Int,
    events: List<CalendarEventEntity>,
    prayerTimes: PrayerTimes,
    hijriDate: HijriDateInfo,
    onSelectDay: (Int) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onGoToToday: () -> Unit,
    onAddEventForDay: () -> Unit,
    onToggleComplete: (CalendarEventEntity) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit,
    modifier: Modifier = Modifier,
    viewMode: String = "MONTH",
    onViewModeChange: (String) -> Unit = {},
    isEnglish: Boolean = false,
    tasks: List<TaskEntity> = emptyList(),
    notes: List<NoteEntity> = emptyList(),
    onToggleTaskComplete: (TaskEntity) -> Unit = {},
    onDeleteTask: (TaskEntity) -> Unit = {},
    onDeleteNote: (NoteEntity) -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val monthCal = remember(selectedYear, selectedMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (monthCal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY + 7) % 7

    val arabicMonths = listOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )
    val englishMonths = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val monthName = if (isEnglish) englishMonths.getOrElse(selectedMonth) { "Month" } else arabicMonths.getOrElse(selectedMonth) { "الشهر" }

    // Hijri info for current month view
    val midMonthCal = remember(selectedYear, selectedMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth)
            set(Calendar.DAY_OF_MONTH, 15)
        }
    }
    val currentMonthHijri = remember(selectedYear, selectedMonth) {
        SaudiCalendarHelper.getHijriDate(midMonthCal)
    }

    val todayCal = Calendar.getInstance()
    val isCurrentRealMonth = todayCal.get(Calendar.YEAR) == selectedYear && todayCal.get(Calendar.MONTH) == selectedMonth
    val todayDay = if (isCurrentRealMonth) todayCal.get(Calendar.DAY_OF_MONTH) else -1

    // Filter events for the selected day
    val selectedDayEvents = remember(events, selectedDay, selectedMonth, selectedYear) {
        events.filter { event ->
            val eventCal = Calendar.getInstance().apply { timeInMillis = event.startTimeMillis }
            eventCal.get(Calendar.DAY_OF_MONTH) == selectedDay &&
                    eventCal.get(Calendar.MONTH) == selectedMonth &&
                    eventCal.get(Calendar.YEAR) == selectedYear
        }
    }

    // Filter tasks for the selected day
    val selectedDayTasks = remember(tasks, selectedDay, selectedMonth, selectedYear) {
        tasks.filter { task ->
            val taskCal = Calendar.getInstance().apply { timeInMillis = task.dueDateMillis }
            taskCal.get(Calendar.DAY_OF_MONTH) == selectedDay &&
                    taskCal.get(Calendar.MONTH) == selectedMonth &&
                    taskCal.get(Calendar.YEAR) == selectedYear
        }
    }

    // Filter notes for the selected day
    val selectedDayNotes = remember(notes, selectedDay, selectedMonth, selectedYear) {
        notes.filter { note ->
            val noteCal = Calendar.getInstance().apply { timeInMillis = note.timestamp }
            noteCal.get(Calendar.DAY_OF_MONTH) == selectedDay &&
                    noteCal.get(Calendar.MONTH) == selectedMonth &&
                    noteCal.get(Calendar.YEAR) == selectedYear
        }
    }

    // Map of dayOfMonth -> count of items (events + tasks + notes)
    val dayEventCountMap = remember(events, tasks, notes, selectedMonth, selectedYear) {
        val map = mutableMapOf<Int, Int>()
        events.forEach { event ->
            val eventCal = Calendar.getInstance().apply { timeInMillis = event.startTimeMillis }
            if (eventCal.get(Calendar.MONTH) == selectedMonth && eventCal.get(Calendar.YEAR) == selectedYear) {
                val d = eventCal.get(Calendar.DAY_OF_MONTH)
                map[d] = (map[d] ?: 0) + 1
            }
        }
        tasks.forEach { task ->
            val taskCal = Calendar.getInstance().apply { timeInMillis = task.dueDateMillis }
            if (taskCal.get(Calendar.MONTH) == selectedMonth && taskCal.get(Calendar.YEAR) == selectedYear) {
                val d = taskCal.get(Calendar.DAY_OF_MONTH)
                map[d] = (map[d] ?: 0) + 1
            }
        }
        notes.forEach { note ->
            val noteCal = Calendar.getInstance().apply { timeInMillis = note.timestamp }
            if (noteCal.get(Calendar.MONTH) == selectedMonth && noteCal.get(Calendar.YEAR) == selectedYear) {
                val d = noteCal.get(Calendar.DAY_OF_MONTH)
                map[d] = (map[d] ?: 0) + 1
            }
        }
        map
    }

    // Selected Day Saudi Lifestyle Info
    val selectedDayCal = remember(selectedYear, selectedMonth, selectedDay) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth)
            set(Calendar.DAY_OF_MONTH, selectedDay)
        }
    }
    val selectedSaudiDayInfo = remember(selectedYear, selectedMonth, selectedDay) {
        SaudiCalendarHelper.getSaudiDayInfo(selectedDayCal)
    }

    // Lifestyle pulses countdowns
    val lifestylePulses = remember {
        SaudiCalendarHelper.getLifestylePulses()
    }

    val salaryDays = lifestylePulses.find { it.title.contains("الرواتب") }?.daysRemaining ?: 0
    val whiteDaysCount = lifestylePulses.find { it.title.contains("الأيام البيض") }?.daysRemaining ?: 0
    val nationalDaysCount = lifestylePulses.find { it.title.contains("الوطني") || it.title.contains("التأسيس") }?.daysRemaining ?: 0

    // Lifestyle filter: ALL, SALARY, FAITH, NATIONAL
    var lifestyleFilter by remember { mutableStateOf("ALL") }

    val dayNameAr = when (selectedSaudiDayInfo.dayOfWeek) {
        Calendar.SUNDAY -> "الأحد"
        Calendar.MONDAY -> "الإثنين"
        Calendar.TUESDAY -> "الثلاثاء"
        Calendar.WEDNESDAY -> "الأربعاء"
        Calendar.THURSDAY -> "الخميس"
        Calendar.FRIDAY -> "الجمعة"
        Calendar.SATURDAY -> "السبت"
        else -> "اليوم"
    }

    val dayNameEn = when (selectedSaudiDayInfo.dayOfWeek) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> "Day"
    }

    var calendarScope by remember { mutableStateOf("MONTH") } // "DAY", "WEEK", "MONTH"

    val weekDaysList = remember(selectedYear, selectedMonth, selectedDay) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth)
            set(Calendar.DAY_OF_MONTH, selectedDay)
        }
        val dayIndex = (cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY + 7) % 7
        val sundayCal = Calendar.getInstance().apply {
            timeInMillis = cal.timeInMillis
            add(Calendar.DAY_OF_MONTH, -dayIndex)
        }
        (0..6).map { offset ->
            Calendar.getInstance().apply {
                timeInMillis = sundayCal.timeInMillis
                add(Calendar.DAY_OF_MONTH, offset)
            }
        }
    }

    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.forLanguageTag("ar")) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 2. DUAL CALENDAR MONTH CARD & GRID (التقويم المزدوج الهجري والميلادي)
        item {
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
                    // Month / Week / Day & Navigation Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title based on viewMode
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (viewMode == "WEEK") {
                                        if (isEnglish) "Week $dayNameEn, $selectedDay $monthName" else "أسبوع $dayNameAr $selectedDay $monthName"
                                    } else {
                                        if (isEnglish) "$monthName $selectedYear" else "$monthName $selectedYear م"
                                    },
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val hijriMonthsEnMap = mapOf(
                                    "محرم" to "Muharram", "صفر" to "Safar", "ربيع الأول" to "Rabi I", "ربيع الثاني" to "Rabi II",
                                    "جمادى الأولى" to "Jumada I", "جمادى الآخرة" to "Jumada II", "رجب" to "Rajab", "شعبان" to "Shaban",
                                    "رمضان" to "Ramadan", "شوال" to "Shawwal", "ذو القعدة" to "Dhu al-Qidah", "ذو الحجة" to "Dhu al-Hijjah"
                                )
                                val hijriMonthEn = hijriMonthsEnMap[currentMonthHijri.second] ?: currentMonthHijri.second
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SaudiGoldDark.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (isEnglish) "🌙 ${currentMonthHijri.first} $hijriMonthEn ${currentMonthHijri.third} AH" else "🌙 ${currentMonthHijri.first} ${currentMonthHijri.second} ${currentMonthHijri.third} هـ",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaudiGoldDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Scope Switcher [ اليوم | أسبوع | شهر ] + Navigation Arrows
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
                                        "DAY" to (if (isEnglish) "Day" else "اليوم"),
                                        "WEEK" to (if (isEnglish) "Week" else "الأسبوع"),
                                        "MONTH" to (if (isEnglish) "Month" else "الشهر")
                                    ).forEach { (scope, label) ->
                                        val isSelected = (scope == "DAY" && viewMode == "DAY") ||
                                                (scope == "WEEK" && viewMode == "WEEK") ||
                                                (scope == "MONTH" && (viewMode == "MONTH" || viewMode == "LIST"))
                                        Surface(
                                            onClick = {
                                                if (scope == "DAY") {
                                                    onGoToToday()
                                                }
                                                onViewModeChange(scope)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) primaryColor else Color.Transparent,
                                            modifier = Modifier.testTag("scope_$scope")
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
                                    if (viewMode == "WEEK") {
                                        if (selectedDay > 7) onSelectDay(selectedDay - 7)
                                        else onPrevMonth()
                                    } else {
                                        onPrevMonth()
                                    }
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "السابق",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (viewMode == "WEEK") {
                                        if (selectedDay + 7 <= daysInMonth) onSelectDay(selectedDay + 7)
                                        else onNextMonth()
                                    } else {
                                        onNextMonth()
                                    }
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "التالي",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }

                    if (viewMode != "WEEK") {
                        Spacer(modifier = Modifier.height(10.dp))

                    // Quick Lifestyle Filter Chips with Countdown Badges
                    val chips = listOf(
                        Triple("ALL", if (isEnglish) "🌟 All" else "🌟 الكل", null),
                        Triple("SALARY", if (isEnglish) "💰 Salaries" else "💰 الرواتب", if (salaryDays == 0) (if (isEnglish) "Today" else "اليوم") else "$salaryDays ${if (isEnglish) "d" else "يوم"}"),
                        Triple("FAITH", if (isEnglish) "🌙 Spirituality" else "🌙 الروحانيات", if (whiteDaysCount == 0) (if (isEnglish) "Today" else "اليوم") else "$whiteDaysCount ${if (isEnglish) "d" else "يوم"}")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chips.forEach { (key, label, badge) ->
                            val isSelected = lifestyleFilter == key
                            Surface(
                                onClick = { lifestyleFilter = key },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) primaryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, primaryColor) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (badge != null) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isSelected) primaryColor.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        ) {
                                            Text(
                                                text = badge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Weekdays Header: (الأحد، الإثنين، الثلاثاء، الأربعاء، الخميس، الجمعة، السبت)
                    val weekDays = if (isEnglish) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    } else {
                        listOf("أحد", "اثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة", "سبت")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekDays.forEach { dayLabel ->
                            val isFri = dayLabel == "جمعة" || dayLabel == "Fri"
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = dayLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFri) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                if (isFri) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(primaryColor)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 7-Column Interactive Dual Calendar Grid
                    val totalCells = firstDayOfWeek + daysInMonth
                    val rowsCount = (totalCells + 6) / 7

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (r in 0 until rowsCount) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (c in 0..6) {
                                    val cellIndex = r * 7 + c
                                    val dayNum = cellIndex - firstDayOfWeek + 1

                                    if (dayNum in 1..daysInMonth) {
                                        val isSelected = dayNum == selectedDay
                                        val isToday = dayNum == todayDay
                                        val hasEvents = (dayEventCountMap[dayNum] ?: 0) > 0

                                        // Compute Day Lifestyle & Hijri info for this specific cell
                                        val cellCal = Calendar.getInstance().apply {
                                            set(Calendar.YEAR, selectedYear)
                                            set(Calendar.MONTH, selectedMonth)
                                            set(Calendar.DAY_OF_MONTH, dayNum)
                                        }
                                        val dayInfo = SaudiCalendarHelper.getSaudiDayInfo(cellCal)

                                        // Check filter match
                                        val matchesFilter = when (lifestyleFilter) {
                                            "SALARY" -> dayInfo.isSalaryDay
                                            "FAITH" -> dayInfo.isWhiteDay || dayInfo.isFriday || dayInfo.religiousOccasion != null
                                            "NATIONAL" -> dayInfo.nationalHoliday != null
                                            else -> true
                                        }

                                        val cellBackground = when {
                                            isSelected -> primaryColor
                                            dayInfo.isSalaryDay -> SaudiGoldLight.copy(alpha = 0.3f)
                                            dayInfo.nationalHoliday != null -> primaryColor.copy(alpha = 0.12f)
                                            dayInfo.isWhiteDay -> SaudiGoldLight.copy(alpha = 0.18f)
                                            isToday -> primaryColor.copy(alpha = 0.1f)
                                            dayInfo.isFriday -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                            else -> Color.Transparent
                                        }

                                        val cellBorder = when {
                                            isSelected -> androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.95f))
                                            isToday -> androidx.compose.foundation.BorderStroke(1.5.dp, primaryColor)
                                            dayInfo.isSalaryDay -> androidx.compose.foundation.BorderStroke(1.2.dp, SaudiGold)
                                            dayInfo.nationalHoliday != null -> androidx.compose.foundation.BorderStroke(1.2.dp, primaryColor)
                                            dayInfo.isWhiteDay -> androidx.compose.foundation.BorderStroke(1.dp, SaudiGold.copy(alpha = 0.5f))
                                            else -> null
                                        }

                                        Surface(
                                            onClick = { onSelectDay(dayNum) },
                                            shape = RoundedCornerShape(12.dp),
                                            color = cellBackground,
                                            border = cellBorder,
                                            shadowElevation = if (isSelected) 6.dp else 0.dp,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(54.dp)
                                                .testTag("calendar_day_$dayNum")
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(2.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                // Top Row: Saudi Lifestyle Badge or event dot
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Lifestyle Mini Badge (💰 / 🌙 / 🇸🇦)
                                                    when {
                                                        dayInfo.isSalaryDay -> Text("💰", fontSize = 8.sp)
                                                        dayInfo.nationalHoliday != null -> Text("🇸🇦", fontSize = 8.sp)
                                                        dayInfo.isWhiteDay -> Text("🌙", fontSize = 8.sp)
                                                        else -> Spacer(modifier = Modifier.size(8.dp))
                                                    }

                                                    // Event Indicator Dot
                                                    if (hasEvents) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(if (isSelected) Color.White else primaryColor)
                                                        )
                                                    } else {
                                                        Spacer(modifier = Modifier.size(4.dp))
                                                    }
                                                }

                                                // Middle: Gregorian Day Number (Primary)
                                                Text(
                                                    text = "$dayNum",
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected || isToday || dayInfo.isSalaryDay) FontWeight.Bold else FontWeight.Medium,
                                                    color = when {
                                                        isSelected -> Color.White
                                                        !matchesFilter -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                        dayInfo.isSalaryDay -> SaudiGoldDark
                                                        isToday -> primaryColor
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    }
                                                )

                                                // Bottom: Hijri Day Number (Dual Calendar Integration)
                                                Text(
                                                    text = if (isEnglish) "${dayInfo.hijriDay} H" else "${dayInfo.hijriDay} هـ",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = when {
                                                        isSelected -> Color.White.copy(alpha = 0.85f)
                                                        !matchesFilter -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                                        dayInfo.isWhiteDay -> SaudiGoldDark
                                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                    }
                                                )
                                            }
                                        }
                                    } else {
                                        // Empty cell for alignment
                                        Spacer(modifier = Modifier.weight(1f).height(54.dp))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // viewMode == "WEEK"
                    Spacer(modifier = Modifier.height(12.dp))

                    // 7-Day Interactive Week Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekDaysList.forEach { calItem ->
                            val itemDayNum = calItem.get(Calendar.DAY_OF_MONTH)
                            val itemMonth = calItem.get(Calendar.MONTH)
                            val isSelected = itemDayNum == selectedDay && itemMonth == selectedMonth
                            val isToday = itemDayNum == todayDay && isCurrentRealMonth
                            val itemInfo = remember(itemDayNum, itemMonth) {
                                SaudiCalendarHelper.getSaudiDayInfo(calItem)
                            }
                            val dayEventsCount = (dayEventCountMap[itemDayNum] ?: 0)
                            val hasEvents = dayEventsCount > 0

                            val itemDayName = when (calItem.get(Calendar.DAY_OF_WEEK)) {
                                Calendar.SUNDAY -> if (isEnglish) "Sun" else "أحد"
                                Calendar.MONDAY -> if (isEnglish) "Mon" else "اثنين"
                                Calendar.TUESDAY -> if (isEnglish) "Tue" else "ثلاثاء"
                                Calendar.WEDNESDAY -> if (isEnglish) "Wed" else "أربعاء"
                                Calendar.THURSDAY -> if (isEnglish) "Thu" else "خميس"
                                Calendar.FRIDAY -> if (isEnglish) "Fri" else "جمعة"
                                else -> if (isEnglish) "Sat" else "سبت"
                            }

                            Surface(
                                onClick = {
                                    onSelectDay(itemDayNum)
                                    if (itemMonth != selectedMonth) {
                                        if (itemMonth > selectedMonth || (selectedMonth == 11 && itemMonth == 0)) {
                                            onNextMonth()
                                        } else {
                                            onPrevMonth()
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.95f)) else if (isToday) androidx.compose.foundation.BorderStroke(1.dp, primaryColor) else null,
                                shadowElevation = if (isSelected) 6.dp else 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .testTag("week_day_$itemDayNum")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = itemDayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$itemDayNum",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${itemInfo.hijriDay}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else SaudiGoldDark
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    if (hasEvents) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White else primaryColor)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(5.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

        // 3. EVENTS, TASKS, AND NOTES FOR SELECTED DAY (مواعيد ومهام وملاحظات اليوم المحدد)
        val totalDayItems = selectedDayEvents.size + selectedDayTasks.size + selectedDayNotes.size
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEnglish) "Schedule for $selectedDay $monthName ($totalDayItems)" else "جدول $selectedDay $monthName ($totalDayItems)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Button(
                    onClick = onAddEventForDay,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("day_add_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(if (isEnglish) "Add" else "إضافة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (totalDayItems == 0) {
            item {
                val isStarry = com.example.ui.components.LocalStarryNightActive.current
                val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
                val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarry) cardAlpha else 1.0f)
                val borderColor = if (isStarry) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = surfaceColor,
                    shadowElevation = if (isStarry) 0.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("day_empty_state")
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(primaryColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = if (isEnglish) "No appointments, tasks, or notes for this day" else "لا توجد مواعيد أو مهام أو ملاحظات لهذا اليوم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (isEnglish) "Tap 'Add' to schedule an appointment, task, or note" else "اضغط على زر 'إضافة' لإدراج موعد أو مهمة أو ملاحظة",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Events for Selected Day
            if (selectedDayEvents.isNotEmpty()) {
                items(selectedDayEvents) { event ->
                    CalendarEventCard(
                        event = event,
                        timeFormat = timeFormat,
                        onToggleComplete = { onToggleComplete(event) },
                        onDelete = { onDeleteEvent(event) }
                    )
                }
            }

            // Tasks for Selected Day
            if (selectedDayTasks.isNotEmpty()) {
                items(selectedDayTasks) { task ->
                    CalendarTaskDayCard(
                        task = task,
                        primaryColor = primaryColor,
                        isEnglish = isEnglish,
                        onToggleComplete = { onToggleTaskComplete(task) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }

            // Notes for Selected Day
            if (selectedDayNotes.isNotEmpty()) {
                items(selectedDayNotes) { note ->
                    CalendarNoteDayCard(
                        note = note,
                        primaryColor = primaryColor,
                        isEnglish = isEnglish,
                        onDelete = { onDeleteNote(note) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarTaskDayCard(
    task: TaskEntity,
    primaryColor: Color,
    isEnglish: Boolean = false,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("day_task_card_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onToggleComplete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.isCompleted) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    )
                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isEnglish) "Task 📝" else "مهمة 📝",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = if (isEnglish) "Delete" else "حذف",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CalendarNoteDayCard(
    note: NoteEntity,
    primaryColor: Color,
    isEnglish: Boolean = false,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("day_note_card_${note.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(20.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (note.content.isNotBlank()) {
                        Text(
                            text = note.content,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SaudiGoldDark.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isEnglish) "Note 📋" else "ملاحظة 📋",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaudiGoldDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = if (isEnglish) "Delete" else "حذف",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

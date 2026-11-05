package com.example.ui.screens.calendar.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.local.CalendarEventEntity
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.screens.calendar.CalendarDayTimelineView

@Composable
fun DayTimelineView(
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
    CalendarDayTimelineView(
        selectedYear = selectedYear,
        selectedMonth = selectedMonth,
        selectedDay = selectedDay,
        events = events,
        prayerTimes = prayerTimes,
        hijriDate = hijriDate,
        onSelectDay = onSelectDay,
        onQuickAddEventAtHour = onQuickAddEventAtHour,
        onToggleComplete = onToggleComplete,
        onDeleteEvent = onDeleteEvent,
        modifier = modifier,
        viewMode = viewMode,
        onViewModeChange = onViewModeChange,
        onGoToToday = onGoToToday
    )
}

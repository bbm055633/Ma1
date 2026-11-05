package com.example.ui.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.domain.calendar.SaudiCalendarHelper
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun MonthGridView(
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
    com.example.ui.screens.calendar.CalendarMonthGridView(
        selectedYear = selectedYear,
        selectedMonth = selectedMonth,
        selectedDay = selectedDay,
        events = events,
        prayerTimes = prayerTimes,
        hijriDate = hijriDate,
        onSelectDay = onSelectDay,
        onPrevMonth = onPrevMonth,
        onNextMonth = onNextMonth,
        onGoToToday = onGoToToday,
        onAddEventForDay = onAddEventForDay,
        onToggleComplete = onToggleComplete,
        onDeleteEvent = onDeleteEvent,
        modifier = modifier,
        viewMode = viewMode,
        onViewModeChange = onViewModeChange,
        isEnglish = isEnglish,
        tasks = tasks,
        notes = notes,
        onToggleTaskComplete = onToggleTaskComplete,
        onDeleteTask = onDeleteTask,
        onDeleteNote = onDeleteNote
    )
}

package com.example.ui.screens.calendar.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.local.CalendarEventEntity
import com.example.ui.screens.calendar.CalendarAgendaListView

@Composable
fun AgendaListView(
    events: List<CalendarEventEntity>,
    onToggleComplete: (CalendarEventEntity) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    CalendarAgendaListView(
        events = events,
        onToggleComplete = onToggleComplete,
        onDeleteEvent = onDeleteEvent,
        modifier = modifier
    )
}

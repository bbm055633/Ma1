package com.example.ui.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NoteEntity
import com.example.data.local.TaskEntity
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.components.IslamicGeometricPatternBackground
import com.example.ui.screens.calendar.components.*
import com.example.ui.screens.calendar.dialogs.*
import com.example.ui.viewmodel.MainViewModel

/**
 * Optimized, Modular, High-Performance Calendar & Notes Screen
 * Featuring specialized views (Month Grid, Day Timeline, Agenda List)
 * with virtualized scrolling, derivedStateOf search filtering, and clean component extraction.
 */
@Composable
fun CalendarScreen(
    viewModel: MainViewModel,
    prayerTimes: PrayerTimes,
    hijriDate: HijriDateInfo
) {
    val events by viewModel.calendarEvents.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val selectedYear by viewModel.calendarSelectedYear.collectAsState()
    val selectedMonth by viewModel.calendarSelectedMonth.collectAsState()
    val selectedDay by viewModel.calendarSelectedDay.collectAsState()
    val viewMode by viewModel.calendarViewMode.collectAsState()
    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()

    var activeTopTab by remember { mutableIntStateOf(0) } // 0: المواعيد, 1: المهام, 2: الملاحظات
    var selectedNoteCategory by remember { mutableStateOf("ALL") } // ALL, PERSONAL, WORK
    var noteSearchQuery by remember { mutableStateOf("") }

    var showAddEventDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showUnifiedDayAddDialog by remember { mutableStateOf(false) }
    var prefilledNoteForEvent by remember { mutableStateOf<NoteEntity?>(null) }

    // 2. Use derivedStateOf for filtered notes search to prevent full recomposition
    val filteredNotes by remember(notes, noteSearchQuery, selectedNoteCategory) {
        derivedStateOf {
            notes.filter { note ->
                val matchesSearch = noteSearchQuery.isBlank() ||
                        note.title.contains(noteSearchQuery, ignoreCase = true) ||
                        note.content.contains(noteSearchQuery, ignoreCase = true)

                val matchesCat = when (selectedNoteCategory) {
                    "PERSONAL" -> note.agentTag == "SECRETARY"
                    "WORK" -> note.agentTag == "BUSINESS" || note.agentTag != "SECRETARY"
                    else -> true
                }
                matchesSearch && matchesCat
            }
        }
    }

    IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            ) {
                // Tab Content
                when (activeTopTab) {
                    0 -> {
                        // ================= CALENDAR VIEWS =================
                        when (viewMode) {
                            "WEEK" -> {
                                MonthGridView(
                                    selectedYear = selectedYear,
                                    selectedMonth = selectedMonth,
                                    selectedDay = selectedDay,
                                    events = events,
                                    prayerTimes = prayerTimes,
                                    hijriDate = hijriDate,
                                    viewMode = "WEEK",
                                    onViewModeChange = { viewModel.setCalendarViewMode(it) },
                                    onSelectDay = { viewModel.setCalendarSelectedDay(it) },
                                    onPrevMonth = { viewModel.prevCalendarMonth() },
                                    onNextMonth = { viewModel.nextCalendarMonth() },
                                    onGoToToday = { viewModel.goToToday() },
                                    onAddEventForDay = { showUnifiedDayAddDialog = true },
                                    onToggleComplete = { viewModel.toggleEventCompleted(it) },
                                    onDeleteEvent = { viewModel.deleteEvent(it) },
                                    modifier = Modifier.weight(1f),
                                    isEnglish = isEnglish,
                                    tasks = tasks,
                                    notes = notes,
                                    onToggleTaskComplete = { viewModel.toggleTaskCompleted(it) },
                                    onDeleteTask = { viewModel.deleteTask(it) },
                                    onDeleteNote = { viewModel.deleteNote(it) }
                                )
                            }

                            "DAY" -> {
                                DayTimelineView(
                                    selectedYear = selectedYear,
                                    selectedMonth = selectedMonth,
                                    selectedDay = selectedDay,
                                    events = events,
                                    prayerTimes = prayerTimes,
                                    hijriDate = hijriDate,
                                    viewMode = "DAY",
                                    onViewModeChange = { viewModel.setCalendarViewMode(it) },
                                    onGoToToday = { viewModel.goToToday() },
                                    onSelectDay = { viewModel.setCalendarSelectedDay(it) },
                                    onQuickAddEventAtHour = { showAddEventDialog = true },
                                    onToggleComplete = { viewModel.toggleEventCompleted(it) },
                                    onDeleteEvent = { viewModel.deleteEvent(it) },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            "LIST" -> {
                                AgendaListView(
                                    events = events,
                                    onToggleComplete = { viewModel.toggleEventCompleted(it) },
                                    onDeleteEvent = { viewModel.deleteEvent(it) },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            else -> {
                                MonthGridView(
                                    selectedYear = selectedYear,
                                    selectedMonth = selectedMonth,
                                    selectedDay = selectedDay,
                                    events = events,
                                    prayerTimes = prayerTimes,
                                    hijriDate = hijriDate,
                                    viewMode = "MONTH",
                                    onViewModeChange = { viewModel.setCalendarViewMode(it) },
                                    onSelectDay = { viewModel.setCalendarSelectedDay(it) },
                                    onPrevMonth = { viewModel.prevCalendarMonth() },
                                    onNextMonth = { viewModel.nextCalendarMonth() },
                                    onGoToToday = { viewModel.goToToday() },
                                    onAddEventForDay = { showUnifiedDayAddDialog = true },
                                    onToggleComplete = { viewModel.toggleEventCompleted(it) },
                                    onDeleteEvent = { viewModel.deleteEvent(it) },
                                    modifier = Modifier.weight(1f),
                                    isEnglish = isEnglish,
                                    tasks = tasks,
                                    notes = notes,
                                    onToggleTaskComplete = { viewModel.toggleTaskCompleted(it) },
                                    onDeleteTask = { viewModel.deleteTask(it) },
                                    onDeleteNote = { viewModel.deleteNote(it) }
                                )
                            }
                        }
                    }

                    1 -> {
                        // ================= TASKS VIEW =================
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEnglish) "Scheduled Tasks & Deliverables" else "المهام والأنشطة المجدولة",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = userPrimaryColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = if (isEnglish) "${tasks.count { it.isCompleted }}/${tasks.size} Done" else "المنجز: ${tasks.count { it.isCompleted }} من ${tasks.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = userPrimaryColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // 3 & 4. LazyColumn with key parameters and virtual scrolling optimization for large lists
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (tasks.isEmpty()) {
                                    item(key = "empty_tasks_item") {
                                        val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
                                        val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
                                        val taskSurfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)

                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = taskSurfaceColor,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = if (isEnglish) "No tasks" else "لا توجد مهام",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    items(
                                        items = tasks,
                                        key = { task -> task.id },
                                        contentType = { "task_item" }
                                    ) { task ->
                                        val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
                                        val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
                                        val taskSurfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)

                                        Card(
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = taskSurfaceColor),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                                            modifier = Modifier.fillMaxWidth().testTag("task_item_${task.id}")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Checkbox(
                                                    checked = task.isCompleted,
                                                    onCheckedChange = { viewModel.toggleTaskCompleted(task) },
                                                    colors = CheckboxDefaults.colors(checkedColor = userPrimaryColor)
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = task.title,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                                                    )
                                                    if (task.description.isNotEmpty()) {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = task.description,
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    Row(
                                                        modifier = Modifier.padding(top = 6.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Surface(
                                                            shape = RoundedCornerShape(6.dp),
                                                            color = when (task.priority) {
                                                                "HIGH" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                                                "MEDIUM" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                                                else -> Color(0xFF10B981).copy(alpha = 0.15f)
                                                            }
                                                        ) {
                                                            Text(
                                                                text = when (task.priority) {
                                                                    "HIGH" -> if (isEnglish) "High" else "أولوية قصوى"
                                                                    "MEDIUM" -> if (isEnglish) "Medium" else "متوسطة"
                                                                    else -> if (isEnglish) "Normal" else "عادية"
                                                                },
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = when (task.priority) {
                                                                    "HIGH" -> Color(0xFFEF4444)
                                                                    "MEDIUM" -> Color(0xFFF59E0B)
                                                                    else -> Color(0xFF10B981)
                                                                },
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                            )
                                                        }
                                                        Text(
                                                            text = "• ${task.category}",
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                                IconButton(onClick = { viewModel.deleteTask(task) }) {
                                                    Icon(
                                                        Icons.Default.DeleteOutline,
                                                        contentDescription = "حذف المهمة",
                                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
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

                    else -> {
                        // ================= NOTES VIEW =================
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Search in Notes
                            OutlinedTextField(
                                value = noteSearchQuery,
                                onValueChange = { noteSearchQuery = it },
                                placeholder = { Text(if (isEnglish) "Search notes..." else "ابحث في الملاحظات...", fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = userPrimaryColor) },
                                trailingIcon = {
                                    if (noteSearchQuery.isNotBlank()) {
                                        IconButton(onClick = { noteSearchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = if (isEnglish) "Clear" else "مسح")
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = userPrimaryColor,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("notes_search_field")
                            )

                            // Sub-category Filter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "ALL" to (if (isEnglish) "All Notes 📋" else "جميع الملاحظات 📋"),
                                    "PERSONAL" to (if (isEnglish) "Personal 👤" else "شخصية 👤"),
                                    "WORK" to (if (isEnglish) "Work 💼" else "أعمال 💼")
                                ).forEach { (catKey, label) ->
                                    val isSelected = selectedNoteCategory == catKey
                                    val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
                                    val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
                                    val noteChipSurface = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)

                                    Surface(
                                        onClick = { selectedNoteCategory = catKey },
                                        shape = RoundedCornerShape(18.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else noteChipSurface,
                                        shadowElevation = if (isLiveSky) 0.dp else (if (isSelected) 2.dp else 1.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else (if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = label,
                                            textAlign = TextAlign.Center,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            // 3 & 4. LazyColumn with key parameters and virtual scrolling for notes
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (filteredNotes.isEmpty()) {
                                    item(key = "empty_notes_item") {
                                        val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
                                        val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
                                        val emptyNoteSurface = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)

                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = emptyNoteSurface,
                                            tonalElevation = if (isLiveSky) 0.dp else 1.dp,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                            ),
                                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.NoteAdd,
                                                    contentDescription = null,
                                                    tint = userPrimaryColor,
                                                    modifier = Modifier.size(36.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = if (noteSearchQuery.isNotBlank()) {
                                                        if (isEnglish) "No results matching '$noteSearchQuery'" else "لا توجد نتائج تطابق '$noteSearchQuery'"
                                                    } else {
                                                        if (isEnglish) "No notes added yet" else "لا توجد ملاحظات مضافة بعد"
                                                    },
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = if (isEnglish) "Add a new note to auto-link to calendar as a smart task" else "أضف ملاحظة جديدة لربطها بالتقويم تلقائياً كمهمة ذكية",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    items(
                                        items = filteredNotes,
                                        key = { note -> note.id },
                                        contentType = { "note_item" }
                                    ) { note ->
                                        NoteCard(
                                            note = note,
                                            userPrimaryColor = userPrimaryColor,
                                            isEnglish = isEnglish,
                                            onDelete = { viewModel.deleteNote(note) },
                                            onConvertToEvent = {
                                                prefilledNoteForEvent = note
                                                showAddEventDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Standalone circular button next to bottom container
            val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
            val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
            val primaryAccent = MaterialTheme.colorScheme.primary

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp, start = 16.dp, end = 16.dp, top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Standalone Circular Add Button (+)
                Surface(
                    onClick = {
                        when (activeTopTab) {
                            0 -> {
                                prefilledNoteForEvent = null
                                showUnifiedDayAddDialog = true
                            }
                            1 -> {
                                showAddTaskDialog = true
                            }
                            else -> {
                                showAddNoteDialog = true
                            }
                        }
                    },
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = primaryAccent,
                    shadowElevation = if (isLiveSky) 0.dp else 8.dp,
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("unified_add_button")
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = if (isEnglish) "Add" else "إضافة",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 2. Bottom Navigation Bar Container
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 0.95f),
                    tonalElevation = if (isLiveSky) 0.dp else 8.dp,
                    shadowElevation = if (isLiveSky) 0.dp else 12.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("unified_calendar_bottom_bar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Section 0: Appointments
                        val isApptSelected = activeTopTab == 0
                        Surface(
                            onClick = { activeTopTab = 0 },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isApptSelected) primaryAccent else Color.Transparent,
                            modifier = Modifier.testTag("tab_appointments")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = if (isApptSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isEnglish) "Appointments" else "المواعيد",
                                    fontWeight = if (isApptSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isApptSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Section 1: Tasks
                        val isTasksSelected = activeTopTab == 1
                        Surface(
                            onClick = { activeTopTab = 1 },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isTasksSelected) primaryAccent else Color.Transparent,
                            modifier = Modifier.testTag("tab_tasks")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isTasksSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isEnglish) "Tasks" else "المهام",
                                    fontWeight = if (isTasksSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTasksSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Section 2: Notes
                        val isNotesSelected = activeTopTab == 2
                        Surface(
                            onClick = { activeTopTab = 2 },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isNotesSelected) primaryAccent else Color.Transparent,
                            modifier = Modifier.testTag("tab_notes")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                                    contentDescription = null,
                                    tint = if (isNotesSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isEnglish) "Notes" else "الملاحظات",
                                    fontWeight = if (isNotesSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isNotesSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Separate Dialog Components
        if (showAddEventDialog) {
            AddEventDialog(
                onDismiss = {
                    showAddEventDialog = false
                    prefilledNoteForEvent = null
                },
                onSave = { title, desc, cat, sh, sm, eh, em, loc ->
                    val conflict = viewModel.addEvent(
                        title = title,
                        description = desc,
                        category = cat,
                        startHour = sh,
                        startMin = sm,
                        endHour = eh,
                        endMin = em,
                        location = loc,
                        day = selectedDay,
                        month = selectedMonth,
                        year = selectedYear
                    )
                    showAddEventDialog = false
                    prefilledNoteForEvent = null
                    conflict
                }
            )
        }

        if (showAddNoteDialog) {
            AddNoteWithSmartLinkingDialog(
                onDismiss = { showAddNoteDialog = false },
                onSave = { title, content, cat, reminderDateMillis ->
                    viewModel.addNoteWithSmartLinking(title, content, cat, reminderDateMillis)
                    showAddNoteDialog = false
                }
            )
        }

        if (showAddTaskDialog) {
            CalendarAddTaskDialog(
                userPrimaryColor = userPrimaryColor,
                isEnglish = isEnglish,
                onDismiss = { showAddTaskDialog = false },
                onSave = { title, desc, priority, category ->
                    viewModel.insertTask(
                        TaskEntity(
                            title = title,
                            description = desc,
                            priority = priority,
                            category = category,
                            isCompleted = false,
                            dueDateMillis = System.currentTimeMillis() + 86400000L
                        )
                    )
                    showAddTaskDialog = false
                }
            )
        }

        if (showUnifiedDayAddDialog) {
            UnifiedDayAddDialog(
                day = selectedDay,
                month = selectedMonth,
                year = selectedYear,
                userPrimaryColor = userPrimaryColor,
                isEnglish = isEnglish,
                onDismiss = { showUnifiedDayAddDialog = false },
                onAddEvent = { title, desc, cat, sh, sm, eh, em, loc ->
                    viewModel.addEvent(
                        title = title,
                        description = desc,
                        category = cat,
                        startHour = sh,
                        startMin = sm,
                        endHour = eh,
                        endMin = em,
                        location = loc,
                        day = selectedDay,
                        month = selectedMonth,
                        year = selectedYear
                    )
                    showUnifiedDayAddDialog = false
                },
                onAddTask = { title, desc, priority, cat ->
                    val taskCal = java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.YEAR, selectedYear)
                        set(java.util.Calendar.MONTH, selectedMonth)
                        set(java.util.Calendar.DAY_OF_MONTH, selectedDay)
                        set(java.util.Calendar.HOUR_OF_DAY, 12)
                        set(java.util.Calendar.MINUTE, 0)
                    }
                    viewModel.insertTask(
                        TaskEntity(
                            title = title,
                            description = desc,
                            priority = priority,
                            category = cat,
                            isCompleted = false,
                            dueDateMillis = taskCal.timeInMillis
                        )
                    )
                    showUnifiedDayAddDialog = false
                },
                onAddNote = { title, content, cat ->
                    val noteCal = java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.YEAR, selectedYear)
                        set(java.util.Calendar.MONTH, selectedMonth)
                        set(java.util.Calendar.DAY_OF_MONTH, selectedDay)
                    }
                    viewModel.addNoteWithSmartLinking(
                        title = title,
                        content = content,
                        category = cat,
                        reminderDateMillis = noteCal.timeInMillis
                    )
                    showUnifiedDayAddDialog = false
                }
            )
        }
    }
}

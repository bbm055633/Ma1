package com.example.ui.screens.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalendarEventEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TaskEntity
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Redesigned, High-Performance Calendar & Notes Screen
 * Featuring 3 specialized views (Month Grid, Day Timeline, Agenda List)
 * with Saudi Prayer Times integration, conflict detection, and smart note linking.
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

    var activeTopTab by remember { mutableStateOf(0) } // 0: المواعيد, 1: المهام, 2: الملاحظات
    var selectedNoteCategory by remember { mutableStateOf("ALL") } // ALL, PERSONAL, WORK
    var noteSearchQuery by remember { mutableStateOf("") }

    var showAddEventDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var prefilledNoteForEvent by remember { mutableStateOf<NoteEntity?>(null) }

    com.example.ui.components.IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 80.dp, start = 16.dp, end = 16.dp)
            ) {
                // Tab Content
            when (activeTopTab) {
                0 -> {
                    // ================= CALENDAR VIEWS =================
                    when (viewMode) {
                        "WEEK" -> {
                            CalendarMonthGridView(
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
                                onAddEventForDay = { showAddEventDialog = true },
                                onToggleComplete = { viewModel.toggleEventCompleted(it) },
                                onDeleteEvent = { viewModel.deleteEvent(it) },
                                modifier = Modifier.weight(1f),
                                isEnglish = isEnglish
                            )
                        }

                        "DAY" -> {
                            CalendarDayTimelineView(
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
                            CalendarAgendaListView(
                                events = events,
                                onToggleComplete = { viewModel.toggleEventCompleted(it) },
                                onDeleteEvent = { viewModel.deleteEvent(it) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        else -> {
                            CalendarMonthGridView(
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
                                onAddEventForDay = { showAddEventDialog = true },
                                onToggleComplete = { viewModel.toggleEventCompleted(it) },
                                onDeleteEvent = { viewModel.deleteEvent(it) },
                                modifier = Modifier.weight(1f),
                                isEnglish = isEnglish
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

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (tasks.isEmpty()) {
                                item {
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
                                items(tasks, key = { it.id }) { task ->
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
                        // Search in Notes: One UI 9 rounded container
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

                        // Sub-category Filter: [الكل] | [ملاحظات شخصية] | [ملاحظات عمل]
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

                        val filteredNotes = notes.filter { note ->
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

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (filteredNotes.isEmpty()) {
                                item {
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
                                items(filteredNotes) { note ->
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

        // Standalone circular button next to elongated horizontal container holding Appointments, Tasks, Notes
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
                            showAddEventDialog = true
                        }
                        1 -> {
                            showAddTaskDialog = true
                        }
                        else -> {
                            showAddNoteDialog = true
                        }
                    }
                },
                shape = CircleShape,
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

            // 2. Elongated Horizontal Container holding (Appointments - Tasks - Notes)
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
                    // Section 0: المواعيد (Appointments)
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

                    // Section 1: المهام (Tasks)
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

                    // Section 2: الملاحظات (Notes)
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
    }
}

@Composable
fun CalendarAddTaskDialog(
    userPrimaryColor: Color,
    isEnglish: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("HIGH") }
    var category by remember { mutableStateOf("PROJECT") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isEnglish) "Add New Task 📝" else "إضافة مهمة جديدة 📝",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isEnglish) "Task Title *" else "عنوان المهمة *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (isEnglish) "Description (Optional)" else "الوصف أو الملاحظة (اختياري)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Text(
                    text = if (isEnglish) "Priority Level:" else "مستوى الأولوية:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("HIGH" to (if (isEnglish) "High" else "قصوى"), "MEDIUM" to (if (isEnglish) "Medium" else "متوسطة"), "LOW" to (if (isEnglish) "Low" else "عادية")).forEach { (pKey, pLabel) ->
                        val isSelected = priority == pKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { priority = pKey },
                            label = { Text(pLabel, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isEnglish) "Cancel" else "إلغاء")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title, description, priority, category)
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = userPrimaryColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isEnglish) "Save" else "حفظ المهمة", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: NoteEntity,
    userPrimaryColor: Color,
    isEnglish: Boolean = false,
    onDelete: () -> Unit,
    onConvertToEvent: () -> Unit
) {
    val isWork = note.agentTag == "BUSINESS" || note.agentTag != "SECRETARY"
    val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
    val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = surfaceColor,
        shadowElevation = if (isLiveSky) 0.dp else 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isWork) userPrimaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.background
                    ) {
                        Text(
                            text = if (isWork) {
                                if (isEnglish) "Work Note 💼" else "ملاحظة عمل 💼"
                            } else {
                                if (isEnglish) "Personal Note 👤" else "ملاحظة شخصية 👤"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWork) userPrimaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = note.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = if (isEnglish) "Delete" else "حذف",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1-Click Convert to Calendar Event
                Surface(
                    onClick = onConvertToEvent,
                    shape = RoundedCornerShape(12.dp),
                    color = userPrimaryColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = userPrimaryColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isEnglish) "Convert to Event" else "تحويل لموعد بالتقويم",
                            fontSize = 11.sp,
                            color = userPrimaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val dateLocale = if (isEnglish) Locale.US else Locale.forLanguageTag("ar")
                Text(
                    text = SimpleDateFormat("dd MMMM, hh:mm a", dateLocale).format(note.timestamp),
                    fontSize = 11.sp,
                    color = Color(0xFF6E685F).copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun AddNoteWithSmartLinkingDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, category: String, reminderDateMillis: Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("PERSONAL") }
    var enableSmartLinking by remember { mutableStateOf(true) }
    var selectedDueDaysAhead by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إضافة ملاحظة جديدة (Smart Linking) 📝", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الملاحظة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("PERSONAL" to "شخصية 👤", "WORK" to "عمل 💼").forEach { (catKey, label) ->
                        val isSelected = category == catKey
                        Surface(
                            onClick = { category = catKey },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("تفاصيل الملاحظة") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Smart Linking Section
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("الربط التلقائي بالتقويم", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("تظهر كموعد ذكي مع تذكير مسبق", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = enableSmartLinking,
                                onCheckedChange = { enableSmartLinking = it }
                            )
                        }

                        if (enableSmartLinking) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("موعد الاستحقاق والتذكير:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(0 to "اليوم", 1 to "غداً", 2 to "بعد غد").forEach { (days, lbl) ->
                                    val isSelected = selectedDueDaysAhead == days
                                    Surface(
                                        onClick = { selectedDueDaysAhead = days },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = lbl,
                                            textAlign = TextAlign.Center,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val reminderDateMillis = if (enableSmartLinking) {
                            Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, selectedDueDaysAhead)
                                set(Calendar.HOUR_OF_DAY, 10)
                                set(Calendar.MINUTE, 0)
                            }.timeInMillis
                        } else null

                        onSave(title, content, category, reminderDateMillis)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("حفظ وربط بالتقويم")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun CalendarEventCard(
    event: CalendarEventEntity,
    timeFormat: SimpleDateFormat,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = when (event.category) {
        "MEETING" -> EmeraldPrimary
        "PRAYER" -> PrayerPurple
        "WORK" -> InfoBlue
        else -> SaudiGoldDark
    }

    val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
    val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor,
        tonalElevation = if (isLiveSky) 0.dp else 2.dp,
        border = if (event.isPrayerOverlapWarning) androidx.compose.foundation.BorderStroke(1.5.dp, WarningOrange) else androidx.compose.foundation.BorderStroke(1.dp, if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onToggleComplete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (event.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = "اكتمال",
                            tint = if (event.isCompleted) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (event.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                        Text(
                            text = "${timeFormat.format(event.startTimeMillis)} - ${timeFormat.format(event.endTimeMillis)}" +
                                    if (event.location.isNotBlank()) " • 📍 ${event.location}" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = categoryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = when (event.category) {
                                "MEETING" -> "اجتماع"
                                "PRAYER" -> "صلاة"
                                "WORK" -> "عمل"
                                else -> "ملاحظة"
                            },
                            color = categoryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (event.isPrayerOverlapWarning) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WarningOrange.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "تنبيه: وقت هذا الموعد يتعارض مع وقت الأذان وأداء الصلاة!",
                            color = WarningOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

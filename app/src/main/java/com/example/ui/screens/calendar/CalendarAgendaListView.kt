package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.EmeraldPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Full Agenda and Chronological List View for all Calendar Events,
 * with search and category filters.
 */
@Composable
fun CalendarAgendaListView(
    events: List<CalendarEventEntity>,
    onToggleComplete: (CalendarEventEntity) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
    val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)
    val borderColor = if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline

    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.forLanguageTag("ar")) }
    val dateFormat = remember { SimpleDateFormat("EEEE، d MMMM yyyy", Locale.forLanguageTag("ar")) }

    // Filter events
    val filteredEvents = remember(events, searchQuery, selectedCategory) {
        events.filter { event ->
            val matchesSearch = searchQuery.isBlank() ||
                    event.title.contains(searchQuery, ignoreCase = true) ||
                    event.description.contains(searchQuery, ignoreCase = true) ||
                    event.location.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedCategory) {
                "WORK" -> event.category == "WORK" || event.category == "MEETING"
                "PERSONAL" -> event.category == "PERSONAL"
                "PRAYER" -> event.category == "PRAYER" || event.category == "FASTING"
                else -> true
            }

            matchesSearch && matchesCategory
        }.sortedBy { it.startTimeMillis }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search & Filter Box
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Search Input: One UI 9 rounded container
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث عن موعد، اجتماع، أو موقع...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = primaryColor) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = borderColor,
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("calendar_agenda_search")
                )

                // Category Filter Pills: One UI 9 pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "ALL" to "الكل 📋",
                        "WORK" to "أعمال 💼",
                        "PERSONAL" to "شخصي 👤",
                        "PRAYER" to "عبادة 🕌"
                    ).forEach { (catKey, label) ->
                        val isSelected = selectedCategory == catKey
                        Surface(
                            onClick = { selectedCategory = catKey },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) primaryColor else surfaceColor,
                            shadowElevation = if (isSelected) 2.dp else 1.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) primaryColor else borderColor
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Events list or empty state
        if (filteredEvents.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = surfaceColor,
                    shadowElevation = if (isLiveSky) 0.dp else 3.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        borderColor
                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "لم يتم العثور على نتائج تطابق '$searchQuery'" else "لا توجد مواعيد مضافة في هذه الفئة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1C1B1A)
                        )
                    }
                }
            }
        } else {
            // Group events by date
            val groupedByDay = filteredEvents.groupBy { event ->
                val cal = Calendar.getInstance().apply { timeInMillis = event.startTimeMillis }
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }

            groupedByDay.forEach { (dayMillis, dayEventsList) ->
                item {
                    val dateHeader = dateFormat.format(dayMillis)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                        )
                        Text(
                            text = dateHeader,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = primaryColor
                        )
                    }
                }

                items(dayEventsList) { event ->
                    CalendarEventCard(
                        event = event,
                        timeFormat = timeFormat,
                        onToggleComplete = { onToggleComplete(event) },
                        onDelete = { onDeleteEvent(event) }
                    )
                }
            }
        }
    }
}

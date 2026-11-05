package com.example.ui.screens.calendar.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UnifiedDayAddDialog(
    day: Int,
    month: Int,
    year: Int,
    userPrimaryColor: Color,
    isEnglish: Boolean,
    onDismiss: () -> Unit,
    onAddEvent: (title: String, desc: String, category: String, startHour: Int, startMin: Int, endHour: Int, endMin: Int, location: String) -> Unit,
    onAddTask: (title: String, desc: String, priority: String, category: String) -> Unit,
    onAddNote: (title: String, content: String, category: String) -> Unit
) {
    var selectedType by remember { mutableIntStateOf(0) } // 0: موعد, 1: مهمة, 2: ملاحظة
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("WORK") }
    var priority by remember { mutableStateOf("HIGH") }
    var startHour by remember { mutableIntStateOf(9) }
    var startMin by remember { mutableIntStateOf(0) }
    var endHour by remember { mutableIntStateOf(10) }
    var endMin by remember { mutableIntStateOf(0) }
    var location by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("unified_day_add_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title and Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "Add to Schedule" else "إضافة إلى الجدول",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = userPrimaryColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "$day/${month + 1}/$year",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = userPrimaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Type Segmented Switcher (موعد | مهمة | ملاحظة)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        0 to (if (isEnglish) "Event 📅" else "موعد 📅"),
                        1 to (if (isEnglish) "Task 📝" else "مهمة 📝"),
                        2 to (if (isEnglish) "Note 📋" else "ملاحظة 📋")
                    ).forEach { (typeIdx, label) ->
                        val isSelected = selectedType == typeIdx
                        Surface(
                            onClick = { selectedType = typeIdx },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) userPrimaryColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
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

                // Input: Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text(
                            when (selectedType) {
                                0 -> if (isEnglish) "Event Title *" else "عنوان الموعد *"
                                1 -> if (isEnglish) "Task Title *" else "عنوان المهمة *"
                                else -> if (isEnglish) "Note Title *" else "عنوان الملاحظة *"
                            }
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("day_add_title_input")
                )

                // Dynamic Fields based on Type
                when (selectedType) {
                    0 -> {
                        // Event: Category & Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("WORK", "MEETING", "PERSONAL", "PRAYER").forEach { cat ->
                                val isSelected = category == cat
                                val label = when (cat) {
                                    "WORK" -> if (isEnglish) "Work" else "عمل"
                                    "MEETING" -> if (isEnglish) "Meeting" else "اجتماع"
                                    "PERSONAL" -> if (isEnglish) "Personal" else "شخصي"
                                    else -> if (isEnglish) "Faith" else "عبادة"
                                }
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { category = cat },
                                    label = { Text(label, fontSize = 10.sp) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Time: Start & End hours
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = String.format("%02d:%02d", startHour, startMin),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isEnglish) "Starts" else "البداية", fontSize = 11.sp) },
                                trailingIcon = {
                                    IconButton(onClick = { startHour = (startHour + 1) % 24 }) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = String.format("%02d:%02d", endHour, endMin),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isEnglish) "Ends" else "النهاية", fontSize = 11.sp) },
                                trailingIcon = {
                                    IconButton(onClick = { endHour = (endHour + 1) % 24 }) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text(if (isEnglish) "Location / Link (Optional)" else "الموقع أو الرابط (اختياري)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    1 -> {
                        // Task: Priority
                        Text(
                            text = if (isEnglish) "Priority:" else "الأولوية:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "HIGH" to (if (isEnglish) "High" else "قصوى"),
                                "MEDIUM" to (if (isEnglish) "Medium" else "متوسطة"),
                                "LOW" to (if (isEnglish) "Low" else "عادية")
                            ).forEach { (pKey, pLabel) ->
                                val isSelected = priority == pKey
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { priority = pKey },
                                    label = { Text(pLabel, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(if (isEnglish) "Details (Optional)" else "تفاصيل المهمة (اختياري)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    else -> {
                        // Note: Category & Content
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "SECRETARY" to (if (isEnglish) "Personal" else "شخصية"),
                                "BUSINESS" to (if (isEnglish) "Work" else "أعمال")
                            ).forEach { (catKey, catLabel) ->
                                val isSelected = category == catKey
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { category = catKey },
                                    label = { Text(catLabel, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(if (isEnglish) "Note Content *" else "محتوى الملاحظة *") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("day_add_content_input"),
                            minLines = 3
                        )
                    }
                }

                // Actions: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
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
                                when (selectedType) {
                                    0 -> onAddEvent(title, description, category, startHour, startMin, endHour, endMin, location)
                                    1 -> onAddTask(title, description, priority, category)
                                    else -> onAddNote(title, description, category)
                                }
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = userPrimaryColor),
                        modifier = Modifier.weight(1f).testTag("day_add_save_button")
                    ) {
                        Text(if (isEnglish) "Save" else "حفظ", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

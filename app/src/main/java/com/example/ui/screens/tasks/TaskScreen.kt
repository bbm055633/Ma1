package com.example.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
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
import com.example.data.local.TaskEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: MainViewModel
) {
    val tasks by viewModel.tasks.collectAsState()
    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, PENDING, COMPLETED, HIGH
    var showAddTaskDialog by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var newPriority by remember { mutableStateOf("HIGH") }
    var newCategory by remember { mutableStateOf("PROJECT") }

    val filteredTasks = tasks.filter { task ->
        when (selectedFilter) {
            "PENDING" -> !task.isCompleted
            "COMPLETED" -> task.isCompleted
            "HIGH" -> task.priority == "HIGH" && !task.isCompleted
            else -> true
        }
    }

    val completedCount = tasks.count { it.isCompleted }
    val pendingCount = tasks.count { !it.isCompleted }
    val highCount = tasks.count { it.priority == "HIGH" && !it.isCompleted }

    com.example.ui.components.IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header & Metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "إدارة المهام المتقدمة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF1C1B1A)
                        )
                        Text(
                            text = "نظام مستقل ومتكامل مع التقويم والذكاء الاصطناعي",
                            fontSize = 12.sp,
                            color = Color(0xFF6E685F)
                        )
                    }

                    FloatingActionButton(
                        onClick = { showAddTaskDialog = true },
                        containerColor = userPrimaryColor,
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("add_task_fab")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة مهمة")
                    }
                }

                // Metrics Overview Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TaskMetricCard(
                        title = "قيد التنفيذ",
                        count = "$pendingCount",
                        color = userPrimaryColor,
                        modifier = Modifier.weight(1f)
                    )
                    TaskMetricCard(
                        title = "عالية الأهمية",
                        count = "$highCount",
                        color = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    TaskMetricCard(
                        title = "المنجزة",
                        count = "$completedCount",
                        color = Color(0xFF107C41),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Filter Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        "ALL" to "الكل",
                        "PENDING" to "المعلقة",
                        "HIGH" to "الهامة",
                        "COMPLETED" to "المنجزة"
                    )
                    filters.forEach { (key, label) ->
                        val isSelected = selectedFilter == key
                        Surface(
                            onClick = { selectedFilter = key },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) userPrimaryColor else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) userPrimaryColor else Color(0xFFE8E4DC)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF1C1B1A),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }

                // Tasks List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (filteredTasks.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                                modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Outlined.TaskAlt, contentDescription = null, tint = userPrimaryColor, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(text = "لا توجد مهام مطابقة للتصنيف الحالي", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1C1B1A))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "اضغط على زر الإضافة (+) لإنشاء مهمة جديدة", fontSize = 12.sp, color = Color(0xFF6E685F))
                                }
                            }
                        }
                    } else {
                        items(filteredTasks) { task ->
                            TaskItemCard(
                                task = task,
                                userPrimaryColor = userPrimaryColor,
                                onToggleComplete = { viewModel.toggleTaskCompleted(task) },
                                onDelete = { viewModel.deleteTask(task) },
                                onSyncToCalendar = { viewModel.syncTaskToCalendar(task) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("إنشاء مهمة جديدة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("عنوان المهمة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("التفاصيل والوصف") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { newPriority = if (newPriority == "HIGH") "NORMAL" else "HIGH" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (newPriority == "HIGH") Color(0xFFD97706) else Color.Gray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (newPriority == "HIGH") "⚠️ مهمة عاجلة" else "عادية", fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                newCategory = when (newCategory) {
                                    "PROJECT" -> "MEETING"
                                    "MEETING" -> "PERSONAL"
                                    else -> "PROJECT"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = userPrimaryColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("التصنيف: $newCategory", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.insertTask(
                                TaskEntity(
                                    title = newTitle,
                                    description = newDescription,
                                    priority = newPriority,
                                    category = newCategory,
                                    dueDateMillis = System.currentTimeMillis() + 86400000L
                                )
                            )
                            newTitle = ""
                            newDescription = ""
                            showAddTaskDialog = false
                        }
                    }
                ) {
                    Text("إضافة", fontWeight = FontWeight.Bold, color = userPrimaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun TaskMetricCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = count, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            Text(text = title, fontSize = 11.sp, color = Color(0xFF6E685F))
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    userPrimaryColor: Color,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onSyncToCalendar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.forLanguageTag("ar"))
    val dateStr = dateFormat.format(Date(task.dueDateMillis))

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(checkedColor = userPrimaryColor)
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (task.isCompleted) Color(0xFF9CA3AF) else Color(0xFF1C1B1A),
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (task.priority == "HIGH" && !task.isCompleted) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFEF3C7)) {
                            Text("عاجل", color = Color(0xFFD97706), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = Color(0xFF6E685F)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "📅 $dateStr", fontSize = 10.sp, color = Color(0xFF9CA3AF))
                    Surface(shape = RoundedCornerShape(6.dp), color = userPrimaryColor.copy(alpha = 0.1f)) {
                        Text(task.category, color = userPrimaryColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onSyncToCalendar, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Outlined.EventNote, contentDescription = "مزامنة للتقويم", tint = userPrimaryColor, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "حذف المهمة", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

package com.example.ui.screens.calendar.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import java.util.Calendar

@Composable
fun AddNoteWithSmartLinkingDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, category: String, reminderDateMillis: Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("PERSONAL") }
    var enableSmartLinking by remember { mutableStateOf(true) }
    var selectedDueDaysAhead by remember { mutableIntStateOf(1) }

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

package com.example.ui.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CalendarEventEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
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

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEF4444).copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف الفعالية",
                    tint = Color.White
                )
            }
        }
    ) {
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
}

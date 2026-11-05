package com.example.ui.screens.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NoteEntity
import java.text.SimpleDateFormat
import java.util.Locale

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

package com.example.ui.screens.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.prayer.PrayerTimes

@Composable
fun NextPrayerHeroCard(
    prayerTimes: PrayerTimes,
    isEnglish: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    // Live countdown calculation
    var remainingSeconds by remember(prayerTimes.nextPrayerName, prayerTimes.minutesUntilNext) {
        mutableStateOf((prayerTimes.minutesUntilNext * 60).coerceAtLeast(0))
    }

    LaunchedEffect(prayerTimes.nextPrayerName, prayerTimes.minutesUntilNext) {
        remainingSeconds = (prayerTimes.minutesUntilNext * 60).coerceAtLeast(0)
        while (remainingSeconds > 0) {
            kotlinx.coroutines.delay(1000L)
            remainingSeconds--
        }
    }

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60
    val countdownFormatted = if (hours >= 1) {
        String.format(java.util.Locale.US, "%d:%02d", hours, minutes)
    } else {
        String.format(java.util.Locale.US, "%d:%02d", minutes, seconds)
    }

    // Glowing progress pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Calculate progress ratio (assumes total window between prayers ~ 4 hours = 14400s)
    val totalWindowSeconds = 14400f
    val progressRatio = (1f - (remainingSeconds.toFloat() / totalWindowSeconds)).coerceIn(0.08f, 1.0f)

    // Rak'ahs lookup
    val rakahCount = remember(prayerTimes.nextPrayerName) {
        when {
            prayerTimes.nextPrayerName.contains("فجر") || prayerTimes.nextPrayerName.contains("Fajr", ignoreCase = true) -> 2
            prayerTimes.nextPrayerName.contains("ظهر") || prayerTimes.nextPrayerName.contains("Dhuhr", ignoreCase = true) -> 4
            prayerTimes.nextPrayerName.contains("عصر") || prayerTimes.nextPrayerName.contains("Asr", ignoreCase = true) -> 4
            prayerTimes.nextPrayerName.contains("مغرب") || prayerTimes.nextPrayerName.contains("Maghrib", ignoreCase = true) -> 3
            prayerTimes.nextPrayerName.contains("عشاء") || prayerTimes.nextPrayerName.contains("Isha", ignoreCase = true) -> 4
            else -> 2
        }
    }

    // Hero Card Container
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        shadowElevation = 12.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .testTag("home_next_prayer_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0x991B4332), // rgba(27, 67, 50, 0.6)
                            Color(0x660F2D24)  // rgba(15, 45, 36, 0.4)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0x26D4A373), // rgba(212, 163, 115, 0.15)
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Thin top decorative gradient bar (2dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFD4A373), // Gold
                                    Color(0xFF52B788), // Green
                                    Color(0xFF00A896)  // Teal
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Bar: Badge "القادمة" + Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge "قادمة" with pulsing dot & clock icon
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x33D4A373), // Gold transparent
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66D4A373))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD4A373).copy(alpha = pulseAlpha))
                                )
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFFD4A373),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isEnglish) "Next" else "القادمة",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFE3B3)
                                )
                            }
                        }

                        // Next Prayer Time
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33D4A373))
                        ) {
                            Text(
                                text = prayerTimes.nextPrayerTime,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFE3B3),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Main Info: Prayer Name + Live Countdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = prayerTimes.nextPrayerName,
                                fontFamily = com.example.ui.theme.AppTypography.headings,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isEnglish) "Time remaining until Adhan" else "المتبقي حتى يحين موعد الأذان",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }

                        // Live Countdown Badge (HH:MM:SS)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x40D4A373))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFFD4A373),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = countdownFormatted,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Glowing Progress Bar (Gradient Pulse Animation)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressRatio)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFFD4A373).copy(alpha = pulseAlpha),
                                            Color(0xFF52B788),
                                            Color(0xFF00A896)
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }
    }

    // Details Dialog on Card Click
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B4332)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Mosque,
                            contentDescription = null,
                            tint = Color(0xFFD4A373),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = prayerTimes.nextPrayerName,
                            fontFamily = com.example.ui.theme.AppTypography.headings,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isEnglish) "Prayer Details" else "تفاصيل الصلاة",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Item 1: Number of Rak'ahs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEnglish) "Number of Rak'ahs" else "عدد الركعات",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1B4332)
                        ) {
                            Text(
                                text = if (isEnglish) "$rakahCount Rak'ahs" else "$rakahCount ركعات",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Item 2: Status (State)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEnglish) "Status" else "الحالة",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x33D4A373),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66D4A373))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD4A373))
                                )
                                Text(
                                    text = if (isEnglish) "Upcoming ◉" else "قادمة ◉",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD4A373)
                                )
                            }
                        }
                    }

                    // Item 3: Remaining Time & Progress
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) "Remaining Time" else "الوقت المتبقي",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = countdownFormatted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B4332)
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF1B4332),
                            trackColor = Color.Gray.copy(alpha = 0.2f)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text = if (isEnglish) "Close" else "إغلاق",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B4332)
                    )
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

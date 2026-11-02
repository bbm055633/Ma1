package com.example.ui.screens.worship

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Motivational Unified Commitment Banner & Hero Countdown Card
 * Combines date headers, hero prayer countdown, 5-prayer attendance circles, and streak badges.
 */
@Composable
fun WorshipCommitmentBanner(
    fajrStatus: PrayerAttendance,
    dhuhrStatus: PrayerAttendance,
    asrStatus: PrayerAttendance,
    maghribStatus: PrayerAttendance,
    ishaStatus: PrayerAttendance,
    sunnahCount: Int,
    streakDays: Int,
    hijriDate: HijriDateInfo,
    dayOffset: Int,
    prayerTimes: PrayerTimes,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onOpenQibla: (() -> Unit)? = null,
    onToggleAlert: (() -> Unit)? = null,
    isAlertEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val statuses = listOf(
        Triple("الفجر", fajrStatus, "🕌"),
        Triple("الظهر", dhuhrStatus, "☀️"),
        Triple("العصر", asrStatus, "🌤️"),
        Triple("المغرب", maghribStatus, "🌅"),
        Triple("العشاء", ishaStatus, "🌌")
    )

    val completedCount = statuses.count { it.second != PrayerAttendance.NONE }
    val jamaahCount = statuses.count { it.second == PrayerAttendance.JAMAAH }
    val progress = completedCount / 5f
    val isAllCompleted = completedCount == 5

    // Gregorian date string
    val gregorianDateText = remember(dayOffset) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -dayOffset)
        val dayName = SimpleDateFormat("EEEE", Locale("ar")).format(cal.time)
        val dayNumMonth = SimpleDateFormat("d MMMM", Locale("ar")).format(cal.time)
        "$dayName $dayNumMonth"
    }

    // Live countdown calculation
    var remainingSeconds by remember(prayerTimes.nextPrayerTime, prayerTimes.minutesUntilNext) {
        mutableLongStateOf((prayerTimes.minutesUntilNext * 60L).coerceAtLeast(0L))
    }

    LaunchedEffect(prayerTimes.nextPrayerTime, prayerTimes.minutesUntilNext) {
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
    }

    val isArrived = remainingSeconds <= 0
    val isApproaching = remainingSeconds in 1..900 // <= 15 minutes

    val hours = remainingSeconds / 3600
    val mins = (remainingSeconds % 3600) / 60
    val secs = remainingSeconds % 60
    val countdownFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs)

    val timerTextColor by animateColorAsState(
        targetValue = when {
            isArrived -> SaudiGold
            isApproaching -> Color(0xFFFF9800) // Orange warning for < 15 mins
            else -> Color.White
        },
        label = "timer_color"
    )

    val primaryBg = MaterialTheme.colorScheme.primary
    val surfaceVariantBg = MaterialTheme.colorScheme.surfaceVariant

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAllCompleted) SaudiGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("worship_commitment_banner")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryBg,
                            primaryBg.copy(alpha = 0.88f),
                            surfaceVariantBg.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            // Background subtle stars decor
            Canvas(modifier = Modifier.matchParentSize()) {
                val starColor = Color.White.copy(alpha = 0.12f)
                val stars = listOf(
                    Offset(size.width * 0.15f, size.height * 0.18f),
                    Offset(size.width * 0.85f, size.height * 0.12f),
                    Offset(size.width * 0.75f, size.height * 0.45f),
                    Offset(size.width * 0.10f, size.height * 0.55f),
                    Offset(size.width * 0.90f, size.height * 0.82f),
                    Offset(size.width * 0.35f, size.height * 0.88f)
                )
                stars.forEach { pos ->
                    drawCircle(color = starColor, radius = 2.5.dp.toPx(), center = pos)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 1. Top Row: Hijri Date & Day Switcher (Right), Gregorian Date Badge (Left)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Gregorian Date Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = gregorianDateText,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    // Right: Hijri Date & Day Navigator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${hijriDate.day} ${hijriDate.monthNameAr} ${hijriDate.year} هـ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = Color.White
                            )
                            Text(
                                text = if (dayOffset == 0) "اليوم (أم القرى)" else if (dayOffset == 1) "غداً" else if (dayOffset == -1) "أمس" else "يوم $dayOffset",
                                fontSize = 10.5.sp,
                                color = SaudiGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.18f)
                        ) {
                            Row(modifier = Modifier.padding(2.dp)) {
                                IconButton(onClick = onPreviousDay, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "السابق", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = onNextDay, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = "التالي", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Hero Center Section: Next Prayer Name, Countdown Timer, Azan Time
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isArrived) "حان وقت صلاة ${prayerTimes.nextPrayerName ?: "العصر"}" else "صلاة ${prayerTimes.nextPrayerName ?: "العصر"}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "متبقي",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Text(
                        text = countdownFormatted,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = timerTextColor,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "موعد الأذان: ${prayerTimes.nextPrayerTime ?: "--:--"}",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Progress Bar: Completed Prayers out of 5
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(if (isAllCompleted) "👑" else "🌟", fontSize = 16.sp)
                        Text(
                            text = if (isAllCompleted) "تاج الالتزام مكتمل!" else "مستوى الالتزام بالصلوات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "$completedCount من 5 صلوات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color(0xFF1E3A2F), // Dark rich text for white badge
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    color = if (isAllCompleted) SaudiGold else Color(0xFF81C784), // Light green for high contrast
                    trackColor = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4. The 5 Prayers Attendance Track
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    statuses.forEach { (name, attendance, iconEmoji) ->
                        val isDone = attendance != PrayerAttendance.NONE
                        val isJamaah = attendance == PrayerAttendance.JAMAAH
                        val isLate = attendance == PrayerAttendance.LATE

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isJamaah -> EmeraldPrimary
                                            isDone && !isLate -> Color(0xFF0288D1)
                                            isLate -> Color(0xFFFFA000)
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                        }
                                    )
                                    .border(
                                        width = if (isDone) 1.5.dp else 1.dp,
                                        color = if (isDone) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Text(
                                        text = if (isJamaah) "🕌" else if (isLate) "⏰" else "✓",
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                } else {
                                    Text(iconEmoji, fontSize = 16.sp)
                                }
                            }

                            Text(
                                text = name,
                                fontSize = 11.sp,
                                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Bottom Motivational Badges (Streak, Jamaah, Sunnah)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Streak Badge
                    CommitmentMiniBadge(
                        icon = "🔥",
                        title = "السلسلة",
                        value = "${streakDays.coerceAtLeast(1)} أيام",
                        highlightColor = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )

                    // Jamaah Badge
                    CommitmentMiniBadge(
                        icon = "🕌",
                        title = "الجماعة",
                        value = "$jamaahCount صلوات",
                        highlightColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    // Sunnah Badge
                    CommitmentMiniBadge(
                        icon = "✨",
                        title = "السنن",
                        value = "$sunnahCount ركعات",
                        highlightColor = SaudiGoldDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CommitmentMiniBadge(
    icon: String,
    title: String,
    value: String,
    highlightColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(icon, fontSize = 16.sp)
            Column {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = highlightColor
                )
            }
        }
    }
}


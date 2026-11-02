package com.example.ui.screens.worship

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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

    // Gregorian date string with year
    val gregorianDateText = remember(dayOffset) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -dayOffset)
        SimpleDateFormat("EEEE d MMMM yyyy", Locale("ar")).format(cal.time)
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
    val hours = remainingSeconds / 3600
    val mins = (remainingSeconds % 3600) / 60
    val secs = remainingSeconds % 60
    val countdownFormatted = if (hours >= 1) {
        String.format(Locale.US, "%d:%02d", hours, mins)
    } else {
        String.format(Locale.US, "%d:%02d", mins, secs)
    }

    val primaryBg = MaterialTheme.colorScheme.primary
    val surfaceVariantBg = MaterialTheme.colorScheme.surfaceVariant

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAllCompleted) SaudiGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                    drawCircle(color = starColor, radius = 2.dp.toPx(), center = pos)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Main Box containing Right Side (Hijri & Gregorian dates at 14sp) and Left Side (Prayer name & enlarged countdown)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // الجهة اليمنى من الصندوق: التاريخ الهجري والتاريخ الميلادي بنفس حجم الخط (14sp) فوق بعضهما
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "${hijriDate.day} ${hijriDate.monthNameAr} ${hijriDate.year} هـ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = gregorianDateText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        // الجهة اليسرى من الصندوق: اسم الصلاة فوق الوقت المتبقي المُكبّر
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = if (isArrived) "صلاة ${prayerTimes.nextPrayerName ?: "العصر"}" else "صلاة ${prayerTimes.nextPrayerName ?: "العصر"}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = countdownFormatted,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaudiGold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Bottom Motivational Badges: "السلسلة" & "السنن" only with equal weight
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommitmentMiniBadge(
                        icon = "🔥",
                        title = "السلسلة",
                        value = "${streakDays.coerceAtLeast(1)} أيام",
                        highlightColor = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )

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


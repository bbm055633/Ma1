package com.example.ui.screens.worship

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.domain.prayer.HijriDateInfo
import com.example.ui.theme.*

/**
 * Motivational Commitment Banner using Islamic Badges & Icons
 * Inspiring the user to maintain daily prayers, congregation, and voluntary sunnahs.
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
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onOpenQibla: () -> Unit,
    onToggleAlert: () -> Unit,
    isAlertEnabled: Boolean,
    onOpenSettings: () -> Unit,
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

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAllCompleted) SaudiGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("worship_commitment_banner")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. Top Bar: Date, Day Switcher, and Fast Action Icons (Qibla, Alert, Settings)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Tools (Qibla, Bell, Settings)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onOpenQibla,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Text("🕋", fontSize = 16.sp)
                    }

                    IconButton(
                        onClick = onToggleAlert,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = if (isAlertEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = "التنبيهات",
                            tint = if (isAlertEnabled) SaudiGoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "إعدادات المواقيت",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Center/Right: Hijri Date & Day Navigator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${hijriDate.day} ${hijriDate.monthNameAr} ${hijriDate.year} هـ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (dayOffset == 0) "اليوم (أم القرى)" else if (dayOffset == 1) "غداً" else if (dayOffset == -1) "أمس" else "يوم $dayOffset",
                            fontSize = 11.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(modifier = Modifier.padding(2.dp)) {
                            IconButton(onClick = onPreviousDay, modifier = Modifier.size(26.dp)) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "السابق", modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = onNextDay, modifier = Modifier.size(26.dp)) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "التالي", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Commitment Motivation Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(if (isAllCompleted) "👑" else "🌟", fontSize = 18.sp)
                    Text(
                        text = if (isAllCompleted) "تاج الالتزام اليومي مكتمل!" else "مستوى التزام اليوم بالصلوات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAllCompleted) SaudiGold.copy(alpha = 0.18f) else EmeraldPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "$completedCount من 5 صلوات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isAllCompleted) SaudiGoldDark else EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = if (isAllCompleted) SaudiGold else EmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. The 5 Prayers Motivational Badges Track
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
                                    color = if (isDone) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
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
                            color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Motivational Badges (Streak, Jamaah, Sunnah, Adhkar)
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
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
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

package com.example.ui.screens.worship

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.prayer.PrayerTimes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.example.ui.theme.SaudiGold
import com.example.ui.theme.*
import kotlin.random.Random

// Colors matching the exact luxury dark design in the screenshot with frosted translucency
val FaithCardBackground = MilkyFrostedSurface
val FaithCardBorder = SoftSandOutline
val FaithSubtleText = MutedEarthSecondary
val FaithCyanGlow = ActiveEmeraldMist
val FaithCyanCore = ActiveEmeraldMist
val FaithCyanDark = Color(0xFF144731)
val FaithUncheckedBorder = SoftSandOutline
val FaithGoldPrimary = ActiveSunGold
val FaithGoldLight = Color(0xFFE09B13)
val FaithGoldDark = Color(0xFF996408)

// Prayer Attendance Column Selection
enum class AttendanceColumn {
    NONE,
    IN_MOSQUE,  // في الجامع
    AT_HOME,    // في البيت
    LATE        // متأخر
}

/**
 * Top Tab Bar matching the exact pill design from the user's screenshot
 * (صلاتي with golden glowing pill, صيامي, قرآني, أذكاري)
 */
@Composable
fun FaithJourneyTopTabBar(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isEnglish: Boolean = false
) {
    val tabs = if (isEnglish) {
        listOf(
            Triple(0, "Prayers", "🕌"),
            Triple(1, "Qibla", "🧭"),
            Triple(2, "Quran", "📖"),
            Triple(3, "Adhkar", "📿")
        )
    } else {
        listOf(
            Triple(0, "صلاتي", "🕌"),
            Triple(1, "القبلة", "🧭"),
            Triple(2, "قرآني", "📖"),
            Triple(3, "أذكاري", "📿")
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val activeColor = MaterialTheme.colorScheme.primary

        tabs.forEach { (index, title, emoji) ->
            val isSelected = selectedTab == index

            if (isSelected) {
                // Active Tab: Solid Emerald Pill
                Surface(
                    onClick = { onTabSelect(index) },
                    shape = RoundedCornerShape(16.dp),
                    color = activeColor,
                    shadowElevation = 2.dp,
                    modifier = Modifier.testTag("faith_tab_$index")
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(text = emoji, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                // Inactive Tab: Clean Text + Emoji
                TextButton(
                    onClick = { onTabSelect(index) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("faith_tab_$index")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = emoji, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/**
 * Main Faith Journey Card: "رحلة الإيمان: أتمم الفروض والسنن"
 * Faithful reproduction of the uploaded screenshot layout and styling.
 */
@Composable
fun FaithJourneyPrayerTrackingCard(
    prayerTimes: PrayerTimes,
    selectedDayIndex: Int,
    onDayChange: (Int) -> Unit,
    isEnglish: Boolean = false,
    // Fajr
    fajrAttendance: AttendanceColumn,
    onFajrAttendanceChange: (AttendanceColumn) -> Unit,
    fajrSunnahQabliyah: Boolean,
    onToggleFajrSunnahQabliyah: () -> Unit,
    // Duha, Adhkar, Qiyam
    isDuhaDone: Boolean,
    onToggleDuha: () -> Unit,
    isMorningAdhkarDone: Boolean,
    onToggleMorningAdhkar: () -> Unit,
    onOpenMorningAdhkarSheet: () -> Unit,
    // Dhuhr
    dhuhrAttendance: AttendanceColumn,
    onDhuhrAttendanceChange: (AttendanceColumn) -> Unit,
    dhuhrSunnahQabliyah: Boolean,
    onToggleDhuhrSunnahQabliyah: () -> Unit,
    dhuhrSunnahBadiyah: Boolean,
    onToggleDhuhrSunnahBadiyah: () -> Unit,
    // Asr
    asrAttendance: AttendanceColumn,
    onAsrAttendanceChange: (AttendanceColumn) -> Unit,
    // Maghrib
    maghribAttendance: AttendanceColumn,
    onMaghribAttendanceChange: (AttendanceColumn) -> Unit,
    maghribSunnahBadiyah: Boolean,
    onToggleMaghribSunnahBadiyah: () -> Unit,
    // Isha
    ishaAttendance: AttendanceColumn,
    onIshaAttendanceChange: (AttendanceColumn) -> Unit,
    ishaSunnahBadiyah: Boolean,
    onToggleIshaSunnahBadiyah: () -> Unit,
    // Qiyam & Evening Adhkar
    isQiyamDone: Boolean,
    onToggleQiyam: () -> Unit,
    isEveningAdhkarDone: Boolean,
    onToggleEveningAdhkar: () -> Unit,
    onOpenEveningAdhkarSheet: () -> Unit,
    streaks: com.example.ui.viewmodel.PrayerStreaks,
    modifier: Modifier = Modifier
) {
    val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
    val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)
    val borderColor = if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline
    val dividerColor = if (isLiveSky) Color.White.copy(alpha = 0.12f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = surfaceColor,
        shadowElevation = if (isLiveSky) 0.dp else 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("faith_journey_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            var dragAccumulator by remember(selectedDayIndex) { mutableStateOf(0f) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(selectedDayIndex) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                dragAccumulator += dragAmount
                            },
                            onDragEnd = {
                                if (dragAccumulator > 35f) {
                                    // Swipe Right -> Older Day
                                    onDayChange(selectedDayIndex + 1)
                                } else if (dragAccumulator < -35f && selectedDayIndex > 0) {
                                    // Swipe Left -> Toward Today (boundary restriction: never past Today)
                                    onDayChange(selectedDayIndex - 1)
                                }
                                dragAccumulator = 0f
                            }
                        )
                    }
            ) {
                // 4. Unified Prayer Table Header Row
                UnifiedPrayerTableHeaderRow(
                    selectedDayIndex = selectedDayIndex,
                    onDayChange = onDayChange
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = dividerColor, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                val nextName = prayerTimes.nextPrayerName
                val targetNextPrayer = if (nextName == "الشروق") "الظهر" else nextName

              // 5. Prayer Row: الفجر (Qabliyah only)
            PrayerTrackingTableRow(
                prayerName = "الفجر",
                azanTime = prayerTimes.fajr,
                isEnglish = isEnglish,
                attendance = fajrAttendance,
                onAttendanceSelect = onFajrAttendanceChange,
                hasQabliyah = true,
                sunnahQabliyah = fajrSunnahQabliyah,
                onToggleSunnahQabliyah = onToggleFajrSunnahQabliyah,
                hasBadiyah = false,
                sunnahBadiyah = false,
                onToggleSunnahBadiyah = {},
                qabliyahStreak = streaks.fajrQabliyah,
                masjidStreak = streaks.fajrMasjid,
                homeStreak = streaks.fajrHome,
                lateStreak = streaks.fajrLate,
                badiyahStreak = streaks.fajrBadiyah,
                isNextPrayer = targetNextPrayer == "الفجر",
                minutesUntilNext = prayerTimes.minutesUntilNext,
                selectedDayIndex = selectedDayIndex
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = dividerColor, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // 6. Sunrise Paired Capsules Row: Duha & Morning Adhkar
            SunrisePairedCapsulesRow(
                sunriseTime = prayerTimes.sunrise,
                isDuhaDone = isDuhaDone,
                onToggleDuha = onToggleDuha,
                isMorningAdhkarDone = isMorningAdhkarDone,
                onToggleMorningAdhkar = onToggleMorningAdhkar,
                onOpenMorningAdhkarSheet = onOpenMorningAdhkarSheet,
                duhaStreak = streaks.duha
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = dividerColor, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // 7. Prayer Row: الظهر (Both Qabliyah & Ba'diyah)
            PrayerTrackingTableRow(
                prayerName = "الظهر",
                azanTime = prayerTimes.dhuhr,
                isEnglish = isEnglish,
                attendance = dhuhrAttendance,
                onAttendanceSelect = onDhuhrAttendanceChange,
                hasQabliyah = true,
                sunnahQabliyah = dhuhrSunnahQabliyah,
                onToggleSunnahQabliyah = onToggleDhuhrSunnahQabliyah,
                hasBadiyah = true,
                sunnahBadiyah = dhuhrSunnahBadiyah,
                onToggleSunnahBadiyah = onToggleDhuhrSunnahBadiyah,
                qabliyahStreak = streaks.dhuhrQabliyah,
                masjidStreak = streaks.dhuhrMasjid,
                homeStreak = streaks.dhuhrHome,
                lateStreak = streaks.dhuhrLate,
                badiyahStreak = streaks.dhuhrBadiyah,
                isNextPrayer = targetNextPrayer == "الظهر",
                minutesUntilNext = prayerTimes.minutesUntilNext,
                selectedDayIndex = selectedDayIndex
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = dividerColor, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // 8. Prayer Row: العصر (No Sunnah)
            PrayerTrackingTableRow(
                prayerName = "العصر",
                azanTime = prayerTimes.asr,
                isEnglish = isEnglish,
                attendance = asrAttendance,
                onAttendanceSelect = onAsrAttendanceChange,
                hasQabliyah = false,
                sunnahQabliyah = false,
                onToggleSunnahQabliyah = {},
                hasBadiyah = false,
                sunnahBadiyah = false,
                onToggleSunnahBadiyah = {},
                qabliyahStreak = streaks.asrQabliyah,
                masjidStreak = streaks.asrMasjid,
                homeStreak = streaks.asrHome,
                lateStreak = streaks.asrLate,
                badiyahStreak = streaks.asrBadiyah,
                isNextPrayer = targetNextPrayer == "العصر",
                minutesUntilNext = prayerTimes.minutesUntilNext,
                selectedDayIndex = selectedDayIndex
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = dividerColor, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // 9. Prayer Row: المغرب (Ba'diyah only)
            PrayerTrackingTableRow(
                prayerName = "المغرب",
                azanTime = prayerTimes.maghrib,
                isEnglish = isEnglish,
                attendance = maghribAttendance,
                onAttendanceSelect = onMaghribAttendanceChange,
                hasQabliyah = false,
                sunnahQabliyah = false,
                onToggleSunnahQabliyah = {},
                hasBadiyah = true,
                sunnahBadiyah = maghribSunnahBadiyah,
                onToggleSunnahBadiyah = onToggleMaghribSunnahBadiyah,
                qabliyahStreak = streaks.maghribQabliyah,
                masjidStreak = streaks.maghribMasjid,
                homeStreak = streaks.maghribHome,
                lateStreak = streaks.maghribLate,
                badiyahStreak = streaks.maghribBadiyah,
                isNextPrayer = targetNextPrayer == "المغرب",
                minutesUntilNext = prayerTimes.minutesUntilNext,
                selectedDayIndex = selectedDayIndex
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = dividerColor, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // 10. Prayer Row: العشاء (Ba'diyah only)
            PrayerTrackingTableRow(
                prayerName = "العشاء",
                azanTime = prayerTimes.isha,
                isEnglish = isEnglish,
                attendance = ishaAttendance,
                onAttendanceSelect = onIshaAttendanceChange,
                hasQabliyah = false,
                sunnahQabliyah = false,
                onToggleSunnahQabliyah = {},
                hasBadiyah = true,
                sunnahBadiyah = ishaSunnahBadiyah,
                onToggleSunnahBadiyah = onToggleIshaSunnahBadiyah,
                qabliyahStreak = streaks.ishaQabliyah,
                masjidStreak = streaks.ishaMasjid,
                homeStreak = streaks.ishaHome,
                lateStreak = streaks.ishaLate,
                badiyahStreak = streaks.ishaBadiyah,
                isNextPrayer = targetNextPrayer == "العشاء",
                minutesUntilNext = prayerTimes.minutesUntilNext,
                selectedDayIndex = selectedDayIndex
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = dividerColor, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // 11. Night Paired Capsules Row: Qiyam & Evening Adhkar
            NightPairedCapsulesRow(
                isQiyamDone = isQiyamDone,
                onToggleQiyam = onToggleQiyam,
                isEveningAdhkarDone = isEveningAdhkarDone,
                onToggleEveningAdhkar = onToggleEveningAdhkar,
                onOpenEveningAdhkarSheet = onOpenEveningAdhkarSheet
            )
        }
        }
    }
}

@Composable
fun UnifiedPrayerTableHeaderRow(
    selectedDayIndex: Int,
    onDayChange: (Int) -> Unit
) {
    val dayLabels = listOf("اليوم", "البارحة", "قبل يومين", "قبل 3 أيام", "قبل 4 أيام", "قبل 5 أيام", "قبل 6 أيام", "قبل أسبوع")
    val currentLabel = dayLabels.getOrElse(selectedDayIndex) { "قبل $selectedDayIndex أيام" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Right Column: Compact Date Indicator with arrows (< Today >) matching 76.dp width
        Row(
            modifier = Modifier.width(76.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { onDayChange(selectedDayIndex + 1) },
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Previous Day",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
            }

            Text(
                text = currentLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            val canGoNext = selectedDayIndex > 0
            IconButton(
                onClick = { if (canGoNext) onDayChange(selectedDayIndex - 1) },
                enabled = canGoNext,
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Next Day",
                    tint = if (canGoNext) MaterialTheme.colorScheme.primary else FaithSubtleText.copy(alpha = 0.3f),
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        // 5 Data Columns Headers (strictly aligned above row items: سنة قبلية, في المسجد, في البيت, متأخر, سنة بعدية)
        val headers = listOf("سنة قبلية", "في المسجد", "في البيت", "متأخر", "سنة بعدية")
        headers.forEach { title ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FaithSubtleText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun calculateSunriseProhibitedRemainingSeconds(sunriseTimeStr: String): Int {
    try {
        val parts = sunriseTimeStr.split(":")
        if (parts.size == 2) {
            val sunriseHour = parts[0].toInt()
            val sunriseMin = parts[1].toInt()
            val now = java.util.Calendar.getInstance()
            val nowHour = now.get(java.util.Calendar.HOUR_OF_DAY)
            val nowMin = now.get(java.util.Calendar.MINUTE)
            val nowSec = now.get(java.util.Calendar.SECOND)

            val sunriseTotalSec = sunriseHour * 3600 + sunriseMin * 60
            val prohibitedEndSec = sunriseTotalSec + 15 * 60 // 15 mins after sunrise
            val nowTotalSec = nowHour * 3600 + nowMin * 60 + nowSec

            val diff = prohibitedEndSec - nowTotalSec
            if (diff > 0) return diff
        }
    } catch (_: Exception) {}
    return 0
}

@Composable
fun SunrisePairedCapsulesRow(
    sunriseTime: String,
    isDuhaDone: Boolean,
    onToggleDuha: () -> Unit,
    isMorningAdhkarDone: Boolean,
    onToggleMorningAdhkar: () -> Unit,
    onOpenMorningAdhkarSheet: () -> Unit,
    duhaStreak: Int = 0
) {
    var remainingSeconds by remember(sunriseTime) { mutableStateOf(calculateSunriseProhibitedRemainingSeconds(sunriseTime)) }
    val isPreview = androidx.compose.ui.platform.LocalInspectionMode.current
    LaunchedEffect(sunriseTime, isPreview) {
        if (!isPreview) {
            while (true) {
                kotlinx.coroutines.delay(1000L)
                remainingSeconds = (remainingSeconds - 1).coerceAtLeast(0)
            }
        }
    }
    val isProhibited = remainingSeconds > 0
    val formattedCountdown = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60)

    val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
    val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
    val capsuleDefaultBg = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isLiveSky) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Capsule A (Right in RTL): صلاة الضحى
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
            Surface(
                onClick = { if (!isProhibited) onToggleDuha() },
                enabled = !isProhibited,
                shape = RoundedCornerShape(12.dp),
                color = if (isDuhaDone) Color(0xFFFEF08A) else if (isProhibited) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else capsuleDefaultBg,
                border = BorderStroke(
                    1.dp,
                    if (isDuhaDone) Color(0xFFEAB308) else if (isProhibited) Color(0xFFCBD5E1) else (if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("duha_capsule")
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🌅", fontSize = 13.sp)
                        Text(
                            text = if (isProhibited) "وقت النهي" else "صلاة الضحى",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDuhaDone) Color(0xFF78350F) else if (isProhibited) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isProhibited) "متبقي $formattedCountdown" else if (isDuhaDone) "تمت الصلاة ✓" else "سنة مؤكدة",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDuhaDone) Color(0xFF92400E) else if (isProhibited) Color(0xFFEF4444) else FaithSubtleText,
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (duhaStreak > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-6.dp))
                ) {
                    StreakOrbitalStars(streakCount = duhaStreak)
                }
            }
        }

        // Capsule B (Left in RTL): أذكار الصباح
        Surface(
            onClick = {
                onToggleMorningAdhkar()
                onOpenMorningAdhkarSheet()
            },
            shape = RoundedCornerShape(12.dp),
            color = if (isMorningAdhkarDone) Color(0xFFD1FAE5) else capsuleDefaultBg,
            border = BorderStroke(
                1.dp,
                if (isMorningAdhkarDone) ActiveEmeraldMist else (if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ),
            modifier = Modifier
                .weight(1f)
                .testTag("morning_adhkar_capsule")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("📿", fontSize = 13.sp)
                    Text(
                        text = "أذكار الصباح",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepWarmInk
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isMorningAdhkarDone) "تم الإتمام ✔" else "مستحب الآن",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isMorningAdhkarDone) ActiveEmeraldMist else MutedEarthSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun calculateIqamaTime(azanTime: String): String {
    try {
        val parts = azanTime.split(":")
        if (parts.size >= 2) {
            val hStr = parts[0].trim()
            val mAndPeriod = parts[1].trim()
            val mStr = mAndPeriod.take(2)
            val period = if (mAndPeriod.length > 2) mAndPeriod.substring(3).trim() else ""
            
            val h = hStr.toIntOrNull() ?: 5
            val m = mStr.toIntOrNull() ?: 0
            
            val isAzanPm = period.contains("م") || period.contains("PM", ignoreCase = true)
            val isAzanAm = period.contains("ص") || period.contains("AM", ignoreCase = true)
            val h24Start = when {
                isAzanPm && h < 12 -> h + 12
                isAzanAm && h == 12 -> 0
                else -> h
            }
            val totalM = h24Start * 60 + m + 15
            val newH24 = (totalM / 60) % 24
            val newM = totalM % 60
            
            val newH12 = if (newH24 % 12 == 0) 12 else newH24 % 12
            val isPm = newH24 >= 12
            val newPeriod = if (period.isNotEmpty()) {
                if (period.contains("ص") || period.contains("AM", ignoreCase = true)) {
                    if (isPm) "م" else "ص"
                } else {
                    if (isPm) "PM" else "AM"
                }
            } else ""
            
            return String.format(java.util.Locale.US, "%02d:%02d%s", newH12, newM, if (newPeriod.isNotEmpty()) " $newPeriod" else "")
        }
    } catch (e: Exception) {
        // Fallback
    }
    return azanTime
}

fun hasPrayerTimePassed(azanTimeStr: String, selectedDayIndex: Int): Boolean {
    if (selectedDayIndex > 0) return true
    try {
        val parts = azanTimeStr.split(":")
        if (parts.size >= 2) {
            val hStr = parts[0].trim()
            val mAndPeriod = parts[1].trim()
            val mStr = mAndPeriod.take(2)
            val period = if (mAndPeriod.length > 2) mAndPeriod.substring(3).trim() else ""
            
            val h = hStr.toIntOrNull() ?: 5
            val m = mStr.toIntOrNull() ?: 0
            
            val isPm = period.contains("م") || period.contains("PM", ignoreCase = true)
            val isAm = period.contains("ص") || period.contains("AM", ignoreCase = true)
            val h24 = when {
                isPm && h < 12 -> h + 12
                isAm && h == 12 -> 0
                else -> h
            }
            val prayerTotalM = h24 * 60 + m
            
            val now = java.util.Calendar.getInstance()
            val nowTotalM = now.get(java.util.Calendar.HOUR_OF_DAY) * 60 + now.get(java.util.Calendar.MINUTE)
            
            return nowTotalM > (prayerTotalM + 30)
        }
    } catch (_: Exception) {}
    return false
}

@Composable
fun PrayerTrackingTableRow(
    prayerName: String,
    azanTime: String,
    isEnglish: Boolean = false,
    attendance: AttendanceColumn,
    onAttendanceSelect: (AttendanceColumn) -> Unit,
    hasQabliyah: Boolean,
    sunnahQabliyah: Boolean,
    onToggleSunnahQabliyah: () -> Unit,
    hasBadiyah: Boolean,
    sunnahBadiyah: Boolean,
    onToggleSunnahBadiyah: () -> Unit,
    qabliyahStreak: Int = 0,
    masjidStreak: Int = 0,
    homeStreak: Int = 0,
    lateStreak: Int = 0,
    badiyahStreak: Int = 0,
    isNextPrayer: Boolean = false,
    minutesUntilNext: Int = 0,
    selectedDayIndex: Int = 0
) {
    val iqamaLabel = if (isEnglish) "Iqama" else "الإقامة"
    val iqamaTime = calculateIqamaTime(azanTime)
    val primaryColor = MaterialTheme.colorScheme.primary
    val isDarkMode = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val rowBg = when {
        attendance == AttendanceColumn.LATE -> if (isDarkMode) Color(0xFF451A03).copy(alpha = 0.4f) else Color(0xFFFEF3C7) // Light orange
        attendance == AttendanceColumn.IN_MOSQUE || attendance == AttendanceColumn.AT_HOME -> if (isDarkMode) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFD1FAE5) // Light green
        isNextPrayer -> Color.Transparent
        hasPrayerTimePassed(azanTime, selectedDayIndex) -> if (isDarkMode) Color(0xFF7F1D1D).copy(alpha = 0.35f) else Color(0xFFFEE2E2) // Very light red
        else -> Color.Transparent
    }

    val rowModifier = if (isNextPrayer) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBg)
            .border(1.dp, primaryColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBg)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Prayer Name & Time (Rightmost in RTL)
        Column(
            modifier = Modifier.width(96.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = prayerName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNextPrayer) primaryColor else MaterialTheme.colorScheme.onSurface
                )
                if (isNextPrayer) {
                    val hours = minutesUntilNext / 60
                    val mins = minutesUntilNext % 60
                    val countdownStr = String.format(java.util.Locale.US, "%02d:%02d", hours, mins)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = primaryColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "متبقي $countdownStr",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Text(
                text = azanTime,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = FaithSubtleText
            )
            Text(
                text = "$iqamaLabel: $iqamaTime",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Normal,
                color = FaithSubtleText.copy(alpha = 0.75f)
            )
        }

        // 2. Qabliyah Sunnah (Prayer Mat Icon)
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (hasQabliyah) {
                Box(contentAlignment = Alignment.TopCenter) {
                    PrayerMatIcon(
                        isActive = sunnahQabliyah,
                        onClick = onToggleSunnahQabliyah,
                        testTag = "${prayerName}_sunnah_qabliyah"
                    )
                    if (qabliyahStreak > 0) {
                        Box(modifier = Modifier.align(Alignment.TopCenter).offset(y = (-8.dp))) {
                            StreakOrbitalStars(streakCount = qabliyahStreak)
                        }
                    }
                }
            } else {
                Text("-", color = FaithSubtleText, fontSize = 14.sp)
            }
        }

        // 3. In Mosque (Congregation) with orbital streak stars
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(contentAlignment = Alignment.TopCenter) {
                GlowingLocationButton(
                    isSelected = attendance == AttendanceColumn.IN_MOSQUE,
                    icon = Icons.Default.Mosque,
                    onClick = {
                        onAttendanceSelect(if (attendance == AttendanceColumn.IN_MOSQUE) AttendanceColumn.NONE else AttendanceColumn.IN_MOSQUE)
                    },
                    testTag = "${prayerName}_in_mosque"
                )
                if (masjidStreak > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-8.dp))
                    ) {
                        StreakOrbitalStars(streakCount = masjidStreak)
                    }
                }
            }
        }

        // 4. At Home
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(contentAlignment = Alignment.TopCenter) {
                GlowingLocationButton(
                    isSelected = attendance == AttendanceColumn.AT_HOME,
                    icon = Icons.Default.Home,
                    onClick = {
                        onAttendanceSelect(if (attendance == AttendanceColumn.AT_HOME) AttendanceColumn.NONE else AttendanceColumn.AT_HOME)
                    },
                    testTag = "${prayerName}_at_home"
                )
                if (homeStreak > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-8.dp))
                    ) {
                        StreakOrbitalStars(streakCount = homeStreak)
                    }
                }
            }
        }

        // 5. Delayed / Qadaa
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(contentAlignment = Alignment.TopCenter) {
                GlowingLocationButton(
                    isSelected = attendance == AttendanceColumn.LATE,
                    icon = Icons.Default.History,
                    onClick = {
                        onAttendanceSelect(if (attendance == AttendanceColumn.LATE) AttendanceColumn.NONE else AttendanceColumn.LATE)
                    },
                    testTag = "${prayerName}_late"
                )
                if (lateStreak > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-8.dp))
                    ) {
                        StreakOrbitalStars(streakCount = lateStreak)
                    }
                }
            }
        }

        // 6. Ba'diyah Sunnah (Prayer Mat Icon)
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (hasBadiyah) {
                Box(contentAlignment = Alignment.TopCenter) {
                    PrayerMatIcon(
                        isActive = sunnahBadiyah,
                        onClick = onToggleSunnahBadiyah,
                        testTag = "${prayerName}_sunnah_badiyah"
                    )
                    if (badiyahStreak > 0) {
                        Box(modifier = Modifier.align(Alignment.TopCenter).offset(y = (-8.dp))) {
                            StreakOrbitalStars(streakCount = badiyahStreak)
                        }
                    }
                }
            } else {
                Text("-", color = FaithSubtleText, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun PrayerMatIcon(
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) ActiveSunGold.copy(alpha = 0.2f) else SlotNeutralBackground,
        border = BorderStroke(
            1.dp,
            if (isActive) ActiveSunGold else SoftSandOutline
        ),
        modifier = Modifier
            .size(34.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isActive) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (isActive) ActiveSunGold else InactiveIconBeige,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun GlowingLocationButton(
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (isSelected) ActiveEmeraldMist.copy(alpha = 0.2f) else SlotNeutralBackground,
        border = BorderStroke(
            1.dp,
            if (isSelected) ActiveEmeraldMist else SoftSandOutline
        ),
        modifier = Modifier
            .size(34.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (isSelected) ActiveEmeraldMist else InactiveIconBeige,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun NightPairedCapsulesRow(
    isQiyamDone: Boolean,
    onToggleQiyam: () -> Unit,
    isEveningAdhkarDone: Boolean,
    onToggleEveningAdhkar: () -> Unit,
    onOpenEveningAdhkarSheet: () -> Unit
) {
    val isLiveSky = com.example.ui.components.LocalStarryNightActive.current
    val cardAlpha = com.example.ui.theme.LocalCardTransparency.current
    val capsuleDefaultBg = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isLiveSky) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Capsule A (Right in RTL): قيام الليل والوتر
        Surface(
            onClick = onToggleQiyam,
            shape = RoundedCornerShape(12.dp),
            color = if (isQiyamDone) Color(0xFFE0E7FF) else capsuleDefaultBg,
            border = BorderStroke(
                1.dp,
                if (isQiyamDone) Color(0xFF6366F1) else (if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ),
            modifier = Modifier
                .weight(1f)
                .testTag("qiyam_capsule")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🌙", fontSize = 13.sp)
                    Text(
                        text = "قيام الليل والوتر",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isQiyamDone) Color(0xFF312E81) else MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isQiyamDone) "تمت الصلاة ✓" else "نافذة الليل",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isQiyamDone) Color(0xFF4338CA) else FaithSubtleText,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Capsule B (Left in RTL): أذكار المساء
        Surface(
            onClick = {
                onToggleEveningAdhkar()
                onOpenEveningAdhkarSheet()
            },
            shape = RoundedCornerShape(12.dp),
            color = if (isEveningAdhkarDone) Color(0xFFD1FAE5) else capsuleDefaultBg,
            border = BorderStroke(
                1.dp,
                if (isEveningAdhkarDone) Color(0xFF059669) else (if (isLiveSky) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ),
            modifier = Modifier
                .weight(1f)
                .testTag("evening_adhkar_capsule")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("📿", fontSize = 13.sp)
                    Text(
                        text = "أذكار المساء",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEveningAdhkarDone) Color(0xFF34D399) else MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isEveningAdhkarDone) "تم الإتمام ✔" else "بعد العصر والمغرب",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isEveningAdhkarDone) Color(0xFF6EE7B7) else FaithSubtleText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * A Single Interactive Row for an Obligatory Prayer
 */
@Composable
fun PrayerTrackingTableRow(
    prayerName: String,
    azanTime: String,
    iqamaTime: String,
    attendance: AttendanceColumn,
    onAttendanceSelect: (AttendanceColumn) -> Unit,
    sunnahQabliyah: Boolean,
    onToggleSunnahQabliyah: () -> Unit,
    sunnahBadiyah: Boolean,
    onToggleSunnahBadiyah: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prayer Info Column (Rightmost in RTL)
        Column(
            modifier = Modifier.width(68.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = prayerName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = azanTime,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = FaithSubtleText
            )
            Text(
                text = iqamaTime,
                fontSize = 9.sp,
                color = FaithSubtleText.copy(alpha = 0.8f)
            )
        }

        // Column 1: في الجامع
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            GlowingCyanCircle(
                isSelected = attendance == AttendanceColumn.IN_MOSQUE,
                onClick = {
                    onAttendanceSelect(
                        if (attendance == AttendanceColumn.IN_MOSQUE) AttendanceColumn.NONE else AttendanceColumn.IN_MOSQUE
                    )
                },
                testTag = "${prayerName}_in_mosque"
            )
        }

        // Column 2: في البيت
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            GlowingCyanCircle(
                isSelected = attendance == AttendanceColumn.AT_HOME,
                onClick = {
                    onAttendanceSelect(
                        if (attendance == AttendanceColumn.AT_HOME) AttendanceColumn.NONE else AttendanceColumn.AT_HOME
                    )
                },
                testTag = "${prayerName}_at_home"
            )
        }

        // Column 3: متأخر
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            GlowingCyanCircle(
                isSelected = attendance == AttendanceColumn.LATE,
                onClick = {
                    onAttendanceSelect(
                        if (attendance == AttendanceColumn.LATE) AttendanceColumn.NONE else AttendanceColumn.LATE
                    )
                },
                testTag = "${prayerName}_late"
            )
        }

        // Column 4: سنة قبلية
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            GlowingCyanCircle(
                isSelected = sunnahQabliyah,
                onClick = onToggleSunnahQabliyah,
                testTag = "${prayerName}_sunnah_qabliyah"
            )
        }

        // Column 5: سنة بعدية
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            GlowingCyanCircle(
                isSelected = sunnahBadiyah,
                onClick = onToggleSunnahBadiyah,
                testTag = "${prayerName}_sunnah_badiyah"
            )
        }
    }
}

/**
 * Mid-morning container: الشروق + صلاة الضحى + أذكار الصباح
 */
@Composable
fun SunriseDuhaAdhkarRow(
    sunriseTime: String,
    isDuhaDone: Boolean,
    onToggleDuha: () -> Unit,
    isMorningAdhkarDone: Boolean,
    onToggleMorningAdhkar: () -> Unit,
    onOpenMorningAdhkarSheet: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sunrise Info (Rightmost in RTL)
        Column(
            modifier = Modifier.width(68.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "الشروق",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFDE68A)
            )
            Text(
                text = sunriseTime,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = FaithSubtleText
            )
            Text(
                text = "الضحى: 06:15",
                fontSize = 8.sp,
                color = FaithSubtleText.copy(alpha = 0.8f)
            )
        }

        // Middle & Left: صلاة الضحى + أذكار الصباح
        Row(
            modifier = Modifier.weight(5f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // صلاة الضحى 🌅 Pill Button (Cream/Gold)
            Surface(
                onClick = onToggleDuha,
                shape = RoundedCornerShape(14.dp),
                color = if (isDuhaDone) Color(0xFFFDE68A) else Color(0xFFFEF3C7),
                shadowElevation = if (isDuhaDone) 3.dp else 0.dp,
                modifier = Modifier
                    .weight(1f)
                    .testTag("duha_pill_button")
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "صلاة الضحى",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                        Text("🌅", fontSize = 11.sp)
                    }
                    Text(
                        text = if (isDuhaDone) "تمت الصلاة ✓" else "لم يُبدأ",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF92400E)
                    )
                }
            }

            // أذكار الصباح 📿 Pill Button (Dark Teal with Glowing Cyan Border)
            Surface(
                onClick = {
                    onToggleMorningAdhkar()
                    onOpenMorningAdhkarSheet()
                },
                shape = RoundedCornerShape(14.dp),
                color = if (isMorningAdhkarDone) Color(0xFF0D3338) else Color(0xFF0F262C),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    if (isMorningAdhkarDone) FaithCyanGlow else FaithCyanDark
                ),
                shadowElevation = if (isMorningAdhkarDone) 4.dp else 0.dp,
                modifier = Modifier
                    .weight(1f)
                    .testTag("morning_adhkar_pill_button")
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "أذكار الصباح",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FaithCyanGlow
                        )
                        Text("📿", fontSize = 11.sp)
                    }
                    Text(
                        text = if (isMorningAdhkarDone) "تم الإتمام ✓" else "تم البدء",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = FaithCyanGlow.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

/**
 * The Signature Glowing Cyan Circle Button from the screenshot
 */
@Composable
fun GlowingCyanCircle(
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyan_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cyan_glow_alpha"
    )

    Box(
        modifier = modifier
            .size(30.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Outer Glowing Cyan Bloom
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                FaithCyanGlow.copy(alpha = glowAlpha * 0.6f),
                                FaithCyanCore.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Inner Active Cyan Circle
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF67E8F9),
                                FaithCyanGlow,
                                FaithCyanCore
                            )
                        )
                    )
                    .border(1.5.dp, Color(0xFFA5F3FC), CircleShape)
            )
        } else {
            // Unselected Hollow Circle
            val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
            val unselectedBorder = if (isDark) FaithUncheckedBorder else Color(0xFFCBD5E1)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(2.dp, unselectedBorder, CircleShape)
            )
        }
    }
}

/**
 * Bottom Card: "إنجازاتك" (Your Achievements)
 * Displays 7-Day Streak, Sunnah Seeker, and More Achievements link.
 */
@Composable
fun FaithJourneyAchievementsCard(
    onMoreAchievementsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val cardColor = if (isDark) FaithCardBackground else MaterialTheme.colorScheme.surface
    val cardBorderColor = if (isDark) FaithCardBorder else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val titleTextColor = MaterialTheme.colorScheme.onSurface

    Surface(
        shape = RoundedCornerShape(32.dp),
        color = Color.White.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E4DC)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .testTag("faith_achievements_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: إنجازاتك
            Text(
                text = "إنجازاتك",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = titleTextColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Row with 2 Badges + More Achievements Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Badge 1: 7-Day Streak 🔥
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Flame Badge Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFEA580C), Color(0xFF9A3412))
                                )
                            )
                            .border(1.dp, Color(0xFFFDBA74), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔥", fontSize = 16.sp)
                            Text("7 Day", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Column {
                        Text(
                            text = "التقدم: ٧ أيام",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FaithCyanGlow
                        )
                        Text(
                            text = "سلسلة الالتزام",
                            fontSize = 11.sp,
                            color = FaithSubtleText
                        )
                    }
                }

                // Badge 2: Sunnah Seeker 🛡️
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Shield Badge Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0D9488), Color(0xFF042F2E))
                                )
                            )
                            .border(1.dp, Color(0xFF5EEAD4), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛡️", fontSize = 16.sp)
                            Text("سنن", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE68A))
                        }
                    }

                    Column {
                        Text(
                            text = "التقدم: ١٤ سنة",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FaithCyanGlow
                        )
                        Text(
                            text = "ملازم السنن",
                            fontSize = 11.sp,
                            color = FaithSubtleText
                        )
                    }
                }

                // Link: More Achievements >
                Row(
                    modifier = Modifier
                        .clickable { onMoreAchievementsClick() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "المزيد من\nالأوسمة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FaithCyanGlow,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "المزيد",
                        tint = FaithCyanGlow,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Animated Sparkle Particle Dust Effect on the Progress Bar Head
 */
@Composable
fun SparkleParticleEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkles")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_anim"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Generate glowing sparkle particles near the progress tip
        val random = Random(42)
        for (i in 0..14) {
            val offsetX = (width - 16.dp.toPx() + random.nextFloat() * 20.dp.toPx()).coerceAtLeast(0f)
            val offsetY = (height / 2 + (random.nextFloat() - 0.5f) * height * 1.4f)
            val radius = (1.5.dp.toPx() + random.nextFloat() * 2.dp.toPx())
            val alpha = ((animProgress + random.nextFloat()) % 1f)

            drawCircle(
                color = if (i % 2 == 0) FaithCyanGlow.copy(alpha = alpha) else Color(0xFFFDE68A).copy(alpha = alpha),
                radius = radius,
                center = Offset(offsetX, offsetY)
            )
        }
    }
}

@Composable
fun StreakOrbitalStars(streakCount: Int) {
    if (streakCount <= 0) return
    Box(
        modifier = Modifier
            .size(34.dp)
            .offset(y = (-6.dp)),
        contentAlignment = Alignment.TopCenter
    ) {
        when (streakCount) {
            1 -> {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Streak 1",
                    tint = SaudiGold,
                    modifier = Modifier.size(11.dp)
                )
            }
            2 -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SaudiGold, modifier = Modifier.size(10.dp))
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SaudiGold, modifier = Modifier.size(10.dp))
                }
            }
            else -> {
                val count = minOf(streakCount, 4)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until count) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SaudiGold, modifier = Modifier.size(9.dp))
                    }
                }
            }
        }
    }
}

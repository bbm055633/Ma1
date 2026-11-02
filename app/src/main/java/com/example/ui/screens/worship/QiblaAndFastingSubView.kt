package com.example.ui.screens.worship

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.components.LocalStarryNightActive
import com.example.ui.theme.LocalCardTransparency
import com.example.ui.theme.SaudiGold
import com.example.ui.theme.Shapes2027

/**
 * تبويب القبلة والصيام - QiblaAndFastingSubView
 */
@Composable
fun QiblaAndFastingSubView(
    hijriDate: HijriDateInfo,
    prayerTimes: PrayerTimes,
    isEnglish: Boolean = false,
    onOpenFullscreenQibla: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isLiveSky = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)
    val emeraldColor = MaterialTheme.colorScheme.primary

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("qibla_and_fasting_subview"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // بطاقة القبلة التفاعلية
        item {
            Surface(
                shape = Shapes2027.Large,
                color = surfaceColor,
                shadowElevation = if (isLiveSky) 0.dp else 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SaudiGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = SaudiGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isEnglish) "Qibla Direction" else "اتجاه القبلة الشريفة 🕋",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isEnglish) "Mecca Bearing: 240° WSW" else "زاوية مكة المكرمة: 240° غرب-جنوب",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onOpenFullscreenQibla,
                            shape = Shapes2027.Pill,
                            colors = ButtonDefaults.buttonColors(containerColor = emeraldColor),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_qibla_compass_btn")
                        ) {
                            Text(
                                text = if (isEnglish) "Compass" else "البوصلة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = Shapes2027.Medium,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = SaudiGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isEnglish)
                                    "Compass is calibrated using high accuracy magnetometer."
                                else
                                    "تمت معايرة القبلة بدقة اعتماداً على الحسابات الفلكية لمنطقتك الحالية.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // جدول صيام التطوع والأيام البيض
        item {
            Surface(
                shape = Shapes2027.Large,
                color = surfaceColor,
                shadowElevation = if (isLiveSky) 0.dp else 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(emeraldColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = emeraldColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isEnglish) "Sunnah Fasting" else "صيام التطوع والسنن 🌙",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${hijriDate.day} ${hijriDate.monthNameAr} ${hijriDate.year}هـ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FastingDayRow(
                            day = if (isEnglish) "Monday" else "صيام يوم الإثنين",
                            badge = if (isEnglish) "Recommended" else "سنة مؤكدة",
                            isUpcoming = true
                        )
                        FastingDayRow(
                            day = if (isEnglish) "Thursday" else "صيام يوم الخميس",
                            badge = if (isEnglish) "Recommended" else "سنة مؤكدة",
                            isUpcoming = false
                        )
                        FastingDayRow(
                            day = if (isEnglish) "White Days (13, 14, 15)" else "الأيام البيض (13، 14، 15)",
                            badge = if (isEnglish) "Monthly" else "شهرياً",
                            isUpcoming = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FastingDayRow(
    day: String,
    badge: String,
    isUpcoming: Boolean
) {
    Surface(
        shape = Shapes2027.Medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Surface(
                shape = Shapes2027.Pill,
                color = if (isUpcoming) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = badge,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUpcoming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

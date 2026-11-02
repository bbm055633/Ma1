package com.example.ui.screens.worship

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.prayer.PrayerTimes
import com.example.ui.components.LocalStarryNightActive
import com.example.ui.theme.LocalCardTransparency
import com.example.ui.theme.SaudiGold
import com.example.ui.theme.Shapes2027

/**
 * شريط العد التنازلي للصلاة القادمة
 */
@Composable
fun NextPrayerCountdownBanner(
    prayerTimes: PrayerTimes,
    modifier: Modifier = Modifier
) {
    val isLiveSky = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        shape = Shapes2027.Large,
        color = surfaceColor,
        shadowElevation = if (isLiveSky) 0.dp else 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("next_prayer_countdown_banner")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "الصلاة القادمة: صلاة ${prayerTimes.nextPrayerName ?: "الظهر"}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "موعد الأذان: ${prayerTimes.nextPrayerTime ?: "--:--"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = Shapes2027.Pill,
                color = primaryColor
            ) {
                Text(
                    text = "متبقي ${prayerTimes.minutesUntilNext} دقيقة",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

/**
 * شريط التبويب العلوي لرحلة الإيمان (صلاتي، صيامي، قرآني، أذكاري)
 */
@Composable
fun FaithJourneyTopTabBar(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Pair("صلاتي", "🕌"),
        Pair("صيامي", "🌙"),
        Pair("قرآني", "📖"),
        Pair("أذكاري", "📿")
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, (title, icon) ->
            val isSelected = selectedTab == index
            val primaryColor = MaterialTheme.colorScheme.primary

            Surface(
                onClick = { onTabSelect(index) },
                shape = Shapes2027.Pill,
                color = if (isSelected) primaryColor else Color.Transparent,
                modifier = Modifier.height(38.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(icon, fontSize = 14.sp)
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * نافذة حوار بوصلة القبلة
 */
@Composable
fun QiblaCompassDialog(onDismiss: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = Shapes2027.Large,
            color = if (isDark) Color(0xFF141923) else MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "اتجاه القبلة الشريفة 🕋",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                // Compass Visual
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = SaudiGold,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "240° WSW",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = "وجّه هاتفك أفقياً نحو زاوية 240° باتجاه مكة المكرمة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * نافذة تفاصيل إنجازات الالتزام بالعبادات
 */
@Composable
fun AchievementsFullDialog(onDismiss: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = Shapes2027.Large,
            color = if (isDark) Color(0xFF141923) else MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "سجل الإنجازات والالتزام 🏆",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AchievementItem(
                        icon = "🕌",
                        title = "المحافظة على صلاة الجماعة",
                        subtitle = "سلسلة متواصلة لمدة 14 يوماً",
                        progress = 1.0f
                    )
                    AchievementItem(
                        icon = "📖",
                        title = "الورد اليومي للقرآن",
                        subtitle = "قراءة حزبين يومياً بانتظام",
                        progress = 0.85f
                    )
                    AchievementItem(
                        icon = "🤲",
                        title = "أذكار الصباح والمساء",
                        subtitle = "إتمام الأذكار بنسبة 95% هذا الشهر",
                        progress = 0.95f
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementItem(
    icon: String,
    title: String,
    subtitle: String,
    progress: Float
) {
    Surface(
        shape = Shapes2027.Medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, fontSize = 24.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(Shapes2027.Pill),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }
        }
    }
}


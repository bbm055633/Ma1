package com.example.ui.screens.worship

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
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
import com.example.domain.models.QuranKhatmahState
import com.example.ui.components.LocalStarryNightActive
import com.example.ui.theme.LocalCardTransparency
import com.example.ui.theme.Shapes2027

/**
 * تبويب المصحف الشريف والختمة القرآنية - QuranSubView
 */
@Composable
fun QuranSubView(
    khatmah: QuranKhatmahState,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLiveSky = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)
    val emeraldColor = MaterialTheme.colorScheme.primary

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quran_subview"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // بطاقة تقدم الختمة
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
                                    .background(emeraldColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = emeraldColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "الختمة القرآنية 📖",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "الجزء ${khatmah.currentJuz} • الحزب ${khatmah.currentHizb}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = Shapes2027.Pill,
                            color = emeraldColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "صفحة ${khatmah.currentPage} من 604",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = emeraldColor
                            )
                        }
                    }

                    // Progress Bar
                    val progress = (khatmah.currentPage.toFloat() / 604f).coerceIn(0f, 1f)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(Shapes2027.Pill),
                            color = emeraldColor,
                            trackColor = emeraldColor.copy(alpha = 0.15f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المقروء اليوم: ${khatmah.pagesReadToday} صفحات",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(progress * 100).toInt()}% مكتمل",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = emeraldColor
                            )
                        }
                    }

                    // Page Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onPageChanged((khatmah.currentPage - 1).coerceAtLeast(1)) },
                            modifier = Modifier.weight(1f),
                            shape = Shapes2027.Medium
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("السابقة", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onPageChanged((khatmah.currentPage + 1).coerceAtMost(604)) },
                            modifier = Modifier.weight(1f),
                            shape = Shapes2027.Medium,
                            colors = ButtonDefaults.buttonColors(containerColor = emeraldColor)
                        ) {
                            Text("التالية", fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        }
                    }
                }
            }
        }

        // بطاقة القراءة السريعة
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = emeraldColor
                    )

                    Text(
                        text = "«اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ • خَلَقَ الْإِنسَانَ مِنْ عَلَقٍ • اقْرَأْ وَرَبُّكَ الْأَكْرَمُ • الَّذِي عَلَّمَ بِالْقَلَمِ»",
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Surface(
                        shape = Shapes2027.Pill,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = emeraldColor, modifier = Modifier.size(14.dp))
                            Text(
                                text = "سورة العلق - الآيات 1-4",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

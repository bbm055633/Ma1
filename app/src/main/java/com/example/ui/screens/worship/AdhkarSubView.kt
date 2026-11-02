package com.example.ui.screens.worship

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
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
import com.example.ui.components.LocalStarryNightActive
import com.example.ui.theme.LocalCardTransparency
import com.example.ui.theme.Shapes2027

/**
 * تبويب الأذكار والتسبيح الإلكتروني - AdhkarSubView
 */
@Composable
fun AdhkarSubView(
    modifier: Modifier = Modifier
) {
    val isLiveSky = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isLiveSky) cardAlpha else 1.0f)
    val emeraldColor = MaterialTheme.colorScheme.primary

    var tasbeehCount by remember { mutableIntStateOf(0) }
    var selectedTasbeehIndex by remember { mutableIntStateOf(0) }
    val tasbeehPhrases = listOf(
        "سُبْحَانَ اللَّهِ",
        "الْحَمْدُ لِلَّهِ",
        "لَا إِلَهَ إِلَّا اللَّهُ",
        "اللَّهُ أَكْبَرُ",
        "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("adhkar_subview"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // بطاقة المسبحة الإلكترونية التفاعلية
        item {
            Surface(
                shape = Shapes2027.Large,
                color = surfaceColor,
                shadowElevation = if (isLiveSky) 0.dp else 2.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "المسبحة الإلكترونية الذكية 📿",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Tab selector for phrases
                    ScrollableTabRow(
                        selectedTabIndex = selectedTasbeehIndex,
                        edgePadding = 0.dp,
                        divider = {},
                        containerColor = Color.Transparent
                    ) {
                        tasbeehPhrases.forEachIndexed { index, phrase ->
                            Tab(
                                selected = selectedTasbeehIndex == index,
                                onClick = {
                                    selectedTasbeehIndex = index
                                    tasbeehCount = 0
                                },
                                text = {
                                    Text(
                                        text = phrase,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedTasbeehIndex == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }

                    // Big Interactive Counter Circle Button
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(emeraldColor.copy(alpha = 0.12f))
                            .testTag("tasbeeh_tap_circle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$tasbeehCount",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                color = emeraldColor
                            )
                            Text(
                                text = "/ 33",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Action buttons: Tap + Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { tasbeehCount = 0 },
                            shape = Shapes2027.Medium,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تصفير", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { tasbeehCount++ },
                            shape = Shapes2027.Medium,
                            colors = ButtonDefaults.buttonColors(containerColor = emeraldColor),
                            modifier = Modifier.weight(2f).testTag("tasbeeh_increment_btn")
                        ) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تسبيح (+1)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

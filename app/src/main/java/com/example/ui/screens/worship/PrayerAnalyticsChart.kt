package com.example.ui.screens.worship

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SaudiGold

/**
 * Data model for a single day's prayer trend metrics.
 */
data class DayTrendMetric(
    val dayName: String,
    val dayShort: String,
    val punctualityScore: Float, // 0..100
    val consistencyScore: Float, // 0..100
    val jamaahCount: Int,
    val individualCount: Int,
    val lateCount: Int,
    val sunnahCount: Int
)

/**
 * Recharts-inspired native Jetpack Compose Data Visualization component
 * displaying weekly prayer punctuality and consistency trends with dual monotone spline curves,
 * gradient area fills, dashed gridlines, and interactive tooltips.
 */
@Composable
fun PrayerWeeklyTrendSection(
    modifier: Modifier = Modifier
) {
    var selectedWeekIndex by remember { mutableStateOf(0) } // 0: الأسبوع الحالي, 1: الأسبوع الماضي
    var selectedDayIndex by remember { mutableStateOf<Int?>(4) } // Default select Thursday/current day

    val currentWeekData = remember {
        listOf(
            DayTrendMetric("السبت", "سبت", 80f, 85f, 3, 1, 1, 2),
            DayTrendMetric("الأحد", "أحد", 90f, 95f, 4, 1, 0, 3),
            DayTrendMetric("الإثنين", "إثنين", 95f, 100f, 5, 0, 0, 4),
            DayTrendMetric("الثلاثاء", "ثلاثاء", 85f, 90f, 4, 1, 0, 3),
            DayTrendMetric("الأربعاء", "أربعاء", 92f, 95f, 4, 1, 0, 3),
            DayTrendMetric("الخميس", "خميس", 98f, 100f, 5, 0, 0, 4),
            DayTrendMetric("الجمعة", "جمعة", 100f, 100f, 5, 0, 0, 5)
        )
    }

    val previousWeekData = remember {
        listOf(
            DayTrendMetric("السبت", "سبت", 75f, 80f, 3, 1, 1, 2),
            DayTrendMetric("الأحد", "أحد", 80f, 85f, 3, 2, 0, 2),
            DayTrendMetric("الإثنين", "إثنين", 88f, 90f, 4, 1, 0, 3),
            DayTrendMetric("الثلاثاء", "ثلاثاء", 80f, 85f, 3, 1, 1, 2),
            DayTrendMetric("الأربعاء", "أربعاء", 85f, 90f, 4, 1, 0, 3),
            DayTrendMetric("الخميس", "خميس", 90f, 95f, 4, 1, 0, 3),
            DayTrendMetric("الجمعة", "جمعة", 95f, 100f, 5, 0, 0, 4)
        )
    }

    val activeData = if (selectedWeekIndex == 0) currentWeekData else previousWeekData
    val avgPunctuality = activeData.map { it.punctualityScore }.average().toInt()
    val avgConsistency = activeData.map { it.consistencyScore }.average().toInt()
    val totalJamaah = activeData.sumOf { it.jamaahCount }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_trend_analytics_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Title & Week Filter Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "تحليل المواظبة والالتزام",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "مؤشر الانضباط الأسبوعي (Recharts Trend)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Week Toggle Tabs
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        listOf("الحالي", "السابق").forEachIndexed { index, label ->
                            val isSelected = selectedWeekIndex == index
                            Surface(
                                onClick = {
                                    selectedWeekIndex = index
                                    selectedDayIndex = null
                                },
                                shape = RoundedCornerShape(9.dp),
                                color = if (isSelected) EmeraldPrimary else Color.Transparent,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // KPI Stats Row (3 Highlights)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrendKpiItem(
                    title = "دقة التوقيت",
                    value = "$avgPunctuality%",
                    badge = "+5%",
                    badgeColor = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
                TrendKpiItem(
                    title = "معدل الإتمام",
                    value = "$avgConsistency%",
                    badge = "ممتاز",
                    badgeColor = SaudiGold,
                    modifier = Modifier.weight(1f)
                )
                TrendKpiItem(
                    title = "صلاة جماعة",
                    value = "$totalJamaah/35",
                    badge = "${(totalJamaah * 100) / 35}%",
                    badgeColor = Color(0xFF0288D1),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recharts Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Punctuality Series
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    )
                    Text(
                        text = "دقة المواظبة في الوقت (Punctuality)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Consistency Series
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SaudiGold)
                    )
                    Text(
                        text = "نسبة الإتمام (Consistency)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recharts-Style Interactive Area Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                RechartsNativeCanvas(
                    data = activeData,
                    selectedIndex = selectedDayIndex,
                    onSelectDay = { selectedDayIndex = it },
                    modifier = Modifier.fillMaxSize()
                )

                // Floating Tooltip when a day is tapped
                selectedDayIndex?.let { idx ->
                    if (idx in activeData.indices) {
                        val day = activeData[idx]
                        RechartsFloatingTooltip(
                            day = day,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day Labels Row below X-axis
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                activeData.forEachIndexed { idx, item ->
                    val isSelected = selectedDayIndex == idx
                    Surface(
                        onClick = { selectedDayIndex = if (isSelected) null else idx },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else Color.Transparent
                    ) {
                        Text(
                            text = item.dayShort,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Motivational note
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EmeraldPrimary.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "أحسنت! نسبة أدائك لصلاة الجماعة ارتفعت بمقدار 12% هذا الأسبوع. واظب على الصف الأول.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TrendKpiItem(
    title: String,
    value: String,
    badge: String,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Native Canvas implementation of Recharts-style Area & Monotone Line Chart.
 */
@Composable
fun RechartsNativeCanvas(
    data: List<DayTrendMetric>,
    selectedIndex: Int?,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val emeraldColor = EmeraldPrimary
    val goldColor = SaudiGold
    val gridLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)

    Canvas(
        modifier = modifier.pointerInput(data) {
            detectTapGestures { offset ->
                val w = size.width
                val paddingX = 24.dp.toPx()
                val availableW = w - paddingX * 2
                val stepX = availableW / (data.size - 1).coerceAtLeast(1)

                val tapX = offset.x - paddingX
                val index = (tapX / stepX + 0.5f).toInt().coerceIn(0, data.lastIndex)
                onSelectDay(index)
            }
        }
    ) {
        val w = size.width
        val h = size.height
        val paddingX = 24.dp.toPx()
        val paddingYTop = 16.dp.toPx()
        val paddingYBottom = 24.dp.toPx()
        val chartH = h - paddingYTop - paddingYBottom
        val chartW = w - paddingX * 2
        val stepX = chartW / (data.size - 1).coerceAtLeast(1)

        // 1. Draw 4 Horizontal Dashed Gridlines (0%, 25%, 50%, 75%, 100%)
        val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        for (i in 0..4) {
            val ratio = i / 4f
            val y = paddingYTop + chartH * (1f - ratio)
            drawLine(
                color = gridLineColor,
                start = Offset(paddingX, y),
                end = Offset(w - paddingX, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashedEffect
            )
        }

        // Compute points for Series 1 (Punctuality) and Series 2 (Consistency)
        val punctualityPoints = data.mapIndexed { idx, d ->
            val x = paddingX + idx * stepX
            val normY = (d.punctualityScore / 100f).coerceIn(0f, 1f)
            val y = paddingYTop + chartH * (1f - normY)
            Offset(x, y)
        }

        val consistencyPoints = data.mapIndexed { idx, d ->
            val x = paddingX + idx * stepX
            val normY = (d.consistencyScore / 100f).coerceIn(0f, 1f)
            val y = paddingYTop + chartH * (1f - normY)
            Offset(x, y)
        }

        // 2. Draw Recharts Gradient Area for Punctuality Series
        val areaPath = Path().apply {
            moveTo(punctualityPoints.first().x, paddingYTop + chartH)
            lineTo(punctualityPoints.first().x, punctualityPoints.first().y)
            for (i in 0 until punctualityPoints.size - 1) {
                val p0 = punctualityPoints[i]
                val p1 = punctualityPoints[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
            lineTo(punctualityPoints.last().x, paddingYTop + chartH)
            close()
        }

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    emeraldColor.copy(alpha = 0.38f),
                    emeraldColor.copy(alpha = 0.03f)
                ),
                startY = paddingYTop,
                endY = paddingYTop + chartH
            )
        )

        // 3. Draw Monotone Spline Curve for Series 1 (Punctuality)
        val punctualityLinePath = Path().apply {
            moveTo(punctualityPoints.first().x, punctualityPoints.first().y)
            for (i in 0 until punctualityPoints.size - 1) {
                val p0 = punctualityPoints[i]
                val p1 = punctualityPoints[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
        }
        drawPath(
            path = punctualityLinePath,
            color = emeraldColor,
            style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
        )

        // 4. Draw Monotone Spline Curve for Series 2 (Consistency)
        val consistencyLinePath = Path().apply {
            moveTo(consistencyPoints.first().x, consistencyPoints.first().y)
            for (i in 0 until consistencyPoints.size - 1) {
                val p0 = consistencyPoints[i]
                val p1 = consistencyPoints[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
        }
        drawPath(
            path = consistencyLinePath,
            color = goldColor,
            style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
        )

        // 5. Draw Circular Dots at each point
        punctualityPoints.forEachIndexed { idx, pt ->
            val isSelected = selectedIndex == idx
            drawCircle(
                color = Color.White,
                radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = emeraldColor,
                radius = if (isSelected) 4.5.dp.toPx() else 2.8.dp.toPx(),
                center = pt
            )
        }

        consistencyPoints.forEachIndexed { idx, pt ->
            val isSelected = selectedIndex == idx
            drawCircle(
                color = Color.White,
                radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = goldColor,
                radius = if (isSelected) 3.5.dp.toPx() else 2.2.dp.toPx(),
                center = pt
            )
        }

        // 6. Draw vertical guide line for selected point
        selectedIndex?.let { idx ->
            if (idx in punctualityPoints.indices) {
                val selX = punctualityPoints[idx].x
                drawLine(
                    color = emeraldColor.copy(alpha = 0.5f),
                    start = Offset(selX, paddingYTop),
                    end = Offset(selX, paddingYTop + chartH),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                )
            }
        }
    }
}

/**
 * Recharts-style Tooltip popup card with detailed breakdown.
 */
@Composable
fun RechartsFloatingTooltip(
    day: DayTrendMetric,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${day.dayName} • تفاصيل الالتزام",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldPrimary))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "الوقت: ${day.punctualityScore.toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SaudiGold))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "الإتمام: ${day.consistencyScore.toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SaudiGold
                    )
                }

                Text(
                    text = "جماعة: ${day.jamaahCount}/5",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

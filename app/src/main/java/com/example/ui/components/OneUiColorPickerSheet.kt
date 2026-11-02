package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ZheenCard

/**
 * 5 Standard One UI 8.5 Preset Colors as specified:
 * - Saudi Green: #0B3D2E (Default)
 * - Blue: #3B82F6
 * - Purple: #8B5CF6
 * - Orange: #F97316
 * - Gold: #D4A75C
 */
data class OneUiColorPreset(
    val nameAr: String,
    val hex: String,
    val color: Color
)

val ONE_UI_COLOR_PRESETS = listOf(
    OneUiColorPreset("الأخضر السعودي", "#0B3D2E", Color(0xFF0B3D2E)),
    OneUiColorPreset("الأزرق", "#3B82F6", Color(0xFF3B82F6)),
    OneUiColorPreset("البنفسجي", "#8B5CF6", Color(0xFF8B5CF6)),
    OneUiColorPreset("البرتقالي", "#F97316", Color(0xFFF97316)),
    OneUiColorPreset("الذهبي", "#D4A75C", Color(0xFFD4A75C))
)

/**
 * Generates harmonious gradient shades from the user-selected primary color for the 56x56 circular icons.
 */
fun generateGradientForColor(baseColor: Color, variantIndex: Int = 0): Brush {
    return when (variantIndex % 5) {
        0 -> Brush.linearGradient(
            listOf(
                baseColor.copy(alpha = 0.85f),
                baseColor
            )
        )
        1 -> Brush.linearGradient(
            listOf(
                Color(0xFF38BDF8),
                baseColor
            )
        )
        2 -> Brush.linearGradient(
            listOf(
                Color(0xFFFDE68A),
                baseColor
            )
        )
        3 -> Brush.linearGradient(
            listOf(
                baseColor.copy(alpha = 0.7f),
                baseColor
            )
        )
        else -> Brush.linearGradient(
            listOf(
                baseColor,
                baseColor.copy(alpha = 0.9f)
            )
        )
    }
}

/**
 * Samsung One UI 8.5 Standard Card:
 * - Pure White container #FFFFFF
 * - 24.dp corner radius
 * - Soft shadow (0 4 16 rgba(0,0,0,0.06))
 * - 20.dp padding
 * - 56x56 circular colorful icon on the right (RTL start)
 * - Title Bold 18 on left, Description Regular 14 gray
 */
@Composable
fun OneUi85Card(
    title: String,
    description: String,
    icon: ImageVector,
    primaryColor: Color,
    gradientVariantIndex: Int = 0,
    modifier: Modifier = Modifier,
    trailingBadge: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    ZheenCard(
        modifier = modifier.fillMaxWidth(),
        primaryColor = primaryColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 56x56 Circular Colorful Icon on the Right (RTL start)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(generateGradientForColor(primaryColor, gradientVariantIndex))
                        .shadow(3.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Title Bold 17 on Left, Description Regular 13 Gray
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }

            trailingBadge?.invoke()
        }
    }
}

val LocalStarryNightActive = androidx.compose.runtime.compositionLocalOf { false }

/**
 * Authentic 8% Opacity Islamic Geometric Arabesque Pattern Background on #F3F0EB warm off-white (2027 design).
 * Automatically adapts when Starry Night Live Theme is active across the application.
 */
@Composable
fun IslamicGeometricPatternBackground(
    modifier: Modifier = Modifier,
    patternColor: Color = if (LocalStarryNightActive.current) Color.Transparent else if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) Color(0x0AFFFFFF) else Color(0x08000000),
    backgroundColor: Color = if (LocalStarryNightActive.current) Color.Transparent else MaterialTheme.colorScheme.background,
    content: @Composable BoxScope.() -> Unit
) {
    val isLiveSkyActive = LocalStarryNightActive.current
    val effectiveBg = if (isLiveSkyActive) Color.Transparent else backgroundColor
    val effectivePattern = if (isLiveSkyActive) Color.Transparent else patternColor

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(effectiveBg)
    ) {
        if (!isLiveSkyActive && effectivePattern != Color.Transparent) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 84.dp.toPx()
                val starRadius = 24.dp.toPx()
                val strokeWidth = 0.75.dp.toPx()

                val cols = (size.width / step).toInt() + 2
                val rows = (size.height / step).toInt() + 2

            for (i in 0..cols) {
                for (j in 0..rows) {
                    val cx = i * step
                    val cy = j * step

                    // Overlapping squares forming an 8-pointed star
                    val path1 = Path().apply {
                        moveTo(cx - starRadius, cy - starRadius)
                        lineTo(cx + starRadius, cy - starRadius)
                        lineTo(cx + starRadius, cy + starRadius)
                        lineTo(cx - starRadius, cy + starRadius)
                        close()
                    }
                    val path2 = Path().apply {
                        val diag = starRadius * 1.414f
                        moveTo(cx, cy - diag)
                        lineTo(cx + diag, cy)
                        lineTo(cx, cy + diag)
                        lineTo(cx - diag, cy)
                        close()
                    }

                    drawPath(
                        path = path1,
                        color = patternColor,
                        style = Stroke(width = strokeWidth)
                    )
                    drawPath(
                        path = path2,
                        color = patternColor,
                        style = Stroke(width = strokeWidth)
                    )

                    // Arabesque connector lines
                    drawLine(
                        color = patternColor,
                        start = Offset(cx - step / 2, cy),
                        end = Offset(cx + step / 2, cy),
                        strokeWidth = strokeWidth * 0.8f
                    )
                    drawLine(
                        color = patternColor,
                        start = Offset(cx, cy - step / 2),
                        end = Offset(cx, cy + step / 2),
                        strokeWidth = strokeWidth * 0.8f
                    )
                }
            }
        }
        }
        content()
    }
}

/**
 * One UI 8.5 Dynamic Color Selection Panel & Custom Color Picker:
 * - 5 Preset Colors: Saudi Green #0B3D2E, Blue #3B82F6, Purple #8B5CF6, Orange #F97316, Gold #D4A75C
 * - Custom Color button opening color picker
 * - Live preview instantly updating app colors
 * - Saved in SharedPreferences
 */
@Composable
fun OneUiColorPickerSheet(
    selectedHex: String,
    onSelectHex: (String) -> Unit,
    onDismiss: () -> Unit
) {
    OneUiThemeSelectionDialog(
        selectedHex = selectedHex,
        onSelectHex = onSelectHex,
        onDismiss = onDismiss
    )
}

@Composable
fun OneUiThemeSelectionDialog(
    selectedHex: String,
    onSelectHex: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var isCustomPickerOpen by remember { mutableStateOf(false) }
    var customHexInput by remember { mutableStateOf(selectedHex) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "مركز التصميم والألوان",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1B1A)
                        )
                        Text(
                            text = "Samsung One UI 8.5 • تخصيص ديناميكي مباشر",
                            fontSize = 12.sp,
                            color = Color(0xFF6E685F)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                try {
                                    Color(android.graphics.Color.parseColor(selectedHex))
                                } catch (e: Exception) {
                                    Color(0xFF0B3D2E)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = Color(0xFFE8E4DC))

                Text(
                    text = "الألوان المعتمدة الجاهزة (5 Presets):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1A)
                )

                // 5 Presets Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ONE_UI_COLOR_PRESETS.forEach { preset ->
                        val isSelected = selectedHex.equals(preset.hex, ignoreCase = true)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clickable {
                                    onSelectHex(preset.hex)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(preset.color)
                                    .border(
                                        width = if (isSelected) 3.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF1C1B1A) else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Text(
                                text = preset.nameAr,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF1C1B1A) else Color(0xFF6E685F)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Custom Color Button
                OutlinedButton(
                    onClick = { isCustomPickerOpen = !isCustomPickerOpen },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isCustomPickerOpen) Color(0xFFF9F6F0) else Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "لون مخصص (Custom Color Picker)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Interactive Custom Color Palette & Input
                AnimatedVisibility(visible = isCustomPickerOpen) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9F6F0), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "اختر درجة من الباليت المتنوعة أو أدخل كود Hex:",
                            fontSize = 12.sp,
                            color = Color(0xFF6E685F)
                        )

                        // Extended Palette Swatches
                        val extendedSwatches = listOf(
                            "#047857", "#0D9488", "#0284C7", "#2563EB",
                            "#4F46E5", "#7C3AED", "#9333EA", "#C026D3",
                            "#DB2777", "#E11D48", "#DC2626", "#EA580C",
                            "#D97706", "#CA8A04", "#65A30D", "#16A34A"
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(extendedSwatches) { hex ->
                                val swatchColor = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Gray }
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(swatchColor)
                                        .clickable {
                                            customHexInput = hex
                                            onSelectHex(hex)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedHex.equals(hex, ignoreCase = true)) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        // Hex Input Field
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customHexInput,
                                onValueChange = {
                                    customHexInput = it
                                    if (it.startsWith("#") && (it.length == 7 || it.length == 9)) {
                                        try {
                                            android.graphics.Color.parseColor(it)
                                            onSelectHex(it)
                                        } catch (e: Exception) {
                                            // Invalid hex while typing
                                        }
                                    }
                                },
                                label = { Text("رمز اللون (Hex Code)", fontSize = 11.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    val formatted = if (!customHexInput.startsWith("#")) "#$customHexInput" else customHexInput
                                    try {
                                        android.graphics.Color.parseColor(formatted)
                                        onSelectHex(formatted)
                                    } catch (e: Exception) {
                                        // Ignore
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = try {
                                        Color(android.graphics.Color.parseColor(selectedHex))
                                    } catch (e: Exception) {
                                        Color(0xFF0B3D2E)
                                    }
                                )
                            ) {
                                Text("تطبيق", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Live Preview Indicator
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF9F6F0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    try {
                                        Color(android.graphics.Color.parseColor(selectedHex))
                                    } catch (e: Exception) {
                                        Color(0xFF0B3D2E)
                                    }
                                )
                        )
                        Text(
                            text = "المعاينة الحية: تم تطبيق اللون فورياً على شريط العنوان والبطاقات والتنقل السفلي.",
                            fontSize = 11.sp,
                            color = Color(0xFF1C1B1A),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Dismiss Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = try {
                            Color(android.graphics.Color.parseColor(selectedHex))
                        } catch (e: Exception) {
                            Color(0xFF0B3D2E)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تم وحفظ الاختيار", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

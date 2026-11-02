package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ألوان الخلفيات والأسطح الراقية 2027
val WarmAlabasterScaffold = FahadColors2027.Light.Background
val MilkyFrostedSurface   = FahadColors2027.Light.Surface1
val SoftSandOutline       = FahadColors2027.Light.Outline
val CardSubtleGlow        = FahadColors2027.Light.Surface2

val DeepWarmInk           = FahadColors2027.Light.TextPrimary
val MutedEarthSecondary   = FahadColors2027.Light.TextSecondary
val InactiveIconBeige     = Color(0xFF94A3B8)

val SlotNeutralBackground = Color(0xFFF1F5F9)
val ActiveSunGold          = Color(0xFFFFB703)
val ActiveEmeraldMist      = Color(0xFF52B788)
val ProhibitedTimeAmber    = Color(0xFFFF6B35)

const val GlassSurfaceDarkAlpha = 0.88f
const val GlassSurfaceLightAlpha = 0.92f
const val GlassSurfaceVariantDarkAlpha = 0.72f
const val GlassSurfaceVariantLightAlpha = 0.75f


fun createDynamicColorScheme(
    darkTheme: Boolean,
    primaryColor: Color = FahadColors2027.Light.Primary
): ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            primary = if (primaryColor == FahadColors2027.Light.Primary) FahadColors2027.Dark.Primary else primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryColor.copy(alpha = 0.22f),
            onPrimaryContainer = Color.White,
            secondary = FahadColors2027.Dark.Secondary,
            onSecondary = Color.Black,
            secondaryContainer = FahadColors2027.Dark.Secondary.copy(alpha = 0.18f),
            onSecondaryContainer = Color.White,
            tertiary = FahadColors2027.Dark.Accent,
            background = FahadColors2027.Dark.Background,
            onBackground = FahadColors2027.Dark.TextPrimary,
            surface = FahadColors2027.Dark.Surface1,
            onSurface = FahadColors2027.Dark.TextPrimary,
            surfaceVariant = FahadColors2027.Dark.Surface2,
            onSurfaceVariant = FahadColors2027.Dark.TextSecondary,
            outline = FahadColors2027.Dark.Outline,
            error = FahadColors2027.Dark.Error
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = primaryColor.copy(alpha = 0.12f),
            onPrimaryContainer = primaryColor,
            secondary = FahadColors2027.Light.Secondary,
            onSecondary = Color.White,
            secondaryContainer = FahadColors2027.Light.Secondary.copy(alpha = 0.14f),
            onSecondaryContainer = primaryColor,
            tertiary = FahadColors2027.Light.Accent,
            background = FahadColors2027.Light.Background,
            onBackground = FahadColors2027.Light.TextPrimary,
            surface = FahadColors2027.Light.Surface1,
            onSurface = FahadColors2027.Light.TextPrimary,
            surfaceVariant = FahadColors2027.Light.Surface2,
            onSurfaceVariant = FahadColors2027.Light.TextSecondary,
            outline = FahadColors2027.Light.Outline,
            error = FahadColors2027.Light.Error
        )
    }
}

val LocalDesignStyle = androidx.compose.runtime.staticCompositionLocalOf { AppDesignStyle.STANDARD }

@Composable
fun ZheenTheme(
    darkTheme: Boolean = false,
    palette: AppThemePalette = AppThemePalette.SAUDI_EMERALD,
    primaryColor: Color = FahadColors2027.Light.Primary,
    designStyle: AppDesignStyle = AppDesignStyle.STANDARD,
    content: @Composable () -> Unit
) {
    val effectiveColor = if (primaryColor != FahadColors2027.Light.Primary) primaryColor else palette.primaryColor
    val colorScheme = createDynamicColorScheme(darkTheme, effectiveColor)

    androidx.compose.runtime.CompositionLocalProvider(
        LocalDesignStyle provides designStyle
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

val LocalCardTransparency = androidx.compose.runtime.compositionLocalOf { 1.0f }

@Composable
fun Modifier.zheenCardStyle(
    shape: androidx.compose.ui.graphics.Shape = Shapes2027.Large,
    primaryColor: Color = MaterialTheme.colorScheme.primary
): Modifier {
    val style = LocalDesignStyle.current
    val isDarkMode = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val transparency = LocalCardTransparency.current
    return when (style) {
        AppDesignStyle.MINIMALISM -> {
            val bg = (if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFFFFFFF)).copy(alpha = transparency)
            val borderCol = if (isDarkMode) Color(0xFF2E2E2E) else Color(0x0F000000)
            this
                .shadow(
                    elevation = 2.dp,
                    shape = Shapes2027.Medium,
                    clip = false,
                    spotColor = if (isDarkMode) Color.Black.copy(alpha = 0.5f) else Color(0x14000000),
                    ambientColor = if (isDarkMode) Color.Black.copy(alpha = 0.3f) else Color(0x08000000)
                )
                .clip(Shapes2027.Medium)
                .background(bg)
                .border(1.dp, borderCol, Shapes2027.Medium)
        }
        AppDesignStyle.FLAT_DESIGN -> {
            val bg = (if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFFFFFFF)).copy(alpha = transparency)
            val borderCol = if (isDarkMode) Color(0xFF2E2E2E) else Color(0x0F000000)
            this
                .shadow(
                    elevation = 2.dp,
                    shape = shape,
                    clip = false,
                    spotColor = if (isDarkMode) Color.Black.copy(alpha = 0.4f) else Color(0x12000000)
                )
                .clip(shape)
                .background(bg)
                .border(1.dp, borderCol, shape)
        }
        AppDesignStyle.NEOBRUTALISM -> {
            val bg = (if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFFFFFFF)).copy(alpha = transparency)
            val borderCol = if (isDarkMode) Color.White else Color(0xFF1A1A1A)
            this
                .then(
                    Modifier.drawBehind {
                        val offset = 4.dp.toPx()
                        drawRoundRect(
                            color = borderCol,
                            topLeft = Offset(offset, offset),
                            size = Size(size.width, size.height),
                            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Fill
                        )
                    }
                )
                .clip(Shapes2027.Large)
                .background(bg)
                .border(2.dp, borderCol, Shapes2027.Large)
        }
        else -> {
            // Standard 2027 Modern Aesthetic: Pure White (#FFFFFF), 3dp Elevation, 1dp Border rgba(0,0,0,0.06)
            val bg = (if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFFFFFFF)).copy(alpha = transparency)
            val borderCol = if (isDarkMode) Color(0xFF2E2E2E) else Color(0x0F000000)
            this
                .shadow(
                    elevation = 3.dp,
                    shape = shape,
                    clip = false,
                    spotColor = if (isDarkMode) Color.Black.copy(alpha = 0.5f) else Color(0x18000000),
                    ambientColor = if (isDarkMode) Color.Black.copy(alpha = 0.3f) else Color(0x0A000000)
                )
                .clip(shape)
                .background(bg)
                .border(1.dp, borderCol, shape)
        }
    }
}

/**
 * بطاقة موحدة للنظام تدعم جميع أنماط التصميم
 */
@Composable
fun ZheenCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = Shapes2027.Large,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Box(
        modifier = modifier
            .zheenCardStyle(shape, primaryColor)
            .then(clickModifier)
    ) {
        Column {
            content()
        }
    }
}



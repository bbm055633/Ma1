package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import sa.com.fahada.R

/**
 * AppTypography defines custom Arabic font families for Fahad AI 2027 design system.
 * Headings: Cairo
 * Body: Tajawal
 * Quran / Religious: Amiri
 */
object AppTypography {
    val headings = FontFamily(
        Font(R.font.cairo_bold, FontWeight.Bold),
        Font(R.font.cairo, FontWeight.Normal)
    )

    val body = FontFamily(
        Font(R.font.tajawal_regular, FontWeight.Normal),
        Font(R.font.tajawal_medium, FontWeight.Medium),
        Font(R.font.tajawal, FontWeight.Normal)
    )

    val quran = FontFamily(
        Font(R.font.amiri_regular, FontWeight.Normal),
        Font(R.font.amiri_bold, FontWeight.Bold),
        Font(R.font.amiri, FontWeight.Normal)
    )
}

// Aliases for backward compatibility
val AmiriFontFamily = AppTypography.quran
val QuranFontFamily = AppTypography.quran

/**
 * Custom TextStyle for Quranic verses and religious text.
 */
@Composable
fun QuranTextStyle(): TextStyle = TextStyle(
    fontFamily = AppTypography.quran,
    fontSize = 22.sp,
    lineHeight = 36.sp,
    textAlign = TextAlign.Center,
    letterSpacing = 0.5.sp,
)

/**
 * Material Theme Typography configuration mapping M3 styles to AppTypography.
 */
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppTypography.headings,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    displayMedium = TextStyle(
        fontFamily = AppTypography.headings,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    displaySmall = TextStyle(
        fontFamily = AppTypography.headings,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = AppTypography.headings,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AppTypography.headings,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AppTypography.headings,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AppTypography.headings,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppTypography.headings,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = AppTypography.headings,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppTypography.body,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppTypography.body,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = AppTypography.body,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AppTypography.body,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = AppTypography.body,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AppTypography.body,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )
)

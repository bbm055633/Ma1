package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PrayerPurple
import com.example.ui.theme.SaudiGold
import com.example.ui.theme.SaudiGoldDark
import com.example.ui.theme.SaudiGoldLight
import com.example.ui.theme.SuccessGreen

/**
 * Interactive Profile Avatar with:
 * 1. User photo display with authentic aspect ratio and circular crop (or monogram fallback).
 * 2. Day/Night reactive borders (Golden/Emerald day glow vs Midnight/Cosmic night ring with crescent moon badge).
 * 3. Animated circular progress bar tracking daily prayers & tasks completion.
 * 4. Pulsing celebration halo (Glow/Pulse effect) upon full completion.
 * 5. Photo picker integration using Android Photo Picker.
 */
@Composable
fun ZheenInteractiveProfileAvatar(
    imageUri: String?,
    onImageSelect: ((String?) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    progress: Float = 0.75f,
    isNight: Boolean = true,
    isCelebrationActive: Boolean = false,
    size: Dp = 48.dp,
    showProgressRing: Boolean = true,
    showBadge: Boolean = true,
    showEditBadge: Boolean = false,
    allowPhotoPickOnTap: Boolean = false,
    modifier: Modifier = Modifier
) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onImageSelect?.invoke(uri.toString())
            }
        }
    )

    val effectiveProgress = progress.coerceIn(0f, 1f)
    val isComplete = effectiveProgress >= 0.999f || isCelebrationActive

    // Smooth progress animation
    val animatedProgress by animateFloatAsState(
        targetValue = effectiveProgress,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "avatarProgress"
    )

    // Celebration pulsing halo effect
    val infiniteTransition = rememberInfiniteTransition(label = "avatarPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isComplete) 1.22f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (isComplete) 0.55f else 0.0f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Theme Color Palettes for Progress Ring & Frame Borders
    val dayRingBrush = remember {
        Brush.sweepGradient(
            listOf(
                SaudiGold,
                EmeraldPrimary,
                SaudiGoldLight,
                EmeraldLight,
                SaudiGold
            )
        )
    }

    val nightRingBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF38BDF8), // Cyan sky
                PrayerPurple,
                SaudiGoldLight,
                Color(0xFF818CF8), // Indigo
                Color(0xFF38BDF8)
            )
        )
    }

    // Day Frame Border: Clean, bright daylight gradient
    val dayFrameBorderBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFFF59E0B), // Warm amber gold
                Color(0xFFFBBF24), // Radiant sun gold
                Color(0xFF10B981), // Clean morning emerald
                Color(0xFFFEF3C7), // Dawn shimmer
                Color(0xFFF59E0B)
            )
        )
    }

    // Night Frame Border: Calm, serene starlight & midnight gradient
    val nightFrameBorderBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF38BDF8), // Celestial cyan
                Color(0xFF6366F1), // Gentle indigo
                Color(0xFF8B5CF6), // Cosmic starlight violet
                Color(0xFF1E293B), // Midnight deep slate
                Color(0xFF38BDF8)
            )
        )
    }

    val ringBrush = if (isNight) nightRingBrush else dayRingBrush
    val trackColor = if (isNight) Color(0xFF1E293B).copy(alpha = 0.65f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)

    // Proportional dimensions
    val ringStrokeWidth = (size.value * 0.075f).coerceIn(2.5f, 4.5f).dp
    val avatarPadding = if (showProgressRing) (size.value * 0.09f).coerceIn(3.5f, 6.5f).dp else 0.dp
    val badgeSize = (size.value * 0.38f).coerceIn(17f, 26f).dp

    Box(
        modifier = modifier
            .size(size)
            .testTag("zheen_interactive_profile_avatar"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Celebration Pulse / Glow Halo Effect (تأثير وميض أو نبض ضوئي ناعم احتفاءً بالإنجاز)
        if (isComplete) {
            // Outer breathing pulse aura
            Box(
                modifier = Modifier
                    .size(size * 1.35f)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            if (isNight) listOf(
                                Color(0xFF38BDF8).copy(alpha = pulseAlpha),
                                PrayerPurple.copy(alpha = pulseAlpha * 0.75f),
                                SaudiGoldLight.copy(alpha = pulseAlpha * 0.35f),
                                Color.Transparent
                            ) else listOf(
                                SaudiGoldLight.copy(alpha = pulseAlpha * 0.9f),
                                EmeraldLight.copy(alpha = pulseAlpha * 0.7f),
                                SuccessGreen.copy(alpha = pulseAlpha * 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Inner shimmering celebration halo
            Box(
                modifier = Modifier
                    .size(size * 1.15f)
                    .scale(pulseScale * 0.95f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            if (isNight) listOf(
                                Color(0xFF818CF8).copy(alpha = 0.35f),
                                Color.Transparent
                            ) else listOf(
                                SaudiGold.copy(alpha = 0.40f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // 2. Animated Circular Progress Track & Arc (حلقة تقدم دائرية متحركة)
        if (showProgressRing) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ringStrokeWidth / 2)
            ) {
                // Background Track
                drawCircle(
                    color = trackColor,
                    style = Stroke(width = ringStrokeWidth.toPx())
                )

                // Foreground Animated Progress Arc
                if (animatedProgress > 0f) {
                    drawArc(
                        brush = ringBrush,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(
                            width = ringStrokeWidth.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
        }

        // 3. User Avatar Frame (Photo / Monogram with Day/Night Theme Gradient Borders)
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = if (isNight) 6.dp else 4.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = if (showProgressRing) 2.dp else 2.5.dp,
                brush = if (isNight) nightFrameBorderBrush else dayFrameBorderBrush
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(avatarPadding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (allowPhotoPickOnTap && onImageSelect != null) {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        onClick?.invoke()
                    }
                }
                .testTag("avatar_inner_image")
        ) {
            if (!imageUri.isNullOrEmpty()) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "الصورة الشخصية للمستخدم",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                // Luxurious Saudi Arabic Monogram Fallback ("ف")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                if (isNight) listOf(
                                    Color(0xFF0F172A),
                                    PrayerPurple.copy(alpha = 0.7f),
                                    SaudiGoldDark.copy(alpha = 0.85f)
                                ) else listOf(
                                    EmeraldPrimary,
                                    EmeraldLight.copy(alpha = 0.85f),
                                    SaudiGold
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ف",
                        fontSize = (size.value * 0.42f).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 4. Dynamic Time of Day / Celebration / Edit Corner Badge (شارة الهلال ليلاً أو الإنجاز / التعديل)
        if (showBadge) {
            val badgeBorderBrush = if (isNight) {
                Brush.linearGradient(listOf(Color(0xFF38BDF8), SaudiGoldLight))
            } else {
                Brush.linearGradient(listOf(SaudiGold, Color(0xFFF59E0B)))
            }

            Surface(
                shape = CircleShape,
                color = when {
                    isNight -> Color(0xFF0B1120) // Deep celestial nocturnal background
                    showEditBadge -> MaterialTheme.colorScheme.primary
                    isComplete -> SuccessGreen
                    else -> Color(0xFFFFFBEB)
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.4.dp,
                    badgeBorderBrush
                ),
                shadowElevation = 5.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(badgeSize)
                    .offset(x = 1.dp, y = 1.dp)
                    .clickable(
                        enabled = onImageSelect != null,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    .testTag("avatar_corner_badge")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
                        // Night crescent moon badge (شارة الهلال في الليل)
                        isNight -> {
                            Text(
                                text = "🌙",
                                fontSize = (badgeSize.value * 0.62f).sp
                            )
                        }
                        showEditBadge -> {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "تغيير الصورة",
                                tint = Color.White,
                                modifier = Modifier.size(badgeSize * 0.58f)
                            )
                        }
                        isComplete -> {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "مكتمل",
                                tint = Color.White,
                                modifier = Modifier.size(badgeSize * 0.65f)
                            )
                        }
                        else -> {
                            Text(
                                text = "☀️",
                                fontSize = (badgeSize.value * 0.58f).sp
                            )
                        }
                    }
                }
            }
        }
    }
}

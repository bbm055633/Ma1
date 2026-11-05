package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom Shimmer loading animation modifier for skeleton loading screens.
 */
fun Modifier.shimmerLoadingAnimation(
    shimmerColor: Color = Color(0xFFE2E8F0),
    highlightColor: Color = Color(0xFFF8FAFC),
    durationMillis: Int = 1000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_float"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            shimmerColor.copy(alpha = 0.6f),
            highlightColor.copy(alpha = 0.9f),
            shimmerColor.copy(alpha = 0.6f)
        ),
        start = Offset(translateAnim.value - 200f, translateAnim.value - 200f),
        end = Offset(translateAnim.value, translateAnim.value)
    )

    this.background(brush)
}

/**
 * Skeleton Card Component for placeholding loading UI content.
 */
@Composable
fun SkeletonCard(
    height: Dp = 100.dp,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp
) {
    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shimmerLoadingAnimation()
    ) {}
}

/**
 * Skeleton Chat Bubble placeholder for AI responses.
 */
@Composable
fun SkeletonChatBubble(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SkeletonCard(height = 16.dp, cornerRadius = 8.dp, modifier = Modifier.fillMaxWidth(0.7f))
        SkeletonCard(height = 14.dp, cornerRadius = 8.dp, modifier = Modifier.fillMaxWidth(0.9f))
        SkeletonCard(height = 14.dp, cornerRadius = 8.dp, modifier = Modifier.fillMaxWidth(0.4f))
    }
}

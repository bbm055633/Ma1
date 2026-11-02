package com.example.ui.components.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.SkyColorPreset
import com.example.ui.viewmodel.StarryNightSettings
import com.example.ui.viewmodel.TerrainType
import java.util.Calendar
import kotlin.math.sin
import kotlin.random.Random

// Fixed star structure to avoid allocations in draw loop
private data class StarParticle(
    val xFraction: Float,
    val yFraction: Float,
    val radius: Float,
    val phase: Float,
    val speed: Float,
    val color: Color
)

private data class FireflyParticle(
    val xFraction: Float,
    val yFraction: Float,
    val radius: Float,
    val phase: Float
)

private data class SnowflakeParticle(
    val xFraction: Float,
    val speed: Float,
    val radius: Float,
    val drift: Float
)

/**
 * Live animated Starry Night sky backdrop with canvas-rendered stars, moon, clouds, meteors, and terrain.
 */
@Composable
fun StarryNightSkyView(
    settings: StarryNightSettings,
    modifier: Modifier = Modifier
) {
    // Generate persistent star field
    val stars = remember {
        val rand = Random(42)
        List(85) {
            StarParticle(
                xFraction = rand.nextFloat(),
                yFraction = rand.nextFloat() * 0.78f, // upper 78% of the sky
                radius = 1.0f + rand.nextFloat() * 2.2f,
                phase = rand.nextFloat() * 6.28f,
                speed = 0.8f + rand.nextFloat() * 1.5f,
                color = when (rand.nextInt(5)) {
                    0 -> Color(0xFFFDE047) // golden
                    1 -> Color(0xFF93C5FD) // cyan/blue
                    2 -> Color(0xFFF472B6) // rose
                    else -> Color(0xFFFFFFFF) // white
                }
            )
        }
    }

    // Generate persistent fireflies
    val fireflies = remember {
        val rand = Random(1337)
        List(18) {
            FireflyParticle(
                xFraction = rand.nextFloat(),
                yFraction = 0.55f + rand.nextFloat() * 0.35f,
                radius = 2.0f + rand.nextFloat() * 2.0f,
                phase = rand.nextFloat() * 6.28f
            )
        }
    }

    // Generate snowflakes
    val snowflakes = remember {
        val rand = Random(999)
        List(35) {
            SnowflakeParticle(
                xFraction = rand.nextFloat(),
                speed = 0.08f + rand.nextFloat() * 0.12f,
                radius = 1.5f + rand.nextFloat() * 2.5f,
                drift = (rand.nextFloat() - 0.5f) * 0.05f
            )
        }
    }

    // Infinite animation clock
    val infiniteTransition = rememberInfiniteTransition(label = "starryClock")
    val durationBase = if (settings.batterySaverMode) 25000 else 18000
    val effectiveDuration = (durationBase / settings.animationSpeedMultiplier.coerceIn(0.5f, 3.0f)).toInt()

    val rawTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(effectiveDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skyTime"
    )

    // Lightning pulse timer
    val lightningAlpha = remember(rawTime, settings.showLightning) {
        if (!settings.showLightning) 0f
        else {
            val progressInCycle = (rawTime * 12f) % 1.0f
            if (progressInCycle in 0.02f..0.05f) 0.65f
            else if (progressInCycle in 0.07f..0.085f) 0.40f
            else 0f
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. SKY GRADIENT
        val skyColors = getSkyColors(settings.skyColorPreset)
        drawRect(
            brush = Brush.verticalGradient(
                colors = skyColors,
                startY = 0f,
                endY = h
            ),
            size = size
        )

        // Lightning flash illumination
        if (lightningAlpha > 0f) {
            drawRect(
                color = Color.White.copy(alpha = lightningAlpha),
                size = size
            )
        }

        // 2. HORIZON GLOW
        if (settings.showHorizonGlow) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFFFB703).copy(alpha = 0.15f),
                        Color(0xFFE63946).copy(alpha = 0.28f)
                    ),
                    startY = h * 0.55f,
                    endY = h
                ),
                size = size
            )
        }

        // 3. MOON
        if (settings.showMoon) {
            drawCrescentMoon(w = w, h = h)
        }

        // 4. CLOUDS
        if (settings.showClouds) {
            drawSoftClouds(w = w, h = h, time = rawTime)
        }

        // 5. TWINKLING STARS
        if (settings.showStars) {
            stars.forEach { s ->
                val twinkle = (sin(rawTime * 20f * s.speed + s.phase) + 1f) / 2f
                val alpha = (0.25f + 0.75f * twinkle).coerceIn(0.1f, 1.0f)
                drawCircle(
                    color = s.color.copy(alpha = alpha),
                    radius = s.radius * (0.8f + 0.3f * twinkle),
                    center = Offset(s.xFraction * w, s.yFraction * h)
                )
            }
        }

        // 6. SHOOTING STARS / METEORS
        if (settings.showShootingStars) {
            val cycle = (rawTime * 4.5f) % 1.0f
            if (cycle in 0.1f..0.28f) {
                val meteorProgress = (cycle - 0.1f) / 0.18f
                val startX = w * (0.85f - meteorProgress * 0.5f)
                val startY = h * (0.08f + meteorProgress * 0.28f)
                val length = 80.dp.toPx()
                val endX = startX + length * 0.8f
                val endY = startY - length * 0.45f

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 1.0f - meteorProgress),
                            Color(0xFFFFB703).copy(alpha = (1.0f - meteorProgress) * 0.6f),
                            Color.Transparent
                        ),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2.5f
                )
            }
        }

        // 7. BIRDS
        if (settings.showBirds) {
            drawGlidingBirds(w = w, h = h, time = rawTime)
        }

        // 8. FIREFLIES
        if (settings.showFireflies) {
            fireflies.forEachIndexed { i, f ->
                val x = (f.xFraction + sin(rawTime * 6f + f.phase) * 0.04f) * w
                val y = (f.yFraction + sin(rawTime * 4f + f.phase * 1.5f) * 0.03f) * h
                val glowAlpha = ((sin(rawTime * 12f + f.phase) + 1f) / 2f).coerceIn(0.2f, 0.95f)

                drawCircle(
                    color = Color(0xFFA3E635).copy(alpha = glowAlpha * 0.35f),
                    radius = f.radius * 2.8f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color(0xFFFEF08A).copy(alpha = glowAlpha),
                    radius = f.radius,
                    center = Offset(x, y)
                )
            }
        }

        // 9. SNOWFALL
        if (settings.showSnow) {
            snowflakes.forEachIndexed { idx, flake ->
                val y = ((rawTime * flake.speed * 8f + idx * 0.15f) % 1.0f) * h
                val x = (flake.xFraction + sin(rawTime * 3f + idx) * 0.03f) * w
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = flake.radius,
                    center = Offset(x, y)
                )
            }
        }

        // 10. TERRAIN HORIZON SILHOUETTES
        drawTerrain(terrainType = settings.terrainType, w = w, h = h)
    }
}

private fun getSkyColors(preset: SkyColorPreset): List<Color> {
    return when (preset) {
        SkyColorPreset.AUTO -> {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            when (hour) {
                in 5..7 -> listOf(Color(0xFF0F172A), Color(0xFF312E81), Color(0xFFEA580C), Color(0xFFFBBF24))
                in 8..16 -> listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6), Color(0xFF93C5FD))
                in 17..19 -> listOf(Color(0xFF1E1B4B), Color(0xFF6B21A8), Color(0xFFBE185D), Color(0xFFF97316))
                else -> listOf(Color(0xFF070B14), Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF2E1065))
            }
        }
        SkyColorPreset.FOLLOW_DAY -> {
            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF020617))
        }
        SkyColorPreset.DUSK -> listOf(Color(0xFF1E1B4B), Color(0xFF4C1D95), Color(0xFF831843))
        SkyColorPreset.EMBER -> listOf(Color(0xFF350808), Color(0xFF661414), Color(0xFF8B1919))
        SkyColorPreset.TWILIGHT -> listOf(Color(0xFF042F2E), Color(0xFF065F46), Color(0xFF0F766E))
        SkyColorPreset.OCEAN -> listOf(Color(0xFF082F49), Color(0xFF0369A1), Color(0xFF0EA5E9))
        SkyColorPreset.PINK_SUNSET -> listOf(Color(0xFF581C87), Color(0xFF9D174D), Color(0xFFF43F5E))
        SkyColorPreset.LAVENDER -> listOf(Color(0xFF312E81), Color(0xFF4F46E5), Color(0xFF818CF8))
    }
}

private fun DrawScope.drawCrescentMoon(w: Float, h: Float) {
    val centerX = w * 0.82f
    val centerY = h * 0.16f
    val moonRadius = 26.dp.toPx()

    // Outer soft glow aura
    drawCircle(
        color = Color(0xFFFEF08A).copy(alpha = 0.18f),
        radius = moonRadius * 1.7f,
        center = Offset(centerX, centerY)
    )

    // Full moon base disc
    drawCircle(
        color = Color(0xFFFEF9C3).copy(alpha = 0.95f),
        radius = moonRadius,
        center = Offset(centerX, centerY)
    )

    // Shadow cutout circle to form crescent
    drawCircle(
        color = Color(0xFF0F172A),
        radius = moonRadius * 0.88f,
        center = Offset(centerX - moonRadius * 0.42f, centerY - moonRadius * 0.22f)
    )
}

private fun DrawScope.drawSoftClouds(w: Float, h: Float, time: Float) {
    val cloudColor = Color(0xFF38435E).copy(alpha = 0.28f)
    val offsetX = (time * 0.3f) % 1.0f

    val cloud1X = ((0.15f + offsetX) % 1.4f - 0.2f) * w
    drawOval(
        color = cloudColor,
        topLeft = Offset(cloud1X, h * 0.14f),
        size = Size(180.dp.toPx(), 45.dp.toPx())
    )

    val cloud2X = ((0.65f + offsetX * 0.8f) % 1.4f - 0.2f) * w
    drawOval(
        color = cloudColor.copy(alpha = 0.20f),
        topLeft = Offset(cloud2X, h * 0.24f),
        size = Size(220.dp.toPx(), 55.dp.toPx())
    )
}

private fun DrawScope.drawGlidingBirds(w: Float, h: Float, time: Float) {
    val birdProgress = (time * 1.5f) % 1.4f - 0.2f
    val bx = birdProgress * w
    val by = h * 0.32f
    val birdColor = Color(0xFF1E293B).copy(alpha = 0.75f)

    // First bird
    drawBirdV(bx, by, 12f, birdColor)
    // Follower birds
    drawBirdV(bx - 32f, by + 18f, 10f, birdColor)
    drawBirdV(bx - 55f, by + 8f, 8f, birdColor)
}

private fun DrawScope.drawBirdV(x: Float, y: Float, span: Float, color: Color) {
    val path = Path().apply {
        moveTo(x - span, y)
        quadraticTo(x - span / 2f, y - span / 2f, x, y)
        quadraticTo(x + span / 2f, y - span / 2f, x + span, y)
    }
    drawPath(path, color = color, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.0f))
}

private fun DrawScope.drawTerrain(terrainType: TerrainType, w: Float, h: Float) {
    if (terrainType == TerrainType.NONE) return

    val terrainBaseY = h * 0.86f
    val darkHorizon = Color(0xFF090D16)
    val midHorizon = Color(0xFF111827)

    when (terrainType) {
        TerrainType.NONE -> {}
        TerrainType.HILLS -> {
            // Background hills layer
            val bgPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, terrainBaseY - 35f)
                quadraticTo(w * 0.35f, terrainBaseY - 80f, w * 0.65f, terrainBaseY - 20f)
                quadraticTo(w * 0.85f, terrainBaseY + 10f, w, terrainBaseY - 45f)
                lineTo(w, h)
                close()
            }
            drawPath(bgPath, color = midHorizon, style = Fill)

            // Foreground rolling hills
            val fgPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, terrainBaseY + 20f)
                quadraticTo(w * 0.25f, terrainBaseY - 25f, w * 0.55f, terrainBaseY + 15f)
                quadraticTo(w * 0.8f, terrainBaseY + 45f, w, terrainBaseY)
                lineTo(w, h)
                close()
            }
            drawPath(fgPath, color = darkHorizon, style = Fill)
        }

        TerrainType.PEAKS -> {
            // Mountain peaks
            val peakPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, terrainBaseY)
                lineTo(w * 0.2f, terrainBaseY - 90f)
                lineTo(w * 0.38f, terrainBaseY - 20f)
                lineTo(w * 0.6f, terrainBaseY - 110f)
                lineTo(w * 0.78f, terrainBaseY - 35f)
                lineTo(w * 0.9f, terrainBaseY - 70f)
                lineTo(w, terrainBaseY)
                lineTo(w, h)
                close()
            }
            drawPath(peakPath, color = darkHorizon, style = Fill)
        }

        TerrainType.DUNES -> {
            // Desert dunes
            val dunePath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, terrainBaseY - 15f)
                cubicTo(w * 0.25f, terrainBaseY - 65f, w * 0.5f, terrainBaseY + 10f, w * 0.75f, terrainBaseY - 40f)
                lineTo(w, terrainBaseY)
                lineTo(w, h)
                close()
            }
            drawPath(dunePath, color = Color(0xFF1C1308), style = Fill)
        }

        TerrainType.FOREST -> {
            // Layer 1: Distant blue mountain backdrop
            val hillPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, terrainBaseY - 25f)
                quadraticTo(w * 0.5f, terrainBaseY - 60f, w, terrainBaseY - 20f)
                lineTo(w, h)
                close()
            }
            drawPath(hillPath, color = Color(0xFF141A29), style = Fill)

            // Layer 2: Dense pine tree forest line (matching the screenshot)
            val treeStep = 11.dp.toPx()
            val numTrees = (w / treeStep).toInt() + 2
            val treeRand = Random(777)

            val forestPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, terrainBaseY + 20f)

                for (i in 0..numTrees) {
                    val tx = i * treeStep
                    val treeHeight = 24.dp.toPx() + (treeRand.nextFloat() * 26.dp.toPx())
                    val topY = terrainBaseY - treeHeight + 15f
                    // Draw pine peak
                    lineTo(tx - treeStep * 0.5f, topY + treeHeight * 0.45f)
                    lineTo(tx, topY)
                    lineTo(tx + treeStep * 0.5f, topY + treeHeight * 0.45f)
                }

                lineTo(w, terrainBaseY + 20f)
                lineTo(w, h)
                close()
            }
            drawPath(forestPath, color = darkHorizon, style = Fill)
        }
    }
}

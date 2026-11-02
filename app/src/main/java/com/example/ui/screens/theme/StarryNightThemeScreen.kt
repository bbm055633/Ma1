package com.example.ui.screens.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.theme.StarryNightSkyView
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.SkyColorPreset
import com.example.ui.viewmodel.TerrainType

/**
 * شاشة تخصيص الخلفية الحية "ليلة مرصعة بالنجوم" (Starry Night Live Theme)
 * مطابقة تماماً للمعاينة والتصميم المطلوب لعام 2027
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarryNightThemeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.starryNightSettings.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()

    androidx.activity.compose.BackHandler { onBack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D111E)) // Deep cosmic midnight canvas
    ) {
        // Ambient soft live sky background behind this settings screen itself
        StarryNightSkyView(
            settings = settings.copy(animationSpeedMultiplier = (settings.animationSpeedMultiplier * 0.7f).coerceAtLeast(0.4f)),
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isEnglish) "Starry Night Live Theme" else "ليلة مرصعة بالنجوم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("starry_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (isEnglish) "Back" else "رجوع",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val maxW = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)

                // 1. MASTER TOGGLE CARD (بطاقة التفعيل الرئيسية)
                Surface(
                    shape = Shapes2027.Large,
                    color = Color(0xFF14192B).copy(alpha = 0.88f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3552).copy(alpha = 0.7f)),
                    shadowElevation = 4.dp,
                    modifier = maxW.testTag("starry_master_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Switch(
                            checked = settings.isEnabled,
                            onCheckedChange = { viewModel.toggleStarryNightEnabled() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF14192B),
                                checkedTrackColor = Color(0xFFFFB703),
                                uncheckedThumbColor = Color(0xFF8B8B8B),
                                uncheckedTrackColor = Color(0xFF252D42)
                            ),
                            modifier = Modifier.testTag("starry_master_switch")
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isEnglish) "Starry Night Live Sky" else "ليلة مرصعة بالنجوم",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isEnglish) "Show live celestial sky as theme across the entire app" else "إظهار سماء ليلية حيّة كثيم لكامل التطبيق",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text("🌙", fontSize = 24.sp)
                        }
                    }
                }

                // 2. PREVIEW & CONTROLS SECTION (بطاقة المعاينة والتحكم)
                Column(modifier = maxW, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEnglish) "Live Preview" else "معاينة",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🌙", fontSize = 14.sp)
                    }

                    Surface(
                        shape = Shapes2027.Large,
                        color = Color(0xFF14192B).copy(alpha = 0.88f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3552).copy(alpha = 0.7f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left Column: Sliders for Card Transparency & Speed
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isEnglish) "Card Transparency • ${(settings.cardTransparency * 100).toInt()}%"
                                        else "شفافية البطاقات • %${(settings.cardTransparency * 100).toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Slider(
                                        value = settings.cardTransparency,
                                        onValueChange = { newVal ->
                                            viewModel.updateStarryNightSettings { it.copy(cardTransparency = newVal) }
                                        },
                                        valueRange = 0.20f..1.0f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color(0xFFFFB703),
                                            activeTrackColor = Color(0xFFFFB703),
                                            inactiveTrackColor = Color(0xFF2E3852)
                                        )
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (isEnglish) "Motion Speed • ${(settings.animationSpeedMultiplier * 100).toInt()}%"
                                        else "سرعة الحركة • %${(settings.animationSpeedMultiplier * 100).toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Slider(
                                        value = settings.animationSpeedMultiplier,
                                        onValueChange = { newVal ->
                                            viewModel.updateStarryNightSettings { it.copy(animationSpeedMultiplier = newVal) }
                                        },
                                        valueRange = 0.5f..3.0f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color(0xFFFFB703),
                                            activeTrackColor = Color(0xFFFFB703),
                                            inactiveTrackColor = Color(0xFF2E3852)
                                        )
                                    )
                                }
                            }

                            // Right: Mini Phone Screen Mockup displaying live background and test floating cards
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.Black,
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF38435E)),
                                modifier = Modifier
                                    .width(115.dp)
                                    .height(185.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Mini Live Canvas
                                    StarryNightSkyView(
                                        settings = settings,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // 2 Floating Cards showcasing transparency
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 8.dp, vertical = 35.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF1E293B).copy(alpha = settings.cardTransparency),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = if (isEnglish) "Reading" else "القراءة",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF1E293B).copy(alpha = settings.cardTransparency),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = if (isEnglish) "Wake up early" else "الاستيقاظ باكراً",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. SKY COLOR PALETTES (ألوان السماء)
                Column(modifier = maxW, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isEnglish) "Sky Colors" else "ألوان السماء",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )

                    Surface(
                        shape = Shapes2027.Large,
                        color = Color(0xFF14192B).copy(alpha = 0.88f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3552).copy(alpha = 0.7f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Row 1: Follow Day / Auto
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SkyPresetPill(
                                    preset = SkyColorPreset.DUSK,
                                    currentPreset = settings.skyColorPreset,
                                    onClick = { viewModel.updateStarryNightSettings { it.copy(skyColorPreset = SkyColorPreset.DUSK) } },
                                    modifier = Modifier.weight(0.8f)
                                )
                                SkyPresetPill(
                                    preset = SkyColorPreset.AUTO,
                                    currentPreset = settings.skyColorPreset,
                                    isHighlight = true,
                                    onClick = { viewModel.updateStarryNightSettings { it.copy(skyColorPreset = SkyColorPreset.AUTO) } },
                                    modifier = Modifier.weight(1.4f)
                                )
                                SkyPresetPill(
                                    preset = SkyColorPreset.FOLLOW_DAY,
                                    currentPreset = settings.skyColorPreset,
                                    onClick = { viewModel.updateStarryNightSettings { it.copy(skyColorPreset = SkyColorPreset.FOLLOW_DAY) } },
                                    modifier = Modifier.weight(1.1f)
                                )
                            }

                            // Row 2: Ember / Twilight / Ocean / Pink Sunset
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SkyPresetPill(
                                    preset = SkyColorPreset.EMBER,
                                    currentPreset = settings.skyColorPreset,
                                    onClick = { viewModel.updateStarryNightSettings { it.copy(skyColorPreset = SkyColorPreset.EMBER) } },
                                    modifier = Modifier.weight(1f)
                                )
                                SkyPresetPill(
                                    preset = SkyColorPreset.TWILIGHT,
                                    currentPreset = settings.skyColorPreset,
                                    onClick = { viewModel.updateStarryNightSettings { it.copy(skyColorPreset = SkyColorPreset.TWILIGHT) } },
                                    modifier = Modifier.weight(1f)
                                )
                                SkyPresetPill(
                                    preset = SkyColorPreset.OCEAN,
                                    currentPreset = settings.skyColorPreset,
                                    onClick = { viewModel.updateStarryNightSettings { it.copy(skyColorPreset = SkyColorPreset.OCEAN) } },
                                    modifier = Modifier.weight(1f)
                                )
                                SkyPresetPill(
                                    preset = SkyColorPreset.PINK_SUNSET,
                                    currentPreset = settings.skyColorPreset,
                                    onClick = { viewModel.updateStarryNightSettings { it.copy(skyColorPreset = SkyColorPreset.PINK_SUNSET) } },
                                    modifier = Modifier.weight(1.1f)
                                )
                            }

                            // Row 3: Lavender
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                SkyPresetPill(
                                    preset = SkyColorPreset.LAVENDER,
                                    currentPreset = settings.skyColorPreset,
                                    onClick = { viewModel.updateStarryNightSettings { it.copy(skyColorPreset = SkyColorPreset.LAVENDER) } },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // 4. TERRAIN HORIZONS (تضاريس الأفق السفلية)
                Column(modifier = maxW, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isEnglish) "Horizon Terrain" else "جبال وتضاريس الأفق",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )

                    Surface(
                        shape = Shapes2027.Large,
                        color = Color(0xFF14192B).copy(alpha = 0.88f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3552).copy(alpha = 0.7f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TerrainType.entries.forEach { terrain ->
                                val isSelected = terrain == settings.terrainType
                                Surface(
                                    onClick = {
                                        viewModel.updateStarryNightSettings { it.copy(terrainType = terrain) }
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) Color(0xFFFFB703) else Color(0xFF1F273E),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = terrain.titleAr,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF14192B) else Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. SKY ELEMENTS (عناصر السماء - شبكة ثنائية 2-Column Grid)
                Column(modifier = maxW, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEnglish) "Sky Elements" else "عناصر السماء",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )

                    Surface(
                        shape = Shapes2027.Large,
                        color = Color(0xFF14192B).copy(alpha = 0.88f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3552).copy(alpha = 0.7f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Row 1: نجوم متلألئة & برق
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Twinkling Stars" else "نجوم متلألئة",
                                    icon = Icons.Outlined.AutoAwesome,
                                    checked = settings.showStars,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(showStars = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Lightning" else "برق",
                                    icon = Icons.Outlined.FlashOn,
                                    checked = settings.showLightning,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(showLightning = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Row 2: شهب & القمر
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Meteors / Shooting" else "شهب",
                                    icon = Icons.Outlined.TrendingDown,
                                    checked = settings.showShootingStars,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(showShootingStars = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Moon" else "القمر",
                                    icon = Icons.Outlined.Nightlight,
                                    checked = settings.showMoon,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(showMoon = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Row 3: غيوم & طيور
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Drifting Clouds" else "غيوم",
                                    icon = Icons.Outlined.Cloud,
                                    checked = settings.showClouds,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(showClouds = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Birds" else "طيور",
                                    icon = Icons.Outlined.Flight,
                                    checked = settings.showBirds,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(showBirds = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Row 4: يراعات & تساقط الثلج
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Fireflies" else "يراعات مضيئة",
                                    icon = Icons.Outlined.Grain,
                                    checked = settings.showFireflies,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(showFireflies = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Snowfall" else "تساقط الثلج",
                                    icon = Icons.Outlined.AcUnit,
                                    checked = settings.showSnow,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(showSnow = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Row 5: توهج الأفق & وضع توفير الطاقة
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Horizon Glow" else "توهج الأفق",
                                    icon = Icons.Outlined.WbTwilight,
                                    checked = settings.showHorizonGlow,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(showHorizonGlow = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                                SkyElementToggleCard(
                                    title = if (isEnglish) "Battery Saver Mode" else "وضع توفير البطارية",
                                    icon = Icons.Outlined.BatterySaver,
                                    checked = settings.batterySaverMode,
                                    onCheckedChange = { v -> viewModel.updateStarryNightSettings { it.copy(batterySaverMode = v) } },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun SkyPresetPill(
    preset: SkyColorPreset,
    currentPreset: SkyColorPreset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    val isSelected = preset == currentPreset
    val colors = preset.hexColors.map { Color(it) }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        border = if (isSelected || isHighlight) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFB703))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
        },
        modifier = modifier.height(38.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(colors)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = preset.titleAr,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun SkyElementToggleCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        shape = Shapes2027.Medium,
        color = Color(0xFF1E263D).copy(alpha = 0.75f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (checked) Color(0xFFFFB703).copy(alpha = 0.6f) else Color(0xFF2C3552).copy(alpha = 0.5f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF14192B),
                    checkedTrackColor = Color(0xFFFFB703),
                    uncheckedThumbColor = Color(0xFF8B8B8B),
                    uncheckedTrackColor = Color(0xFF252D42)
                ),
                modifier = Modifier.size(width = 38.dp, height = 24.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) Color(0xFFFFB703) else Color(0xFF94A3B8),
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

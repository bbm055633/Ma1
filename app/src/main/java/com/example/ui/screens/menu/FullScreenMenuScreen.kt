package com.example.ui.screens.menu

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.ui.components.LocalStarryNightActive
import com.example.ui.components.OneUiThemeSelectionDialog
import com.example.ui.components.ZheenInteractiveProfileAvatar
import com.example.ui.components.theme.StarryNightSkyView
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.SkyColorPreset
import com.example.ui.viewmodel.TerrainType

/**
 * شاشة الإعدادات ومركز التحكم التنفيذي الشامل 2027 (Fahad AI Modern Control Hub)
 * تصميم Material 3 عصري مختصر ومدمج يجمع:
 * 1. الهوية البصرية وثيمات 2027 المدمجة مع ليلة مرصعة بالنجوم والتحكم الكامل في شفافية وسرعة وألوان السماء.
 * 2. إدارة الملف الشخصي والحساب التنفيذي.
 * 3. إعدادات النظام، الصلوات، التنبيهات، والذكاء الاصطناعي.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenMenuScreen(
    viewModel: MainViewModel,
    isDarkMode: Boolean,
    themePalette: AppThemePalette,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // State from ViewModel
    val userProfileImageUri by viewModel.userProfileImageUri.collectAsState()
    val dailyProgress by viewModel.dailyProgress.collectAsState()
    val isCelebrationActive by viewModel.isDailyCelebrationActive.collectAsState()
    val fontScale by viewModel.appFontScale.collectAsState()
    val densityScale by viewModel.appDensityScale.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()
    val currentDesignStyle by viewModel.designStyle.collectAsState()
    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()
    val userPrimaryColorHex by viewModel.userPrimaryColorHex.collectAsState()
    val starryNightSettings by viewModel.starryNightSettings.collectAsState()
    val themeSettings by viewModel.themeSettings.collectAsState()
    val repetitionDhikrText by viewModel.repetitionDhikrText.collectAsState()
    val repetitionTarget by viewModel.repetitionTarget.collectAsState()

    val isPrayerAlertsEnabled by viewModel.isPrayerAlertsEnabled.collectAsState()
    val calculationMethod by viewModel.calculationMethod.collectAsState()
    val iqamaDurationMinutes by viewModel.iqamaDurationMinutes.collectAsState()
    val azanSoundType by viewModel.azanSoundType.collectAsState()
    val scheduleAlertsEnabled by viewModel.scheduleAlertsEnabled.collectAsState()
    val scheduleAlertTone by viewModel.scheduleAlertTone.collectAsState()
    val taskAlertsEnabled by viewModel.taskAlertsEnabled.collectAsState()
    val taskAlertTone by viewModel.taskAlertTone.collectAsState()
    val isDndDuringWorship by viewModel.isDndDuringWorship.collectAsState()
    val dndDurationMinutes by viewModel.dndDurationMinutes.collectAsState()

    // Active Tab in Settings (0 = المظهر وثيمات 2027, 1 = الملف الشخصي, 2 = النظام والصلوات)
    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialogs & UI state
    var showColorDialog by remember { mutableStateOf(false) }
    var showCityDropdown by remember { mutableStateOf(false) }
    var showCalcMethodDropdown by remember { mutableStateOf(false) }
    var showIqamaDropdown by remember { mutableStateOf(false) }
    var showAzanSoundDropdown by remember { mutableStateOf(false) }
    var showScheduleToneDropdown by remember { mutableStateOf(false) }
    var showTaskToneDropdown by remember { mutableStateOf(false) }
    var showSaveToast by remember { mutableStateOf(false) }

    // Profile form state
    var editableName by remember { mutableStateOf(userName.ifEmpty { "فهد بن محمد القحطاني" }) }
    var editableEmail by remember { mutableStateOf(userEmail.ifEmpty { "fahad61119@gmail.com" }) }
    var editableAge by remember { mutableStateOf("34") }
    var editableHeight by remember { mutableStateOf("178") }
    var editableWeight by remember { mutableStateOf("75") }
    var editablePassword by remember { mutableStateOf("••••••••") }

    // System toggles
    var biometricEnabled by remember { mutableStateOf(true) }
    var localSyncEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var adhanAlertsEnabled by remember { mutableStateOf(true) }
    var selectedVoiceTone by remember { mutableStateOf("رسمي مهني / Professional") }

    androidx.activity.compose.BackHandler { onBack() }

    val isStarryActive = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "Settings & Hub 2027" else "الإعدادات ومركز التحكم 2027",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = Shapes2027.Pill,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "2027",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isEnglish) "Close" else "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // مفتاح سريع لليلة مرصعة بالنجوم
                    IconButton(
                        onClick = { viewModel.toggleStarryNightEnabled() },
                        modifier = Modifier.testTag("settings_quick_starry_toggle")
                    ) {
                        Icon(
                            imageVector = if (starryNightSettings.isEnabled) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Toggle Starry Night",
                            tint = if (starryNightSettings.isEnabled) Color(0xFFFFB703) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // تبديل الوضع الليلي / النهاري السريع
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("settings_quick_dark_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isStarryActive) Color.Transparent else MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = if (isStarryActive) Color.Transparent else MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val maxW = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)

            // ================================================================
            // شريط التبويبات الثلاثي المختصر (Concise Segmented Navigation 2027)
            // ================================================================
            Surface(
                shape = Shapes2027.Pill,
                color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                shadowElevation = if (isStarryActive) 0.dp else 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = maxW
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf(
                        Triple(0, if (isEnglish) "Themes 2027" else "المظهر والثيمات", Icons.Default.Palette),
                        Triple(1, if (isEnglish) "Profile" else "الملف الشخصي", Icons.Default.Person),
                        Triple(2, if (isEnglish) "System & Prayer" else "النظام والصلوات", Icons.Default.Tune)
                    )

                    tabs.forEach { (index, title, icon) ->
                        val isSelected = selectedTab == index
                        Surface(
                            shape = Shapes2027.Pill,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clip(Shapes2027.Pill)
                                .clickable { selectedTab = index }
                                .testTag("settings_tab_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 9.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // ================================================================
            // التبويب 0: 🎨 المظهر وثيمات 2027 المدمجة مع ليلة مرصعة بالنجوم
            // ================================================================
            if (selectedTab == 0) {
                // 1. بطاقة الوضع الرئيسي 2027 (نهاري ترابي ناعم #F3F0EB vs ليلي عميق #0D0D0D)
                Surface(
                    shape = Shapes2027.Large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                    shadowElevation = if (isStarryActive) 0.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = maxW
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("☀️", fontSize = 16.sp)
                                Text(
                                    text = if (isEnglish) "Primary Theme Mode 2027" else "نمط العرض الرئيسي 2027",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = if (isDarkMode) (if (isEnglish) "Dark Mode Active" else "الوضع الليلي نشط") else (if (isEnglish) "Light Mode Active" else "الوضع النهاري نشط"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // خيار النهار 2027 (#F3F0EB خلفية، #FFFFFF بطاقات)
                            Surface(
                                shape = Shapes2027.Medium,
                                color = if (!isDarkMode) FahadColors2027.Light.Primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (!isDarkMode) 2.dp else 1.dp,
                                    if (!isDarkMode) FahadColors2027.Light.Primary else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(Shapes2027.Medium)
                                    .clickable { if (isDarkMode) viewModel.toggleDarkMode() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(FahadColors2027.Light.Background)
                                            .border(1.dp, FahadColors2027.Light.Primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("☀️", fontSize = 13.sp)
                                    }
                                    Column {
                                        Text(
                                            text = if (isEnglish) "Light Mode" else "النهار 2027",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "#F3F0EB ترابي ناعم",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // خيار الليل 2027 (#0D0D0D خلفية، #1A1A1A بطاقات)
                            Surface(
                                shape = Shapes2027.Medium,
                                color = if (isDarkMode) FahadColors2027.Dark.Primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isDarkMode) 2.dp else 1.dp,
                                    if (isDarkMode) FahadColors2027.Dark.Primary else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(Shapes2027.Medium)
                                    .clickable { if (!isDarkMode) viewModel.toggleDarkMode() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(FahadColors2027.Dark.Background)
                                            .border(1.dp, FahadColors2027.Dark.Primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🌙", fontSize = 13.sp)
                                    }
                                    Column {
                                        Text(
                                            text = if (isEnglish) "Dark Mode" else "الليل 2027",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "#0D0D0D هدوء عميق",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. بطاقة مركز تحكم "ليلة مرصعة بالنجوم" المدمج (Starry Night Live Sky Hub)
                Surface(
                    shape = Shapes2027.Large,
                    color = if (starryNightSettings.isEnabled) Color(0xFF131A2B) else MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                    shadowElevation = if (starryNightSettings.isEnabled) 0.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        if (starryNightSettings.isEnabled) Color(0xFFFFB703).copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline
                    ),
                    modifier = maxW.testTag("starry_night_hub_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // تفعيل الثيم الحي
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(Color(0xFF2E1065), Color(0xFF0F172A)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🌌", fontSize = 18.sp)
                                }
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = if (isEnglish) "Starry Night (Live Sky Background)" else "ليلة مرصعة بالنجوم (سماء حية)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (starryNightSettings.isEnabled) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (starryNightSettings.isEnabled) {
                                            Surface(
                                                shape = Shapes2027.Pill,
                                                color = Color(0xFFFFB703).copy(alpha = 0.2f),
                                                border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFFFFB703))
                                            ) {
                                                Text(
                                                    text = if (isEnglish) "ACTIVE" else "نشط",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFFB703),
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = if (isEnglish) "Applies live moving sky across the entire app" else "تطبيق سماء حية متحركة لخلفية التطبيق بالكامل",
                                        fontSize = 11.sp,
                                        color = if (starryNightSettings.isEnabled) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = starryNightSettings.isEnabled,
                                onCheckedChange = { viewModel.toggleStarryNightEnabled() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFFFB703),
                                    checkedTrackColor = Color(0xFF2E1065)
                                ),
                                modifier = Modifier.testTag("starry_night_toggle")
                            )
                        }

                        // المحتوى المدمج عند التفعيل (أو للعرض والتحكم السريع)
                        AnimatedVisibility(visible = starryNightSettings.isEnabled) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

                                // معاينة مصغرة مع سلايدر الشفافية والسرعة
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // مصغر المعاينة الحية
                                    Box(
                                        modifier = Modifier
                                            .width(96.dp)
                                            .height(130.dp)
                                            .clip(Shapes2027.Medium)
                                            .border(1.dp, Color(0xFFFFB703).copy(alpha = 0.4f), Shapes2027.Medium)
                                    ) {
                                        StarryNightSkyView(
                                            settings = starryNightSettings,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        // طبقة بطاقة شفافة توضيحية
                                        Surface(
                                            shape = Shapes2027.Small,
                                            color = Color(0xFF1E293B).copy(alpha = starryNightSettings.cardTransparency),
                                            modifier = Modifier
                                                .fillMaxWidth(0.85f)
                                                .align(Alignment.Center)
                                                .padding(4.dp)
                                        ) {
                                            Text(
                                                text = if (isEnglish) "Preview" else "معاينة",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                    }

                                    // سلايدر الشفافية وسلايدر السرعة
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // شفافية البطاقات
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isEnglish) "Card Transparency" else "شفافية البطاقات",
                                                fontSize = 11.sp,
                                                color = Color(0xFFE2E8F0),
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${(starryNightSettings.cardTransparency * 100).toInt()}%",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB703)
                                            )
                                        }
                                        Slider(
                                            value = starryNightSettings.cardTransparency,
                                            onValueChange = { newAlpha ->
                                                viewModel.updateStarryNightSettings { it.copy(cardTransparency = newAlpha) }
                                            },
                                            valueRange = 0.35f..1.0f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = Color(0xFFFFB703),
                                                activeTrackColor = Color(0xFFFFB703),
                                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                                            ),
                                            modifier = Modifier.height(28.dp)
                                        )

                                        // سرعة حركة السماء
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isEnglish) "Motion Speed" else "سرعة حركة السماء",
                                                fontSize = 11.sp,
                                                color = Color(0xFFE2E8F0),
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${(starryNightSettings.animationSpeedMultiplier * 100).toInt()}%",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB703)
                                            )
                                        }
                                        Slider(
                                            value = starryNightSettings.animationSpeedMultiplier,
                                            onValueChange = { newSpeed ->
                                                viewModel.updateStarryNightSettings { it.copy(animationSpeedMultiplier = newSpeed) }
                                            },
                                            valueRange = 0.5f..3.0f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = Color(0xFFFFB703),
                                                activeTrackColor = Color(0xFFFFB703),
                                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                                            ),
                                            modifier = Modifier.height(28.dp)
                                        )
                                    }
                                }

                                // ألوان السماء السريعة
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = if (isEnglish) "Sky Atmosphere Presets:" else "تدرجات ألوان السماء:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE2E8F0)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        SkyColorPreset.entries.take(4).forEach { preset ->
                                            val isSel = starryNightSettings.skyColorPreset == preset
                                            Surface(
                                                shape = Shapes2027.Pill,
                                                color = if (isSel) Color(0xFFFFB703).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    if (isSel) 1.5.dp else 0.5.dp,
                                                    if (isSel) Color(0xFFFFB703) else Color.White.copy(alpha = 0.2f)
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(Shapes2027.Pill)
                                                    .clickable {
                                                        viewModel.updateStarryNightSettings { it.copy(skyColorPreset = preset) }
                                                    }
                                            ) {
                                                Text(
                                                    text = if (isEnglish) preset.titleEn else preset.titleAr,
                                                    fontSize = 10.sp,
                                                    color = if (isSel) Color(0xFFFFB703) else Color.White,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // زر فتح شاشة التخصيص الكاملة بالتفصيل
                                OutlinedButton(
                                    onClick = { viewModel.navigateTo(ActiveScreen.StarryNightCustomizer) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = Shapes2027.Medium,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB703).copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB703),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isEnglish) "Advanced Sky Elements & Terrains..." else "تخصيص عناصر السماء والتضاريس بالتفصيل...",
                                        fontSize = 11.sp,
                                        color = Color(0xFFFFB703),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. لوحة ألوان فهد المعتمدة 2027 (Fahad Colors 2027 Palettes)
                Surface(
                    shape = Shapes2027.Large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                    shadowElevation = if (isStarryActive) 0.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = maxW
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("🎨", fontSize = 16.sp)
                                Text(
                                    text = if (isEnglish) "2027 Color Palette" else "لوحة ألوان فهد المعتمدة 2027",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            TextButton(onClick = { showColorDialog = true }) {
                                Text(
                                    text = if (isEnglish) "Custom Hex" else "لون مخصص",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // الألوان الرئيسية المعتمدة لعام 2027
                        val modern2027Colors = listOf(
                            Triple("#1B4332", if (isEnglish) "Deep Emerald" else "أخضر عميق", Color(0xFF1B4332)),
                            Triple("#D4A373", if (isEnglish) "Warm Gold" else "ذهبي دافئ", Color(0xFFD4A373)),
                            Triple("#FF6B35", if (isEnglish) "Vivid Orange" else "برتقالي حي", Color(0xFFFF6B35)),
                            Triple("#4895EF", if (isEnglish) "Royal Ocean" else "أزرق ملكي", Color(0xFF4895EF)),
                            Triple("#9D4EDD", if (isEnglish) "Modern Purple" else "بنفسجي عصري", Color(0xFF9D4EDD))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            modern2027Colors.forEach { (hex, label, color) ->
                                val isSelected = userPrimaryColorHex.equals(hex, ignoreCase = true)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(Shapes2027.Medium)
                                        .clickable { viewModel.setUserPrimaryColor(hex) }
                                        .padding(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                if (isSelected) 3.dp else 1.dp,
                                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.15f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. نمط التصميم المعتمد لعام 2027 (Design Styles)
                Surface(
                    shape = Shapes2027.Large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                    shadowElevation = if (isStarryActive) 0.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = maxW
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "Visual Architecture Style" else "نمط الهندسة البصرية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Pair(AppDesignStyle.STANDARD, if (isEnglish) "2027 Standard" else "قياسي 2027"),
                                Pair(AppDesignStyle.MINIMALISM, if (isEnglish) "Minimalism" else "مينيمال"),
                                Pair(AppDesignStyle.FLAT_DESIGN, if (isEnglish) "Flat" else "مسطح"),
                                Pair(AppDesignStyle.NEOBRUTALISM, if (isEnglish) "NeoBrutal" else "نيوبروتال")
                            ).forEach { (style, title) ->
                                val isSel = currentDesignStyle == style
                                Surface(
                                    shape = Shapes2027.Pill,
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(Shapes2027.Pill)
                                        .clickable { viewModel.selectDesignStyle(style) }
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ================================================================
            // التبويب 1: 👤 الملف الشخصي التنفيذي والحساب
            // ================================================================
            if (selectedTab == 1) {
                // بطاقة VIP الفاخرة
                Surface(
                    shape = Shapes2027.Large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                    shadowElevation = if (isStarryActive) 0.dp else 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = maxW.testTag("profile_vip_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ZheenInteractiveProfileAvatar(
                                imageUri = userProfileImageUri,
                                onImageSelect = { uri -> viewModel.setUserProfileImageUri(uri) },
                                progress = dailyProgress,
                                isNight = isDarkMode,
                                isCelebrationActive = isCelebrationActive,
                                size = 64.dp,
                                showProgressRing = true,
                                showBadge = true,
                                showEditBadge = true,
                                allowPhotoPickOnTap = true
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = editableName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("👑", fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isEnglish) "Enterprise Admin & AI Consultant" else "مدير تنفيذي ومستشار الذكاء الاصطناعي",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = editableEmail,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                        // رصيد العمليات والباقة التنفيذية
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = Shapes2027.Small,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Text(if (isEnglish) "VIP Tier" else "الباقة التنفيذية VIP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            FilledTonalButton(
                                onClick = { viewModel.navigateTo(ActiveScreen.PaymentHistory) },
                                shape = Shapes2027.Pill,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isEnglish) "Wallet & Logs" else "المحفظة والعمليات", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // بطاقة تعديل البيانات السريعة
                Surface(
                    shape = Shapes2027.Large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                    shadowElevation = if (isStarryActive) 0.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = maxW
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "Edit Profile Information" else "تعديل البيانات الأساسية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = editableName,
                            onValueChange = { editableName = it },
                            label = { Text(if (isEnglish) "Name" else "الاسم") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            singleLine = true,
                            shape = Shapes2027.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = editableEmail,
                            onValueChange = { editableEmail = it },
                            label = { Text(if (isEnglish) "Email" else "البريد الإلكتروني") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            singleLine = true,
                            shape = Shapes2027.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editableAge,
                                onValueChange = { editableAge = it },
                                label = { Text(if (isEnglish) "Age" else "العمر") },
                                singleLine = true,
                                shape = Shapes2027.Medium,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editableHeight,
                                onValueChange = { editableHeight = it },
                                label = { Text(if (isEnglish) "Height (cm)" else "الطول") },
                                singleLine = true,
                                shape = Shapes2027.Medium,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editableWeight,
                                onValueChange = { editableWeight = it },
                                label = { Text(if (isEnglish) "Weight (kg)" else "الوزن") },
                                singleLine = true,
                                shape = Shapes2027.Medium,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.setUserName(editableName)
                                viewModel.setUserEmail(editableEmail)
                                showSaveToast = true
                            },
                            shape = Shapes2027.Medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEnglish) "Save Changes" else "حفظ التعديلات", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ================================================================
            // التبويب 2: ⚙️ النظام، الصلوات، التنبيهات، والذكاء الاصطناعي
            // ================================================================
            if (selectedTab == 2) {
                // 1. بطاقة المواقيت والتنبيهات Comprehensive Prayer Times & Notifications
                Surface(
                    shape = Shapes2027.Large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                    shadowElevation = if (isStarryActive) 0.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = maxW
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("🕌", fontSize = 16.sp)
                                Text(
                                    text = if (isEnglish) "Prayer Times & Notifications" else "المواقيت والتنبيهات",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            TextButton(onClick = { showCityDropdown = !showCityDropdown }) {
                                Text(
                                    text = if (isEnglish) selectedCity.nameEn else selectedCity.nameAr,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // اختيار المدينة
                        if (showCityDropdown) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), Shapes2027.Medium)
                                    .padding(8.dp)
                            ) {
                                PrayerTimeCalculator.SAUDI_CITIES.forEach { city ->
                                    val isSelected = selectedCity.nameAr == city.nameAr
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(Shapes2027.Small)
                                            .clickable {
                                                viewModel.setCity(city)
                                                showCityDropdown = false
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isEnglish) city.nameEn else city.nameAr,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        // طريقة حساب مواقيت الأذان
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showCalcMethodDropdown = !showCalcMethodDropdown }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isEnglish) "Calculation Method" else "طريقة حساب مواقيت الأذان",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = calculationMethod,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            if (showCalcMethodDropdown) {
                                val methods = listOf(
                                    "تقويم أم القرى (المملكة العربية السعودية)",
                                    "رابطة العالم الإسلامي",
                                    "الهيئة العامة المصرية للمساحة",
                                    "جامعة العلوم الإسلامية بكراتشي",
                                    "الاتحاد الإسلامي بأمريكا الشمالية (ISNA)"
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), Shapes2027.Medium)
                                        .padding(8.dp)
                                ) {
                                    methods.forEach { m ->
                                        val isSel = calculationMethod == m
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(Shapes2027.Small)
                                                .clickable {
                                                    viewModel.setCalculationMethod(m)
                                                    showCalcMethodDropdown = false
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = m,
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isSel) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // مدة الإقامة بعد الأذان بالدقائق
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showIqamaDropdown = !showIqamaDropdown }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isEnglish) "Iqama Duration" else "مدة الإقامة بعد الأذان",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$iqamaDurationMinutes دقيقة",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            if (showIqamaDropdown) {
                                val durations = listOf(10, 15, 20, 25, 30)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    durations.forEach { d ->
                                        val isSel = iqamaDurationMinutes == d
                                        FilterChip(
                                            selected = isSel,
                                            onClick = {
                                                viewModel.setIqamaDurationMinutes(d)
                                                showIqamaDropdown = false
                                            },
                                            label = { Text("$d دقيقة", fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        // تفعيل أو إيقاف تنبيهات الأذان
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isEnglish) "Adhan & Prayer Alerts" else "تنبيهات الأذان والصلوات",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isPrayerAlertsEnabled) "التنبيهات مفعلة لمواقيت الصلاة" else "التنبيهات معطلة",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isPrayerAlertsEnabled,
                                onCheckedChange = { viewModel.setPrayerAlertsEnabled(it) }
                            )
                        }

                        // نوع التنبيه للأذان
                        if (isPrayerAlertsEnabled) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showAzanSoundDropdown = !showAzanSoundDropdown }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isEnglish) "Adhan Alert Sound" else "نوع التنبيه للأذان",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = azanSoundType,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                if (showAzanSoundDropdown) {
                                    val soundTypes = listOf(
                                        "صوت المؤذن (الأذان الكامل)",
                                        "صوت تنبيه عادي (نغمة هادئة)",
                                        "اهتزاز فقط (صامت)"
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), Shapes2027.Medium)
                                            .padding(8.dp)
                                    ) {
                                        soundTypes.forEach { st ->
                                            val isSel = azanSoundType == st
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(Shapes2027.Small)
                                                    .clickable {
                                                        viewModel.setAzanSoundType(st)
                                                        showAzanSoundDropdown = false
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = st,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (isSel) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // وضع عدم الإزعاج DND
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isEnglish) "DND Mode During Prayer" else "وضع الصامت تلقائياً عند الصلاة (DND)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isDndDuringWorship) "كتم الجوال تلقائياً أثناء الصلاة" else "معطل",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isDndDuringWorship,
                                onCheckedChange = { viewModel.setDndDuringWorshipEnabled(it) }
                            )
                        }
                    }
                }

                // 2. بطاقة تنبيهات المواعيد والمهام Schedule & Task Alerts
                Surface(
                    shape = Shapes2027.Large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                    shadowElevation = if (isStarryActive) 0.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = maxW
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🔔", fontSize = 16.sp)
                            Text(
                                text = if (isEnglish) "Schedule & Task Alerts" else "تنبيهات المواعيد والمهام",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // تنبيهات المواعيد والتقويم
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isEnglish) "Schedule Alerts" else "تنبيهات المواعيد والتقويم",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (scheduleAlertsEnabled) "التنبيهات مفعلة" else "التنبيهات معطلة",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = scheduleAlertsEnabled,
                                onCheckedChange = { viewModel.setScheduleAlertsEnabled(it) }
                            )
                        }

                        if (scheduleAlertsEnabled) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showScheduleToneDropdown = !showScheduleToneDropdown }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isEnglish) "Schedule Alert Tone" else "نغمة تنبيه المواعيد",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = scheduleAlertTone,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                if (showScheduleToneDropdown) {
                                    val tones = listOf(
                                        "الافتراضي (تنبيه تنفيذي)",
                                        "نغمة هادئة",
                                        "نغمة كلاسيكية",
                                        "جرس رقمي"
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), Shapes2027.Medium)
                                            .padding(8.dp)
                                    ) {
                                        tones.forEach { t ->
                                            val isSel = scheduleAlertTone == t
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(Shapes2027.Small)
                                                    .clickable {
                                                        viewModel.setScheduleAlertTone(t)
                                                        showScheduleToneDropdown = false
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = t,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (isSel) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        // تنبيهات المهام والتذكيرات
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isEnglish) "Task Alerts" else "تنبيهات المهام والتذكيرات",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (taskAlertsEnabled) "التنبيهات مفعلة" else "التنبيهات معطلة",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = taskAlertsEnabled,
                                onCheckedChange = { viewModel.setTaskAlertsEnabled(it) }
                            )
                        }

                        if (taskAlertsEnabled) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showTaskToneDropdown = !showTaskToneDropdown }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isEnglish) "Task Alert Tone" else "نغمة تنبيه المهام",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = taskAlertTone,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                if (showTaskToneDropdown) {
                                    val tones = listOf(
                                        "الافتراضي (نغمة إنجاز)",
                                        "تنبيه خفيف",
                                        "نغمة طوارئ",
                                        "تنبيه ذكي"
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), Shapes2027.Medium)
                                            .padding(8.dp)
                                    ) {
                                        tones.forEach { t ->
                                            val isSel = taskAlertTone == t
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(Shapes2027.Small)
                                                    .clickable {
                                                        viewModel.setTaskAlertTone(t)
                                                        showTaskToneDropdown = false
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = t,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (isSel) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. بطاقة خيارات الأمان والذكاء الاصطناعي
                Surface(
                    shape = Shapes2027.Large,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isStarryActive) cardAlpha else 1.0f),
                    shadowElevation = if (isStarryActive) 0.dp else 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = maxW
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "Security & Intelligence Settings" else "خيارات الأمان والذكاء الاصطناعي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isEnglish) "Biometric Lock" else "القفل البيومتري بالبصمة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Switch(checked = biometricEnabled, onCheckedChange = { biometricEnabled = it })
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isEnglish) "Push Notifications" else "الإشعارات الفورية والتنبيهات", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (isEnglish) "Encrypted Local Sync" else "المزامنة المشفرة محلياً", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            Switch(checked = localSyncEnabled, onCheckedChange = { localSyncEnabled = it })
                        }
                    }
                }

                // زر تسجيل الخروج التنفيذي
                OutlinedButton(
                    onClick = {
                        viewModel.navigateTo(ActiveScreen.Login)
                    },
                    modifier = maxW,
                    shape = Shapes2027.Medium,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isEnglish) "Sign Out" else "تسجيل الخروج", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Color Picker Dialog
    if (showColorDialog) {
        OneUiThemeSelectionDialog(
            selectedHex = userPrimaryColorHex,
            onSelectHex = { hex ->
                viewModel.setUserPrimaryColor(hex)
                showColorDialog = false
            },
            onDismiss = { showColorDialog = false }
        )
    }

    // Save Confirmation Snackbar
    if (showSaveToast) {
        AlertDialog(
            onDismissRequest = { showSaveToast = false },
            title = { Text(if (isEnglish) "Saved" else "تم الحفظ بنجاح", fontWeight = FontWeight.Bold) },
            text = { Text(if (isEnglish) "Profile settings have been updated across the app." else "تم تحديث كافة بياناتك وإعداداتك بنجاح في كامل التطبيق.") },
            confirmButton = {
                TextButton(onClick = { showSaveToast = false }) {
                    Text(if (isEnglish) "OK" else "حسناً", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

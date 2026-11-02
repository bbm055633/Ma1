package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.prayer.CityLocation
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.domain.prayer.PrayerTimes
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveScreen

@Composable
fun ZheenTopBar(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    userProfileImageUri: String? = null,
    onImageSelect: ((String?) -> Unit)? = null,
    dailyProgress: Float = 0.8f,
    isNight: Boolean = true,
    isCelebrationActive: Boolean = false,
    currentTab: Int = 0,
    hijriDate: HijriDateInfo? = null,
    prayerTimes: PrayerTimes? = null,
    onMenuClick: () -> Unit = onProfileClick,
    selectedCity: CityLocation? = null,
    onCitySelect: ((CityLocation) -> Unit)? = null,
    onTrailingClick: () -> Unit = {},
    onOpenThemePicker: () -> Unit = {},
    isEnglish: Boolean = false,
    onToggleLanguage: () -> Unit = {}
) {
    val screenTitle = when (currentTab) {
        0 -> if (isEnglish) "Hayati • Home" else "حياتي • الرئيسية"
        1 -> if (isEnglish) "Worship & Quran" else "العبادة والقرآن"
        2 -> if (isEnglish) "Daily Tasks" else "المهام اليومية"
        3 -> if (isEnglish) "Hijri Calendar" else "التقويم والمواعيد"
        4 -> if (isEnglish) "Smart Agents" else "الوكلاء الأذكياء"
        else -> if (isEnglish) "Hayati" else "حياتي"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("one_ui_85_top_bar"),
        contentAlignment = Alignment.Center
    ) {
        val isDarkMode = MaterialTheme.colorScheme.surface.luminance() < 0.5f
        val isStarry = LocalStarryNightActive.current
        val cardAlpha = LocalCardTransparency.current
        val topBarBg = (if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFFFFFFF)).copy(alpha = if (isStarry) cardAlpha else 1.0f)
        val topBarBorder = if (isStarry) Color.White.copy(alpha = 0.15f) else (if (isDarkMode) Color(0xFF2E2E2E) else Color(0x0F000000))
        val textPrimaryColor = if (isDarkMode) Color.White else Color(0xFF1A1A1A)
        val primaryAccent = MaterialTheme.colorScheme.primary

        Surface(
            shape = RoundedCornerShape(32.dp),
            color = topBarBg,
            shadowElevation = if (isStarry) 0.dp else 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, topBarBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Language Switcher Button (EN / عربي)
                Surface(
                    onClick = onToggleLanguage,
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDarkMode) Color(0xFF252525) else Color(0xFFF3F0EB),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF333333) else Color(0x0F000000)),
                    modifier = Modifier.testTag("top_bar_language_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = if (isEnglish) "Switch to Arabic" else "Switch to English",
                            tint = primaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isEnglish) "عربي" else "EN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryAccent
                        )
                    }
                }

                // Center: Dynamic Screen Title
                Text(
                    text = screenTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimaryColor,
                    textAlign = TextAlign.Center
                )

                // Right: Small Profile Picture (Avatar) as interactive button leading to Settings
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(onClick = onProfileClick)
                        .testTag("top_bar_profile_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    ZheenInteractiveProfileAvatar(
                        imageUri = userProfileImageUri,
                        onImageSelect = onImageSelect,
                        onClick = onProfileClick,
                        progress = dailyProgress,
                        isNight = isNight,
                        isCelebrationActive = isCelebrationActive,
                        size = 42.dp,
                        showProgressRing = true,
                        showBadge = false,
                        showEditBadge = false,
                        allowPhotoPickOnTap = false
                    )
                    // Small circular settings indicator at the bottom-start/end
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = if (isEnglish) "Settings" else "الإعدادات",
                            tint = Color.White,
                            modifier = Modifier.size(9.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ZheenBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isEnglish: Boolean = false
) {
    val isDarkMode = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val isStarry = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current
    val containerBg = (if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFFFFFFF)).copy(alpha = if (isStarry) cardAlpha else 1.0f)
    val borderStroke = androidx.compose.foundation.BorderStroke(1.dp, if (isStarry) Color.White.copy(alpha = 0.15f) else (if (isDarkMode) Color(0xFF2E2E2E) else Color(0x0F000000)))
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = if (isDarkMode) Color(0xFFA0AEC0) else Color(0xFF64748B)

    // Bottom Navigation with elegant active pill indicator
    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = containerBg,
        shadowElevation = if (isStarry) 0.dp else 8.dp,
        border = borderStroke,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("one_ui_85_bottom_nav")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                Triple(if (isEnglish) "Home" else "الرئيسية", Icons.Default.Home, Icons.Outlined.Home),
                Triple(if (isEnglish) "Agents" else "الوكلاء", Icons.Default.AutoAwesome, Icons.Outlined.AutoAwesome),
                Triple(if (isEnglish) "Products" else "المنتجات", Icons.Default.Inventory2, Icons.Outlined.Inventory2),
                Triple(if (isEnglish) "Calendar" else "التقويم", Icons.Default.CalendarMonth, Icons.Outlined.CalendarMonth),
                Triple(if (isEnglish) "Worship" else "العبادة", Icons.Default.Mosque, Icons.Outlined.Mosque)
            )

            items.forEachIndexed { index, item ->
                val isSelected = selectedTab == index

                Surface(
                    onClick = { onTabSelected(index) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) (if (isDarkMode) activeColor.copy(alpha = 0.22f) else activeColor.copy(alpha = 0.12f)) else Color.Transparent,
                    modifier = Modifier
                        .height(56.dp)
                        .weight(1f)
                        .testTag("bottom_tab_$index")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.second else item.third,
                            contentDescription = item.first,
                            tint = if (isSelected) activeColor else inactiveColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.first,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            fontFamily = AppTypography.body,
                            color = if (isSelected) activeColor else inactiveColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ZheenDrawerContent(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onNavigate: (ActiveScreen) -> Unit,
    onCloseDrawer: () -> Unit,
    currentThemePalette: AppThemePalette = AppThemePalette.SAUDI_EMERALD,
    onSelectThemePalette: ((AppThemePalette) -> Unit)? = null,
    currentFontScale: Float = 1.0f,
    onFontScaleChange: ((Float) -> Unit)? = null,
    currentDensityScale: Float = 1.0f,
    onDensityScaleChange: ((Float) -> Unit)? = null,
    customWallpaperUri: String? = null,
    onWallpaperSelected: ((String?) -> Unit)? = null,
    wallpaperDimAlpha: Float = 0.65f,
    onWallpaperDimChange: ((Float) -> Unit)? = null,
    repetitionCount: Int = 0,
    repetitionTarget: Int = 33,
    repetitionLap: Int = 0,
    repetitionDhikrText: String = "سُبْحَانَ اللَّهِ",
    onIncrementRepetition: (() -> Unit)? = null,
    onDecrementRepetition: (() -> Unit)? = null,
    onResetRepetition: (() -> Unit)? = null,
    onSetRepetitionTarget: ((Int) -> Unit)? = null,
    onSetRepetitionDhikr: ((String) -> Unit)? = null,
    selectedCity: CityLocation? = null,
    onCitySelect: ((CityLocation) -> Unit)? = null,
    prayerTimes: PrayerTimes? = null,
    userProfileImageUri: String? = null,
    onProfileImageSelect: ((String?) -> Unit)? = null,
    dailyProgress: Float = 0.8f,
    isCelebrationActive: Boolean = false,
    isFullScreen: Boolean = false,
    isEnglish: Boolean = false,
    currentDesignStyle: AppDesignStyle = AppDesignStyle.STANDARD,
    onSelectDesignStyle: ((AppDesignStyle) -> Unit)? = null
) {
    var showCityDropdown by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .let { if (isFullScreen) it.fillMaxWidth() else it.width(320.dp) },
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = if (isFullScreen) Alignment.TopCenter else Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .let { if (isFullScreen) it.widthIn(max = 680.dp) else it }
                    .let { if (!isFullScreen) it.statusBarsPadding() else it }
                    .verticalScroll(rememberScrollState())
                    .padding(if (isFullScreen) 24.dp else 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // 1. USER PROFILE CARD AT TOP (بطاقة المستخدم في الأعلى)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, SaudiGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCloseDrawer()
                        onNavigate(ActiveScreen.Profile)
                    }
                    .testTag("drawer_user_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Interactive User Avatar with photo picker and progress
                        ZheenInteractiveProfileAvatar(
                            imageUri = userProfileImageUri,
                            onImageSelect = onProfileImageSelect,
                            progress = dailyProgress,
                            isNight = isDarkMode,
                            isCelebrationActive = isCelebrationActive,
                            size = 56.dp,
                            showProgressRing = true,
                            showBadge = true,
                            showEditBadge = true,
                            allowPhotoPickOnTap = true
                        )

                        // User Info: Name & Role & Daily Progress
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isEnglish) "Fahad M. Al-Qahtani" else "فهد بن محمد القحطاني",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("👑", fontSize = 13.sp)
                            }
                            Text(
                                text = if (isEnglish) "CEO & Tech Investor" else "مدير تنفيذي ومستثمر تقني",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCelebrationActive) SuccessGreen.copy(alpha = 0.2f) else SaudiGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isCelebrationActive) {
                                        if (isEnglish) "✨ 100% Completed" else "✨ إنجاز مكتمل 100%"
                                    } else {
                                        if (isEnglish) "Today's Progress: ${(dailyProgress * 100).toInt()}%" else "إنجاز اليوم: ${(dailyProgress * 100).toInt()}%"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCelebrationActive) SuccessGreen else SaudiGoldDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Package & Points Balance (الباقة ورصيد النقاط)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, SaudiGold.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = SaudiGoldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isEnglish) "VIP Business Plan" else "باقة الأعمال VIP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = SaudiGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isEnglish) "12,500 Points" else "12,500 نقطة",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Quick City & Prayer Bar inside Drawer
            if (selectedCity != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // City Dropdown button
                        Box {
                            Row(
                                modifier = Modifier
                                    .clickable { showCityDropdown = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                Text(selectedCity.nameAr, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(14.dp))
                            }

                            DropdownMenu(
                                expanded = showCityDropdown,
                                onDismissRequest = { showCityDropdown = false }
                            ) {
                                PrayerTimeCalculator.SAUDI_CITIES.forEach { city ->
                                    val isSelected = city.nameAr == selectedCity.nameAr
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = city.nameAr,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            onCitySelect?.invoke(city)
                                            showCityDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        if (prayerTimes != null) {
                            Text(
                                text = "🕌 ${prayerTimes.nextPrayerName} (${prayerTimes.nextPrayerTime})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 2. THEMES & COLOR CUSTOMIZATION QUICK PICKER (خاصية الثيمات والألوان)
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("drawer_theme_section")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isEnglish) "App Themes & Colors" else "ألوان وثيمات التطبيق",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(
                            onClick = { showThemeDialog = true },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "Customize" else "تخصيص كامل",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = if (isEnglish) "Current Theme: ${currentThemePalette.emoji} ${currentThemePalette.titleAr}" else "الثيم الحالي: ${currentThemePalette.emoji} ${currentThemePalette.titleAr}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6 Luxury Color Swatches
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppThemePalette.entries.forEach { palette ->
                            val isSelected = palette == currentThemePalette
                            Surface(
                                onClick = { onSelectThemePalette?.invoke(palette) },
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.5.dp, MaterialTheme.colorScheme.onSurface) else null,
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("theme_swatch_${palette.name}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            androidx.compose.ui.graphics.Brush.linearGradient(
                                                palette.previewGradient
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = if (isEnglish) "Selected" else "محدد",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(14.dp))

            // 3. ORDERED NAVIGATION OPTIONS (قائمة الخيارات)
            Text(
                text = if (isEnglish) "Menu Options" else "قائمة الخيارات",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // تخصيص التصميم والخط والمظهر
            DrawerNavRow(
                icon = Icons.Outlined.DesignServices,
                title = if (isEnglish) "Design & Font Settings" else "إعدادات التصميم والخط",
                subtitle = if (isEnglish) "Font size, display density & 6 color schemes" else "حجم الخط، كثافة التطبيق، و6 أنماط لونية",
                onClick = {
                    showThemeDialog = true
                }
            )

            // 1. صفحة المزيد والمحفظة
            DrawerNavRow(
                icon = Icons.Outlined.Person,
                title = if (isEnglish) "More & Smart Wallet" else "صفحة المزيد والمحفظة",
                subtitle = if (isEnglish) "Subscriptions, payments & wallet balance" else "الاشتراكات، الدفع، ورصيد المحفظة الذكية",
                onClick = {
                    onCloseDrawer()
                    onNavigate(ActiveScreen.Profile)
                }
            )

            // 2. مركز العبادات والعناية
            DrawerNavRow(
                icon = Icons.Outlined.Mosque,
                title = if (isEnglish) "Worship & Spiritual Center" else "مركز العبادات والعناية",
                subtitle = if (isEnglish) "Prayer times, Adhkar & Quran Khatmah" else "مواقيت الصلاة، الأذكار والختمة القرآنية",
                onClick = {
                    onCloseDrawer()
                    onNavigate(ActiveScreen.Worship)
                }
            )

            // 3. إعدادات الوكلاء والأصوات
            DrawerNavRow(
                icon = Icons.Outlined.RecordVoiceOver,
                title = if (isEnglish) "Agents & Voice Settings" else "إعدادات الوكلاء والأصوات",
                subtitle = if (isEnglish) "Customize tones & voice personas" else "تخصيص النبرات والشخصيات الصوتية",
                onClick = {
                    onCloseDrawer()
                    onNavigate(ActiveScreen.AgentSettings)
                }
            )

            // 4. الأذونات والأمان
            DrawerNavRow(
                icon = Icons.Outlined.Security,
                title = if (isEnglish) "Security & Permissions" else "الأذونات والأمان",
                subtitle = if (isEnglish) "Biometrics, privacy & local sync" else "البصمة، الخصوصية والمزامنة المحلية",
                onClick = {
                    onCloseDrawer()
                    onNavigate(ActiveScreen.PermissionsSecurity)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. DARK MODE TOGGLE (مفتاح التبديل لتفعيل المظهر الداكن في أسفل القائمة)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = if (isEnglish) "Dark Mode" else "المظهر الداكن",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isDarkMode) (if (isEnglish) "Enabled (Priority)" else "مفعّل (أولوي)") else (if (isEnglish) "Light Mode" else "الوضع الفاتح"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmeraldPrimary,
                            checkedTrackColor = EmeraldContainerDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version info & Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEnglish) "Fahad Ai App - Saudi Edition 2.5" else "تطبيق Fahad Ai - النسخة السعودية 2.5",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isEnglish) "Designed with highest privacy & PDPL compliance" else "مصمم وفق أعلى معايير الخصوصية ونظام حماية البيانات",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (showThemeDialog) {
            ThemeCustomizerDialog(
                currentPalette = currentThemePalette,
                onSelectPalette = { palette ->
                    onSelectThemePalette?.invoke(palette)
                },
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                currentFontScale = currentFontScale,
                onFontScaleChange = { scale -> onFontScaleChange?.invoke(scale) },
                currentDensityScale = currentDensityScale,
                onDensityScaleChange = { scale -> onDensityScaleChange?.invoke(scale) },
                customWallpaperUri = customWallpaperUri,
                onWallpaperSelected = { uri -> onWallpaperSelected?.invoke(uri) },
                wallpaperDimAlpha = wallpaperDimAlpha,
                onWallpaperDimChange = { alpha -> onWallpaperDimChange?.invoke(alpha) },
                currentDesignStyle = currentDesignStyle,
                onSelectDesignStyle = { onSelectDesignStyle?.invoke(it) },
                isEnglish = isEnglish,
                repetitionCount = repetitionCount,
                repetitionTarget = repetitionTarget,
                repetitionLap = repetitionLap,
                repetitionDhikrText = repetitionDhikrText,
                onIncrementRepetition = { onIncrementRepetition?.invoke() },
                onDecrementRepetition = { onDecrementRepetition?.invoke() },
                onResetRepetition = { onResetRepetition?.invoke() },
                onSetRepetitionTarget = { onSetRepetitionTarget?.invoke(it) },
                onSetRepetitionDhikr = { onSetRepetitionDhikr?.invoke(it) },
                onDismiss = { showThemeDialog = false }
            )
        }
    }
}
}

@Composable
fun ZheenUniversalHeaderRow(
    title: String,
    subtitle: String,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    userProfileImageUri: String? = null,
    onImageSelect: ((String?) -> Unit)? = null,
    dailyProgress: Float = 0.8f,
    isNight: Boolean = true,
    isCelebrationActive: Boolean = false,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Notifications,
    onTrailingClick: () -> Unit = {}
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("universal_header_row")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Right (RTL): Interactive User Profile Avatar
            ZheenInteractiveProfileAvatar(
                imageUri = userProfileImageUri,
                onImageSelect = onImageSelect,
                onClick = onProfileClick,
                progress = dailyProgress,
                isNight = isNight,
                isCelebrationActive = isCelebrationActive,
                size = 46.dp,
                showProgressRing = true,
                showBadge = true,
                modifier = Modifier.testTag("header_profile_avatar")
            )

            // Middle: Screen Title & Subtitle / Quick Summary
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Left (RTL opposite): Trailing Action Button
            IconButton(
                onClick = onTrailingClick,
                modifier = Modifier
                    .size(38.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun DrawerNavRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

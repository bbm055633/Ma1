package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ZheenBottomBar
import com.example.ui.components.ZheenDrawerContent
import com.example.ui.components.ZheenFloatingCapsule
import com.example.ui.screens.agents.AgentChatScreen
import com.example.ui.screens.agents.AgentsScreen
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.menu.FullScreenMenuScreen
import com.example.ui.screens.more.MoreScreen
import com.example.ui.screens.products.ProductGeneratorScreen
import com.example.ui.screens.products.ProductsScreen
import com.example.ui.screens.profile.AgentSettingsScreen
import com.example.ui.screens.profile.PermissionsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.tasks.TaskScreen
import com.example.ui.screens.worship.AdhkarScreen
import com.example.ui.screens.worship.WorshipScreen
import com.example.ui.theme.ZheenTheme
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.service.ZheenWorkManagerScheduler
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ZheenWorkManagerScheduler.scheduleBackgroundServices(applicationContext)
        setContent {
            val viewModel: MainViewModel = viewModel()
            val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
            val userPrimaryColor by viewModel.userPrimaryColor.collectAsStateWithLifecycle()

            val isEnglish by viewModel.isEnglish.collectAsStateWithLifecycle()
            val layoutDirection = if (isEnglish) LayoutDirection.Ltr else LayoutDirection.Rtl

            ZheenTheme(
                darkTheme = themeSettings.isDarkMode,
                palette = themeSettings.themePalette,
                primaryColor = userPrimaryColor,
                designStyle = themeSettings.designStyle
            ) {
                val systemDensity = androidx.compose.ui.platform.LocalDensity.current
                val customDensity = remember(systemDensity, themeSettings.fontScale, themeSettings.densityScale) {
                    androidx.compose.ui.unit.Density(
                        density = systemDensity.density * themeSettings.densityScale,
                        fontScale = systemDensity.fontScale * themeSettings.fontScale
                    )
                }

                val view = LocalView.current
                if (!view.isInEditMode) {
                    SideEffect {
                        val window = (view.context as? android.app.Activity)?.window
                        if (window != null) {
                            WindowCompat.getInsetsController(window, view).apply {
                                isAppearanceLightStatusBars = true
                                isAppearanceLightNavigationBars = true
                            }
                        }
                    }
                }

                val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val starryNightSettings by viewModel.starryNightSettings.collectAsStateWithLifecycle()
                val userName by viewModel.userName.collectAsStateWithLifecycle()
                val userProfileImageUri by viewModel.userProfileImageUri.collectAsStateWithLifecycle()

                // Starry Night live sky and card transparency apply globally across the entire app as requested
                val isLiveThemeActive = starryNightSettings.isEnabled
                val cardTransparency = if (isLiveThemeActive) starryNightSettings.cardTransparency else 1.0f

                val userProfileData = remember(userName, userProfileImageUri, isEnglish) {
                    com.example.ui.components.UserProfileData(
                        userName = userName,
                        userProfileImageUri = userProfileImageUri,
                        notificationCount = 3,
                        onAvatarClick = { viewModel.navigateTo(ActiveScreen.FullScreenMenu) },
                        onNotificationClick = { viewModel.navigateTo(ActiveScreen.FullScreenMenu) },
                        isEnglish = isEnglish,
                        onToggleLanguage = { viewModel.toggleLanguage() }
                    )
                }

                CompositionLocalProvider(
                    LocalLayoutDirection provides layoutDirection,
                    androidx.compose.ui.platform.LocalDensity provides customDensity,
                    com.example.ui.util.LocalAppLanguage provides isEnglish,
                    com.example.ui.theme.LocalCardTransparency provides cardTransparency,
                    com.example.ui.components.LocalStarryNightActive provides isLiveThemeActive,
                    com.example.ui.components.LocalUserProfile provides userProfileData
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (isLiveThemeActive) Color.Transparent
                                else MaterialTheme.colorScheme.background
                            )
                    ) {
                        if (isLiveThemeActive) {
                            com.example.ui.components.theme.StarryNightSkyView(
                                settings = starryNightSettings,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (!themeSettings.customWallpaperUri.isNullOrEmpty()) {
                            coil.compose.AsyncImage(
                                model = themeSettings.customWallpaperUri,
                                contentDescription = "خلفية مخصصة",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (themeSettings.isDarkMode) androidx.compose.ui.graphics.Color.Black.copy(alpha = themeSettings.wallpaperDimAlpha)
                                        else androidx.compose.ui.graphics.Color.White.copy(alpha = (themeSettings.wallpaperDimAlpha * 0.7f).coerceAtLeast(0.35f))
                                    )
                            )
                        }

                        ZheenApp(
                            viewModel = viewModel,
                            isDarkMode = themeSettings.isDarkMode,
                            themePalette = themeSettings.themePalette,
                            hasCustomWallpaper = isLiveThemeActive || !themeSettings.customWallpaperUri.isNullOrEmpty()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ZheenApp(
    viewModel: MainViewModel,
    isDarkMode: Boolean,
    themePalette: com.example.ui.theme.AppThemePalette,
    hasCustomWallpaper: Boolean = false
) {
    val activeScreen by viewModel.activeScreen.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val hijriDate by viewModel.hijriDate.collectAsState()
    val fontScale by viewModel.appFontScale.collectAsState()
    val densityScale by viewModel.appDensityScale.collectAsState()
    val customWallpaperUri by viewModel.customWallpaperUri.collectAsState()
    val wallpaperDimAlpha by viewModel.wallpaperDimAlpha.collectAsState()
    val repetitionCount by viewModel.repetitionCount.collectAsState()
    val repetitionTarget by viewModel.repetitionTarget.collectAsState()
    val repetitionLap by viewModel.repetitionLap.collectAsState()
    val repetitionDhikrText by viewModel.repetitionDhikrText.collectAsState()
    val userProfileImageUri by viewModel.userProfileImageUri.collectAsState()
    val dailyProgress by viewModel.dailyProgress.collectAsState()
    val isCelebrationActive by viewModel.isDailyCelebrationActive.collectAsState()
    val designStyle by viewModel.designStyle.collectAsState()
    val userName by viewModel.userName.collectAsState()

    var showThemeSelectionDialog by remember { mutableStateOf(false) }
    val userPrimaryColorHex by viewModel.userPrimaryColorHex.collectAsState()

    if (showThemeSelectionDialog) {
        com.example.ui.components.OneUiThemeSelectionDialog(
            selectedHex = userPrimaryColorHex,
            onSelectHex = { hex ->
                viewModel.setUserPrimaryColor(hex)
            },
            onDismiss = { showThemeSelectionDialog = false }
        )
    }

    when (val screen = activeScreen) {
            is ActiveScreen.MainTabs -> {
                Scaffold(
                    bottomBar = {
                        ZheenBottomBar(
                            selectedTab = currentTab,
                            onTabSelected = { tab -> viewModel.selectTab(tab) },
                            isEnglish = isEnglish
                        )
                    },
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    val density = LocalDensity.current
                    var measuredCapsuleHeightPx by remember { mutableIntStateOf(0) }
                    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    val topContentPadding = remember(measuredCapsuleHeightPx, density, statusBarTop) {
                        if (measuredCapsuleHeightPx > 0) {
                            with(density) { measuredCapsuleHeightPx.toDp() }
                        } else {
                            statusBarTop + 80.dp
                        }
                    }

                    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    val mainBottomBarHeight = 68.dp + navBarBottom
                    val bottomContentPadding = innerPadding.calculateBottomPadding().coerceAtLeast(mainBottomBarHeight)

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val currentTabTitle = remember(currentTab, isEnglish) {
                            when (currentTab) {
                                0 -> if (isEnglish) "Home" else "الرئيسية"
                                1 -> if (isEnglish) "Agents" else "الوكلاء"
                                2 -> if (isEnglish) "Products" else "المنتجات"
                                3 -> if (isEnglish) "Schedule & Calendar" else "الجدول والتقويم"
                                4 -> if (isEnglish) "Worship & Surahs" else "العبادات والسور"
                                else -> if (isEnglish) "Fahad AI" else "فهد للذكاء الاصطناعي"
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = topContentPadding,
                                    bottom = bottomContentPadding
                                )
                        ) {
                            when (currentTab) {
                                0 -> HomeScreen(
                                    viewModel = viewModel,
                                    prayerTimes = prayerTimes,
                                    hijriDate = hijriDate,
                                    onNavigate = { s -> viewModel.navigateTo(s) }
                                )
                                1 -> AgentsScreen(
                                    viewModel = viewModel,
                                    onNavigate = { s -> viewModel.navigateTo(s) }
                                )
                                2 -> ProductsScreen(
                                    viewModel = viewModel,
                                    onNavigate = { s -> viewModel.navigateTo(s) }
                                )
                                3 -> CalendarScreen(
                                    viewModel = viewModel,
                                    prayerTimes = prayerTimes,
                                    hijriDate = hijriDate
                                )
                                4 -> WorshipScreen(
                                    viewModel = viewModel,
                                    prayerTimes = prayerTimes,
                                    hijriDate = hijriDate,
                                    onNavigate = { s -> viewModel.navigateTo(s) }
                                )
                            }
                        }

                        // Standalone ZheenFloatingCapsule floating over content
                        ZheenFloatingCapsule(
                            screenTitle = currentTabTitle,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .onGloballyPositioned { coordinates ->
                                    if (coordinates.size.height > 0) {
                                        measuredCapsuleHeightPx = coordinates.size.height
                                    }
                                }
                        )
                    }
                }
            }

            is ActiveScreen.AgentChat -> {
                AgentChatScreen(
                    agentId = screen.agentId,
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
            }

            is ActiveScreen.ProductGenerator -> {
                ProductGeneratorScreen(
                    productId = screen.productId,
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
            }

            is ActiveScreen.Profile -> {
                MoreScreen(
                    viewModel = viewModel,
                    prayerTimes = prayerTimes,
                    onNavigate = { s -> viewModel.navigateTo(s) }
                )
            }

            is ActiveScreen.Worship -> {
                androidx.activity.compose.BackHandler { viewModel.navigateBack() }
                WorshipScreen(
                    viewModel = viewModel,
                    prayerTimes = prayerTimes,
                    hijriDate = hijriDate,
                    onNavigate = { s -> viewModel.navigateTo(s) }
                )
            }

            is ActiveScreen.AgentSettings -> {
                AgentSettingsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
            }

            is ActiveScreen.PermissionsSecurity -> {
                PermissionsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
            }

            is ActiveScreen.AdhkarSession -> {
                AdhkarScreen(
                    isMorning = screen.isMorning,
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
            }

            is ActiveScreen.PaymentHistory -> {
                androidx.activity.compose.BackHandler { viewModel.navigateBack() }
                com.example.ui.screens.more.TransactionsHistoryDialog(
                    transactions = viewModel.paymentTransactions.collectAsState().value,
                    onDismiss = { viewModel.navigateBack() }
                )
            }

            is ActiveScreen.MobeenAiSpecSheet -> {
                com.example.ui.screens.agents.MobeenAiSpecSheetScreen(
                    onBack = { viewModel.navigateBack() },
                    onStartChat = { viewModel.navigateTo(ActiveScreen.AgentChat("mobeen")) }
                )
            }

            is ActiveScreen.FullScreenMenu -> {
                FullScreenMenuScreen(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    themePalette = themePalette,
                    onBack = { viewModel.navigateBack() }
                )
            }

            is ActiveScreen.StarryNightCustomizer -> {
                com.example.ui.screens.theme.StarryNightThemeScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
            }

            is ActiveScreen.Login -> {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { viewModel.navigateTo(ActiveScreen.MainTabs) }
                )
            }
        }
}

@Composable
fun UserAvatarCircle(
    userName: String = "فهد",
    userProfileImageUri: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 16.dp, end = 16.dp)
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(0xFF064E3B))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val initialLetter = userName.trim().take(1).ifEmpty { "م" }
        Text(
            text = initialLetter,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}



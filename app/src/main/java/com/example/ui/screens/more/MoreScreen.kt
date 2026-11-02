package com.example.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.models.PaymentTransaction
import com.example.domain.prayer.PrayerTimes
import com.example.ui.components.IslamicGeometricPatternBackground
import com.example.ui.components.NotificationAndDndSettingsDialog
import com.example.ui.components.OneUiColorPickerSheet
import com.example.ui.components.ThemeCustomizerDialog
import com.example.ui.components.ZheenInteractiveProfileAvatar
import com.example.ui.components.generateGradientForColor
import com.example.ui.theme.*
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.MainViewModel

/**
 * Samsung One UI 9 - More & System Center (المزيد ومركز الإعدادات والسمات)
 *
 * Full One UI 9 Specifications:
 * - Background: #F9F6F0 warm off-white with 8% Islamic geometric pattern
 * - All cards: Pure White #FFFFFF with 24px radius, soft ambient shadow, 56x56 circular gradient icon on right
 * - Dedicated "Design & Theme Center" (مركز التصميم والسمات) with 5 presets:
 *   Saudi Green #0B3D2E, Blue #3B82F6, Purple #8B5CF6, Orange #F97316, Gold #D4A75C + Custom Color Picker
 * - Exact functionality 100% preserved
 */
@Composable
fun MoreScreen(
    viewModel: MainViewModel,
    prayerTimes: PrayerTimes,
    onNavigate: (ActiveScreen) -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val themePalette by viewModel.themePalette.collectAsState()
    val fontScale by viewModel.appFontScale.collectAsState()
    val densityScale by viewModel.appDensityScale.collectAsState()
    val customWallpaperUri by viewModel.customWallpaperUri.collectAsState()
    val wallpaperDimAlpha by viewModel.wallpaperDimAlpha.collectAsState()
    val repetitionCount by viewModel.repetitionCount.collectAsState()
    val repetitionTarget by viewModel.repetitionTarget.collectAsState()
    val repetitionLap by viewModel.repetitionLap.collectAsState()
    val repetitionDhikrText by viewModel.repetitionDhikrText.collectAsState()
    val walletBalance by viewModel.walletBalanceSar.collectAsState()
    val smartPoints by viewModel.smartPoints.collectAsState()
    val currentSubscription by viewModel.currentSubscription.collectAsState()
    val transactions by viewModel.paymentTransactions.collectAsState()
    val userProfileImageUri by viewModel.userProfileImageUri.collectAsState()
    val dailyProgress by viewModel.dailyProgress.collectAsState()
    val isCelebrationActive by viewModel.isDailyCelebrationActive.collectAsState()
    val designStyle by viewModel.designStyle.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()

    var showPaymentModal by remember { mutableStateOf(false) }
    var paymentModalMode by remember { mutableStateOf(PaymentModalMode.TOP_UP) }
    var showTransactionsDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDesignSettingsDialog by remember { mutableStateOf(false) }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }

    val notificationSound by viewModel.notificationSound.collectAsState()
    val isDndDuringWorship by viewModel.isDndDuringWorship.collectAsState()
    val dndSelectedPrayers by viewModel.dndSelectedPrayers.collectAsState()
    val dndDurationMinutes by viewModel.dndDurationMinutes.collectAsState()

    // Dynamic Theming with Live Preview and SharedPreferences persistence
    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()
    val userPrimaryColorHex by viewModel.userPrimaryColorHex.collectAsState()
    var showCustomColorPicker by remember { mutableStateOf(false) }

    val presetColors = listOf(
        Pair("أخضر سعودي", "#0B3D2E"),
        Pair("أزرق أعمال", "#3B82F6"),
        Pair("بنفسجي ملكي", "#8B5CF6"),
        Pair("برتقالي حيوي", "#F97316"),
        Pair("ذهبي فاخر", "#D4A75C")
    )

    IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. HEADER CARD: Profile & National ID (نفاذ) - One UI 9 White Card
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_user_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ZheenInteractiveProfileAvatar(
                            imageUri = userProfileImageUri,
                            onImageSelect = { uri -> viewModel.setUserProfileImageUri(uri) },
                            onClick = { onNavigate(ActiveScreen.Profile) },
                            progress = dailyProgress,
                            isNight = isDarkMode,
                            isCelebrationActive = isCelebrationActive,
                            size = 56.dp,
                            showProgressRing = true,
                            showBadge = true,
                            showEditBadge = true,
                            allowPhotoPickOnTap = true,
                            modifier = Modifier.testTag("more_screen_profile_avatar")
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "فهد بن محمد القحطاني",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1B1A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "موثّق",
                                    tint = userPrimaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "fahad.alqahtani@enterprise.sa • +966 50 123 4567",
                                fontSize = 12.sp,
                                color = Color(0xFF6E685F)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF107C41).copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "موثق عبر نفاذ 🟢",
                                        color = Color(0xFF107C41),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = userPrimaryColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "عضوية الأعمال VIP 👑",
                                        color = userPrimaryColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. DESIGN & THEME CENTER (مركز التصميم والسمات - Samsung One UI 9)
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("design_theme_center_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(generateGradientForColor(userPrimaryColor, 0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "مركز التصميم والسمات (One UI 9)",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1B1A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "اختر السمة اللونية للتطبيق مع معاينة فورية وحفظ تلقائي",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6E685F)
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1EFE9))

                        // 5 Preset color circles
                        Text(
                            text = "الألوان المعتمدة (Presets):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1B1A)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            presetColors.forEach { (name, hex) ->
                                val color = Color(android.graphics.Color.parseColor(hex))
                                val isSelected = userPrimaryColorHex.equals(hex, ignoreCase = true)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clickable { viewModel.setUserPrimaryColor(hex) }
                                        .testTag("theme_preset_$hex")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .then(
                                                if (isSelected) {
                                                    Modifier.border(3.dp, Color(0xFF1C1B1A), CircleShape)
                                                } else {
                                                    Modifier.border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "محدد",
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = name.split(" ").first(),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) userPrimaryColor else Color(0xFF6E685F)
                                    )
                                }
                            }
                        }

                        // Custom Color Button (Color Picker)
                        Button(
                            onClick = { showCustomColorPicker = true },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = userPrimaryColor.copy(alpha = 0.12f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("custom_color_picker_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = null,
                                tint = userPrimaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تخصيص لون إضافي (Color Picker)",
                                color = userPrimaryColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2.5 NOTIFICATION & DND SETTINGS CARD - One UI 9 White Card
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notification_dnd_settings_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showNotificationSettingsDialog = true }
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(userPrimaryColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = userPrimaryColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "إعدادات التنبيهات ووضع عدم الإزعاج",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1C1B1A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "تخصيص أصوات الأذان والمهام والصامت التلقائي أثناء العبادات",
                                fontSize = 12.sp,
                                color = Color(0xFF6E685F)
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF6E685F).copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 3. SMART WALLET & PAYMENTS CARD - One UI 9 White Card
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wallet_payment_hero_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(generateGradientForColor(userPrimaryColor, 1)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "المحفظة الذكية والمدفوعات",
                                        color = Color(0xFF1C1B1A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "بوابة دفع متوافقة مع الأنظمة السعودية",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6E685F)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF9F6F0),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E4DC)),
                                modifier = Modifier.clickable { showTransactionsDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "سجل العمليات",
                                        color = Color(0xFF1C1B1A),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF1C1B1A),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFF9F6F0),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E4DC)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "الرصيد المالي المتاح",
                                        color = Color(0xFF6E685F),
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = String.format("%.2f ر.س", walletBalance),
                                        color = Color(0xFF1C1B1A),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "النقاط الذكية (Smart Credits)",
                                        color = Color(0xFF6E685F),
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = userPrimaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "%,d نقطة".format(smartPoints),
                                            color = userPrimaryColor,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Payment Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    paymentModalMode = PaymentModalMode.TOP_UP
                                    showPaymentModal = true
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = userPrimaryColor),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("top_up_wallet_button")
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("شحن الرصيد", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    paymentModalMode = PaymentModalMode.SUBSCRIPTION_UPGRADE
                                    showPaymentModal = true
                                },
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, userPrimaryColor),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("upgrade_plan_button")
                            ) {
                                Icon(Icons.Default.Upgrade, contentDescription = null, modifier = Modifier.size(18.dp), tint = userPrimaryColor)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ترقية الباقة", color = userPrimaryColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "طرق الدفع: مدى • Apple Pay • stc pay • بطاقة ائتمانية • سريع",
                            fontSize = 11.sp,
                            color = Color(0xFF6E685F),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 4. ACTIVE SUBSCRIPTION CARD - One UI 9 White Card
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("الاشتراك المفعّل حالياً", fontSize = 12.sp, color = Color(0xFF6E685F))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(currentSubscription, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1A))
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF107C41).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "نشط • تجديد تلقائي",
                                    color = Color(0xFF107C41),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1EFE9))
                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            "استخدام غير محدود لجميع وكلاء الأعمال الـ 10 المتخصصين",
                            "توليد الفواتير ومطابقة أنظمة ZATCA الضريبية",
                            "مزامنة كاملة لمواقيت الصلاة والتقويم بدون أي تعارض",
                            "أولوية المعالجة الفائقة عبر نماذج الذكاء الاصطناعي السريعة"
                        ).forEach { feat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = userPrimaryColor, modifier = Modifier.size(16.dp))
                                Text(feat, fontSize = 12.sp, color = Color(0xFF1C1B1A))
                            }
                        }
                    }
                }
            }

            // 6. SPECIALIZED SETTINGS & SYSTEM PREFERENCES - One UI 9 White Cards
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        OneUi9NavRow(
                            icon = Icons.Outlined.RecordVoiceOver,
                            title = "إعدادات الوكلاء والأصوات",
                            subtitle = "لهجة نجدية، حجازية، أو فصحى أعمال سعودية",
                            primaryColor = userPrimaryColor,
                            onClick = { onNavigate(ActiveScreen.AgentSettings) }
                        )

                        HorizontalDivider(color = Color(0xFFF1EFE9))

                        OneUi9NavRow(
                            icon = Icons.Outlined.Smartphone,
                            title = "مواصفات ونموذج مبين AI (Galaxy S24 One UI)",
                            subtitle = "استعراض النموذج المصغر وشيت المواصفات الهندسية الكاملة",
                            primaryColor = userPrimaryColor,
                            onClick = { onNavigate(ActiveScreen.MobeenAiSpecSheet) }
                        )

                        HorizontalDivider(color = Color(0xFFF1EFE9))

                        OneUi9NavRow(
                            icon = Icons.Outlined.Security,
                            title = "الأمان والخصوصية وحماية البيانات",
                            subtitle = "تسجيل الدخول بالبصمة ونظام حماية البيانات KSA",
                            primaryColor = userPrimaryColor,
                            onClick = { onNavigate(ActiveScreen.PermissionsSecurity) }
                        )

                        HorizontalDivider(color = Color(0xFFF1EFE9))

                        OneUi9NavRow(
                            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                            title = "الفواتير الضريبية والمحاسبة",
                            subtitle = "الفواتير الصادرة ورموز الاستجابة ZATCA",
                            primaryColor = userPrimaryColor,
                            onClick = { showTransactionsDialog = true }
                        )
                    }
                }
            }

            // 7. DISPLAY & PREFERENCES - One UI 9 White Card
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Dark Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                    tint = userPrimaryColor
                                )
                                Column {
                                    Text("المظهر الداكن (Dark Mode)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1C1B1A))
                                    Text(if (isDarkMode) "مفعّل لتوفير الطاقة وراحة العين" else "الوضع الفاتح مفعّل", fontSize = 11.sp, color = Color(0xFF6E685F))
                                }
                            }

                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                colors = SwitchDefaults.colors(checkedThumbColor = userPrimaryColor, checkedTrackColor = userPrimaryColor.copy(alpha = 0.35f))
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFF1EFE9))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Advanced Fonts and Sizing Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDesignSettingsDialog = true }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Outlined.FormatSize, contentDescription = null, tint = userPrimaryColor)
                                Column {
                                    Text("أحجام الخطوط والتخصيص المتقدم", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1C1B1A))
                                    Text("حجم الخط (${(fontScale * 100).toInt()}%) • كثافة العرض (${(densityScale * 100).toInt()}%)", fontSize = 11.sp, color = Color(0xFF6E685F))
                                }
                            }

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "فتح",
                                tint = Color(0xFF6E685F),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 8. LOGOUT - One UI 9 White Card
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text(
                            text = "تسجيل الخروج الآمن",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // App Footer & Version
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "منظومة Fahad Ai السعودية • One UI 9 Edition",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6E685F)
                    )
                    Text(
                        text = "مدعومة ببوابة الدفع الوطنية وأنظمة ZATCA الضريبية",
                        fontSize = 10.sp,
                        color = Color(0xFF6E685F).copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Custom Color Picker Sheet
        if (showCustomColorPicker) {
            OneUiColorPickerSheet(
                selectedHex = userPrimaryColorHex,
                onSelectHex = { hex: String ->
                    viewModel.setUserPrimaryColor(hex)
                },
                onDismiss = { showCustomColorPicker = false }
            )
        }

        // Design & Font Settings Dialog
        if (showDesignSettingsDialog) {
            ThemeCustomizerDialog(
                currentPalette = themePalette,
                onSelectPalette = { viewModel.selectThemePalette(it) },
                isDarkMode = isDarkMode,
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                currentFontScale = fontScale,
                onFontScaleChange = { viewModel.setAppFontScale(it) },
                currentDensityScale = densityScale,
                onDensityScaleChange = { viewModel.setAppDensityScale(it) },
                customWallpaperUri = customWallpaperUri,
                onWallpaperSelected = { viewModel.setCustomWallpaperUri(it) },
                wallpaperDimAlpha = wallpaperDimAlpha,
                onWallpaperDimChange = { viewModel.setWallpaperDimAlpha(it) },
                currentDesignStyle = designStyle,
                onSelectDesignStyle = { viewModel.selectDesignStyle(it) },
                isEnglish = isEnglish,
                repetitionCount = repetitionCount,
                repetitionTarget = repetitionTarget,
                repetitionLap = repetitionLap,
                repetitionDhikrText = repetitionDhikrText,
                onIncrementRepetition = { viewModel.incrementRepetition() },
                onDecrementRepetition = { viewModel.decrementRepetition() },
                onResetRepetition = { viewModel.resetRepetition() },
                onSetRepetitionTarget = { viewModel.setRepetitionTarget(it) },
                onSetRepetitionDhikr = { viewModel.setRepetitionDhikrText(it) },
                onDismiss = { showDesignSettingsDialog = false }
            )
        }

        // Payment Bottom Sheet
        if (showPaymentModal) {
            SaudiPaymentBottomSheet(
                mode = paymentModalMode,
                onDismiss = { showPaymentModal = false },
                onPaymentSuccess = { _, _, _ -> },
                onProcessTopUp = { pkg, method, cardLast4 ->
                    viewModel.processTopUp(pkg, method, cardLast4)
                },
                onProcessUpgrade = { plan, method, cardLast4 ->
                    viewModel.processSubscriptionUpgrade(plan, method, cardLast4)
                }
            )
        }

        // Transactions History Dialog
        if (showTransactionsDialog) {
            TransactionsHistoryDialog(
                transactions = transactions,
                onDismiss = { showTransactionsDialog = false }
            )
        }

        // Notification & DND Settings Dialog
        if (showNotificationSettingsDialog) {
            NotificationAndDndSettingsDialog(
                currentSound = notificationSound,
                onSoundSelected = { viewModel.setNotificationSound(it) },
                isDndEnabled = isDndDuringWorship,
                onDndToggled = { viewModel.setDndDuringWorshipEnabled(it) },
                selectedPrayers = dndSelectedPrayers,
                onPrayerToggled = { viewModel.toggleDndPrayer(it) },
                dndDuration = dndDurationMinutes,
                onDurationChanged = { viewModel.setDndDurationMinutes(it) },
                onDismiss = { showNotificationSettingsDialog = false }
            )
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("تسجيل الخروج", fontWeight = FontWeight.Bold) },
                text = { Text("هل أنت متأكد من رغبتك في تسجيل الخروج من حسابك؟ بياناتك ومحفظتك محفوظة بشكل آمن.") },
                confirmButton = {
                    Button(
                        onClick = { showLogoutDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("نعم، تسجيل الخروج")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}

@Composable
private fun OneUi9NavRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(22.dp))
            }

            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1C1B1A))
                Text(subtitle, fontSize = 11.sp, color = Color(0xFF6E685F))
            }
        }

        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF6E685F).copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun TransactionsHistoryDialog(
    transactions: List<PaymentTransaction>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سجل المعاملات والمدفوعات", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF1C1B1A))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider(color = Color(0xFFF1EFE9))

                if (transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("لا توجد عمليات سابقة حتى الآن", fontSize = 13.sp, color = Color(0xFF6E685F))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(transactions) { tx ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF9F6F0),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E4DC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(tx.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1C1B1A))
                                        Text(tx.dateFormatted, fontSize = 11.sp, color = Color(0xFF6E685F))
                                        Text("رقم المرجع: ${tx.id}", fontSize = 10.sp, color = Color(0xFF6E685F).copy(alpha = 0.8f))
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = String.format("%.2f ر.س", tx.amountSar),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF107C41)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF107C41).copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = "ناجحة ✅",
                                                color = Color(0xFF107C41),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1B1A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تم", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

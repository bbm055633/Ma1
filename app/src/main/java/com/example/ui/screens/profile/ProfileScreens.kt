package com.example.ui.screens.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.domain.models.AgentDataDefaults
import com.example.ui.components.IslamicGeometricPatternBackground
import com.example.ui.components.ZheenInteractiveProfileAvatar
import com.example.ui.components.generateGradientForColor
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

/**
 * Samsung One UI 9 Top Bar for Sub-screens
 */
@Composable
fun OneUi9SubScreenTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingAction: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button pill
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF9F6F0))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "رجوع",
                        tint = Color(0xFF1C1B1A)
                    )
                }

                // Centered Title
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1A)
                )

                // Trailing slot or spacer
                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (trailingAction != null) {
                        trailingAction()
                    }
                }
            }
        }
    }
}

/**
 * Profile Screen - Samsung One UI 9
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val userProfileImageUri by viewModel.userProfileImageUri.collectAsState()
    val dailyProgress by viewModel.dailyProgress.collectAsState()
    val isCelebrationActive by viewModel.isDailyCelebrationActive.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.setUserProfileImageUri(uri.toString())
            }
        }
    )

    IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        Scaffold(
            topBar = {
                OneUi9SubScreenTopBar(
                    title = "الملف الشخصي والمحفظة",
                    onBack = onBack
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Unified Professional Profile Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Profile Picture, Name, and Title Prominently at Top
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ZheenInteractiveProfileAvatar(
                                imageUri = userProfileImageUri,
                                onImageSelect = { uri -> viewModel.setUserProfileImageUri(uri) },
                                progress = dailyProgress,
                                isNight = isDarkMode,
                                isCelebrationActive = isCelebrationActive,
                                size = 72.dp,
                                showProgressRing = true,
                                showBadge = true,
                                showEditBadge = true,
                                allowPhotoPickOnTap = true
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1C1B1A)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("👑", fontSize = 14.sp)
                                }
                                Text(
                                    text = "مدير تنفيذي ومستثمر تقني",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6E685F)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = userPrimaryColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "عضوية ذهبية موثقة • نفاذ",
                                        color = userPrimaryColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = userPrimaryColor.copy(alpha = 0.12f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = userPrimaryColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تغيير الصورة الشخصية", color = userPrimaryColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        HorizontalDivider(color = Color(0xFFF1EFE9))

                        // 2. Account and Subscription Details (Points Balance, Subscription Tier)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("رصيد النقاط الذكية", fontSize = 11.sp, color = Color(0xFF6E685F))
                                Text("8,450 نقطة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = userPrimaryColor)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("الاشتراك الحالي", fontSize = 11.sp, color = Color(0xFF6E685F))
                                Text("باقة الأعمال الاحترافية", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1A))
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1EFE9))

                        // 3. Personal Data and Contact Info (Age, Height, Weight, Email, Password Settings)
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "البيانات الشخصية ومعلومات الاتصال",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1C1B1A)
                            )

                            ProfileDataRow(label = "البريد الإلكتروني", value = userEmail, icon = Icons.Outlined.Email, tintColor = userPrimaryColor)
                            ProfileDataRow(label = "العمر", value = "38 عاماً", icon = Icons.Outlined.Cake, tintColor = userPrimaryColor)
                            ProfileDataRow(label = "الطول", value = "178 سم", icon = Icons.Outlined.Height, tintColor = userPrimaryColor)
                            ProfileDataRow(label = "الوزن", value = "74 كجم", icon = Icons.Outlined.FitnessCenter, tintColor = userPrimaryColor)
                            ProfileDataRow(label = "كلمة المرور والأمان", value = "•••••••••••• (تحديث دائم)", icon = Icons.Outlined.Lock, tintColor = userPrimaryColor)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Agent Settings Screen - Samsung One UI 9
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentSettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val currentPersona by viewModel.selectedVoicePersona.collectAsState()
    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()

    IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        Scaffold(
            topBar = {
                OneUi9SubScreenTopBar(
                    title = "إعدادات الأصوات والشخصيات",
                    onBack = onBack
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "تخصيص اللهجة والنبرة العامة لجميع الوكلاء:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1B1A)
                    )
                }

                val dialects = listOf(
                    "لهجة نجدية وقورة ورسمية (افتراضي)",
                    "فصحى أعمال سعودية راقية",
                    "لهجة حجازية حيوية وعصرية",
                    "أسلوب تنفيذي موجز ومباشر"
                )

                items(dialects) { dialect ->
                    val isSelected = currentPersona == dialect
                    Surface(
                        onClick = { viewModel.setVoicePersona(dialect) },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, userPrimaryColor) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) userPrimaryColor.copy(alpha = 0.12f) else Color(0xFFF9F6F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = if (isSelected) userPrimaryColor else Color(0xFF6E685F),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = dialect,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = Color(0xFF1C1B1A)
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setVoicePersona(dialect) },
                                colors = RadioButtonDefaults.colors(selectedColor = userPrimaryColor)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "الأصوات المسندة للوكلاء الفرديين:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1B1A)
                    )
                }

                items(AgentDataDefaults.AGENTS) { agent ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(generateGradientForColor(userPrimaryColor, agent.name.length)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = agent.name.firstOrNull()?.toString() ?: "و",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                                Column {
                                    Text(agent.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1C1B1A))
                                    Text(agent.roleTitle, fontSize = 11.sp, color = Color(0xFF6E685F))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF9F6F0),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E4DC))
                            ) {
                                Text(
                                    text = agent.voiceName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1C1B1A),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Permissions & Security Screen - Samsung One UI 9
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()

    var localDataOnly by remember { mutableStateOf(true) }
    var calendarSyncGranted by remember { mutableStateOf(true) }
    var micGranted by remember { mutableStateOf(true) }
    var notificationsGranted by remember { mutableStateOf(true) }

    IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.08f)
    ) {
        Scaffold(
            topBar = {
                OneUi9SubScreenTopBar(
                    title = "الأذونات والأمان",
                    onBack = onBack
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "معايير الأمان وحماية البيانات (امتثال لنظام حماية البيانات الشخصية KSA):",
                    fontSize = 12.sp,
                    color = Color(0xFF6E685F)
                )

                // Biometric Login Switch: Pure White 24px Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(userPrimaryColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = userPrimaryColor, modifier = Modifier.size(24.dp))
                            }
                            Column {
                                Text("تسجيل الدخول بالبصمة (Biometrics)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1C1B1A))
                                Text("حماية سرية البيانات والتقويم المالي", fontSize = 12.sp, color = Color(0xFF6E685F))
                            }
                        }
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = { viewModel.toggleBiometric() },
                            colors = SwitchDefaults.colors(checkedThumbColor = userPrimaryColor, checkedTrackColor = userPrimaryColor.copy(alpha = 0.35f))
                        )
                    }
                }

                // Local Data Processing Toggle: Pure White 24px Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(userPrimaryColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Storage, contentDescription = null, tint = userPrimaryColor, modifier = Modifier.size(24.dp))
                            }
                            Column {
                                Text("المعالجة المحلية الآمنة", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1C1B1A))
                                Text("حفظ الملاحظات والمصاريف في ذاكرة الجهاز المشفرة", fontSize = 12.sp, color = Color(0xFF6E685F))
                            }
                        }
                        Switch(
                            checked = localDataOnly,
                            onCheckedChange = { localDataOnly = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = userPrimaryColor, checkedTrackColor = userPrimaryColor.copy(alpha = 0.35f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text("خدمة التنبيهات في الخلفية (WorkManager):", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1A))

                val context = androidx.compose.ui.platform.LocalContext.current
                var testTriggered by remember { mutableStateOf(false) }

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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF107C41).copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF107C41), modifier = Modifier.size(24.dp))
                                }
                                Column {
                                    Text("خدمة التنبيهات الذكية بالخلفية", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1C1B1A))
                                    Text("مجدولة دورياً كل 15 دقيقة (WorkManager)", fontSize = 11.sp, color = Color(0xFF6E685F))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF107C41).copy(alpha = 0.12f)
                            ) {
                                Text("نشط 🟢", color = Color(0xFF107C41), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "تعمل الخدمة أثناء إغلاق التطبيق لتنبيهك قبل الأذان بـ 15 دقيقة، وتفعيل وضع الصامت، والتذكير باجتماعات العمل والورد القرآني.",
                            fontSize = 12.sp,
                            color = Color(0xFF6E685F),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                com.example.service.ZheenWorkManagerScheduler.triggerImmediateCheck(context)
                                testTriggered = true
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = userPrimaryColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (testTriggered) "تم تشغيل الفحص التجريبي الآن ✅" else "اختبار إشعار وتنبيه فوري الآن", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text("حالة أذونات النظام:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1A))

                // Permissions list: Pure White 20px Cards
                listOf(
                    Triple("التقويم والمواعيد (Calendar)", "لقراءة وجدولة مواعيدك ومطابقتها مع مواقيت الصلاة", calendarSyncGranted) to { calendarSyncGranted = !calendarSyncGranted },
                    Triple("الميكروفون (Microphone)", "للتحدث الصوتي المباشر مع الوكلاء الذكيين", micGranted) to { micGranted = !micGranted },
                    Triple("الإشعارات والتنبيهات", "للتنبيه قبل الأذان ووقت الإقامة والسحور والإفطار", notificationsGranted) to { notificationsGranted = !notificationsGranted }
                ).forEach { (perm, toggle) ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1EFE9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(perm.first, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1C1B1A))
                                Text(perm.second, fontSize = 12.sp, color = Color(0xFF6E685F))
                            }
                            Switch(
                                checked = perm.third,
                                onCheckedChange = { toggle() },
                                colors = SwitchDefaults.colors(checkedThumbColor = userPrimaryColor, checkedTrackColor = userPrimaryColor.copy(alpha = 0.35f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDataRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tintColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F6F0), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(18.dp))
            Text(text = label, fontSize = 12.sp, color = Color(0xFF6E685F), fontWeight = FontWeight.Medium)
        }
        Text(text = value, fontSize = 12.sp, color = Color(0xFF1C1B1A), fontWeight = FontWeight.Bold)
    }
}

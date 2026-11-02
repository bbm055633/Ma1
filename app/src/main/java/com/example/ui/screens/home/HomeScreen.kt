package com.example.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.components.IslamicGeometricPatternBackground
import com.example.ui.components.OneUi85Card
import com.example.ui.components.OneUiThemeSelectionDialog
import com.example.ui.components.generateGradientForColor
import com.example.ui.theme.SaudiGoldDark
import com.example.ui.theme.zheenCardStyle
import com.example.ui.theme.ZheenCard
import com.example.ui.theme.AppDesignStyle
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UiChatMessage

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    prayerTimes: PrayerTimes,
    hijriDate: HijriDateInfo,
    onNavigate: (ActiveScreen) -> Unit
) {
    val messages by viewModel.homeChatMessages.collectAsState()
    val isGenerating by viewModel.isHomeGenerating.collectAsState()
    val isVoiceActive by viewModel.isVoiceActive.collectAsState()
    val isEnglish by viewModel.isEnglish.collectAsState()
    var inputPrompt by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    val userPrimaryColor by viewModel.userPrimaryColor.collectAsState()
    val userPrimaryColorHex by viewModel.userPrimaryColorHex.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val starryNightSettings by viewModel.starryNightSettings.collectAsState()
    var showColorPickerSheet by remember { mutableStateOf(false) }
    var showAddPinnedDialog by remember { mutableStateOf(false) }
    var pinnedItems by remember { mutableStateOf(listOf("مشروع منصة إعتماد السحابية", "تقرير ميزانية الربع الثالث 2026")) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    IslamicGeometricPatternBackground(
        patternColor = userPrimaryColor.copy(alpha = 0.03f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 12.dp)
        ) {
            // Main Content LazyColumn
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. DATE, LOCATION & GREETING BAR
                item {
                    val cityDisplayName = if (isEnglish) "${selectedCity.nameEn}, Saudi Arabia" else "${selectedCity.nameAr}، المملكة العربية السعودية"
                    val gregorianDateStr = remember {
                        val sdf = java.text.SimpleDateFormat(if (isEnglish) "EEEE, d MMMM yyyy" else "EEEE، d MMMM yyyy", java.util.Locale.forLanguageTag(if (isEnglish) "en" else "ar"))
                        sdf.format(java.util.Date())
                    }

                    ZheenCard(
                        modifier = Modifier.fillMaxWidth(),
                        primaryColor = userPrimaryColor
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = userPrimaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = cityDisplayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${hijriDate.day} ${hijriDate.monthNameAr} ${hijriDate.year} هـ  •  $gregorianDateStr",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = userPrimaryColor.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, userPrimaryColor.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = if (isEnglish) "Hayati 🌿" else "حياتي 🌿",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = userPrimaryColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                // 2. HERO CARD: NEXT PRAYER & COUNTDOWN (بطاقة الصلاة القادمة والعد التنازلي)
                item {
                    val remainingHours = prayerTimes.minutesUntilNext / 60
                    val remainingMins = prayerTimes.minutesUntilNext % 60
                    val countdownStr = if (remainingHours > 0) {
                        if (isEnglish) "$remainingHours hr $remainingMins min" else "$remainingHours س $remainingMins د"
                    } else {
                        if (isEnglish) "$remainingMins minutes" else "$remainingMins دقيقة"
                    }

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Transparent,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_next_prayer_card")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(
                                            Color(0xFF064E3B), // Emerald 900
                                            Color(0xFF1B4332), // Forest deep green
                                            Color(0xFF2D6A4F)  // Emerald mid
                                        )
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFD4A373).copy(alpha = 0.4f), // Saudi Gold accent
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFD4A373).copy(alpha = 0.25f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Mosque,
                                                contentDescription = null,
                                                tint = Color(0xFFD4A373),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            text = if (isEnglish) "Next Prayer" else "الصلاة القادمة",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFD4A373).copy(alpha = 0.20f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4A373).copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = prayerTimes.nextPrayerTime,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFE3B3),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(
                                            text = prayerTimes.nextPrayerName,
                                            fontFamily = com.example.ui.theme.AmiriFontFamily,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isEnglish) "Time remaining until Adhan" else "المتبقي حتى يحين موعد الأذان",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.75f)
                                        )
                                    }

                                    // Countdown badge
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color.Black.copy(alpha = 0.35f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4A373).copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Timer,
                                                contentDescription = null,
                                                tint = Color(0xFFD4A373),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = countdownStr,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. HORIZONTAL PRAYER TIMES BAR (الفجر، الشروق، الظهر، العصر، المغرب، العشاء)
                item {
                    val prayersList = listOf(
                        Triple(if (isEnglish) "Fajr" else "الفجر", prayerTimes.fajr, Icons.Default.NightsStay),
                        Triple(if (isEnglish) "Sunrise" else "الشروق", prayerTimes.sunrise, Icons.Default.WbSunny),
                        Triple(if (isEnglish) "Dhuhr" else "الظهر", prayerTimes.dhuhr, Icons.Default.LightMode),
                        Triple(if (isEnglish) "Asr" else "العصر", prayerTimes.asr, Icons.Default.WbTwilight),
                        Triple(if (isEnglish) "Maghrib" else "المغرب", prayerTimes.maghrib, Icons.Default.Bedtime),
                        Triple(if (isEnglish) "Isha" else "العشاء", prayerTimes.isha, Icons.Default.DarkMode)
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            prayersList.forEach { (name, time, icon) ->
                                val isNext = prayerTimes.nextPrayerName.contains(name) || name.contains(prayerTimes.nextPrayerName)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isNext) Color(0xFF064E3B).copy(alpha = 0.12f) else Color.Transparent)
                                        .padding(vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = name,
                                        tint = if (isNext) Color(0xFF064E3B) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isNext) Color(0xFF064E3B) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = time,
                                        fontSize = 10.sp,
                                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isNext) Color(0xFF064E3B) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. QUICK ISLAMIC & LIFE SHORTCUTS (القرآن، الأذكار، القبلة، المهام)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = { onNavigate(ActiveScreen.Worship) },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF064E3B).copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF064E3B), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isEnglish) "Quran" else "القرآن الكريم",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            onClick = { onNavigate(ActiveScreen.AdhkarSession(isMorning = true)) },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD4A373).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = Color(0xFFD4A373), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isEnglish) "Adhkar" else "الأذكار",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            onClick = { onNavigate(ActiveScreen.Worship) },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0284C7).copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Explore, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isEnglish) "Qibla" else "القبلة",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 1.5 EXECUTIVE SECRETARY & AI ASSISTANT BRIEFING CARD
                item {
                    ZheenCard(
                        modifier = Modifier.fillMaxWidth(),
                        primaryColor = userPrimaryColor
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(userPrimaryColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = userPrimaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = if (isEnglish) "AI Assistant" else "مساعدك الذكي",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isEnglish) "Ready to assist" else "ينظم يومك ومهامك",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = userPrimaryColor,
                                    modifier = Modifier.clickable {
                                        viewModel.sendHomePrompt(if (isEnglish) "Give me my morning briefing and schedule overview." else "أعطني الملخص الصباحي وجدول الأعمال وأهم الأولويات اليوم.")
                                    }
                                ) {
                                    Text(
                                        text = if (isEnglish) "Briefing ✨" else "الملخص ✨",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.sendHomePrompt(if (isEnglish) "Organize my daily priorities." else "ساعدني في إعادة ترتيب أولويات مهامي لهذا اليوم.") },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = if (isEnglish) "Priorities 📋" else "الأولويات 📋", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.sendHomePrompt(if (isEnglish) "Draft a professional work email." else "ساعدني في صياغة بريد إلكتروني رسمي للعمل.") },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = if (isEnglish) "Draft Email ✉️" else "بريد رسمي ✉️", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                // 2. PINNED SECTION WITH AN ADD (+) BUTTON
                item {
                    ZheenCard(
                        modifier = Modifier.fillMaxWidth(),
                        primaryColor = userPrimaryColor
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.PushPin, contentDescription = null, tint = SaudiGoldDark, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = if (isEnglish) "Pinned" else "المثبتات",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                IconButton(
                                    onClick = { showAddPinnedDialog = true },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(userPrimaryColor)
                                        .testTag("add_pinned_button")
                                 ) {
                                    Icon(Icons.Default.Add, contentDescription = if (isEnglish) "Add" else "إضافة", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }

                            pinnedItems.forEach { itemText ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(userPrimaryColor))
                                        Text(text = itemText, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. QUICK ACTIONS BAR (Note, Task, Summarize)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isEnglish) "Quick Actions" else "خدمات سريعة",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionButton(
                                icon = Icons.Default.NoteAlt,
                                label = if (isEnglish) "Note" else "ملاحظة",
                                color = userPrimaryColor,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.sendHomePrompt("دوّن لي ملاحظة سريعة عن: ") }
                            )
                            QuickActionButton(
                                icon = Icons.Default.TaskAlt,
                                label = if (isEnglish) "Task" else "مهمة",
                                color = SaudiGoldDark,
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.sendHomePrompt("أضف لي مهمة عمل جديدة: ") }
                            )
                            QuickActionButton(
                                icon = Icons.Default.AutoAwesome,
                                label = if (isEnglish) "Summarize" else "تلخيص",
                                color = Color(0xFF2563EB),
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.sendHomePrompt("لخص لي وضع السوق وأعمال المؤسسة اليوم:") }
                            )
                        }
                    }
                }

                // Voice active indicator banner
                item {
                    AnimatedVisibility(visible = isVoiceActive) {
                        Surface(
                            color = userPrimaryColor,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Text(
                                        text = if (isEnglish) "Listening..." else "جار الاستماع...",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.toggleVoiceChat() },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = if (isEnglish) "Cancel" else "إلغاء", tint = Color.White)
                                }
                            }
                        }
                    }
                }

                // 5. FAHAD AI WELCOME BUBBLE & CHAT STREAM
                items(messages) { msg ->
                    OneUiChatBubble(
                        message = msg,
                        primaryColor = userPrimaryColor,
                        isEnglish = isEnglish,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(msg.text))
                        }
                    )
                }

                if (isGenerating) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = userPrimaryColor,
                                    strokeWidth = 2.5.dp
                                )
                                Text(
                                    text = if (isEnglish) "Loading..." else "جار التحميل...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 4. BOTTOM SMART INPUT BAR
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Voice Mic button
                    IconButton(
                        onClick = { viewModel.toggleVoiceChat() },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isVoiceActive) userPrimaryColor else MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("voice_chat_button")
                    ) {
                        Icon(
                            imageVector = if (isVoiceActive) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = if (isEnglish) "Voice" else "صوت",
                            tint = if (isVoiceActive) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Input TextField
                    TextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = {
                            Text(
                                text = if (isEnglish) "Ask anything..." else "اكتب سؤالك أو طلبك...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_chat_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 3
                    )

                    // Send button
                    IconButton(
                        onClick = {
                            if (inputPrompt.isNotBlank()) {
                                viewModel.sendHomePrompt(inputPrompt)
                                inputPrompt = ""
                            }
                        },
                        enabled = inputPrompt.isNotBlank() && !isGenerating,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputPrompt.isNotBlank()) userPrimaryColor
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .testTag("home_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = if (isEnglish) "Send" else "إرسال",
                            tint = if (inputPrompt.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAddPinnedDialog) {
        var newPinnedText by remember { mutableStateOf("") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAddPinnedDialog = false },
            title = { Text(if (isEnglish) "Add New Pinned Item" else "إضافة عنصر مثبت جديد", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newPinnedText,
                    onValueChange = { newPinnedText = it },
                    label = { Text(if (isEnglish) "Pinned Item Title" else "عنوان العنصر المثبت") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        if (newPinnedText.isNotBlank()) {
                            pinnedItems = pinnedItems + newPinnedText
                            showAddPinnedDialog = false
                        }
                    }
                ) {
                    Text(if (isEnglish) "Add" else "إضافة", fontWeight = FontWeight.Bold, color = userPrimaryColor)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showAddPinnedDialog = false }) {
                    Text(if (isEnglish) "Cancel" else "إلغاء", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Dynamic Theme Color Selection Panel Dialog
    if (showColorPickerSheet) {
        OneUiThemeSelectionDialog(
            selectedHex = userPrimaryColorHex,
            onSelectHex = { hex ->
                viewModel.setUserPrimaryColor(hex)
            },
            onDismiss = { showColorPickerSheet = false }
        )
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ZheenCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        primaryColor = color,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
fun OneUiChatBubble(
    message: UiChatMessage,
    primaryColor: Color,
    isEnglish: Boolean = false,
    onCopy: () -> Unit
) {
    val isUser = message.isUser

    if (!isUser) {
        ZheenCard(
            modifier = Modifier.fillMaxWidth(),
            primaryColor = primaryColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(generateGradientForColor(primaryColor, 0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isEnglish) "F" else "ف",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fahad Ai",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Outlined.ContentCopy,
                                contentDescription = if (isEnglish) "Copy" else "نسخ",
                                tint = Color(0xFF6E685F),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                color = primaryColor,
                shadowElevation = 3.dp,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: UiChatMessage,
    onCopy: () -> Unit
) {
    OneUiChatBubble(
        message = message,
        primaryColor = MaterialTheme.colorScheme.primary,
        onCopy = onCopy
    )
}

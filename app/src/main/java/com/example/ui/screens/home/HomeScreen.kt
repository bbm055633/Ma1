package com.example.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.OneUiThemeSelectionDialog
import com.example.ui.screens.home.components.*
import com.example.ui.theme.SaudiGoldDark
import com.example.ui.theme.ZheenCard
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.MainViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    prayerTimes: PrayerTimes,
    hijriDate: HijriDateInfo,
    onNavigate: (ActiveScreen) -> Unit
) {
    val messages by viewModel.homeChatMessages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isHomeGenerating.collectAsStateWithLifecycle()
    val isVoiceActive by viewModel.isVoiceActive.collectAsStateWithLifecycle()
    val isEnglish by viewModel.isEnglish.collectAsStateWithLifecycle()
    val chatError by viewModel.chatViewModel.chatError.collectAsStateWithLifecycle()
    var inputPrompt by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    val userPrimaryColor by viewModel.userPrimaryColor.collectAsStateWithLifecycle()
    val userPrimaryColorHex by viewModel.userPrimaryColorHex.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
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
                .padding(top = 8.dp, bottom = 12.dp)
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

                // 2. HERO CARD: NEXT PRAYER & COUNTDOWN
                item {
                    NextPrayerHeroCard(
                        prayerTimes = prayerTimes,
                        isEnglish = isEnglish
                    )
                }

                // 3. HORIZONTAL PRAYER TIMES BAR
                item {
                    val prayersList = listOf(
                        Triple(if (isEnglish) "Fajr" else "الفجر", prayerTimes.fajr, Icons.Default.NightsStay),
                        Triple(if (isEnglish) "Sunrise" else "الشروق", prayerTimes.sunrise, Icons.Default.WbSunny),
                        Triple(if (isEnglish) "Dhuhr" else "الظهر", prayerTimes.dhuhr, Icons.Default.LightMode),
                        Triple(if (isEnglish) "Asr" else "العصر", prayerTimes.asr, Icons.Default.WbTwilight),
                        Triple(if (isEnglish) "Maghrib" else "المغرب", prayerTimes.maghrib, Icons.Default.Bedtime),
                        Triple(if (isEnglish) "Isha" else "العشاء", prayerTimes.isha, Icons.Default.DarkMode)
                    )

                    val nextIndex = prayersList.indexOfFirst { (name, _, _) ->
                        prayerTimes.nextPrayerName.contains(name) || name.contains(prayerTimes.nextPrayerName)
                    }.let { if (it == -1) 2 else it }

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
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            prayersList.forEachIndexed { index, (name, time, icon) ->
                                val isCompleted = index < nextIndex
                                val isNext = index == nextIndex

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when {
                                                isNext -> Color(0x33D4A373)
                                                isCompleted -> Color(0x2252B788)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .padding(vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isCompleted) Icons.Default.CheckCircle else icon,
                                            contentDescription = name,
                                            tint = when {
                                                isNext -> Color(0xFFD4A373)
                                                isCompleted -> Color(0xFF52B788)
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            modifier = Modifier.size(16.dp)
                                        )
                                        if (isNext) {
                                            Text(
                                                text = "◉",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFD4A373)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isNext) Color(0xFFD4A373) else if (isCompleted) Color(0xFF52B788) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = time,
                                        fontSize = 10.sp,
                                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isNext) Color(0xFFD4A373) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 3.5 DAILY HABITS GRID
                item {
                    DailyHabitsGrid(isEnglish = isEnglish)
                }

                // 4. QUICK ISLAMIC & LIFE SHORTCUTS
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
                            modifier = Modifier.weight(1f).testTag("home_worship_shortcut")
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
                    PinnedSection(
                        pinnedItems = pinnedItems,
                        userPrimaryColor = userPrimaryColor,
                        isEnglish = isEnglish,
                        onAddClick = { showAddPinnedDialog = true }
                    )
                }

                // 3. QUICK ACTIONS BAR
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
                if (messages.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, userPrimaryColor.copy(alpha = 0.18f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(userPrimaryColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = userPrimaryColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isEnglish) "Welcome to Fahad AI" else "مرحبًا بك في فهد للذكاء الاصطناعي",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isEnglish) "Type below or use the microphone to start your intelligent consultation." else "اكتب في الشريط أدناه أو استخدم الميكروفون لبدء استشارتك الذكية وتنظيم مهامك.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

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
                        com.example.ui.components.SkeletonChatBubble()
                    }
                }

                if (chatError != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFEF2F2),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = "رمز الخطأ", tint = Color(0xFFDC2626))
                                    Text(
                                        text = chatError ?: "تعذر الاتصال بـ Gemini API.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF991B1B),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Button(
                                    onClick = { viewModel.chatViewModel.retryLastPrompt() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(if (isEnglish) "Retry" else "إعادة المحاولة", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 4. BOTTOM SMART INPUT BAR
            VoiceInputBar(
                inputPrompt = inputPrompt,
                onPromptChange = { inputPrompt = it },
                isVoiceActive = isVoiceActive,
                isGenerating = isGenerating,
                isEnglish = isEnglish,
                userPrimaryColor = userPrimaryColor,
                onToggleVoice = { viewModel.toggleVoiceChat() },
                onSendPrompt = { prompt ->
                    viewModel.sendHomePrompt(prompt)
                    inputPrompt = ""
                }
            )
        }
    }

    if (showAddPinnedDialog) {
        var newPinnedText by remember { mutableStateOf("") }
        AlertDialog(
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
                TextButton(
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
                TextButton(onClick = { showAddPinnedDialog = false }) {
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

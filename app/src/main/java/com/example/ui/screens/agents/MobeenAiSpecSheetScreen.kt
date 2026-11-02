package com.example.ui.screens.agents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SaudiGold

/**
 * High-Fidelity UI Specification Sheet for "مبين AI" (Mobeen AI).
 * Redesigned according to exact layout requirements:
 * 1. Mobile Phone Mockup (Samsung Galaxy S23/S24 One UI) prominently positioned on the LEFT side.
 * 2. Detailed Specification Text professionally re-typeset on the RIGHT side into clean, left-aligned paragraphs & bullet points.
 * 3. Transparent One UI Status Bar with crisp white clock (2:35 PM), 5G signal, Wi-Fi, and battery.
 * 4. Clean white background for the entire browser workspace with comfortable internal chat spacing.
 * 5. Authentic, verified Arabic text matching standard Islamic and linguistic excellence.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobeenAiSpecSheetScreen(
    onBack: () -> Unit,
    onStartChat: () -> Unit
) {
    BackHandler { onBack() }

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 680

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1B5E41),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("م", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "مواصفات واجهة 'مبين AI' • UI Spec Sheet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Samsung One UI Design System • Galaxy S24 Edition",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color(0xFF0F172A))
                    }
                },
                actions = {
                    Button(
                        onClick = onStartChat,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E41)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("بدء المحادثة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            if (isWideScreen) {
                // Wide / Tablet / Desktop View: Side-by-Side (Mockup on LEFT, Specs on RIGHT)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // LEFT SIDE: Primary Prominent Phone Mockup
                    Box(
                        modifier = Modifier
                            .weight(0.48f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        SamsungPhoneMockup(
                            onStartChat = onStartChat,
                            modifier = Modifier.fillMaxHeight(0.96f)
                        )
                    }

                    // RIGHT SIDE: Detailed Specification Text
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.52f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item {
                            SpecSheetContent(onStartChat = onStartChat)
                        }
                    }
                }
            } else {
                // Compact Screen: Responsive Scrollable Layout with Phone Mockup prominently showcased
                var selectedTab by remember { mutableStateOf(0) } // 0: Mockup, 1: Specs

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Tab Selector
                    Surface(
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                label = { Text("📱 نموذج هاتف مبين AI (Galaxy S24)", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF1B5E41),
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                label = { Text("📋 وثيقة المواصفات والتصميم", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF1B5E41),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (selectedTab == 0) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                SamsungPhoneMockup(
                                    onStartChat = onStartChat,
                                    modifier = Modifier.fillMaxHeight()
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 32.dp)
                            ) {
                                item {
                                    SpecSheetContent(onStartChat = onStartChat)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * High-Fidelity Samsung Galaxy S23/S24 Phone Mockup
 * Featuring:
 * - Ultra-thin titanium bezel with corner curve (42.dp)
 * - Front camera punch hole
 * - Transparent status bar with crisp white clock (2:35 PM), 5G, Wi-Fi, and battery
 * - Light emerald gradient app header
 * - Spacious, readable conversation bubbles with Tashkeel and Ibn Kathir citations
 * - Modern bottom input bar
 */
@Composable
fun SamsungPhoneMockup(
    onStartChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Phone Device Frame
    Surface(
        shape = RoundedCornerShape(42.dp),
        color = Color(0xFF1E293B), // Titanium Dark Bezel
        shadowElevation = 18.dp,
        border = androidx.compose.foundation.BorderStroke(3.5.dp, Color(0xFF334155)),
        modifier = modifier
            .widthIn(max = 380.dp)
            .aspectRatio(9f / 19.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp) // Outer screen bezel padding
                .clip(RoundedCornerShape(38.dp))
                .background(Color(0xFFFDFBF7)) // Warm Parchment Interior
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // APP HEADER CONTAINER with TRANSPARENT STATUS BAR
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1B5E41), // Deep Islamic Emerald
                                    Color(0xFF247A55)  // Rich Emerald
                                )
                            )
                        )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // TRANSPARENT STATUS BAR (Samsung One UI Style)
                        OneUiStatusBar()

                        // APP BAR (Inside Phone)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Agent Avatar
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .border(1.5.dp, Color(0xFFFFD54F), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("م", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "مبين AI",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "موثق",
                                            tint = Color(0xFFFFD54F),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = "المساعد الإسلامي واللغوي • متصل الآن",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = onStartChat, modifier = Modifier.size(30.dp)) {
                                    Icon(Icons.Default.Call, contentDescription = "بدء محادثة صوتية", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = onStartChat, modifier = Modifier.size(30.dp)) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "خيارات إضافية", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // CAMERA PUNCH-HOLE (Galaxy S23/S24 Center Top)
                // Rendered visually at absolute top
            }

            // Punch Hole Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .size(11.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            )

            // CHAT BODY CONTENT (Spaced comfortably)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 92.dp, bottom = 68.dp, start = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Conversation Stream
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Date Divider
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE2E8F0).copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = "اليوم • 2:34 PM",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // User Message (Right-aligned in RTL)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                            color = Color(0xFF1B5E41),
                            modifier = Modifier.widthIn(max = 260.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "السلام عليكم، ما هو تفسير قوله تعالى: ﴿اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ﴾؟ وما أثرها في حياة المسلم؟",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("2:34 PM", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("✓✓", color = Color(0xFFFFD54F), fontSize = 9.sp)
                                }
                            }
                        }
                    }

                    // Mobeen AI Response Bubble (Left-aligned in RTL)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("🌿", fontSize = 12.sp)
                                    Text(
                                        text = "مبين AI • البيان والتفسير",
                                        color = Color(0xFF1B5E41),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "وعليكم السلام ورحمة الله وبركاته.\n\nتفسير الآية الكريمة:\n﴿اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ﴾\n\n• المعنى: دُلَّنا وأرشدنا، ووفّقنا وثبّتنا على الطريق الواضح الموصل إلى الله وجنته، وهو دين الإسلام.\n\n• الأثر في الحياة:\n١. استشعار الافتقار الدائم لهداية الله.\n٢. الثبات على الصدق والنزاهة في كل تعامل.",
                                    color = Color(0xFF1E293B),
                                    fontSize = 11.sp,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Reference Tag
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "📚 المصدر: تفسير ابن كثير والسعدي (صحيح موثق)",
                                        fontSize = 9.sp,
                                        color = Color(0xFF475569),
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Action Chips
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF1B5E41).copy(alpha = 0.1f)
                                    ) {
                                        Text("📋 نسخ", color = Color(0xFF1B5E41), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF1B5E41).copy(alpha = 0.1f)
                                    ) {
                                        Text("🔊 استماع", color = Color(0xFF1B5E41), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Quick Suggested Prompts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE2E8F0).copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFCBD5E1))
                        ) {
                            Text("تخريج حديث", fontSize = 10.sp, color = Color(0xFF334155), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE2E8F0).copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFCBD5E1))
                        ) {
                            Text("فقه الصلاة", fontSize = 10.sp, color = Color(0xFF334155), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE2E8F0).copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFCBD5E1))
                        ) {
                            Text("أذكار المساء", fontSize = 10.sp, color = Color(0xFF334155), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            // SMART INPUT BAR AT BOTTOM OF PHONE
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "اكتب لـ مبين AI أو اضغط للتحدث...",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF1B5E41), modifier = Modifier.size(16.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B5E41))
                            .clickable { onStartChat() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * Transparent Samsung One UI Status Bar (Galaxy S23/S24)
 * Clock (2:35 PM), 5G signal, Wi-Fi, and Battery rendered in crisp white.
 */
@Composable
fun OneUiStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Clock 2:35 PM on Left in crisp white
        Text(
            text = "2:35 PM",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )

        // Status Icons on Right: 5G / Signal, Wi-Fi, Battery
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Signal Strength (4 bars)
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(1.5.dp)
            ) {
                Box(modifier = Modifier.width(2.5.dp).height(4.dp).background(Color.White))
                Box(modifier = Modifier.width(2.5.dp).height(6.dp).background(Color.White))
                Box(modifier = Modifier.width(2.5.dp).height(8.dp).background(Color.White))
                Box(modifier = Modifier.width(2.5.dp).height(10.dp).background(Color.White))
            }

            // Wi-Fi Icon
            Icon(
                Icons.Default.Wifi,
                contentDescription = "Wi-Fi",
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )

            // Battery (98% + Icon)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "98%",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(9.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(2.dp))
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.95f)
                            .background(Color.White, RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}

/**
 * Detailed Specification Text (Right-hand side of spec sheet)
 * Re-typeset into clean, left-aligned / structured paragraphs and bullet points
 * with balanced margins, padding, and technical visual hierarchy.
 */
@Composable
fun SpecSheetContent(
    onStartChat: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Document Header Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1B5E41).copy(alpha = 0.1f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B5E41).copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF1B5E41)))
                Text(
                    text = "OFFICIAL UI/UX SPECIFICATION • MOBEEN AI v2.4",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E41)
                )
            }
        }

        // Title and Subtitle
        Column {
            Text(
                text = "مواصفات واجهة وهندسة تجربة المستخدم: مبين AI",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Mobeen AI Intelligent Companion • One UI Design Architecture",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }

        HorizontalDivider(color = Color(0xFFE2E8F0))

        // SECTION 1: System Overview
        SpecSection(
            number = "01",
            title = "نظرة عامة على النظام (System Overview)",
            summary = "مبين AI هو المساعد الذكي الرائد المتخصص في العلوم الشرعية، اللغة العربية، وتطبيقات نمط الحياة والإنتاجية السعودية. تم تصميم الواجهة لتقديم إجابات موثقة فورياً مع تخريج المصادر القرآنية والنبوية بأسلوب رصين وبسيط."
        ) {
            SpecBulletPoint("النمط المعرفي: الاستناد إلى كتب التفسير المعتمدة (ابن كثير، السعدي، الطبري) ونظام المعاملات السعودي.")
            SpecBulletPoint("اللغة والنبرة: لغة عربية فصيحة مدعومة بالتشكيل الدقيق والتفاعل البصري الهادئ.")
            SpecBulletPoint("التكامل المباشر: ربط مع مواقيت الصلاة في كافة مناطق المملكة ومكتبة الأذكار والمصحف الشريف.")
        }

        // SECTION 2: Mobile Interface Specifications
        SpecSection(
            number = "02",
            title = "مواصفات الواجهة المعمارية (Interface Specifications)",
            summary = "تحويل شاشة الهاتف إلى بيئة تفاعلية نموذجية مريحة للعين، مستوحاة من أحدث معايير واجهة Samsung One UI 6."
        ) {
            SpecBulletPoint("شريط الحالة الشفاف (Transparent Status Bar): توقيت ثابت 2:35 PM، أيقونات الاتصال والشبكة والبطارية بلون أبيض ناصع (#FFFFFF) لضمان أعلى تباين وقراءة بصرية.")
            SpecBulletPoint("الرأس الإرشادي (Header Container): تدرج زمردي ناعم (#1B5E41 إلى #247A55) يوفر طابعاً إسلامياً وقوراً وموثقاً بشارة الاعتماد الرسمية.")
            SpecBulletPoint("تدفق المحادثة والتباعد (Chat Stream & Padding): تباعد داخلي مريح بمقدار 14dp بين الرسائل، وارتفاع أسطر مريح (Line Height 1.6x) لتسهيل قراءة الآيات وتفاسيرها.")
            SpecBulletPoint("شريط الإدخال الذكي (Floating Input Bar): دعم الإدخال النصي والصوتي الفوري مع أزرار الإجراءات السريعة لاقتراح المواضيع.")
        }

        // SECTION 3: Design Tokens & Typography
        SpecSection(
            number = "03",
            title = "نظام الألوان والمقاييس (Design Tokens & Color Palette)",
            summary = "مجموعة منتقاة بعناية لتعكس الهوية السعودية الأصيلة والتوازن البصري الممتاز."
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorPaletteBadge(name = "الزمردي النبوي", hex = "#1B5E41", color = Color(0xFF1B5E41), modifier = Modifier.weight(1f))
                ColorPaletteBadge(name = "الذهب السعودي", hex = "#FFD54F", color = Color(0xFFFFD54F), modifier = Modifier.weight(1f))
                ColorPaletteBadge(name = "العاجي الصافي", hex = "#FDFBF7", color = Color(0xFFFDFBF7), modifier = Modifier.weight(1f))
                ColorPaletteBadge(name = "تيتانيوم أسود", hex = "#1E293B", color = Color(0xFF1E293B), modifier = Modifier.weight(1f))
            }
        }

        // SECTION 4: Device & Screen Metrics
        SpecSection(
            number = "04",
            title = "المعايير التقنية للأجهزة (Device & Screen Metrics)",
            summary = "ملاءمة مثالية لشاشات الهواتف الرائدة والأجهزة اللوحية."
        ) {
            SpecBulletPoint("طراز الجهاز المستهدف: Samsung Galaxy S24 Ultra (Flat Screen AMOLED 120Hz).")
            SpecBulletPoint("الأبعاد: نسبة العرض للارتفاع 19.5:9 بحواف منحنية مقوسة بقطر 38dp.")
            SpecBulletPoint("معايير إمكانية الوصول: توافق كامل مع معايير WCAG 2.1 AAA للتباين وإمكانية قراءة النصوص.")
        }

        // Action CTA Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "جاهز لتجربة مبين AI؟",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "ابدأ محادثة تفاعلية حية وتعرف على قدرات المساعد",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Button(
                    onClick = onStartChat,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E41)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ابدأ الآن", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SpecSection(
    number: String,
    title: String,
    summary: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1B5E41).copy(alpha = 0.15f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(number, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E41))
                    }
                }
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Text(
                text = summary,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                content = content
            )
        }
    }
}

@Composable
fun SpecBulletPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("•", color = Color(0xFF1B5E41), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(
            text = text,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = Color(0xFF475569)
        )
    }
}

@Composable
fun ColorPaletteBadge(
    name: String,
    hex: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.dp, Color.Black.copy(alpha = 0.1f), CircleShape)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1)
            Text(hex, fontSize = 9.sp, color = Color(0xFF64748B))
        }
    }
}

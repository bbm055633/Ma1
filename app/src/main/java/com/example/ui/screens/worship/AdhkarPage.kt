package com.example.ui.screens.worship

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AdhkarItem
import com.example.domain.models.AgentDataDefaults
import com.example.ui.components.IslamicGeometricPatternBackground
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.Shapes2027

data class AdhkarCategory(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val accentColor: Color
)

data class SahihMuslimDuaa(
    val id: Int,
    val title: String,
    val arabicText: String,
    val hadithRef: String,
    val repeatCount: Int,
    val benefit: String,
    var isFavorite: Boolean = false
)

/**
 * صفحة أذكاري - AdhkarPage
 * - عرض أذكار الصباح والمساء في تبويبين واضحين.
 * - كل ذكر في بطاقة مستقلة مع النص والعدد المطلوب وزر عدّ تفاعلي بحركة خفيفة.
 * - شريط تقدم أعلى كل ذكر يوضح نسبة الإنجاز.
 * - علامة إكمال أنيقة عند إكمال العدد المطلوب.
 * - زر رجوع يعمل بشكل صحيح وتصميم عربي واضح.
 */
@Composable
fun AdhkarPage(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val primaryColor = MaterialTheme.colorScheme.primary
    var selectedTab by remember { mutableIntStateOf(0) } // 0: أذكار الصباح, 1: أذكار المساء

    val morningList = remember { AgentDataDefaults.MORNING_ADHKAR }
    val eveningList = remember { AgentDataDefaults.EVENING_ADHKAR }

    val morningCounts = remember { mutableStateMapOf<Int, Int>() }
    val eveningCounts = remember { mutableStateMapOf<Int, Int>() }

    val currentList = if (selectedTab == 0) morningList else eveningList
    val currentCounts = if (selectedTab == 0) morningCounts else eveningCounts

    val completedCount = currentList.count { (currentCounts[it.id] ?: 0) >= it.targetCount }
    val totalCount = currentList.size
    val totalProgress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    IslamicGeometricPatternBackground(
        patternColor = primaryColor.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .testTag("adhkar_page_root")
        ) {
            // 1. Top Header with Back Button, Title, and Reset Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("adhkar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "حصن المسلم • الأذكار اليومية",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = { currentCounts.clear() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("adhkar_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة ضبط العدادات",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 2. Clear Tabs: [ أذكار الصباح ☀️ ] | [ أذكار المساء 🌙 ]
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        onClick = { selectedTab = 0 },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedTab == 0) primaryColor else Color.Transparent,
                        shadowElevation = if (selectedTab == 0) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_morning_adhkar")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "أذكار الصباح ☀️",
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        onClick = { selectedTab = 1 },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedTab == 1) primaryColor else Color.Transparent,
                        shadowElevation = if (selectedTab == 1) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_evening_adhkar")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "أذكار المساء 🌙",
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 3. Overall Tab Completion Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (completedCount == totalCount) "مبارك! أتممت جميع أذكار هذا الوقت" else "نسبة إنجاز الأذكار",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (completedCount == totalCount) SuccessGreen else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$completedCount من أصل $totalCount تم إنجازها",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${(totalProgress * 100).toInt()}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (completedCount == totalCount) SuccessGreen else primaryColor
                    )
                }
            }

            // 4. List of Adhkar Cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("adhkar_cards_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(currentList, key = { it.id }) { item ->
                    val count = currentCounts[item.id] ?: 0
                    AdhkarItemCard(
                        item = item,
                        currentCount = count,
                        primaryColor = primaryColor,
                        onIncrement = {
                            if (count < item.targetCount) {
                                currentCounts[item.id] = count + 1
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Individual Adhkar Card with Top Progress Bar, Text, Target, Completion badge, and Counter Button
 */
@Composable
fun AdhkarItemCard(
    item: AdhkarItem,
    currentCount: Int,
    primaryColor: Color,
    onIncrement: () -> Unit
) {
    val isComplete = currentCount >= item.targetCount
    val remaining = (item.targetCount - currentCount).coerceAtLeast(0)
    val progress = (currentCount.toFloat() / item.targetCount.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "adhkar_progress_${item.id}")

    var isPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "btn_scale_${item.id}"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isComplete) 1.dp else 2.dp,
        border = BorderStroke(
            1.2.dp,
            if (isComplete) SuccessGreen.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("adhkar_card_${item.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // شريط تقدم أعلى كل ذكر يوضح نسبة الإنجاز
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                color = if (isComplete) SuccessGreen else primaryColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header: Badge and Fadl
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isComplete) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreen.copy(alpha = 0.14f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "تم الإنجاز ✓",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = primaryColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "العدد المطلوب: ${item.targetCount} ${if (item.targetCount == 1) "مرة" else "مرات"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (item.benefit.isNotBlank()) {
                        Text(
                            text = item.benefit,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // نص الذكر واضح ومريح للقراءة
                Text(
                    text = item.text,
                    fontSize = 17.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom: Remaining text + Count button with light click effect
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isComplete) "اكتمل الذكر بفضل الله" else "المتبقي: $remaining",
                        fontSize = 12.sp,
                        color = if (isComplete) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )

                    Surface(
                        onClick = {
                            if (!isComplete) {
                                isPressed = true
                                onIncrement()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isComplete) SuccessGreen else primaryColor,
                        modifier = Modifier
                            .height(40.dp)
                            .graphicsLayer {
                                scaleX = buttonScale
                                scaleY = buttonScale
                            }
                            .testTag("adhkar_count_button_${item.id}")
                    ) {
                        LaunchedEffect(isPressed) {
                            if (isPressed) {
                                kotlinx.coroutines.delay(100)
                                isPressed = false
                            }
                        }

                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$currentCount / ${item.targetCount}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getSahihMuslimDuasFullList(): List<SahihMuslimDuaa> {
    return listOf(
        SahihMuslimDuaa(1, "دعاء دخول المسجد", "اللهم افتح لي أبواب رحمتك", "صحيح مسلم (713)", 1, "استجلاب رحمة الله تعالى"),
        SahihMuslimDuaa(2, "دعاء خروج المسجد", "اللهم إني أسألك من فضلك", "صحيح مسلم (713)", 1, "طلب فضل الله ورزقه"),
        SahihMuslimDuaa(3, "دعاء دخول المنزل", "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى رَبِّنَا تَوَكَّلْنَا", "صحيح مسلم (2018)", 1, "طرد الشيطان والبركة"),
        SahihMuslimDuaa(4, "دعاء الخروج من المنزل", "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", "صحيح مسلم (2656)", 1, "الكفاية والوقاية"),
        SahihMuslimDuaa(5, "دعاء ما بعد السلام من الصلاة", "أَسْتَغْفِرُ اللَّهَ (ثلاثاً)، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الجَلَالِ وَالإِكْرَامِ", "صحيح مسلم (591)", 1, "مغفرة الذنوب والسكينة"),
        SahihMuslimDuaa(6, "دعاء الاستغفار والتوبة العظيم", "رَبِّ اغْفِرْ لِي وَتُبْ عَلَيَّ إِنَّكَ أَنْتَ التَّوَّابُ الرَّحِيمُ", "صحيح مسلم (2702)", 100, "التوبة ومحو السيئات"),
        SahihMuslimDuaa(7, "دعاء الكرب والضيق", "لَا إِلَهَ إِلَّا اللَّهُ العَظِيمُ الحَلِيمُ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ العَرْشِ العَظِيمِ", "صحيح مسلم (2730)", 1, "تفريج الهموم والكروب"),
        SahihMuslimDuaa(8, "دعاء السفر والركوب", "الله أكبر، الله أكبر، الله أكبر، سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ", "صحيح مسلم (1342)", 1, "الحفظ والأمان"),
        SahihMuslimDuaa(9, "دعاء الرزق والعلم النافع", "اللهم إني أسألك علماً نافعاً ورزقاً طيباً وعملاً متقبلاً", "صحيح مسلم (1515)", 1, "البركة في الرزق والعلم"),
        SahihMuslimDuaa(10, "دعاء الشفاء والبركة", "أذهب البأس رب الناس، اشف أنت الشافي، لا شفاء إلا شفاؤك شفاء لا يغادر سقماً", "صحيح مسلم (2191)", 1, "الشفاء من الأمراض")
    )
}

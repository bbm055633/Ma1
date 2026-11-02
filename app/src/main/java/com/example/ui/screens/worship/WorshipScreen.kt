package com.example.ui.screens.worship

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
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
import com.example.data.local.PrayerEntity
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimes
import com.example.ui.components.LocalStarryNightActive
import com.example.ui.theme.LocalCardTransparency
import com.example.ui.theme.Shapes2027
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalDate

/**
 * شاشة العبادات والأذكار - WorshipScreen
 * بتصميم محدث ينقل شريط التبويبات إلى أسفل الشاشة مباشرة فوق شريط التنقل السفلي
 */
@Composable
fun WorshipScreen(
    viewModel: MainViewModel,
    prayerTimes: PrayerTimes,
    hijriDate: HijriDateInfo,
    onNavigate: (ActiveScreen) -> Unit
) {
    val isEnglish by viewModel.isEnglish.collectAsState()
    val khatmah by viewModel.khatmahState.collectAsState()
    val morningAdhkar by viewModel.morningAdhkar.collectAsState()
    val eveningAdhkar by viewModel.eveningAdhkar.collectAsState()
    val selectedPrayer by viewModel.selectedPrayerState.collectAsState()
    val streaks by viewModel.prayerStreaksState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isPrayerAlertsEnabled by viewModel.isPrayerAlertsEnabled.collectAsState()

    // 0: صلاتي, 1: القبلة والصيام, 2: قرآني, 3: أذكارى
    var currentSubTab by remember { mutableIntStateOf(0) }
    var selectedDayIndex by remember { mutableIntStateOf(0) }

    // Dialog & Session screen states managed using unified WorshipDialogState
    var activeAdhkarSession by remember { mutableStateOf<Boolean?>(null) } // true: morning, false: evening, null: closed
    var dialogState by remember { mutableStateOf<WorshipDialogState>(WorshipDialogState.None) }

    // Full screen Adhkar session overlay (جلسة الأذكار المركّزة)
    if (activeAdhkarSession != null) {
        AdhkarScreen(
            isMorning = activeAdhkarSession == true,
            viewModel = viewModel,
            onBack = { activeAdhkarSession = null }
        )
        return
    }

    // Render active dialog using sealed WorshipDialogState
    when (dialogState) {
        is WorshipDialogState.MorningAdhkar -> {
            MorningAdhkarFullDialog(
                adhkarList = morningAdhkar,
                onIncrement = { id -> viewModel.incrementAdhkar(true, id) },
                onResetAll = { viewModel.resetAdhkar(true) },
                onDismiss = { dialogState = WorshipDialogState.None }
            )
        }
        is WorshipDialogState.EveningAdhkar -> {
            MorningAdhkarFullDialog(
                adhkarList = eveningAdhkar,
                onIncrement = { id -> viewModel.incrementAdhkar(false, id) },
                onResetAll = { viewModel.resetAdhkar(false) },
                onDismiss = { dialogState = WorshipDialogState.None }
            )
        }
        is WorshipDialogState.Qibla -> {
            QiblaCompassDialog(onDismiss = { dialogState = WorshipDialogState.None })
        }
        is WorshipDialogState.Achievements -> {
            AchievementsFullDialog(onDismiss = { dialogState = WorshipDialogState.None })
        }
        is WorshipDialogState.None -> {}
    }

    val currentPrayerRecord = selectedPrayer ?: PrayerEntity(date = selectedDate)

    fun updateAttendance(prayerName: String, col: PrayerStatus) {
        val updated = when (prayerName.lowercase()) {
            "fajr" -> currentPrayerRecord.copy(fajr = col.name)
            "dhuhr" -> currentPrayerRecord.copy(dhuhr = col.name)
            "asr" -> currentPrayerRecord.copy(asr = col.name)
            "maghrib" -> currentPrayerRecord.copy(maghrib = col.name)
            "isha" -> currentPrayerRecord.copy(isha = col.name)
            else -> currentPrayerRecord
        }
        viewModel.saveOrUpdatePrayer(updated)
    }

    fun toggleSunnah(prayerName: String, isQabliyah: Boolean) {
        val updated = when (prayerName.lowercase()) {
            "fajr" -> if (isQabliyah) currentPrayerRecord.copy(fajrQ = !currentPrayerRecord.fajrQ) else currentPrayerRecord.copy(fajrB = !currentPrayerRecord.fajrB)
            "dhuhr" -> if (isQabliyah) currentPrayerRecord.copy(dhuhrQ = !currentPrayerRecord.dhuhrQ) else currentPrayerRecord.copy(dhuhrB = !currentPrayerRecord.dhuhrB)
            "asr" -> if (isQabliyah) currentPrayerRecord.copy(asrQ = !currentPrayerRecord.asrQ) else currentPrayerRecord.copy(asrB = !currentPrayerRecord.asrB)
            "maghrib" -> if (isQabliyah) currentPrayerRecord.copy(maghribQ = !currentPrayerRecord.maghribQ) else currentPrayerRecord.copy(maghribB = !currentPrayerRecord.maghribB)
            "isha" -> if (isQabliyah) currentPrayerRecord.copy(ishaQ = !currentPrayerRecord.ishaQ) else currentPrayerRecord.copy(ishaB = !currentPrayerRecord.ishaB)
            else -> currentPrayerRecord
        }
        viewModel.saveOrUpdatePrayer(updated)
    }

    fun toggleDuha() {
        viewModel.saveOrUpdatePrayer(currentPrayerRecord.copy(duha = !currentPrayerRecord.duha))
    }

    fun toggleMorningAdhkar() {
        viewModel.saveOrUpdatePrayer(currentPrayerRecord.copy(morningAdhkar = !currentPrayerRecord.morningAdhkar))
    }

    fun toggleEveningAdhkar() {
        viewModel.saveOrUpdatePrayer(currentPrayerRecord.copy(eveningAdhkar = !currentPrayerRecord.eveningAdhkar))
    }

    fun toggleQiyam() {
        viewModel.saveOrUpdatePrayer(currentPrayerRecord.copy(qiyam = !currentPrayerRecord.qiyam))
    }

    fun toggleFasting() {
        viewModel.saveOrUpdatePrayer(currentPrayerRecord.copy(fasting = !currentPrayerRecord.fasting))
    }

    val isLiveSky = LocalStarryNightActive.current
    val cardAlpha = LocalCardTransparency.current
    val bgModifier = if (isLiveSky) Modifier else Modifier.background(MaterialTheme.colorScheme.background)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(bgModifier)
            .testTag("worship_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {

            // ================================================================
            // المحتوى الرئيسي التفاعلي حسب التبويب المختار
            // ================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentSubTab) {
                    0 -> {
                        // TAB 0: صلاتي
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("worship_prayers_tab_content"),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 2.1. بطاقة العبادات والالتزام الموحدة
                            item {
                                val sunnahsDone = listOf(
                                    currentPrayerRecord.fajrQ,
                                    currentPrayerRecord.dhuhrQ,
                                    currentPrayerRecord.dhuhrB,
                                    currentPrayerRecord.maghribB,
                                    currentPrayerRecord.ishaB
                                ).count { it }

                                WorshipCommitmentBanner(
                                    fajrStatus = parsePrayerStatus(currentPrayerRecord.fajr),
                                    dhuhrStatus = parsePrayerStatus(currentPrayerRecord.dhuhr),
                                    asrStatus = parsePrayerStatus(currentPrayerRecord.asr),
                                    maghribStatus = parsePrayerStatus(currentPrayerRecord.maghrib),
                                    ishaStatus = parsePrayerStatus(currentPrayerRecord.isha),
                                    sunnahCount = sunnahsDone,
                                    streakDays = streaks.fajrMasjid.coerceAtLeast(1),
                                    hijriDate = hijriDate,
                                    dayOffset = selectedDayIndex,
                                    prayerTimes = prayerTimes,
                                    onPreviousDay = {
                                        val newDate = LocalDate.parse(selectedDate).minusDays(1).toString()
                                        viewModel.setSelectedDate(newDate)
                                        selectedDayIndex++
                                    },
                                    onNextDay = {
                                        if (selectedDayIndex > 0) {
                                            val newDate = LocalDate.parse(selectedDate).plusDays(1).toString()
                                            viewModel.setSelectedDate(newDate)
                                            selectedDayIndex--
                                        }
                                    },
                                    onOpenQibla = { dialogState = WorshipDialogState.Qibla },
                                    onToggleAlert = { viewModel.togglePrayerAlerts() },
                                    isAlertEnabled = isPrayerAlertsEnabled
                                )
                            }

                            // 2.2. تتبع الصلوات الخمس باستخدام PrayerTrackingData
                            item {
                                val trackingData = PrayerTrackingData(
                                    prayerTimes = prayerTimes,
                                    selectedDayIndex = selectedDayIndex,
                                    onDayChange = { newIdx ->
                                        val diff = (newIdx - selectedDayIndex).toLong()
                                        val newDate = LocalDate.parse(selectedDate).plusDays(diff).toString()
                                        selectedDayIndex = newIdx
                                        viewModel.setSelectedDate(newDate)
                                    },
                                    isEnglish = isEnglish,
                                    fajrAttendance = parsePrayerStatus(currentPrayerRecord.fajr),
                                    onFajrAttendanceChange = { col -> updateAttendance("fajr", col) },
                                    fajrSunnahQabliyah = currentPrayerRecord.fajrQ,
                                    onToggleFajrSunnahQabliyah = { toggleSunnah("fajr", true) },
                                    isDuhaDone = currentPrayerRecord.duha,
                                    onToggleDuha = { toggleDuha() },
                                    isMorningAdhkarDone = currentPrayerRecord.morningAdhkar,
                                    onToggleMorningAdhkar = { toggleMorningAdhkar() },
                                    onOpenMorningAdhkarSheet = { dialogState = WorshipDialogState.MorningAdhkar },
                                    morningAdhkarList = morningAdhkar,
                                    dhuhrAttendance = parsePrayerStatus(currentPrayerRecord.dhuhr),
                                    onDhuhrAttendanceChange = { col -> updateAttendance("dhuhr", col) },
                                    dhuhrSunnahQabliyah = currentPrayerRecord.dhuhrQ,
                                    onToggleDhuhrSunnahQabliyah = { toggleSunnah("dhuhr", true) },
                                    dhuhrSunnahBadiyah = currentPrayerRecord.dhuhrB,
                                    onToggleDhuhrSunnahBadiyah = { toggleSunnah("dhuhr", false) },
                                    asrAttendance = parsePrayerStatus(currentPrayerRecord.asr),
                                    onAsrAttendanceChange = { col -> updateAttendance("asr", col) },
                                    maghribAttendance = parsePrayerStatus(currentPrayerRecord.maghrib),
                                    onMaghribAttendanceChange = { col -> updateAttendance("maghrib", col) },
                                    maghribSunnahBadiyah = currentPrayerRecord.maghribB,
                                    onToggleMaghribSunnahBadiyah = { toggleSunnah("maghrib", false) },
                                    ishaAttendance = parsePrayerStatus(currentPrayerRecord.isha),
                                    onIshaAttendanceChange = { col -> updateAttendance("isha", col) },
                                    ishaSunnahBadiyah = currentPrayerRecord.ishaB,
                                    onToggleIshaSunnahBadiyah = { toggleSunnah("isha", false) },
                                    isQiyamDone = currentPrayerRecord.qiyam,
                                    onToggleQiyam = { toggleQiyam() },
                                    isEveningAdhkarDone = currentPrayerRecord.eveningAdhkar,
                                    onToggleEveningAdhkar = { toggleEveningAdhkar() },
                                    onOpenEveningAdhkarSheet = { dialogState = WorshipDialogState.EveningAdhkar },
                                    isFastingDone = currentPrayerRecord.fasting,
                                    onToggleFasting = { toggleFasting() },
                                    streaks = streaks
                                )

                                FaithJourneyPrayerTrackingCard(trackingData = trackingData)
                            }

                            // 2.3. تحليل الالتزام بالعبادات والرسوم البيانية
                            item {
                                PrayerWeeklyTrendSection()
                            }

                            // 2.4. زر بدء جلسة أذكار الصباح المركّزة
                            item {
                                Button(
                                    onClick = { activeAdhkarSession = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("launch_adhkar_focus_session_btn"),
                                    shape = Shapes2027.Medium,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text(
                                        text = if (isEnglish) "Start Focused Adhkar Session 📿" else "بدء جلسة أذكار الصباح المركّزة 📿",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // TAB 1: القبلة والصيام
                        QiblaAndFastingSubView(
                            hijriDate = hijriDate,
                            prayerTimes = prayerTimes,
                            isEnglish = isEnglish,
                            onOpenFullscreenQibla = { dialogState = WorshipDialogState.Qibla }
                        )
                    }

                    2 -> {
                        // TAB 2: قرآني
                        QuranScreen(
                            khatmah = khatmah,
                            onPageChanged = { newPage ->
                                viewModel.updateCurrentPage(newPage)
                            },
                            onBack = { currentSubTab = 0 }
                        )
                    }

                    3 -> {
                        // TAB 3: أذكارى
                        AdhkarPage(
                            onBack = { currentSubTab = 0 }
                        )
                    }
                }
            }

            // ================================================================
            // 3. تبويبات التنقل السفلي
            // ================================================================
            WorshipBottomDockedTabBar(
                selectedTab = currentSubTab,
                onTabSelect = { currentSubTab = it },
                isEnglish = isEnglish,
                isLiveSky = isLiveSky,
                cardAlpha = cardAlpha
            )
        }
    }
}

/**
 * شريط التبويبات السفلي
 */
@Composable
private fun WorshipBottomDockedTabBar(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    isEnglish: Boolean,
    isLiveSky: Boolean,
    cardAlpha: Float,
    modifier: Modifier = Modifier
) {
    data class WorshipTabItem(val index: Int, val titleAr: String, val titleEn: String, val emoji: String)

    val tabs = listOf(
        WorshipTabItem(index = 3, titleAr = "أذكارى", titleEn = "Adhkar", emoji = "📿"),
        WorshipTabItem(index = 2, titleAr = "قرآني", titleEn = "Quran", emoji = "📖"),
        WorshipTabItem(index = 1, titleAr = "القبلة", titleEn = "Qibla", emoji = "🧭"),
        WorshipTabItem(index = 0, titleAr = "صلاتي", titleEn = "Prayers", emoji = "🕌")
    )

    Surface(
        shape = Shapes2027.Large,
        color = if (isLiveSky) MaterialTheme.colorScheme.surface.copy(alpha = cardAlpha) else MaterialTheme.colorScheme.surface,
        shadowElevation = if (isLiveSky) 0.dp else 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("worship_bottom_docked_tabs")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, tabItem ->
                val isSelected = selectedTab == tabItem.index

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(Shapes2027.Medium)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { onTabSelect(tabItem.index) }
                        .padding(vertical = 10.dp)
                        .testTag("worship_docked_tab_${tabItem.index}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = tabItem.emoji,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isEnglish) tabItem.titleEn else tabItem.titleAr,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (index < tabs.lastIndex) {
                    VerticalDivider(
                        modifier = Modifier
                            .height(22.dp)
                            .padding(horizontal = 2.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

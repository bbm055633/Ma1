package com.example.ui.screens.worship

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.com.fahada.R
import com.example.data.repository.LocalQuranRepository
import com.example.domain.models.QuranKhatmahState
import com.example.ui.theme.Shapes2027

/**
 * صفحة القرآن الكريم - QuranScreen
 * تصميم نظيف ومختصر بخلفية أخضر مائي صلب (#0D9488) وبطاقات RoundedCornerShape(24.dp) مع استخدام خط Amiri.ttf لجميع النصوص القرآنية ودعم RTL.
 */
@Composable
fun QuranScreen(
    khatmah: QuranKhatmahState,
    onPageChanged: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var fontSizeSp by remember { mutableFloatStateOf(28f) }
    var selectedSurahForReading by remember { mutableStateOf<Int?>(null) }

    val quranFont = FontFamily(
        Font(R.font.kfgqpc_uthmanic, FontWeight.Normal),
        Font(R.font.amiri_quran, FontWeight.Normal),
        Font(R.font.amiri, FontWeight.Normal)
    )

    LaunchedEffect(Unit) {
        LocalQuranRepository.initialize(context)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        if (selectedSurahForReading != null) {
            QuranReadingFullView(
                surahNumber = selectedSurahForReading!!,
                fontSize = fontSizeSp,
                onFontSizeChange = { fontSizeSp = it },
                onBack = { selectedSurahForReading = null },
                onSaveBookmark = { page -> onPageChanged(page) },
                quranFont = quranFont,
                savedBookmarkPage = khatmah.currentPage
            )
            return@CompositionLocalProvider
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("quran_screen_root")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Compact Search Bar: "ابحث عن آية أو سورة"
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            "ابحث عن آية أو سورة", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = com.example.ui.theme.AppTypography.body,
                            fontSize = 13.sp
                        ) 
                    },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.Search, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary, 
                            modifier = Modifier.size(20.dp)
                        ) 
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontFamily = com.example.ui.theme.AppTypography.body, fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("quran_search_input")
                )

                // Surahs List
                val surahsList = getSurahsList().filter { 
                    searchQuery.isEmpty() || it.name.contains(searchQuery) || it.englishName.contains(searchQuery, ignoreCase = true) 
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(surahsList) { surah ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSurahForReading = surah.number
                                    onPageChanged(surah.startPage)
                                }
                                .testTag("surah_card_${surah.number}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = surah.number.toArabicDigits(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontFamily = com.example.ui.theme.AppTypography.body
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = surah.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontFamily = quranFont,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = surah.englishName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontFamily = com.example.ui.theme.AppTypography.body
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "ص ${surah.startPage.toArabicDigits()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontFamily = com.example.ui.theme.AppTypography.body,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuranReadingFullView(
    surahNumber: Int,
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    onBack: () -> Unit,
    onSaveBookmark: (Int) -> Unit,
    quranFont: FontFamily,
    savedBookmarkPage: Int
) {
    val context = LocalContext.current
    val surahInfo = remember(surahNumber) {
        getSurahsList().find { it.number == surahNumber }
    }
    val pageData = remember(surahNumber) {
        LocalQuranRepository.getQuranPage(context, surahInfo?.startPage ?: 1)
    }

    var isBookmarked by remember(pageData.number, savedBookmarkPage) {
        mutableStateOf(savedBookmarkPage == pageData.number)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Top Header Row with Back Button, Surah Title, and Controls
                Surface(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = surahInfo?.name ?: "سورة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = quranFont,
                                fontSize = 19.sp
                            )
                            Text(
                                text = "صفحة ${pageData.number.toArabicDigits()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = com.example.ui.theme.AppTypography.body
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    isBookmarked = !isBookmarked
                                    if (isBookmarked) onSaveBookmark(pageData.number)
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "علامة مرجعية",
                                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Reading Content: Harmonious Cards for Verses
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
                ) {
                    // Basmalah Card
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                fontFamily = quranFont,
                                fontSize = (fontSize + 2f).sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            )
                        }
                    }

                    // Verses displayed in harmonious cards with small circular badge
                    items(pageData.ayahs) { ayah ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = ayah.text,
                                    fontSize = fontSize.sp,
                                    lineHeight = (fontSize * 1.8f).sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    fontFamily = quranFont,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Small circular badge following ayah text with clear font
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ayah.numberInSurah.toArabicDigits(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontFamily = com.example.ui.theme.AppTypography.body
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class SurahMeta(
    val number: Int,
    val name: String,
    val englishName: String,
    val ayahCount: Int,
    val revelationType: String,
    val startPage: Int
)

private fun getSurahsList(): List<SurahMeta> {
    return listOf(
        SurahMeta(1, "سُورَةُ ٱلْفَاتِحَةِ", "Al-Faatiha", 7, "مكية", 1),
        SurahMeta(2, "سُورَةُ البَقَرَةِ", "Al-Baqara", 286, "مدنية", 2),
        SurahMeta(3, "سُورَةُ Āلِ عِمْرَانَ", "Aal-i-Imraan", 200, "مدنية", 50),
        SurahMeta(4, "سُورَةُ النِّسَاءِ", "An-Nisaa", 176, "مدنية", 77),
        SurahMeta(5, "سُورَةُ المَائِدَةِ", "Al-Maaida", 120, "مدنية", 106),
        SurahMeta(6, "سُورَةُ الأَنْعَامِ", "Al-An'aam", 165, "مكية", 128),
        SurahMeta(7, "سُورَةُ الأَعْرَافِ", "Al-A'raaf", 206, "مكية", 151),
        SurahMeta(8, "سُورَةُ الأَنْفَالِ", "Al-Anfaal", 75, "مدنية", 177),
        SurahMeta(9, "سُورَةُ التَّوبَةِ", "At-Tawba", 129, "مدنية", 187),
        SurahMeta(10, "سُورَةُ يُونُسَ", "Yunus", 109, "مكية", 208),
        SurahMeta(11, "سُورَةُ هُودٍ", "Hud", 123, "مكية", 221),
        SurahMeta(12, "سُورَةُ يُوسُفَ", "Yusuf", 111, "مكية", 235),
        SurahMeta(13, "سُورَةُ الرَّعْدِ", "Ar-Ra'd", 43, "مدنية", 249),
        SurahMeta(14, "سُورَةُ إِبْرَاهِيمَ", "Ibrahim", 52, "مكية", 255),
        SurahMeta(15, "سُورَةُ الحِجْرِ", "Al-Hijr", 99, "مكية", 262),
        SurahMeta(16, "سُورَةُ النَّحْلِ", "An-Nahl", 128, "مكية", 267),
        SurahMeta(17, "سُورَةُ الإِسْرَاءِ", "Al-Israa", 111, "مكية", 282),
        SurahMeta(18, "سُورَةُ الكَهْفِ", "Al-Kahf", 110, "مكية", 293),
        SurahMeta(19, "سُورَةُ مَرْيَمَ", "Maryam", 98, "مكية", 305),
        SurahMeta(20, "سُورَةُ طَهَ", "Taha", 135, "مكية", 312),
        SurahMeta(21, "سُورَةُ الأَنْبِيَاءِ", "Al-Anbiyaa", 112, "مكية", 322),
        SurahMeta(22, "سُورَةُ الحَجِّ", "Al-Hajj", 78, "مدنية", 332),
        SurahMeta(23, "سُورَةُ المُؤْمِنُونَ", "Al-Mu'minoon", 118, "مكية", 342),
        SurahMeta(24, "سُورَةُ النُّورِ", "An-Noor", 64, "مدنية", 350),
        SurahMeta(25, "سُورَةُ الفُرْقَانِ", "Al-Furqaan", 77, "مكية", 359),
        SurahMeta(26, "سُورَةُ الشُّعَرَاءِ", "Ash-Shu'araa", 227, "مكية", 367),
        SurahMeta(27, "سُورَةُ النَّمْلِ", "An-Naml", 93, "مكية", 377),
        SurahMeta(28, "سُورَةُ القَصَصِ", "Al-Qasas", 88, "مكية", 385),
        SurahMeta(29, "سُورَةُ العَنْكَبُوتِ", "Al-Ankaboot", 69, "مكية", 396),
        SurahMeta(30, "سُورَةُ الرُّومِ", "Ar-Room", 60, "مكية", 404),
        SurahMeta(31, "سُورَةُ لُقْمَانَ", "Luqman", 34, "مكية", 411),
        SurahMeta(32, "سُورَةُ السَّجْدَةِ", "As-Sajda", 30, "مكية", 415),
        SurahMeta(33, "سُورَةُ الأَحْزَابِ", "Al-Ahzaab", 73, "مدنية", 418),
        SurahMeta(34, "سُورَةُ سَبَإٍ", "Saba", 54, "مكية", 428),
        SurahMeta(35, "سُورَةُ فَاطِرٍ", "Faatir", 45, "مكية", 434),
        SurahMeta(36, "سُورَةُ يس", "Yaseen", 83, "مكية", 440),
        SurahMeta(37, "سُورَةُ الصَّافَّاتِ", "As-Saaffaat", 182, "مكية", 446),
        SurahMeta(38, "سُورَةُ ص", "Saad", 88, "مكية", 453),
        SurahMeta(39, "سُورَةُ الزُّمَرِ", "Az-Zumar", 75, "مكية", 458),
        SurahMeta(40, "سُورَةُ غَافِرٍ", "Ghaafir", 85, "مكية", 467),
        SurahMeta(41, "سُورَةُ فُصِّلَتْ", "Fussilat", 54, "مكية", 477),
        SurahMeta(42, "سُورَةُ الشُّورَىٰ", "Ash-Shura", 53, "مكية", 483),
        SurahMeta(43, "سُورَةُ الزُّخْرُفِ", "Az-Zukhruf", 89, "مكية", 489),
        SurahMeta(44, "سُورَةُ الدُّخَانِ", "Ad-Dukhaan", 59, "مكية", 496),
        SurahMeta(45, "سُورَةُ الجَاثِيَةِ", "Al-Jaathiya", 37, "مكية", 499),
        SurahMeta(46, "سُورَةُ الأَحْقَافِ", "Al-Ahqaf", 35, "مكية", 502),
        SurahMeta(47, "سُورَةُ مُحَمَّدٍ", "Muhammad", 38, "مدنية", 507),
        SurahMeta(48, "سُورَةُ الفَتْحِ", "Al-Fath", 29, "مدنية", 511),
        SurahMeta(49, "سُورَةُ الحُجُرَاتِ", "Al-Hujuraat", 18, "مدنية", 515),
        SurahMeta(50, "سُورَةُ ق", "Qaaf", 45, "مكية", 518),
        SurahMeta(51, "سُورَةُ الذَّارِيَاتِ", "Ad-Dhaariyat", 60, "مكية", 520),
        SurahMeta(52, "سُورَةُ الطُّورِ", "At-Tur", 49, "مكية", 523),
        SurahMeta(53, "سُورَةُ النَّجْمِ", "An-Najm", 62, "مكية", 526),
        SurahMeta(54, "سُورَةُ القَمَرِ", "Al-Qamar", 55, "مكية", 528),
        SurahMeta(55, "سُورَةُ الرَّحْمَٰنِ", "Ar-Rahmaan", 78, "مدنية", 531),
        SurahMeta(56, "سُورَةُ الوَاقِعَةِ", "Al-Waaqi'a", 96, "مكية", 534),
        SurahMeta(57, "سُورَةُ الحَدِيدِ", "Al-Hadid", 29, "مدنية", 537),
        SurahMeta(58, "سُورَةُ المُجَادلَةِ", "Al-Mujaadila", 22, "مدنية", 542),
        SurahMeta(59, "سُورَةُ الحَشْرِ", "Al-Hashr", 24, "مدنية", 545),
        SurahMeta(60, "سُورَةُ المُمْتَحَنَةِ", "Al-Mumtahana", 13, "مدنية", 549),
        SurahMeta(61, "سُورَةُ الصَّفِّ", "As-Saff", 14, "مدنية", 551),
        SurahMeta(62, "سُورَةُ الجُمُعَةِ", "Al-Jumu'a", 11, "مدنية", 553),
        SurahMeta(63, "سُورَةُ المُنَافِقُونَ", "Al-Munaafiqoon", 11, "مدنية", 554),
        SurahMeta(64, "سُورَةُ التَّغَابُنِ", "At-Taghaabun", 18, "مدنية", 556),
        SurahMeta(65, "سُورَةُ الطَّلَاقِ", "At-Talaaq", 12, "مدنية", 558),
        SurahMeta(66, "سُورَةُ التَّحْرِيمِ", "At-Tahrim", 12, "مدنية", 560),
        SurahMeta(67, "سُورَةُ المُلْكِ", "Al-Mulk", 30, "مكية", 562),
        SurahMeta(68, "سُورَةُ القَلَمِ", "Al-Qalam", 52, "مكية", 564),
        SurahMeta(69, "سُورَةُ الحَاقَّةِ", "Al-Haaqqa", 52, "مكية", 566),
        SurahMeta(70, "سُورَةُ المَعَارِجِ", "Al-Ma'aarij", 44, "مكية", 568),
        SurahMeta(71, "سُورَةُ نُوحٍ", "Nooh", 28, "مكية", 570),
        SurahMeta(72, "سُورَةُ الجِنِّ", "Al-Jinn", 28, "مكية", 572),
        SurahMeta(73, "سُورَةُ المُزَّمِّلِ", "Al-Muzzammil", 20, "مكية", 574),
        SurahMeta(74, "سُورَةُ المُدَّثِّرِ", "Al-Muddaththir", 56, "مكية", 575),
        SurahMeta(75, "سُورَةُ القِيَامَةِ", "Al-Qiyaama", 40, "مكية", 577),
        SurahMeta(76, "سُورَةُ الإِنْسَانِ", "Al-Insaan", 31, "مدنية", 578),
        SurahMeta(77, "سُورَةُ المُرْسَلَاتِ", "Al-Mursalaat", 50, "مكية", 580),
        SurahMeta(78, "سُورَةُ النَّبَإِ", "An-Naba", 40, "مكية", 582),
        SurahMeta(79, "سُورَةُ النَّازِعَاتِ", "An-Naazi'aat", 46, "مكية", 583),
        SurahMeta(80, "سُورَةُ عَبَسَ", "Abasa", 42, "مكية", 585),
        SurahMeta(81, "سُورَةُ التَّكْوِيرِ", "At-Takwir", 29, "مكية", 586),
        SurahMeta(82, "سُورَةُ الانفِطَارِ", "Al-Infitaar", 19, "مكية", 587),
        SurahMeta(83, "سُورَةُ المُطَفِّفِينَ", "Al-Mutaffifin", 36, "مكية", 587),
        SurahMeta(84, "سُورَةُ الانشِقَاقِ", "Al-Inshiqaaq", 25, "مكية", 589),
        SurahMeta(85, "سُورَةُ البرُوجِ", "Al-Burooj", 22, "مكية", 590),
        SurahMeta(86, "سُورَةُ الطَّارِقِ", "At-Taariq", 17, "مكية", 591),
        SurahMeta(87, "سُورَةُ الأَعْلَىٰ", "Al-A'laa", 19, "مكية", 591),
        SurahMeta(88, "سُورَةُ الغَاشِيَةِ", "Al-Ghaashiya", 26, "مكية", 592),
        SurahMeta(89, "سُورَةُ الفَجْرِ", "Al-Fajr", 30, "مكية", 593),
        SurahMeta(90, "سُورَةُ البَلَدِ", "Al-Balad", 20, "مكية", 594),
        SurahMeta(91, "سُورَةُ الشَّمْسِ", "Ash-Shams", 15, "مكية", 595),
        SurahMeta(92, "سُورَةُ اللَّيْلِ", "Al-Lail", 21, "مكية", 595),
        SurahMeta(93, "سُورَةُ الضُّحَىٰ", "Ad-Dhuha", 11, "مكية", 596),
        SurahMeta(94, "سُورَةُ الشَّرْحِ", "Ash-Sharh", 8, "مكية", 596),
        SurahMeta(95, "سُورَةُ التِّينِ", "At-Tin", 8, "مكية", 597),
        SurahMeta(96, "سُورَةُ العَلَقِ", "Al-Alaq", 19, "مكية", 597),
        SurahMeta(97, "سُورَةُ القَدْرِ", "Al-Qadr", 5, "مكية", 598),
        SurahMeta(98, "سُورَةُ البَيِّنَةِ", "Al-Bayyina", 8, "مدنية", 598),
        SurahMeta(99, "سُورَةُ الزَّلْزَلَةِ", "Az-Zalzala", 8, "مدنية", 599),
        SurahMeta(100, "سُورَةُ العَادِيَاتِ", "Al-Aadiyaat", 11, "مكية", 599),
        SurahMeta(101, "سُورَةُ القَارِعَةِ", "Al-Qaari'a", 11, "مكية", 600),
        SurahMeta(102, "سُورَةُ التَّكَاثُرِ", "At-Takaathur", 8, "مكية", 600),
        SurahMeta(103, "سُورَةُ العَصْرِ", "Al-Asr", 3, "مكية", 601),
        SurahMeta(104, "سُورَةُ الهُمَزَةِ", "Al-Humaza", 9, "مكية", 601),
        SurahMeta(105, "سُورَةُ الفِيلِ", "Al-Fil", 5, "مكية", 601),
        SurahMeta(106, "سُورَةُ قُرَيْشٍ", "Quraish", 4, "مكية", 602),
        SurahMeta(107, "سُورَةُ المَاعُونِ", "Al-Maa'un", 7, "مكية", 602),
        SurahMeta(108, "سُورَةُ الكَوْثَرِ", "Al-Kawthar", 3, "مكية", 602),
        SurahMeta(109, "سُورَةُ الكَافِرُونَ", "Al-Kaafiroun", 6, "مكية", 603),
        SurahMeta(110, "سُورَةُ النَّصْرِ", "An-Nasr", 3, "مدنية", 603),
        SurahMeta(111, "سُورَةُ المَسَدِ", "Al-Masad", 5, "مكية", 603),
        SurahMeta(112, "سُورَةُ الإِخْلَاصِ", "Al-Ikhlas", 4, "مكية", 604),
        SurahMeta(113, "سُورَةُ الفَلَقِ", "Al-Falaq", 5, "مكية", 604),
        SurahMeta(114, "سُورَةُ النَّاسِ", "An-Naas", 6, "مكية", 604)
    )
}

private fun Int.toArabicDigits(): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return this.toString().map { if (it in '0'..'9') arabicDigits[it - '0'] else it }.joinToString("")
}


package com.example.ui.screens.worship

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * تصميم نظيف بخلفية أخضر غامق صلب (#064E3B)، عنوان "أذكاري" بسيط، شبكة بطاقات RoundedCornerShape(24.dp) ومسافات 12dp.
 */
@Composable
fun AdhkarPage(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var favoritesOnly by remember { mutableStateOf(false) }

    val categories = listOf(
        AdhkarCategory("morning", "أذكار الصباح", "☀️", Color(0xFFFB923C)),
        AdhkarCategory("evening", "أذكار المساء", "🌙", Color(0xFF3B82F6)),
        AdhkarCategory("sleep", "أذكار النوم", "🛏️", Color(0xFFEF4444)),
        AdhkarCategory("wakeup", "الاستيقاظ", "🌅", Color(0xA855F7)),
        AdhkarCategory("duaa", "الأدعية", "🤲", Color(0xFF06B6D4)),
        AdhkarCategory("post_prayer", "بعد الصلاة", "🧎", Color(0xFFFBBF24))
    )

    if (selectedCategory == "duaa") {
        SahihMuslimDuasScreen(
            onBack = { selectedCategory = null }
        )
        return
    }

    if (selectedCategory != null) {
        SpecificAdhkarSessionScreen(
            categoryId = selectedCategory!!,
            categoryTitle = categories.find { it.id == selectedCategory }?.title ?: "أذكار",
            onBack = { selectedCategory = null }
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF064E3B)) // Solid Emerald 900
            .testTag("adhkar_page_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title: "أذكاري" بخط أبيض عريض في الأعلى
            Text(
                text = "أذكاري",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Grid 2 columns with RoundedCornerShape(24.dp) and 12dp spacing, name + icon only
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(categories) { category ->
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = category.accentColor.copy(alpha = 0.28f),
                        border = BorderStroke(1.5.dp, category.accentColor.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .height(110.dp)
                            .clickable { selectedCategory = category.id }
                            .testTag("adhkar_category_${category.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = category.accentColor
                            ) {
                                Text(
                                    text = category.iconEmoji,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.titleMedium,
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

@Composable
private fun SahihMuslimDuasScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var favoritesOnly by remember { mutableStateOf(false) }
    val duasList = remember { getSahihMuslimDuasFullList() }

    val filteredList = duasList.filter {
        (it.title.contains(searchQuery) || it.arabicText.contains(searchQuery) || it.benefit.contains(searchQuery)) &&
        (!favoritesOnly || it.isFavorite)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF064E3B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                }
                Text(
                    text = "الأدعية",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = { favoritesOnly = !favoritesOnly }) {
                    Icon(
                        imageVector = if (favoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "المفضلة",
                        tint = if (favoritesOnly) Color(0xFFFFB703) else Color.White
                    )
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في الأدعية...", color = Color.White.copy(alpha = 0.7f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.6f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredList) { duaa ->
                    var countState by remember { mutableIntStateOf(0) }
                    var isFavState by remember { mutableStateOf(duaa.isFavorite) }

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = duaa.title,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB703),
                                    fontSize = 15.sp
                                )
                                IconButton(
                                    onClick = { 
                                        isFavState = !isFavState
                                        duaa.isFavorite = isFavState
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFavState) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (isFavState) Color(0xFFFF6B6D) else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = duaa.arabicText,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 26.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "المصدر: ${duaa.hadithRef}",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "الفائدة: ${duaa.benefit}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF52B788),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = Shapes2027.Pill,
                                    color = Color(0xFF06B6D4).copy(alpha = 0.3f),
                                    modifier = Modifier.clickable { countState++ }
                                ) {
                                    Text(
                                        text = "العدّ: $countState / ${duaa.repeatCount}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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
private fun SpecificAdhkarSessionScreen(
    categoryId: String,
    categoryTitle: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF064E3B)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "📿", fontSize = 48.sp)
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("العودة", color = Color(0xFF064E3B), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun getSahihMuslimDuasFullList(): List<SahihMuslimDuaa> {
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

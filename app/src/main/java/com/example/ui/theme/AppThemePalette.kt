package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppThemePalette(
    val titleAr: String,
    val descriptionAr: String,
    val emoji: String,
    val primaryColor: Color,
    val primaryLightColor: Color,
    val secondaryColor: Color,
    val secondaryLightColor: Color,
    val previewGradient: List<Color>
) {
    SAUDI_EMERALD(
        titleAr = "الأخضر السعودي",
        descriptionAr = "الزمرد الفاخر والذهب الأصيل",
        emoji = "🇸🇦",
        primaryColor = Color(0xFF0F766E),
        primaryLightColor = Color(0xFF14B8A6),
        secondaryColor = Color(0xFFF59E0B),
        secondaryLightColor = Color(0xFFFDE68A),
        previewGradient = listOf(Color(0xFF0F766E), Color(0xFFF59E0B))
    ),
    ROYAL_NAJDI(
        titleAr = "العنابي النجدي",
        descriptionAr = "الفخامة الملكية والتراث العريق",
        emoji = "👑",
        primaryColor = Color(0xFF9D174D),
        primaryLightColor = Color(0xFFFB7185),
        secondaryColor = Color(0xFFD97706),
        secondaryLightColor = Color(0xFFFDE68A),
        previewGradient = listOf(Color(0xFF9D174D), Color(0xFFD97706))
    ),
    EXECUTIVE_NAVY(
        titleAr = "الكحلي التنفيذي",
        descriptionAr = "أزرق الأعمال والريادة التقنية",
        emoji = "💼",
        primaryColor = Color(0xFF1D4ED8),
        primaryLightColor = Color(0xFF38BDF8),
        secondaryColor = Color(0xFF0284C7),
        secondaryLightColor = Color(0xFFBAE6FD),
        previewGradient = listOf(Color(0xFF1D4ED8), Color(0xFF0284C7))
    ),
    DESERT_GOLD(
        titleAr = "ذهب الصحراء",
        descriptionAr = "أصالة رمال النفوذ والبرونز الذهبي",
        emoji = "🏜️",
        primaryColor = Color(0xFFB45309),
        primaryLightColor = Color(0xFFFBBF24),
        secondaryColor = Color(0xFFEA580C),
        secondaryLightColor = Color(0xFFFED7AA),
        previewGradient = listOf(Color(0xFFB45309), Color(0xFFEA580C))
    ),
    LAVENDER_PURPLE(
        titleAr = "الخزامى الملكية",
        descriptionAr = "زهر الخزامى والبنفسج السعودي",
        emoji = "🌸",
        primaryColor = Color(0xFF7C3AED),
        primaryLightColor = Color(0xFFA78BFA),
        secondaryColor = Color(0xFFC026D3),
        secondaryLightColor = Color(0xFFF5D0FE),
        previewGradient = listOf(Color(0xFF7C3AED), Color(0xFFC026D3))
    ),
    MIDNIGHT_ONYX(
        titleAr = "الأسود الماسي",
        descriptionAr = "الليل الحالك مع توهج النيون الفخم",
        emoji = "🌑",
        primaryColor = Color(0xFF10B981),
        primaryLightColor = Color(0xFF6EE7B7),
        secondaryColor = Color(0xFF6366F1),
        secondaryLightColor = Color(0xFFC7D2FE),
        previewGradient = listOf(Color(0xFF18181B), Color(0xFF10B981))
    )
}

enum class AppDesignStyle(
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val emoji: String,
    val id: String
) {
    STANDARD(
        titleAr = "واجهة سامسونج One UI",
        titleEn = "Standard One UI Style",
        descriptionAr = "الواجهة القياسية المتوازنة والناعمة ومريحة للأعين",
        descriptionEn = "Standard balanced soft interface, comfortable for eyes",
        emoji = "📱",
        id = "standard"
    ),
    MINIMALISM(
        titleAr = "مينيماليزم (مبسط وأنيق)",
        titleEn = "Minimalism Clean Style",
        descriptionAr = "مظهر فائق النقاء، مساحات واسعة وبدون تداخلات مرئية",
        descriptionEn = "Ultra clean style, generous spacing, thin minimalist styling",
        emoji = "⚪",
        id = "minimalism"
    ),
    FLAT_DESIGN(
        titleAr = "فلات ديزاين (مسطح وبسيط)",
        titleEn = "Flat Design Classic",
        descriptionAr = "مظهر حديث ومسطح بالكامل، بدون تدرجات أو ظلال",
        descriptionEn = "Classic flat styling, zero gradients, zero shadows",
        emoji = "🎨",
        id = "flat"
    ),
    NEOBRUTALISM(
        titleAr = "نيوبروتاليزم (جريء ومتباين)",
        titleEn = "Neobrutalism Bold",
        descriptionAr = "حدود داكنة سميكة، ظلال حادة سوداء، ومظهر ريادي قوي",
        descriptionEn = "Thick dark borders, hard solid black shadows, extremely high contrast",
        emoji = "⚡",
        id = "neobrutalism"
    );

    fun localizedTitle(isEnglish: Boolean): String = if (isEnglish) titleEn else titleAr
    fun localizedDescription(isEnglish: Boolean): String = if (isEnglish) descriptionEn else descriptionAr
}

package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================================
// نظام تصميم فهد للذكاء الاصطناعي 2027 (Fahad AI Modern Design System 2027)
// تصميم عالمي مبني على Material Design 3 ومتوافق مع أحدث معايير Compose 2027
// ============================================================================

/**
 * 1️⃣ كائن الألوان: FahadColors2027
 * يحتوي على باليتات النهار (Light) والليل (Dark) المحددة بدقة
 */
object FahadColors2027 {

    // 🌅 Light Mode (النهار): إحساس ترابي فاخر ومريح للعين
    object Light {
        val Background = Color(0xFFF3F0EB)      // خلفية أساسية ترابية ناعمة
        val Surface1 = Color(0xFFFFFFFF)        // سطح 1 (بطاقات ناصعة البياض)
        val Surface2 = Color(0xFFFAF8F5)        // سطح 2 (عناصر مرتفعة وقوائم فرعية)

        // الألوان الدلالية
        val Primary = Color(0xFF1B4332)         // أخضر عميق فاخر
        val Secondary = Color(0xFFD4A373)       // ذهبي دافئ أصيل
        val Accent = Color(0xFFFF6B35)          // برتقالي حي للتأكيدات
        val Success = Color(0xFF52B788)         // أخضر فاتح للنجاح والجاهزية
        val Warning = Color(0xFFFFB703)         // ذهبي حي للتنبيهات
        val Error = Color(0xFFE63946)           // أحمر حي للعمليات الحرجة

        // النصوص والمساعدات
        val TextPrimary = Color(0xFF1A1A1A)     // نصوص داكنة صريحة وعالية التباين
        val TextSecondary = Color(0xFF8B8B8B)   // نصوص مساعدة رمادية كلاسيكية
        val Outline = Color(0x0F000000)         // إطار فائق النعومة (6% شفافية)
    }

    // 🌙 Dark Mode (الليل): تباين فائق وعمق بصري مع لمسات نيون هادئة
    object Dark {
        val Background = Color(0xFF0D0D0D)      // خلفية ليلية حالكة عميقة
        val Surface1 = Color(0xFF1A1A1A)        // سطح 1 (بطاقات ليلية)
        val Surface2 = Color(0xFF252525)        // سطح 2 (عناصر مرتفعة)

        // الألوان الدلالية
        val Primary = Color(0xFF52B788)         // أخضر فاتح حي وبارز
        val Secondary = Color(0xFFFFB703)       // ذهبي حي متألق
        val Accent = Color(0xFFFF6B35)          // برتقالي حي متوهج
        val Success = Color(0xFF2D6A4F)         // أخضر عميق زمردي
        val Warning = Color(0xFFFFB703)         // ذهبي تحذيري متوهج
        val Error = Color(0xFFFF6B9D)           // وردي حي ناعم للتحذيرات
        
        // النصوص والمساعدات
        val TextPrimary = Color(0xFFFFFFFF)     // نصوص بيضاء ناصعة
        val TextSecondary = Color(0xFFB0B0B0)   // نصوص مساعدة رمادية فاتحة واضحة
        val Outline = Color(0xFF2E2E2E)         // إطار داكن محدد بدقة
    }

    /**
     * دالة مساعدة للحصول على اللون الأساسي حسب وضع العرض
     */
    @Composable
    fun primary(isDark: Boolean): Color = if (isDark) Dark.Primary else Light.Primary

    @Composable
    fun background(isDark: Boolean): Color = if (isDark) Dark.Background else Light.Background

    @Composable
    fun surface(isDark: Boolean): Color = if (isDark) Dark.Surface1 else Light.Surface1

    @Composable
    fun textPrimary(isDark: Boolean): Color = if (isDark) Dark.TextPrimary else Light.TextPrimary

    @Composable
    fun textSecondary(isDark: Boolean): Color = if (isDark) Dark.TextSecondary else Light.TextSecondary
}

/**
 * 3️⃣ كائن التدرجات اللونية الحديثة: Gradients2027
 * 5 تدرجات خطية حديثة + تدرج زجاجي Glassmorphism + تدرج إشعاعي
 */
object Gradients2027 {
    // 1. Gradient أساسي (أخضر عميق → ذهبي دافئ)
    val Primary = Brush.linearGradient(
        colors = listOf(Color(0xFF1B4332), Color(0xFFD4A373))
    )

    // 2. Gradient دافئ (برتقالي حي → أحمر حي)
    val Warm = Brush.linearGradient(
        colors = listOf(Color(0xFFFF6B35), Color(0xFFE63946))
    )

    // 3. Gradient بارد (أزرق ذكي → بنفسجي عصري)
    val Cool = Brush.linearGradient(
        colors = listOf(Color(0xFF4895EF), Color(0xFF9D4EDD))
    )

    // 4. Gradient غروب (ذهبي حي → وردي متوهج)
    val Sunset = Brush.linearGradient(
        colors = listOf(Color(0xFFFFB703), Color(0xFFFF006E))
    )

    // 5. Gradient طبيعي (أخضر حي → أزرق سماوي)
    val Nature = Brush.linearGradient(
        colors = listOf(Color(0xFF52B788), Color(0xFF4895EF))
    )

    // تدرج زجاجي نصف شفاف (Glassmorphism 2027)
    fun glass(isDark: Boolean): Brush {
        return if (isDark) {
            Brush.linearGradient(
                listOf(
                    Color(0xFF252525).copy(alpha = 0.85f),
                    Color(0xFF1A1A1A).copy(alpha = 0.65f)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    Color(0xFFFFFFFF).copy(alpha = 0.92f),
                    Color(0xFFFAF8F5).copy(alpha = 0.75f)
                )
            )
        }
    }

    // تدرج هالة ضوئية إشعاعية Radial Glow
    fun radialAura(centerColor: Color, edgeColor: Color = Color.Transparent): Brush {
        return Brush.radialGradient(
            colors = listOf(centerColor.copy(alpha = 0.35f), edgeColor)
        )
    }
}

/**
 * 4️⃣ كائن الأشكال والانحناءات: Shapes2027
 * يتبع المعايير الدقيقة: 8dp, 12dp, 16dp, 32dp, Pill
 */
object Shapes2027 {
    val Small = RoundedCornerShape(8.dp)        // للأزرار الصغيرة والشارات والملصقات
    val Medium = RoundedCornerShape(12.dp)      // للبطاقات وحقول الإدخال والعناصر التفاعلية
    val Large = RoundedCornerShape(16.dp)       // للبطاقات الرئيسية والشاشات الفرعية
    val ExtraLarge = RoundedCornerShape(32.dp)  // للحاويات الكبيرة والصفحات السفلية
    val Pill = RoundedCornerShape(percent = 50) // أزرار الكبسولة الدائرية بالكامل
}

/**
 * 5️⃣ كائن التايبوغرافي والخطوط: Typography2027
 * تسلسل هرمي مقاس بـ sp وبأوزان محددة (300, 400, 500, 700)
 */
object Typography2027 {
    // العناوين الرئيسية الكبرى (Bold: 700)
    val DisplayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    )

    // العناوين الثانوية (Bold: 700)
    val DisplayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.25).sp
    )

    // عناوين الأقسام (Bold: 700)
    val Headline = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    )

    // عناوين المحتوى (Medium: 500)
    val TitleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )

    // العناوين الفرعية (Medium: 500)
    val TitleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )

    // النصوص الأساسية (Regular: 400)
    val BodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )

    // النصوص العادية (Regular: 400)
    val BodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )

    // النصوص الصغيرة (Light: 300)
    val BodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )

    // الملصقات والشارات (Regular: 400)
    val Label = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
}

/**
 * 6️⃣ كائن المسافات المحسوبة: Spacing2027
 * نظام شبكي مدروس للمحاذاة والحشو (Padding & Spacing)
 */
object Spacing2027 {
    val XSmall = 4.dp    // مسافات دقيقة بين الأيقونة والنص
    val Small = 8.dp     // مسافات الحشو الداخلي للأزرار المصغرة
    val Medium = 12.dp   // المسافة القياسية بين العناصر المترابطة
    val Large = 16.dp    // حشو البطاقات القياسي وهوامش الشاشة
    val XLarge = 24.dp   // الفواصل بين الأقسام الرئيسية
    val XXLarge = 32.dp  // المسافات الكبيرة بين الكتل البصرية
}

/**
 * 2️⃣ Theme Composable: FahadTheme2027
 * ثيم موحد يعتمد مواصفات Material Design 3 للوضعين النهاري والليلي
 */
@Composable
fun FahadTheme2027(
    isDark: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = FahadColors2027.Dark.Primary,
            onPrimary = Color.Black,
            primaryContainer = FahadColors2027.Dark.Primary.copy(alpha = 0.22f),
            onPrimaryContainer = Color.White,
            secondary = FahadColors2027.Dark.Secondary,
            onSecondary = Color.Black,
            secondaryContainer = FahadColors2027.Dark.Secondary.copy(alpha = 0.18f),
            onSecondaryContainer = Color.White,
            tertiary = FahadColors2027.Dark.Accent,
            background = FahadColors2027.Dark.Background,
            onBackground = FahadColors2027.Dark.TextPrimary,
            surface = FahadColors2027.Dark.Surface1,
            onSurface = FahadColors2027.Dark.TextPrimary,
            surfaceVariant = FahadColors2027.Dark.Surface2,
            onSurfaceVariant = FahadColors2027.Dark.TextSecondary,
            outline = FahadColors2027.Dark.Outline,
            error = FahadColors2027.Dark.Error
        )
    } else {
        lightColorScheme(
            primary = FahadColors2027.Light.Primary,
            onPrimary = Color.White,
            primaryContainer = FahadColors2027.Light.Primary.copy(alpha = 0.12f),
            onPrimaryContainer = FahadColors2027.Light.Primary,
            secondary = FahadColors2027.Light.Secondary,
            onSecondary = Color.White,
            secondaryContainer = FahadColors2027.Light.Secondary.copy(alpha = 0.14f),
            onSecondaryContainer = FahadColors2027.Light.Primary,
            tertiary = FahadColors2027.Light.Accent,
            background = FahadColors2027.Light.Background,
            onBackground = FahadColors2027.Light.TextPrimary,
            surface = FahadColors2027.Light.Surface1,
            onSurface = FahadColors2027.Light.TextPrimary,
            surfaceVariant = FahadColors2027.Light.Surface2,
            onSurfaceVariant = FahadColors2027.Light.TextSecondary,
            outline = FahadColors2027.Light.Outline,
            error = FahadColors2027.Light.Error
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// ============================================================================
// 7️⃣ مثالين عمليين يطبقان نظام Fahad AI 2027 (Production Ready Components)
// ============================================================================

/**
 * 💡 مثال عملي 1: بطاقة الترحيب والذكاء الاصطناعي (FahadHeroAiCard2027)
 * تطبق:
 * - التدرج الأساسي Gradients2027.Primary
 * - تأثير Glassmorphism الزجاجي الشفاف
 * - زوايا Shapes2027.Large و Shapes2027.Pill
 * - تايبوغرافي Typography2027
 * - مسافات Spacing2027 المحسوبة
 * - أنيميشن سبرينغ تفاعلي سلس عند الضغط
 */
@Composable
fun FahadHeroAiCard2027(
    title: String = "محرك فهد للذكاء الاصطناعي 2027",
    subtitle: String = "الجيل القادم من الحوسبة الذكية والمعالجة الفورية الفائقة",
    statusText: String = "متصل وسريع",
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // أنيميشن تفاعلي سلس للمقياس
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "hero_card_scale"
    )

    Surface(
        shape = Shapes2027.Large,
        color = if (isDark) FahadColors2027.Dark.Surface1 else FahadColors2027.Light.Surface1,
        shadowElevation = if (isDark) 4.dp else 2.dp,
        border = BorderStroke(
            1.dp,
            if (isDark) FahadColors2027.Dark.Outline else FahadColors2027.Light.Outline
        ),
        modifier = modifier
            .fillMaxWidth()
            .scale(animatedScale)
    ) {
        Column(
            modifier = Modifier
                .background(Gradients2027.glass(isDark))
                .padding(Spacing2027.Large),
            verticalArrangement = Arrangement.spacedBy(Spacing2027.Medium)
        ) {
            // شريط العنوان والشارة
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing2027.Medium)
                ) {
                    // أيقونة متدرجة بزوايا 12dp
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(Shapes2027.Medium)
                            .background(Gradients2027.Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "أيقونة الذكاء الاصطناعي",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = Typography2027.TitleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) FahadColors2027.Dark.TextPrimary else FahadColors2027.Light.TextPrimary
                        )
                        Text(
                            text = subtitle,
                            style = Typography2027.BodySmall,
                            color = if (isDark) FahadColors2027.Dark.TextSecondary else FahadColors2027.Light.TextSecondary
                        )
                    }
                }

                // شارة الحالة (Pill Shape + Success Color)
                val successColor = if (isDark) FahadColors2027.Dark.Success else FahadColors2027.Light.Success
                Surface(
                    shape = Shapes2027.Pill,
                    color = successColor.copy(alpha = 0.16f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing2027.Small, vertical = Spacing2027.XSmall),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing2027.XSmall)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(successColor)
                        )
                        Text(
                            text = statusText,
                            style = Typography2027.Label,
                            fontWeight = FontWeight.Bold,
                            color = successColor
                        )
                    }
                }
            }

            // زر العمل الأساسي التفاعلي
            Button(
                onClick = onActionClick,
                interactionSource = interactionSource,
                shape = Shapes2027.Medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) FahadColors2027.Dark.Primary else FahadColors2027.Light.Primary
                ),
                contentPadding = PaddingValues(vertical = Spacing2027.Small, horizontal = Spacing2027.Large),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing2027.Small)
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "بدء جلسة ذكاء اصطناعي جديدة",
                        style = Typography2027.BodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * 💡 مثال عملي 2: بطاقة المؤشرات والعمليات السريعة (FahadActionMetricCard2027)
 * تطبق:
 * - تدرجات متنوعة (Warm / Sunset / Cool)
 * - أنظمة التباعد الدقيقة Spacing2027
 * - الخطوط المعتمدة بدقة 13sp و 16sp
 * - استجابة فورية للضغط بتأثير مادي ناعم
 */
@Composable
fun FahadActionMetricCard2027(
    icon: ImageVector = Icons.Default.Speed,
    title: String,
    value: String,
    metricUnit: String = "ملي/ثانية",
    gradient: Brush = Gradients2027.Warm,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Surface(
        onClick = onClick,
        shape = Shapes2027.Medium,
        color = if (isDark) FahadColors2027.Dark.Surface2 else FahadColors2027.Light.Surface2,
        border = BorderStroke(
            1.dp,
            if (isDark) FahadColors2027.Dark.Outline else FahadColors2027.Light.Outline
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing2027.Large, vertical = Spacing2027.Medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing2027.Medium)
        ) {
            // صندوق الأيقونة المتدرجة
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(Shapes2027.Small)
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // التفاصيل النصية
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = Typography2027.BodyMedium,
                    color = if (isDark) FahadColors2027.Dark.TextSecondary else FahadColors2027.Light.TextSecondary
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(Spacing2027.XSmall)
                ) {
                    Text(
                        text = value,
                        style = Typography2027.TitleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) FahadColors2027.Dark.TextPrimary else FahadColors2027.Light.TextPrimary
                    )
                    Text(
                        text = metricUnit,
                        style = Typography2027.Label,
                        color = if (isDark) FahadColors2027.Dark.TextSecondary else FahadColors2027.Light.TextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            // سهم التنقل التوجيهي
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "انتقال",
                tint = if (isDark) FahadColors2027.Dark.TextSecondary else FahadColors2027.Light.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

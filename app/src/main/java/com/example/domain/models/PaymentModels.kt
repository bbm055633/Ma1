package com.example.domain.models

enum class SaudiPaymentMethod(
    val id: String,
    val titleAr: String,
    val subtitleAr: String,
    val badgeColorHex: Long
) {
    MADA("mada", "مدى (Mada)", "بطاقة الخصم المباشر السعودية", 0xFF00835B),
    APPLE_PAY("apple_pay", "Apple Pay", "دفع فوري وآمن بلمسة واحدة", 0xFF1C1C1E),
    STC_PAY("stc_pay", "stc pay", "المحفظة الرقمية الرائدة في المملكة", 0xFF4F008C),
    CREDIT_CARD("card", "بطاقة ائتمانية", "Visa / MasterCard", 0xFF1A365D),
    SARIE("sarie", "سريع (SARIE)", "التحويل البنكي الفوري المحلي", 0xFFDD6B20)
}

data class TopUpPackage(
    val id: String,
    val points: Int,
    val priceSar: Double,
    val bonusBadge: String? = null,
    val isPopular: Boolean = false
)

data class SubscriptionPlan(
    val id: String,
    val titleAr: String,
    val priceSar: Double,
    val periodAr: String,
    val isCurrent: Boolean = false,
    val badgeAr: String? = null,
    val features: List<String>
)

data class PaymentTransaction(
    val id: String,
    val title: String,
    val amountSar: Double,
    val pointsAdded: Int,
    val paymentMethodName: String,
    val dateFormatted: String,
    val status: String = "ناجحة ✅",
    val vatAmountSar: Double = amountSar * 0.15 / 1.15
)

object PaymentDefaults {

    val TOP_UP_PACKAGES = listOf(
        TopUpPackage(
            id = "pkg_basic",
            points = 1000,
            priceSar = 49.0,
            bonusBadge = null
        ),
        TopUpPackage(
            id = "pkg_growth",
            points = 3500,
            priceSar = 149.0,
            bonusBadge = "+500 نقطة بونص مجاناً",
            isPopular = true
        ),
        TopUpPackage(
            id = "pkg_enterprise",
            points = 8000,
            priceSar = 299.0,
            bonusBadge = "+1,500 نقطة بونص مجاناً"
        ),
        TopUpPackage(
            id = "pkg_unlimited",
            points = 20000,
            priceSar = 699.0,
            bonusBadge = "+4,000 نقطة بونص VIP"
        )
    )

    val SUBSCRIPTION_PLANS = listOf(
        SubscriptionPlan(
            id = "plan_free",
            titleAr = "الباقة الأساسية (Free)",
            priceSar = 0.0,
            periodAr = "مجاناً دائماً",
            features = listOf(
                "وصول محدود لـ 3 وكلاء أعمال",
                "مواقيت الصلاة وتقويم أم القرى",
                "100 نقطة ذكية شهرياً"
            )
        ),
        SubscriptionPlan(
            id = "plan_pro",
            titleAr = "باقة المحترفين (Pro)",
            priceSar = 79.0,
            periodAr = "شهرياً",
            badgeAr = "الأكثر طلباً",
            features = listOf(
                "وصول لجميع الوكلاء الـ 9 المتخصصين",
                "توليد الفواتير ومطابقة ZATCA",
                "2,500 نقطة ذكية شهرياً",
                "الوضع الصامت التلقائي قبل الصلاة"
            )
        ),
        SubscriptionPlan(
            id = "plan_enterprise",
            titleAr = "باقة الأعمال والشركات (Enterprise Pro)",
            priceSar = 199.0,
            periodAr = "شهرياً",
            isCurrent = true,
            badgeAr = "باقتك الحالية 👑",
            features = listOf(
                "استخدام غير محدود لكافة الوكلاء الـ 9",
                "توليد كافة المنتجات والأصول الرقمية الـ 12",
                "10,000 نقطة ذكية شهرياً مع أولوية المعالجة",
                "ربط الفواتير بأنظمة الدفع والضرائب",
                "دعم فني مخصص على مدار الساعة 24/7"
            )
        ),
        SubscriptionPlan(
            id = "plan_annual",
            titleAr = "الباقة السنوية الموفّرة",
            priceSar = 1490.0,
            periodAr = "سنوياً (وفر شهرين مجاناً)",
            badgeAr = "وفر 25% 🏷️",
            features = listOf(
                "كافة مزايا باقة الأعمال Enterprise",
                "وفر شهرين مجاناً مع هدية 30,000 نقطة ذكية",
                "جلسة استشارية تنفيذية مجانية"
            )
        )
    )

    val INITIAL_TRANSACTIONS = listOf(
        PaymentTransaction(
            id = "TXN-ZHN-98214",
            title = "شحن محفظة النقاط الذكية (باقة النمو)",
            amountSar = 149.0,
            pointsAdded = 3500,
            paymentMethodName = "مدى (Mada) •••• 4210",
            dateFormatted = "28 سبتمبر 2026، 02:45 م"
        ),
        PaymentTransaction(
            id = "TXN-ZHN-95103",
            title = "تجديد اشتراك باقة الأعمال Enterprise Pro",
            amountSar = 199.0,
            pointsAdded = 10000,
            paymentMethodName = "Apple Pay •••• 8829",
            dateFormatted = "01 سبتمبر 2026، 10:15 ص"
        ),
        PaymentTransaction(
            id = "TXN-ZHN-91048",
            title = "شحن محفظة النقاط الذكية (الباقة الأساسية)",
            amountSar = 49.0,
            pointsAdded = 1000,
            paymentMethodName = "stc pay",
            dateFormatted = "15 أغسطس 2026، 06:20 م"
        )
    )
}

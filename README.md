# 🌟 Fahad-Ai | المساعد التنفيذي والروحي الذكي

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-informational.svg)](https://developer.android.com/about/dashboards)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36%20(Android%2015)-success.svg)](https://developer.android.com/about/versions/15)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 📖 نظرة عامة | Overview

### 🇸🇦 بالعربية
**Fahad-Ai** هو تطبيق أندرويد تنفيذي سعودي متقدم يدمج بين قوة الذكاء الاصطناعي (Google Gemini)، إدارة الأعمال والأجندة اليومية، ونمط الحياة الروحي الملتزم بمواقيت الصلاة في المملكة العربية السعودية. يتميز التطبيق بتصميم عصري مستقبلي متوافق مع معايير **Material Design 3 (2027)**، وخلفية حية ديناميكية تفاعلية بعنوان **"ليلة مرصعة بالنجوم"** (Starry Night Live Sky)، مع الحفاظ على أعلى معايير الخصوصية والأمان وحماية البيانات المحلية.

### 🇬🇧 In English
**Fahad-Ai** is an enterprise-grade Saudi AI executive assistant and spiritual companion application for Android. Powered by Google Gemini AI, it seamlessly unifies daily business tasks, dual Hijri/Gregorian calendars, and structured Islamic worship tracking tailored to Saudi prayer timings. Featuring a futuristic **Material Design 3 (2027)** visual system, an interactive **Starry Night Live Sky** background, and strict zero-trust security and offline-first persistence.

---

## ✨ المميزات الرئيسية | Key Features

* 🤖 **وكلاء الذكاء الاصطناعي المتخصصون (AI Agents Hub):**
  * سكرتير تنفيذي، مستشار أعمال، محاسب زكاة وضريبة (ZATCA 15%)، مستشار موارد بشرية، مستشار مالي، وخبير قانوني سعودي.
* 🕌 **رحلة العبادات والأذكار المتكاملة (Faith & Worship Journey):**
  * تتبع الصلوات الخمس والسنن المؤكدة (مسجد، بيت، قضاء) مع إحصائيات المواظبة.
  * بطاقات منفصلة لأذكار الصباح والمساء، قيام الليل، وصلاة الضحى مع عداد تسبيح رقمي.
  * بوصلة دقيقة للقبلة وتتبع صيام الأيام البيض والاثنين والخميس.
  * تنبيهات أذان ذكية، وتفعيل الوضع الصامت التلقائي للجهاز أثناء فترة الصلاة واستعادته بعدها.
* 📅 **التقويم المزدوج والأجندة الذكية (Dual Calendar & Smart Agenda):**
  * تقويم هجري وميلادي متزامن مع أحداث الرواتب، مواعيد العمل، والمناسبات الرسمية.
  * إدارة المهام والملاحظات والاجتماعات مع كشف التعارض الزمني مع أوقات الصلاة.
* 🌌 **هوية وتصميم 2027 (Futuristic M3 & Live Starry Theme):**
  * محرك سماء تفاعلي حي (نجوم متلألئة، شهب، قمر، ضباب، وتوهج أفق).
  * 5 باليتات لونية ملكية (Emerald, Saudi Gold, Vibrant Orange, Royal Ocean, Modern Purple).
  * زجاجية ناعمة (Glassmorphism)، وضع ليلي/نهاري كامل، وتحكم في شفافية البطاقات.
* 🔒 **الأمان والخصوصية (Enterprise Security):**
  * إرسال مفاتيح الـ API بأمان تام عبر Headers مشفرة (`x-goog-api-key`) مع نظام Rate Limiting بحد أقصى 5 طلبات/دقيقة.
  * دعم ProGuard/R8 وحذف الـ Logs غير الآمنة في بيئة الإنتاج (`Release`).
  * قاعدة بيانات محلية مشفرة عبر Room دون تسريب بيانات الاعتماد.

---

## 🛠️ التقنيات والمكتبات المستخدمة | Tech Stack

| المجال | التقنية المستخدمة |
| :--- | :--- |
| **لغة البرمجة** | Kotlin 2.2.10 (Kotlin Coroutines & Flow) |
| **واجهة المستخدم** | Jetpack Compose (Material Design 3) |
| **معمارية الكود** | MVVM / Clean Architecture |
| **محرك الذكاء الاصطناعي** | Google Gemini API (v1beta/gemini-3.5-flash) |
| **الشبكات والتسلسل** | Retrofit 2.12.0 + OkHttp 4.10.0 + Moshi |
| **قاعدة البيانات المحلية** | Room Database 2.7.0 (مع KSP) |
| **إدارة المهام الخلفية** | WorkManager 2.10.0 (Pre-Alerts & Silent Mode) |
| **الرصد والتحليلات** | Firebase Crashlytics & Firebase Analytics (BOM 34.17.0) |
| **إدارة الأسرار** | Secrets Gradle Plugin + BuildConfig |
| **تحميل الصور** | Coil Compose 2.7.0 |

---

## 📋 المتطلبات التقنية | Prerequisites

قبل البدء في تشغيل المشروع، تأكد من توفر الأدوات التالية:

* **Android Studio:** إصدار Ladybug (2024.2.1) أو أحدث.
* **JDK:** Java Development Kit (JDK 11 أو JDK 17 أو JDK 21).
* **Android SDK:**
  * `minSdk`: 24 (Android 7.0 Nougat)
  * `targetSdk` / `compileSdk`: 36 (Android 15)
* **Gradle:** الإصدار 8.9+ (مُضمن تلقائياً عبر البيئة).
* **Gemini API Key:** مفتاح صالح من [Google AI Studio](https://aistudio.google.com).

---

## 🚀 خطوات التثبيت والتشغيل | Getting Started

### 1. استنساخ المشروع (Clone Repository)
```bash
git clone https://github.com/your-username/fahad-ai.git
cd fahad-ai
```

### 2. إعداد مفتاح الـ API (Configure Environment)
أنشئ ملفاً باسم `.env` في المجلد الرئيسي للمشروع (Root Directory) بجوار ملف `.env.example`:

```bash
cp .env.example .env
```

افتح ملف `.env` وأضف مفتاح Gemini الخاص بك:
```properties
GEMINI_API_KEY=AIzaSyYourActualGeminiApiKeyHere
```

> ⚠️ **ملاحظة أمنية:** ملف `.env` مستثنى تلقائياً من الـ Git (`.gitignore`) لضمان عدم تسريب المفاتيح البرمجية إلى المستودعات العامة.

### 3. فتح وبناء المشروع في Android Studio
1. افتح **Android Studio** واختر **Open Existing Project**.
2. حدد مجلد المشروع وانتظر اكتمال مزامنة Gradle (Gradle Sync).
3. اختر جهاز تشغيل أو محاكي (Emulator بنظام Android 10+ يُفضّل لتجربة الثيم الحي).
4. اضغط على زر **Run** (`Shift + F10`) أو نفّذ الأمر التالي عبر الطرفية:

```bash
gradle assembleDebug
```

---

## 📂 هيكل المجلدات | Directory Structure

```text
fahad-ai/
├── app/
│   ├── build.gradle.kts                # إعدادات تطبيق أندرويد والاعتماديات و R8/ProGuard
│   ├── proguard-rules.pro              # قواعد حماية الكود وتشفيره لـ Room, Retrofit, Moshi
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml     # ملف البيان الرسمي والصلاحيات المحمية
│       │   └── java/com/example/
│       │       ├── MainActivity.kt     # نقطة الدخول الرئيسية وتوجيه الشاشات
│       │       ├── data/
│       │       │   ├── local/          # طبقة التخزين المحلي (Room Entities & DAOs)
│       │       │   ├── remote/         # واجهات GeminiApiService و DTOs و Interceptors
│       │       │   └── repository/     # مستودعات البيانات مع Rate Limiting و Backoff
│       │       ├── domain/
│       │       │   ├── models/         # نماذج الأعمال والبيانات
│       │       │   └── prayer/         # خوارزميات حساب مواقيت الصلاة والقبلة
│       │       ├── service/            # عمال الخلفية (WorkManager) والوضع الصامت
│       │       └── ui/
│       │           ├── components/     # المكونات المشتركة (أشرطة التنقل، البوصلة)
│       │           ├── screens/
│       │           │   ├── auth/       # شاشة تسجيل الدخول الموثوقة
│       │           │   ├── home/       # الشاشة الرئيسية والمساعد اليومي
│       │           │   ├── agents/     # وكلاء الأعمال والذكاء الاصطناعي
│       │           │   ├── calendar/   # التقويم المزدوج وإدارة الأجندة
│       │           │   ├── worship/    # شاشات وبطاقات العبادات والأذكار المقسمة
│       │           │   ├── theme/      # شاشة تخصيص ليلة مرصعة بالنجوم
│       │           │   └── menu/       # الإعدادات الموحدة والشاملة
│       │           ├── theme/          # نظام الألوان 2027 والتيبوغرافي والأشكال
│       │           └── viewmodel/      # نموذج العرض الموحد (MainViewModel)
│       └── test/                       # اختبارات الوحدة واختبارات Robolectric
├── gradle/
│   └── libs.versions.toml              # الكتالوج الموحد لإصدارات المكتبات (Version Catalog)
├── .env.example                        # نموذج إعداد المتغيرات البيئية
├── build.gradle.kts                    # إعدادات الـ Plugins على المستوى الرئيسي
├── settings.gradle.kts                 # ضبط مستودعات التبعيات وتسمية المشروع
└── README.md                           # التوثيق الشامل للمشروع
```

---

## 🛡️ سياسات الأمان والخصوصية | Security & Integrity

* **انعدام البيانات الصريحة (No Hardcoded Secrets):** لا يحتوي الكود على أي مفاتيح أو بريد أو كلمات مرور ثابتة.
* **إدارة الاتصال الآمن (Encrypted Header Transmission):** يتم حقن مفتاح Gemini عبر الترويسة القياسية `x-goog-api-key` بدلاً من Query Parameters لتجنب ظهوره في سجلات الموجهات أو الـ Proxy.
* **حماية الإطلاق (R8 / Minification Enabled):** تم تفعيل تقليص الكود وحذف الـ Resources غير المستخدمة مع حماية الكيانات ضد العبث.
* **إلغاء التتبع في الإنتاج:** يتم تعطيل `HttpLoggingInterceptor` تلقائياً في نسخ الإصدار (`Release`) لمنع طباعة محتوى الطلبات.
* **احترام الصلاحيات الصارمة (Least-Privilege):** التطبيق لا يطلب صلاحيات الميكروفون أو الموقع الجغرافي العريض غير الضرورية، وتتم استعادة وضع صوت الهاتف الفعلي بدقة فور انقضاء وقت الصلاة.

---

## 📄 الترخيص | License

هذا المشروع مرخّص بموجب رخصة **MIT**. راجع نصوص الترخيص أدناه للاستخدام الحر والمفتوح:

```text
MIT License

Copyright (c) 2026 Fahad-Ai Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<p align="center">
  صُنع بإتقان وشغف لتجربة رقمية سعودية ذكية متكاملة 🇸🇦 • <b>Fahad-Ai 2027</b>
</p>

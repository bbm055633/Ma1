package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CalendarEventEntity
import com.example.data.local.ExpenseEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TaskEntity
import com.example.data.local.PrayerEntity
import com.example.data.local.UserEntity
import com.example.data.repository.GeminiRepository
import com.example.data.repository.ZheenRepository
import com.example.domain.models.AdhkarItem
import com.example.domain.models.AgentDataDefaults
import com.example.domain.models.AgentInfo
import com.example.domain.models.PaymentDefaults
import com.example.domain.models.PaymentTransaction
import com.example.domain.models.SaudiPaymentMethod
import com.example.domain.models.SubscriptionPlan
import com.example.domain.models.TopUpPackage
import com.example.domain.models.ProductServiceItem
import com.example.domain.models.QuranKhatmahState
import com.example.domain.prayer.CityLocation
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.domain.prayer.PrayerTimes
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.AppDesignStyle
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface ActiveScreen {
    data object MainTabs : ActiveScreen
    data class AgentChat(val agentId: String) : ActiveScreen
    data class ProductGenerator(val productId: String) : ActiveScreen
    data object Profile : ActiveScreen
    data object Worship : ActiveScreen
    data object PaymentHistory : ActiveScreen
    data object AgentSettings : ActiveScreen
    data object PermissionsSecurity : ActiveScreen
    data class AdhkarSession(val isMorning: Boolean) : ActiveScreen
    data object MobeenAiSpecSheet : ActiveScreen
    data object FullScreenMenu : ActiveScreen
    data object StarryNightCustomizer : ActiveScreen
    data object Login : ActiveScreen
}

data class UiChatMessage(
    val id: Long = System.currentTimeMillis(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SkyColorPreset(val titleAr: String, val titleEn: String, val hexColors: List<Long>) {
    AUTO("تلقائي (يتغير مع الوقت)", "Auto (Time/Prayer)", listOf(0xFF0F172AL, 0xFF1E1B4BL, 0xFF2E1065L)),
    FOLLOW_DAY("يتبع وقت اليوم", "Follow Day", listOf(0xFF1E293BL, 0xFF0F172AL, 0xFF020617L)),
    DUSK("الغسق", "Dusk", listOf(0xFF1E1B4BL, 0xFF4C1D95L, 0xFF831843L)),
    EMBER("الجمر", "Ember", listOf(0xFF450A0AL, 0xFF7F1D1DL, 0xFF991B1BL)),
    TWILIGHT("الشفق", "Twilight", listOf(0xFF064E3BL, 0xFF047857L, 0xFF0D9488L)),
    OCEAN("المحيط", "Ocean", listOf(0xFF082F49L, 0xFF0284C7L, 0xFF38BDF8L)),
    PINK_SUNSET("غروب وردي", "Pink Sunset", listOf(0xFF701A75L, 0xFFBE185DL, 0xFFFB7185L)),
    LAVENDER("اللافندر", "Lavender", listOf(0xFF4338CAL, 0xFF6366F1L, 0xFFA5B4FCL))
}

enum class TerrainType(val titleAr: String, val titleEn: String) {
    NONE("بدون", "None"),
    HILLS("تلال", "Hills"),
    PEAKS("قمم", "Peaks"),
    DUNES("كثبان", "Dunes"),
    FOREST("غابة", "Forest")
}

data class StarryNightSettings(
    val isEnabled: Boolean = false,
    val cardTransparency: Float = 0.88f,
    val animationSpeedMultiplier: Float = 1.0f,
    val skyColorPreset: SkyColorPreset = SkyColorPreset.AUTO,
    val terrainType: TerrainType = TerrainType.FOREST,
    val showStars: Boolean = true,
    val showLightning: Boolean = false,
    val showShootingStars: Boolean = true,
    val showMoon: Boolean = true,
    val showClouds: Boolean = true,
    val showBirds: Boolean = false,
    val showFireflies: Boolean = true,
    val showSnow: Boolean = false,
    val showHorizonGlow: Boolean = true,
    val batterySaverMode: Boolean = false
)

data class AppThemeSettings(
    val isDarkMode: Boolean = true,
    val themePalette: AppThemePalette = AppThemePalette.SAUDI_EMERALD,
    val fontScale: Float = 1.0f,
    val densityScale: Float = 1.0f,
    val customWallpaperUri: String? = null,
    val wallpaperDimAlpha: Float = 0.65f,
    val designStyle: AppDesignStyle = AppDesignStyle.STANDARD
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = ZheenRepository(database)
    private val geminiRepo = GeminiRepository()
    val prayerService = com.example.service.PrayerTimeService.getInstance(application)

    // Navigation state
    private val _currentTab = MutableStateFlow(0) // 0..4
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _activeScreen = MutableStateFlow<ActiveScreen>(ActiveScreen.Login)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    private val _userName = MutableStateFlow("فهد بن محمد القحطاني")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("fahad.alqahtani@enterprise.sa")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    // Notification & DND Settings
    private val _notificationSound = MutableStateFlow("default")
    val notificationSound: StateFlow<String> = _notificationSound.asStateFlow()

    private val _isDndDuringWorship = MutableStateFlow(true)
    val isDndDuringWorship: StateFlow<Boolean> = _isDndDuringWorship.asStateFlow()

    private val _dndSelectedPrayers = MutableStateFlow(setOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء"))
    val dndSelectedPrayers: StateFlow<Set<String>> = _dndSelectedPrayers.asStateFlow()

    private val _dndDurationMinutes = MutableStateFlow(20)
    val dndDurationMinutes: StateFlow<Int> = _dndDurationMinutes.asStateFlow()

    fun setNotificationSound(sound: String) {
        _notificationSound.value = sound
    }

    fun setDndDuringWorshipEnabled(enabled: Boolean) {
        _isDndDuringWorship.value = enabled
    }

    fun toggleDndPrayer(prayerName: String) {
        val current = _dndSelectedPrayers.value.toMutableSet()
        if (current.contains(prayerName)) {
            current.remove(prayerName)
        } else {
            current.add(prayerName)
        }
        _dndSelectedPrayers.value = current
    }

    fun setDndDurationMinutes(minutes: Int) {
        _dndDurationMinutes.value = minutes
    }

    fun setUserName(name: String) {
        if (name.isNotBlank()) {
            _userName.value = name
        }
    }

    fun setUserEmail(email: String) {
        if (email.isNotBlank()) {
            _userEmail.value = email
        }
    }

    fun loginUser(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val trimmedEmail = email.trim()
            val existing = repository.getUserByEmail(trimmedEmail)
            if (existing != null) {
                val hashed = repository.hashPassword(pass)
                if (existing.passwordHash == hashed) {
                    _userName.value = existing.name
                    _userEmail.value = existing.email
                    onSuccess()
                } else {
                    onError("كلمة المرور غير صحيحة.")
                }
            } else {
                if (trimmedEmail == "fahad.alqahtani@enterprise.sa" && pass.length >= 4) {
                    val newUser = UserEntity(
                        email = trimmedEmail,
                        name = "فهد بن محمد القحطاني",
                        passwordHash = repository.hashPassword(pass)
                    )
                    repository.insertUser(newUser)
                    _userName.value = newUser.name
                    _userEmail.value = newUser.email
                    onSuccess()
                } else {
                    onError("البريد الإلكتروني غير مسجل. يرجى إنشاء حساب جديد.")
                }
            }
        }
    }

    fun registerUser(name: String, email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val trimmedEmail = email.trim()
            val trimmedName = name.trim()
            if (trimmedName.isBlank() || trimmedEmail.isBlank() || pass.isBlank()) {
                onError("يرجى تعبئة كافة الحقول المطلوبة.")
                return@launch
            }
            val existing = repository.getUserByEmail(trimmedEmail)
            if (existing != null) {
                onError("البريد الإلكتروني مسجل مسبقاً. يرجى تسجيل الدخول.")
                return@launch
            }
            val newUser = UserEntity(
                email = trimmedEmail,
                name = trimmedName,
                passwordHash = repository.hashPassword(pass)
            )
            repository.insertUser(newUser)
            _userName.value = newUser.name
            _userEmail.value = newUser.email
            onSuccess()
        }
    }

    // Database reactive streams
    val calendarEvents: StateFlow<List<CalendarEventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow(java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _optimisticPrayers = MutableStateFlow<Map<String, PrayerEntity>>(emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    val prayerState: StateFlow<List<PrayerEntity>> = selectedDate.flatMapLatest { date ->
        repository.getPrayersForDate(date).map { list ->
            val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            if (date == todayStr) {
                val dbPrayer = list.firstOrNull()
                if (dbPrayer != null) {
                    _optimisticPrayers.value = _optimisticPrayers.value + (date to dbPrayer)
                }
            }
            list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPrayerState: StateFlow<PrayerEntity?> = combine(selectedDate, _optimisticPrayers, prayerState) { date, optimistic, dbList ->
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        if (date == todayStr) {
            optimistic[date] ?: dbList.firstOrNull()
        } else {
            dbList.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allPrayers: StateFlow<List<PrayerEntity>> = repository.getAllPrayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prayerStreaksState: StateFlow<PrayerStreaks> = combine(selectedDate, allPrayers) { date, prayers ->
        try {
            PrayerStreaks(
                fajrQabliyah = calculateStreakForAction(date, prayers) { it.fajrQ },
                fajrMasjid = calculateStreakForAction(date, prayers) { it.fajr.uppercase() == "IN_MOSQUE" },
                fajrHome = calculateStreakForAction(date, prayers) { it.fajr.uppercase() == "AT_HOME" },
                fajrLate = calculateStreakForAction(date, prayers) { it.fajr.uppercase() == "LATE" },
                fajrBadiyah = calculateStreakForAction(date, prayers) { it.fajrB },

                dhuhrQabliyah = calculateStreakForAction(date, prayers) { it.dhuhrQ },
                dhuhrMasjid = calculateStreakForAction(date, prayers) { it.dhuhr.uppercase() == "IN_MOSQUE" },
                dhuhrHome = calculateStreakForAction(date, prayers) { it.dhuhr.uppercase() == "AT_HOME" },
                dhuhrLate = calculateStreakForAction(date, prayers) { it.dhuhr.uppercase() == "LATE" },
                dhuhrBadiyah = calculateStreakForAction(date, prayers) { it.dhuhrB },

                asrQabliyah = calculateStreakForAction(date, prayers) { it.asrQ },
                asrMasjid = calculateStreakForAction(date, prayers) { it.asr.uppercase() == "IN_MOSQUE" },
                asrHome = calculateStreakForAction(date, prayers) { it.asr.uppercase() == "AT_HOME" },
                asrLate = calculateStreakForAction(date, prayers) { it.asr.uppercase() == "LATE" },
                asrBadiyah = calculateStreakForAction(date, prayers) { it.asrB },

                maghribQabliyah = calculateStreakForAction(date, prayers) { it.maghribQ },
                maghribMasjid = calculateStreakForAction(date, prayers) { it.maghrib.uppercase() == "IN_MOSQUE" },
                maghribHome = calculateStreakForAction(date, prayers) { it.maghrib.uppercase() == "AT_HOME" },
                maghribLate = calculateStreakForAction(date, prayers) { it.maghrib.uppercase() == "LATE" },
                maghribBadiyah = calculateStreakForAction(date, prayers) { it.maghribB },

                ishaQabliyah = calculateStreakForAction(date, prayers) { it.ishaQ },
                ishaMasjid = calculateStreakForAction(date, prayers) { it.isha.uppercase() == "IN_MOSQUE" },
                ishaHome = calculateStreakForAction(date, prayers) { it.isha.uppercase() == "AT_HOME" },
                ishaLate = calculateStreakForAction(date, prayers) { it.isha.uppercase() == "LATE" },
                ishaBadiyah = calculateStreakForAction(date, prayers) { it.ishaB },

                duha = calculateStreakForAction(date, prayers) { it.duha }
            )
        } catch (e: Exception) {
            PrayerStreaks()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PrayerStreaks())

    fun setSelectedDate(dateStr: String) {
        _selectedDate.value = dateStr
    }

    fun saveOrUpdatePrayer(prayer: PrayerEntity) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.insertPrayer(prayer)
        }
    }

    fun onPrayerEvent(event: PrayerUiEvent) {
        viewModelScope.launch {
            val currentDate = selectedDate.value
            val currentPrayers = prayerState.value
            val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            val existing = if (currentDate == todayStr) {
                _optimisticPrayers.value[currentDate] ?: currentPrayers.firstOrNull() ?: PrayerEntity(date = currentDate)
            } else {
                currentPrayers.firstOrNull() ?: PrayerEntity(date = currentDate)
            }

            when (event) {
                is PrayerUiEvent.SelectStatus -> {
                    val updated = when (event.prayerId.lowercase()) {
                        "fajr", "الفجر" -> existing.copy(fajr = event.status.name)
                        "dhuhr", "الظهر" -> existing.copy(dhuhr = event.status.name)
                        "asr", "العصر" -> existing.copy(asr = event.status.name)
                        "maghrib", "المغرب" -> existing.copy(maghrib = event.status.name)
                        "isha", "العشاء" -> existing.copy(isha = event.status.name)
                        else -> existing
                    }
                    if (currentDate == todayStr) {
                        _optimisticPrayers.value = _optimisticPrayers.value + (currentDate to updated)
                    }
                    saveOrUpdatePrayer(updated)
                }
                is PrayerUiEvent.ToggleSunnah -> {
                    val updated = when (event.prayerId.lowercase()) {
                        "fajr", "الفجر" -> {
                            if (event.isQabliyah) existing.copy(fajrQ = !existing.fajrQ)
                            else existing.copy(fajrB = !existing.fajrB)
                        }
                        "dhuhr", "الظهر" -> {
                            if (event.isQabliyah) existing.copy(dhuhrQ = !existing.dhuhrQ)
                            else existing.copy(dhuhrB = !existing.dhuhrB)
                        }
                        "asr", "العصر" -> {
                            if (event.isQabliyah) existing.copy(asrQ = !existing.asrQ)
                            else existing.copy(asrB = !existing.asrB)
                        }
                        "maghrib", "المغرب" -> {
                            if (event.isQabliyah) existing.copy(maghribQ = !existing.maghribQ)
                            else existing.copy(maghribB = !existing.maghribB)
                        }
                        "isha", "العشاء" -> {
                            if (event.isQabliyah) existing.copy(ishaQ = !existing.ishaQ)
                            else existing.copy(ishaB = !existing.ishaB)
                        }
                        else -> existing
                    }
                    if (currentDate == todayStr) {
                        _optimisticPrayers.value = _optimisticPrayers.value + (currentDate to updated)
                    }
                    saveOrUpdatePrayer(updated)
                }
                is PrayerUiEvent.ChangeDate -> {
                    setSelectedDate(event.targetDate.toString())
                }
                is PrayerUiEvent.ToggleDuha -> {
                    val updated = existing.copy(duha = !existing.duha)
                    if (currentDate == todayStr) {
                        _optimisticPrayers.value = _optimisticPrayers.value + (currentDate to updated)
                    }
                    saveOrUpdatePrayer(updated)
                }
                is PrayerUiEvent.ToggleMorningAdhkar -> {
                    val updated = existing.copy(morningAdhkar = !existing.morningAdhkar)
                    if (currentDate == todayStr) {
                        _optimisticPrayers.value = _optimisticPrayers.value + (currentDate to updated)
                    }
                    saveOrUpdatePrayer(updated)
                }
                is PrayerUiEvent.ToggleEveningAdhkar -> {
                    val updated = existing.copy(eveningAdhkar = !existing.eveningAdhkar)
                    if (currentDate == todayStr) {
                        _optimisticPrayers.value = _optimisticPrayers.value + (currentDate to updated)
                    }
                    saveOrUpdatePrayer(updated)
                }
                is PrayerUiEvent.ToggleQiyam -> {
                    val updated = existing.copy(qiyam = !existing.qiyam)
                    if (currentDate == todayStr) {
                        _optimisticPrayers.value = _optimisticPrayers.value + (currentDate to updated)
                    }
                    saveOrUpdatePrayer(updated)
                }
            }
        }
    }

    // Prayer & City State
    private val _selectedCity = MutableStateFlow(PrayerTimeCalculator.SAUDI_CITIES[0])
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val prayerTimes: StateFlow<PrayerTimes> = kotlinx.coroutines.flow.flow {
        while (true) {
            emit(Unit)
            kotlinx.coroutines.delay(60000L) // tick every 60 seconds
        }
    }.combine(selectedCity) { _, city ->
        prayerService.calculatePrayerTimes(city, Calendar.getInstance())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrayerTimeCalculator.calculatePrayerTimes()
    )

    private val _hijriDate = MutableStateFlow(PrayerTimeCalculator.getEstimatedHijriDate())
    val hijriDate: StateFlow<HijriDateInfo> = _hijriDate.asStateFlow()

    // Quran Khatmah & Adhkar
    private val _khatmahState = MutableStateFlow(QuranKhatmahState())
    val khatmahState: StateFlow<QuranKhatmahState> = _khatmahState.asStateFlow()

    private val _morningAdhkar = MutableStateFlow(AgentDataDefaults.MORNING_ADHKAR.map { it.copy() })
    val morningAdhkar: StateFlow<List<AdhkarItem>> = _morningAdhkar.asStateFlow()

    private val _eveningAdhkar = MutableStateFlow(AgentDataDefaults.EVENING_ADHKAR.map { it.copy() })
    val eveningAdhkar: StateFlow<List<AdhkarItem>> = _eveningAdhkar.asStateFlow()

    // General AI Chat on Home Tab
    private val _homeChatMessages = MutableStateFlow(
        listOf(
            UiChatMessage(
                isUser = false,
                text = "أهلاً 👋\n" +
                        "أنا مساعدك الذكي، كيف أساعدك اليوم؟"
            )
        )
    )
    val homeChatMessages: StateFlow<List<UiChatMessage>> = _homeChatMessages.asStateFlow()

    private val _isHomeGenerating = MutableStateFlow(false)
    val isHomeGenerating: StateFlow<Boolean> = _isHomeGenerating.asStateFlow()

    private val _isVoiceActive = MutableStateFlow(false)
    val isVoiceActive: StateFlow<Boolean> = _isVoiceActive.asStateFlow()

    // Agent dedicated chat
    private val _agentChatMessages = MutableStateFlow<Map<String, List<UiChatMessage>>>(emptyMap())
    val agentChatMessages: StateFlow<Map<String, List<UiChatMessage>>> = _agentChatMessages.asStateFlow()

    private val _isAgentGenerating = MutableStateFlow(false)
    val isAgentGenerating: StateFlow<Boolean> = _isAgentGenerating.asStateFlow()

    // Document & Product Generator State
    private val _generatedProductDoc = MutableStateFlow<String?>(null)
    val generatedProductDoc: StateFlow<String?> = _generatedProductDoc.asStateFlow()

    private val _isProductGenerating = MutableStateFlow(false)
    val isProductGenerating: StateFlow<Boolean> = _isProductGenerating.asStateFlow()

    // Settings & Personalization
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isEnglish = MutableStateFlow(false)
    val isEnglish: StateFlow<Boolean> = _isEnglish.asStateFlow()

    fun toggleLanguage() {
        _isEnglish.value = !_isEnglish.value
    }

    private val _themePalette = MutableStateFlow(AppThemePalette.SAUDI_EMERALD)
    val themePalette: StateFlow<AppThemePalette> = _themePalette.asStateFlow()

    private val _designStyle = MutableStateFlow(AppDesignStyle.STANDARD)
    val designStyle: StateFlow<AppDesignStyle> = _designStyle.asStateFlow()

    fun selectDesignStyle(style: AppDesignStyle) {
        _designStyle.value = style
    }

    // Font Scaling & App Density / Layout Size Settings across all sections
    private val _appFontScale = MutableStateFlow(1.0f) // 0.85f (صغير), 1.0f (قياسي), 1.15f (كبير), 1.30f (كبير جداً)
    val appFontScale: StateFlow<Float> = _appFontScale.asStateFlow()

    private val _appDensityScale = MutableStateFlow(1.0f) // 0.90f (مدمج), 1.0f (متوازن), 1.10f (مريح/رحب)
    val appDensityScale: StateFlow<Float> = _appDensityScale.asStateFlow()

    fun selectThemePalette(palette: AppThemePalette) {
        _themePalette.value = palette
    }

    fun setAppFontScale(scale: Float) {
        _appFontScale.value = scale
    }

    fun setAppDensityScale(scale: Float) {
        _appDensityScale.value = scale
    }

    // Custom Wallpaper Upload & Scrim Alpha
    private val _customWallpaperUri = MutableStateFlow<String?>(null)
    val customWallpaperUri: StateFlow<String?> = _customWallpaperUri.asStateFlow()

    private val _wallpaperDimAlpha = MutableStateFlow(0.65f)
    val wallpaperDimAlpha: StateFlow<Float> = _wallpaperDimAlpha.asStateFlow()

    fun setCustomWallpaperUri(uri: String?) {
        _customWallpaperUri.value = uri
    }

    fun setWallpaperDimAlpha(alpha: Float) {
        _wallpaperDimAlpha.value = alpha
    }

    fun clearCustomWallpaper() {
        _customWallpaperUri.value = null
    }

    val themeSettings: StateFlow<AppThemeSettings> = combine<Any?, AppThemeSettings>(
        _isDarkMode,
        _themePalette,
        _appFontScale,
        _designStyle,
        _appDensityScale,
        _customWallpaperUri,
        _wallpaperDimAlpha
    ) { array ->
        AppThemeSettings(
            isDarkMode = array[0] as Boolean,
            themePalette = array[1] as AppThemePalette,
            fontScale = array[2] as Float,
            designStyle = array[3] as AppDesignStyle,
            densityScale = array[4] as Float,
            customWallpaperUri = array[5] as String?,
            wallpaperDimAlpha = array[6] as Float
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppThemeSettings()
    )

    // Starry Night Live Theme Settings
    private val _starryNightSettings = MutableStateFlow(StarryNightSettings())
    val starryNightSettings: StateFlow<StarryNightSettings> = _starryNightSettings.asStateFlow()

    fun updateStarryNightSettings(update: (StarryNightSettings) -> StarryNightSettings) {
        _starryNightSettings.value = update(_starryNightSettings.value)
    }

    fun toggleStarryNightEnabled() {
        _starryNightSettings.value = _starryNightSettings.value.copy(
            isEnabled = !_starryNightSettings.value.isEnabled
        )
    }

    private val userPrefs = application.getSharedPreferences("zheen_user_prefs", android.content.Context.MODE_PRIVATE)

    // Dynamic One UI 8.5 User-Selected Theme Color (Defaults to Saudi Green #0B3D2E, persisted in SharedPreferences)
    private val _userPrimaryColorHex = MutableStateFlow(
        userPrefs.getString("app_primary_color_hex", "#0B3D2E") ?: "#0B3D2E"
    )
    val userPrimaryColorHex: StateFlow<String> = _userPrimaryColorHex.asStateFlow()

    val userPrimaryColor: StateFlow<androidx.compose.ui.graphics.Color> = _userPrimaryColorHex.map { hex ->
        try {
            androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            androidx.compose.ui.graphics.Color(0xFF0B3D2E)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, androidx.compose.ui.graphics.Color(0xFF0B3D2E))

    fun setUserPrimaryColor(hex: String) {
        val validHex = if (!hex.startsWith("#")) "#$hex" else hex
        try {
            android.graphics.Color.parseColor(validHex)
            _userPrimaryColorHex.value = validHex
            userPrefs.edit().putString("app_primary_color_hex", validHex).apply()
        } catch (e: Exception) {
            // invalid hex
        }
    }

    // User Profile Image URI (صورة الملف الشخصي للمستخدم)
    private val _userProfileImageUri = MutableStateFlow<String?>(userPrefs.getString("user_profile_image_uri", null))
    val userProfileImageUri: StateFlow<String?> = _userProfileImageUri.asStateFlow()

    fun setUserProfileImageUri(uri: String?) {
        _userProfileImageUri.value = uri
        userPrefs.edit().putString("user_profile_image_uri", uri).apply()
    }

    // Prayer & Notification Persistent Settings
    private val _isPrayerAlertsEnabled = MutableStateFlow(userPrefs.getBoolean("prayer_alerts_enabled", true))
    val isPrayerAlertsEnabled: StateFlow<Boolean> = _isPrayerAlertsEnabled.asStateFlow()

    fun togglePrayerAlerts() {
        val newValue = !_isPrayerAlertsEnabled.value
        _isPrayerAlertsEnabled.value = newValue
        userPrefs.edit().putBoolean("prayer_alerts_enabled", newValue).apply()
    }

    fun setPrayerAlertsEnabled(enabled: Boolean) {
        _isPrayerAlertsEnabled.value = enabled
        userPrefs.edit().putBoolean("prayer_alerts_enabled", enabled).apply()
    }

    private val _calculationMethod = MutableStateFlow(
        userPrefs.getString("calculation_method", "تقويم أم القرى (المملكة العربية السعودية)") ?: "تقويم أم القرى (المملكة العربية السعودية)"
    )
    val calculationMethod: StateFlow<String> = _calculationMethod.asStateFlow()

    fun setCalculationMethod(method: String) {
        _calculationMethod.value = method
        userPrefs.edit().putString("calculation_method", method).apply()
    }

    private val _iqamaDurationMinutes = MutableStateFlow(userPrefs.getInt("iqama_duration_minutes", 15))
    val iqamaDurationMinutes: StateFlow<Int> = _iqamaDurationMinutes.asStateFlow()

    fun setIqamaDurationMinutes(minutes: Int) {
        _iqamaDurationMinutes.value = minutes
        userPrefs.edit().putInt("iqama_duration_minutes", minutes).apply()
    }

    private val _azanSoundType = MutableStateFlow(
        userPrefs.getString("azan_sound_type", "صوت المؤذن (الأذان الكامل)") ?: "صوت المؤذن (الأذان الكامل)"
    )
    val azanSoundType: StateFlow<String> = _azanSoundType.asStateFlow()

    fun setAzanSoundType(type: String) {
        _azanSoundType.value = type
        userPrefs.edit().putString("azan_sound_type", type).apply()
    }

    private val _scheduleAlertsEnabled = MutableStateFlow(userPrefs.getBoolean("schedule_alerts_enabled", true))
    val scheduleAlertsEnabled: StateFlow<Boolean> = _scheduleAlertsEnabled.asStateFlow()

    fun setScheduleAlertsEnabled(enabled: Boolean) {
        _scheduleAlertsEnabled.value = enabled
        userPrefs.edit().putBoolean("schedule_alerts_enabled", enabled).apply()
    }

    private val _scheduleAlertTone = MutableStateFlow(
        userPrefs.getString("schedule_alert_tone", "الافتراضي (تنبيه تنفيذي)") ?: "الافتراضي (تنبيه تنفيذي)"
    )
    val scheduleAlertTone: StateFlow<String> = _scheduleAlertTone.asStateFlow()

    fun setScheduleAlertTone(tone: String) {
        _scheduleAlertTone.value = tone
        userPrefs.edit().putString("schedule_alert_tone", tone).apply()
    }

    private val _taskAlertsEnabled = MutableStateFlow(userPrefs.getBoolean("task_alerts_enabled", true))
    val taskAlertsEnabled: StateFlow<Boolean> = _taskAlertsEnabled.asStateFlow()

    fun setTaskAlertsEnabled(enabled: Boolean) {
        _taskAlertsEnabled.value = enabled
        userPrefs.edit().putBoolean("task_alerts_enabled", enabled).apply()
    }

    private val _taskAlertTone = MutableStateFlow(
        userPrefs.getString("task_alert_tone", "الافتراضي (نغمة إنجاز)") ?: "الافتراضي (نغمة إنجاز)"
    )
    val taskAlertTone: StateFlow<String> = _taskAlertTone.asStateFlow()

    fun setTaskAlertTone(tone: String) {
        _taskAlertTone.value = tone
        userPrefs.edit().putString("task_alert_tone", tone).apply()
    }

    // Daily Prayers Attendance Tracking (متابعة صلوات اليوم الخمس) - Defaults to NONE
    private val _fajrAttendance = MutableStateFlow(com.example.ui.screens.worship.AttendanceColumn.NONE)
    val fajrAttendance: StateFlow<com.example.ui.screens.worship.AttendanceColumn> = _fajrAttendance.asStateFlow()

    private val _dhuhrAttendance = MutableStateFlow(com.example.ui.screens.worship.AttendanceColumn.NONE)
    val dhuhrAttendance: StateFlow<com.example.ui.screens.worship.AttendanceColumn> = _dhuhrAttendance.asStateFlow()

    private val _asrAttendance = MutableStateFlow(com.example.ui.screens.worship.AttendanceColumn.NONE)
    val asrAttendance: StateFlow<com.example.ui.screens.worship.AttendanceColumn> = _asrAttendance.asStateFlow()

    private val _maghribAttendance = MutableStateFlow(com.example.ui.screens.worship.AttendanceColumn.NONE)
    val maghribAttendance: StateFlow<com.example.ui.screens.worship.AttendanceColumn> = _maghribAttendance.asStateFlow()

    private val _ishaAttendance = MutableStateFlow(com.example.ui.screens.worship.AttendanceColumn.NONE)
    val ishaAttendance: StateFlow<com.example.ui.screens.worship.AttendanceColumn> = _ishaAttendance.asStateFlow()

    fun setFajrAttendance(attendance: com.example.ui.screens.worship.AttendanceColumn) {
        _fajrAttendance.value = attendance
        recalculateDailyProgress()
    }

    fun setDhuhrAttendance(attendance: com.example.ui.screens.worship.AttendanceColumn) {
        _dhuhrAttendance.value = attendance
        recalculateDailyProgress()
    }

    fun setAsrAttendance(attendance: com.example.ui.screens.worship.AttendanceColumn) {
        _asrAttendance.value = attendance
        recalculateDailyProgress()
    }

    fun setMaghribAttendance(attendance: com.example.ui.screens.worship.AttendanceColumn) {
        _maghribAttendance.value = attendance
        recalculateDailyProgress()
    }

    fun setIshaAttendance(attendance: com.example.ui.screens.worship.AttendanceColumn) {
        _ishaAttendance.value = attendance
        recalculateDailyProgress()
    }

    // Dynamic Daily Progress (حلقة الإنجاز اليومية المدمجة مع صورة الملف الشخصي)
    private val _dailyProgress = MutableStateFlow(0.0f)
    val dailyProgress: StateFlow<Float> = _dailyProgress.asStateFlow()

    private val _isDailyCelebrationActive = MutableStateFlow(false)
    val isDailyCelebrationActive: StateFlow<Boolean> = _isDailyCelebrationActive.asStateFlow()

    fun recalculateDailyProgress() {
        val prayersCompleted = listOf(
            _fajrAttendance.value,
            _dhuhrAttendance.value,
            _asrAttendance.value,
            _maghribAttendance.value,
            _ishaAttendance.value
        ).count { it != com.example.ui.screens.worship.AttendanceColumn.NONE }

        val todayCal = Calendar.getInstance()
        val todayYear = todayCal.get(Calendar.YEAR)
        val todayMonth = todayCal.get(Calendar.MONTH)
        val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)

        val todayEvents = calendarEvents.value.filter { event ->
            val eventCal = Calendar.getInstance().apply { timeInMillis = event.startTimeMillis }
            eventCal.get(Calendar.YEAR) == todayYear &&
            eventCal.get(Calendar.MONTH) == todayMonth &&
            eventCal.get(Calendar.DAY_OF_MONTH) == todayDay
        }

        val totalTasks = todayEvents.size
        val tasksCompleted = todayEvents.count { it.isCompleted }

        val totalUnits = 5 + totalTasks
        val completedUnits = prayersCompleted + tasksCompleted

        val progressRatio = (completedUnits.toFloat() / totalUnits.toFloat()).coerceIn(0f, 1f)
        _dailyProgress.value = progressRatio
        _isDailyCelebrationActive.value = (progressRatio >= 1.0f) || (prayersCompleted == 5 && (totalTasks == 0 || tasksCompleted == totalTasks))
    }

    fun toggleDailyCelebration() {
        _isDailyCelebrationActive.value = !_isDailyCelebrationActive.value
        if (_isDailyCelebrationActive.value) {
            _dailyProgress.value = 1.0f
        } else {
            recalculateDailyProgress()
        }
    }

    // Smart Repetition Counter (عداد التكرار والتسبيح)
    private val _repetitionCount = MutableStateFlow(0)
    val repetitionCount: StateFlow<Int> = _repetitionCount.asStateFlow()

    private val _repetitionTarget = MutableStateFlow(33)
    val repetitionTarget: StateFlow<Int> = _repetitionTarget.asStateFlow()

    private val _repetitionLap = MutableStateFlow(0)
    val repetitionLap: StateFlow<Int> = _repetitionLap.asStateFlow()

    private val _repetitionDhikrText = MutableStateFlow("سُبْحَانَ اللَّهِ")
    val repetitionDhikrText: StateFlow<String> = _repetitionDhikrText.asStateFlow()

    fun incrementRepetition() {
        val next = _repetitionCount.value + 1
        if (_repetitionTarget.value > 0 && next >= _repetitionTarget.value) {
            _repetitionCount.value = 0
            _repetitionLap.value += 1
        } else {
            _repetitionCount.value = next
        }
    }

    fun decrementRepetition() {
        if (_repetitionCount.value > 0) {
            _repetitionCount.value -= 1
        }
    }

    fun resetRepetition() {
        _repetitionCount.value = 0
        _repetitionLap.value = 0
    }

    fun setRepetitionTarget(target: Int) {
        _repetitionTarget.value = target
        _repetitionCount.value = 0
    }

    fun setRepetitionDhikrText(text: String) {
        _repetitionDhikrText.value = text
        _repetitionCount.value = 0
    }

    private val _biometricEnabled = MutableStateFlow(true)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    private val _selectedVoicePersona = MutableStateFlow("لهجة نجدية وقورة ورسمية")
    val selectedVoicePersona: StateFlow<String> = _selectedVoicePersona.asStateFlow()

    // Smart Wallet, Points & Payment State
    private val _walletBalanceSar = MutableStateFlow(450.00)
    val walletBalanceSar: StateFlow<Double> = _walletBalanceSar.asStateFlow()

    private val _smartPoints = MutableStateFlow(8450)
    val smartPoints: StateFlow<Int> = _smartPoints.asStateFlow()

    private val _currentSubscription = MutableStateFlow("باقة الأعمال Enterprise Pro")
    val currentSubscription: StateFlow<String> = _currentSubscription.asStateFlow()

    private val _paymentTransactions = MutableStateFlow(PaymentDefaults.INITIAL_TRANSACTIONS)
    val paymentTransactions: StateFlow<List<PaymentTransaction>> = _paymentTransactions.asStateFlow()

    // Calendar selected date
    private val _calendarSelectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    private val _calendarSelectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    private val _calendarSelectedDay = MutableStateFlow(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    val calendarSelectedDay: StateFlow<Int> = _calendarSelectedDay.asStateFlow()
    val calendarSelectedMonth: StateFlow<Int> = _calendarSelectedMonth.asStateFlow()
    val calendarSelectedYear: StateFlow<Int> = _calendarSelectedYear.asStateFlow()

    private val _calendarViewMode = MutableStateFlow("MONTH") // MONTH, WEEK, DAY, LIST
    val calendarViewMode: StateFlow<String> = _calendarViewMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            prayerService.scheduleWorkManagerAlerts()
            refreshPrayerTimes()
            recalculateDailyProgress()
        }
        viewModelScope.launch {
            calendarEvents.collect {
                recalculateDailyProgress()
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _currentTab.value = tabIndex
        _activeScreen.value = ActiveScreen.MainTabs
    }

    fun navigateTo(screen: ActiveScreen) {
        _activeScreen.value = screen
    }

    fun navigateBack() {
        _activeScreen.value = ActiveScreen.MainTabs
    }

    fun processTopUp(packageItem: TopUpPackage, method: SaudiPaymentMethod, cardLast4: String = "4210") {
        _walletBalanceSar.value += packageItem.priceSar
        _smartPoints.value += packageItem.points
        val randomNum = (10000..99999).random()
        val newTxn = PaymentTransaction(
            id = "TXN-ZHN-$randomNum",
            title = "شحن رصيد المحفظة (${packageItem.points} نقطة)",
            amountSar = packageItem.priceSar,
            pointsAdded = packageItem.points,
            paymentMethodName = "${method.titleAr} •••• $cardLast4",
            dateFormatted = "اليوم، الآن"
        )
        _paymentTransactions.value = listOf(newTxn) + _paymentTransactions.value
    }

    fun processSubscriptionUpgrade(plan: SubscriptionPlan, method: SaudiPaymentMethod, cardLast4: String = "4210") {
        _currentSubscription.value = plan.titleAr
        _smartPoints.value += 5000
        val randomNum = (10000..99999).random()
        val newTxn = PaymentTransaction(
            id = "TXN-ZHN-$randomNum",
            title = "ترقية الاشتراك: ${plan.titleAr}",
            amountSar = plan.priceSar,
            pointsAdded = 5000,
            paymentMethodName = "${method.titleAr} •••• $cardLast4",
            dateFormatted = "اليوم، الآن"
        )
        _paymentTransactions.value = listOf(newTxn) + _paymentTransactions.value
    }

    fun setCity(city: CityLocation) {
        _selectedCity.value = city
        refreshPrayerTimes()
    }

    fun detectCurrentLocation(context: android.content.Context) {
        val resolved = com.example.domain.prayer.PrayerTimeCalculator.resolveSaudiLocation(context)
        _selectedCity.value = resolved
        refreshPrayerTimes()
    }

    fun refreshPrayerTimes() {
        val now = Calendar.getInstance()
        _hijriDate.value = prayerService.getHijriInfo(now)
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleBiometric() {
        _biometricEnabled.value = !_biometricEnabled.value
    }

    fun setVoicePersona(persona: String) {
        _selectedVoicePersona.value = persona
    }

    fun setCalendarViewMode(mode: String) {
        _calendarViewMode.value = mode
    }

    fun setCalendarSelectedDay(day: Int) {
        _calendarSelectedDay.value = day
    }

    fun nextCalendarMonth() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, _calendarSelectedYear.value)
            set(Calendar.MONTH, _calendarSelectedMonth.value)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, 1)
        }
        _calendarSelectedYear.value = cal.get(Calendar.YEAR)
        _calendarSelectedMonth.value = cal.get(Calendar.MONTH)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (_calendarSelectedDay.value > maxDays) {
            _calendarSelectedDay.value = maxDays
        }
    }

    fun prevCalendarMonth() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, _calendarSelectedYear.value)
            set(Calendar.MONTH, _calendarSelectedMonth.value)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, -1)
        }
        _calendarSelectedYear.value = cal.get(Calendar.YEAR)
        _calendarSelectedMonth.value = cal.get(Calendar.MONTH)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (_calendarSelectedDay.value > maxDays) {
            _calendarSelectedDay.value = maxDays
        }
    }

    fun goToToday() {
        val now = Calendar.getInstance()
        _calendarSelectedYear.value = now.get(Calendar.YEAR)
        _calendarSelectedMonth.value = now.get(Calendar.MONTH)
        _calendarSelectedDay.value = now.get(Calendar.DAY_OF_MONTH)
    }

    // Home AI Chat
    fun sendHomePrompt(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = UiChatMessage(isUser = true, text = prompt)
        _homeChatMessages.value = _homeChatMessages.value + userMsg
        _isHomeGenerating.value = true

        viewModelScope.launch {
            val prayerInfo = "الوقت الحالي بالرياض. القادم: ${prayerTimes.value.nextPrayerName} الساعة ${prayerTimes.value.nextPrayerTime}"
            val response = geminiRepo.generateAgentResponse(
                agentId = "secretary",
                systemPrompt = "أنت مساعد 'Fahad Ai' الشخصي المتكامل في السعودية. تجمع بين الذكاء التنفيذي والتنظيم ومراعاة العبادات والتقويم.",
                userPrompt = prompt,
                contextInfo = prayerInfo
            )
            _homeChatMessages.value = _homeChatMessages.value + UiChatMessage(isUser = false, text = response)
            _isHomeGenerating.value = false
        }
    }

    fun toggleVoiceChat() {
        _isVoiceActive.value = !_isVoiceActive.value
        if (_isVoiceActive.value) {
            // Simulate voice listening and instant response
            viewModelScope.launch {
                kotlinx.coroutines.delay(2500)
                if (_isVoiceActive.value) {
                    val prompt = "لخص لي جدول اليوم وتأكد من مواعيد الصلاة"
                    sendHomePrompt(prompt)
                    _isVoiceActive.value = false
                }
            }
        }
    }

    // Dedicated Agent Chat
    fun sendAgentPrompt(agent: AgentInfo, prompt: String) {
        if (prompt.isBlank()) return
        val currentList = _agentChatMessages.value[agent.id] ?: listOf(
            UiChatMessage(isUser = false, text = "أهلاً بك! أنا ${agent.name} (${agent.roleTitle}). كيف أخدمك اليوم؟")
        )
        val userMsg = UiChatMessage(isUser = true, text = prompt)
        val updated = currentList + userMsg
        _agentChatMessages.value = _agentChatMessages.value + (agent.id to updated)
        _isAgentGenerating.value = true

        viewModelScope.launch {
            val prayerInfo = "المدينة: ${_selectedCity.value.nameAr}، الصلاة القادمة: ${prayerTimes.value.nextPrayerName} ${prayerTimes.value.nextPrayerTime}"
            val response = geminiRepo.generateAgentResponse(
                agentId = agent.id,
                systemPrompt = agent.systemPrompt,
                userPrompt = prompt,
                contextInfo = prayerInfo
            )
            val withAgentResponse = updated + UiChatMessage(isUser = false, text = response)
            _agentChatMessages.value = _agentChatMessages.value + (agent.id to withAgentResponse)
            _isAgentGenerating.value = false
        }
    }

    fun getMessagesForAgent(agentId: String): List<UiChatMessage> {
        val list = _agentChatMessages.value[agentId]
        if (list != null) return list
        val agent = AgentDataDefaults.AGENTS.firstOrNull { it.id == agentId }
        val initial = listOf(
            UiChatMessage(
                isUser = false,
                text = "مرحباً بك! أنا ${agent?.name ?: "الوكيل الذكي"} (${agent?.roleTitle ?: ""}).\n" +
                        "أنا جاهز لمباشرة مهامك المتخصصة وتقديم الدعم والتحليل الفوري وفق أنظمة المملكة وسوق العمل."
            )
        )
        _agentChatMessages.value = _agentChatMessages.value + (agentId to initial)
        return initial
    }

    // Product & Digital Asset Generation
    fun generateProduct(product: ProductServiceItem, inputs: Map<String, String>) {
        _isProductGenerating.value = true
        _generatedProductDoc.value = null

        val prompt = buildString {
            append("طلب إنشاء وثيقة ذكية: ").append(product.title).append("\n")
            append("الوصف: ").append(product.description).append("\n")
            append("المدخلات والمواصفات:\n")
            inputs.forEach { (key, value) ->
                append("• ").append(key).append(": ").append(value).append("\n")
            }
        }

        viewModelScope.launch {
            val response = geminiRepo.generateAgentResponse(
                agentId = "business",
                systemPrompt = "أنت صانع وثائق ورخص ونماذج أعمال ذكية في السعودية لتطبيق 'Fahad Ai'. ولد وثيقة متكاملة، احترافية، منسقة، صالحة للاستخدام المباشر والتصدير باللغة العربية الفصحى.",
                userPrompt = prompt
            )
            _generatedProductDoc.value = response
            _isProductGenerating.value = false
        }
    }

    fun clearGeneratedProduct() {
        _generatedProductDoc.value = null
    }

    // Calendar Event Operations
    fun addEvent(
        title: String,
        description: String,
        category: String,
        startHour: Int,
        startMin: Int,
        endHour: Int,
        endMin: Int,
        location: String,
        day: Int? = null,
        month: Int? = null,
        year: Int? = null
    ): String? {
        val targetDay = day ?: _calendarSelectedDay.value
        val targetMonth = month ?: _calendarSelectedMonth.value
        val targetYear = year ?: _calendarSelectedYear.value

        val calStart = Calendar.getInstance().apply {
            set(Calendar.YEAR, targetYear)
            set(Calendar.MONTH, targetMonth)
            set(Calendar.DAY_OF_MONTH, targetDay)
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMin)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calEnd = Calendar.getInstance().apply {
            set(Calendar.YEAR, targetYear)
            set(Calendar.MONTH, targetMonth)
            set(Calendar.DAY_OF_MONTH, targetDay)
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMin)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val conflict = PrayerTimeCalculator.checkPrayerConflict(
            calStart.timeInMillis,
            calEnd.timeInMillis,
            _selectedCity.value
        )

        viewModelScope.launch {
            repository.insertEvent(
                CalendarEventEntity(
                    title = title,
                    description = description,
                    startTimeMillis = calStart.timeInMillis,
                    endTimeMillis = calEnd.timeInMillis,
                    category = category,
                    location = location,
                    isPrayerOverlapWarning = conflict != null
                )
            )
        }
        return conflict
    }

    fun deleteEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun toggleEventCompleted(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.updateEvent(event.copy(isCompleted = !event.isCompleted))
        }
    }

    // Expense Operations
    fun addExpense(title: String, amount: Double, category: String, isCorporate: Boolean, notes: String = "") {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    isCorporate = isCorporate,
                    notes = notes
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // Note Operations
    fun addNote(title: String, content: String, agentTag: String) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(
                    title = title,
                    content = content,
                    agentTag = agentTag
                )
            )
        }
    }

    fun addNoteWithSmartLinking(
        title: String,
        content: String,
        category: String, // "PERSONAL" or "WORK"
        reminderDateMillis: Long? = null
    ) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(
                    title = title,
                    content = content,
                    agentTag = if (category == "WORK") "BUSINESS" else "SECRETARY"
                )
            )
            // Smart Linking: Automatically insert into Calendar for the specified reminder/due date!
            if (reminderDateMillis != null) {
                val cal = Calendar.getInstance().apply { timeInMillis = reminderDateMillis }
                val startMillis = cal.timeInMillis
                val endMillis = startMillis + 3600000L
                val conflict = PrayerTimeCalculator.checkPrayerConflict(startMillis, endMillis, _selectedCity.value)

                repository.insertEvent(
                    CalendarEventEntity(
                        title = "ملاحظة: $title",
                        description = content,
                        startTimeMillis = startMillis,
                        endTimeMillis = endMillis,
                        category = if (category == "WORK") "WORK" else "PERSONAL",
                        location = if (category == "WORK") "ملاحظات العمل" else "ملاحظات شخصية",
                        isPrayerOverlapWarning = conflict != null
                    )
                )
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Task Operations
    fun insertTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleTaskCompleted(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun syncTaskToCalendar(task: TaskEntity) {
        viewModelScope.launch {
            val startMillis = task.dueDateMillis
            val endMillis = startMillis + 3600000L
            repository.insertEvent(
                CalendarEventEntity(
                    title = "مهمة: ${task.title}",
                    description = task.description,
                    startTimeMillis = startMillis,
                    endTimeMillis = endMillis,
                    category = task.category,
                    isCompleted = task.isCompleted
                )
            )
        }
    }

    // Khatmah & Adhkar
    fun incrementKhatmahPage() {
        val cur = _khatmahState.value
        val newPage = (cur.currentPage + 1).coerceAtMost(604)
        val newReadToday = cur.pagesReadToday + 1
        val newHizb = (newPage / 10).coerceAtLeast(1)
        val newJuz = (newPage / 20).coerceAtLeast(1)
        _khatmahState.value = cur.copy(
            currentPage = newPage,
            pagesReadToday = newReadToday,
            currentHizb = newHizb,
            currentJuz = newJuz
        )
    }

    fun decrementKhatmahPage() {
        val cur = _khatmahState.value
        val newPage = (cur.currentPage - 1).coerceAtLeast(1)
        val newReadToday = (cur.pagesReadToday - 1).coerceAtLeast(0)
        val newHizb = (newPage / 10).coerceAtLeast(1)
        val newJuz = (newPage / 20).coerceAtLeast(1)
        _khatmahState.value = cur.copy(
            currentPage = newPage,
            pagesReadToday = newReadToday,
            currentHizb = newHizb,
            currentJuz = newJuz
        )
    }

    fun updateCurrentPage(page: Int) {
        val cur = _khatmahState.value
        val newPage = page.coerceIn(1, 604)
        val newHizb = (newPage / 10).coerceAtLeast(1)
        val newJuz = (newPage / 20).coerceAtLeast(1)
        _khatmahState.value = cur.copy(
            currentPage = newPage,
            currentHizb = newHizb,
            currentJuz = newJuz
        )
    }

    fun toggleQuranPrayerPortion(prayerName: String) {
        val cur = _khatmahState.value
        val isCompleted = cur.prayerPortionsCompleted[prayerName] ?: false
        val newCompletedMap = cur.prayerPortionsCompleted.toMutableMap()
        newCompletedMap[prayerName] = !isCompleted

        val portionPages = cur.prayerPortions[prayerName] ?: 0
        val pageDelta = if (!isCompleted) portionPages else -portionPages
        val newReadToday = (cur.pagesReadToday + pageDelta).coerceIn(0, 604)
        val newPage = (cur.currentPage + pageDelta).coerceIn(1, 604)
        val newHizb = (newPage / 10).coerceAtLeast(1)
        val newJuz = (newPage / 20).coerceAtLeast(1)

        _khatmahState.value = cur.copy(
            pagesReadToday = newReadToday,
            currentPage = newPage,
            currentHizb = newHizb,
            currentJuz = newJuz,
            prayerPortionsCompleted = newCompletedMap
        )
    }

    fun incrementAdhkar(isMorning: Boolean, itemId: Int) {
        if (isMorning) {
            val list = _morningAdhkar.value.map {
                if (it.id == itemId && it.currentCount < it.targetCount) {
                    it.copy(currentCount = it.currentCount + 1)
                } else it
            }
            _morningAdhkar.value = list
        } else {
            val list = _eveningAdhkar.value.map {
                if (it.id == itemId && it.currentCount < it.targetCount) {
                    it.copy(currentCount = it.currentCount + 1)
                } else it
            }
            _eveningAdhkar.value = list
        }
    }

    fun resetAdhkar(isMorning: Boolean) {
        if (isMorning) {
            _morningAdhkar.value = _morningAdhkar.value.map { it.copy(currentCount = 0) }
        } else {
            _eveningAdhkar.value = _eveningAdhkar.value.map { it.copy(currentCount = 0) }
        }
    }
}

data class PrayerStreaks(
    val fajrQabliyah: Int = 0,
    val fajrMasjid: Int = 0,
    val fajrHome: Int = 0,
    val fajrLate: Int = 0,
    val fajrBadiyah: Int = 0,
    
    val dhuhrQabliyah: Int = 0,
    val dhuhrMasjid: Int = 0,
    val dhuhrHome: Int = 0,
    val dhuhrLate: Int = 0,
    val dhuhrBadiyah: Int = 0,

    val asrQabliyah: Int = 0,
    val asrMasjid: Int = 0,
    val asrHome: Int = 0,
    val asrLate: Int = 0,
    val asrBadiyah: Int = 0,

    val maghribQabliyah: Int = 0,
    val maghribMasjid: Int = 0,
    val maghribHome: Int = 0,
    val maghribLate: Int = 0,
    val maghribBadiyah: Int = 0,

    val ishaQabliyah: Int = 0,
    val ishaMasjid: Int = 0,
    val ishaHome: Int = 0,
    val ishaLate: Int = 0,
    val ishaBadiyah: Int = 0,

    val duha: Int = 0
)

fun calculateStreakForAction(
    dateStr: String,
    allPrayers: List<PrayerEntity>,
    checkAction: (PrayerEntity) -> Boolean
): Int {
    var streak = 0
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val cal = java.util.Calendar.getInstance()
        val parsed = sdf.parse(dateStr) ?: return 0
        cal.time = parsed
        while (true) {
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            val targetDateStr = sdf.format(cal.time)
            val prayerForDate = allPrayers.firstOrNull { it.date == targetDateStr }
            if (prayerForDate != null && checkAction(prayerForDate)) {
                streak++
            } else {
                break
            }
        }
        streak
    } catch (_: Exception) {
        0
    }
}

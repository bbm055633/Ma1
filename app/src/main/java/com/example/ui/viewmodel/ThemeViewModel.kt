package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.theme.AppDesignStyle
import com.example.ui.theme.AppThemePalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = application.getSharedPreferences("zheen_user_prefs", android.content.Context.MODE_PRIVATE)

    // Dark Mode & Language
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isEnglish = MutableStateFlow(false)
    val isEnglish: StateFlow<Boolean> = _isEnglish.asStateFlow()

    private val _themePalette = MutableStateFlow(AppThemePalette.SAUDI_EMERALD)
    val themePalette: StateFlow<AppThemePalette> = _themePalette.asStateFlow()

    private val _designStyle = MutableStateFlow(AppDesignStyle.STANDARD)
    val designStyle: StateFlow<AppDesignStyle> = _designStyle.asStateFlow()

    private val _appFontScale = MutableStateFlow(1.0f)
    val appFontScale: StateFlow<Float> = _appFontScale.asStateFlow()

    private val _appDensityScale = MutableStateFlow(1.0f)
    val appDensityScale: StateFlow<Float> = _appDensityScale.asStateFlow()

    private val _customWallpaperUri = MutableStateFlow<String?>(null)
    val customWallpaperUri: StateFlow<String?> = _customWallpaperUri.asStateFlow()

    private val _wallpaperDimAlpha = MutableStateFlow(0.65f)
    val wallpaperDimAlpha: StateFlow<Float> = _wallpaperDimAlpha.asStateFlow()

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

    // Dynamic Primary Color
    private val _userPrimaryColorHex = MutableStateFlow(
        userPrefs.getString("app_primary_color_hex", "#0B3D2E") ?: "#0B3D2E"
    )
    val userPrimaryColorHex: StateFlow<String> = _userPrimaryColorHex.asStateFlow()

    val userPrimaryColor: StateFlow<Color> = _userPrimaryColorHex.map { hex ->
        try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            Color(0xFF0B3D2E)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Color(0xFF0B3D2E))

    // User Profile Image URI
    private val _userProfileImageUri = MutableStateFlow<String?>(userPrefs.getString("user_profile_image_uri", null))
    val userProfileImageUri: StateFlow<String?> = _userProfileImageUri.asStateFlow()

    // Alert Settings
    private val _scheduleAlertsEnabled = MutableStateFlow(userPrefs.getBoolean("schedule_alerts_enabled", true))
    val scheduleAlertsEnabled: StateFlow<Boolean> = _scheduleAlertsEnabled.asStateFlow()

    private val _scheduleAlertTone = MutableStateFlow(
        userPrefs.getString("schedule_alert_tone", "الافتراضي (تنبيه تنفيذي)") ?: "الافتراضي (تنبيه تنفيذي)"
    )
    val scheduleAlertTone: StateFlow<String> = _scheduleAlertTone.asStateFlow()

    private val _taskAlertsEnabled = MutableStateFlow(userPrefs.getBoolean("task_alerts_enabled", true))
    val taskAlertsEnabled: StateFlow<Boolean> = _taskAlertsEnabled.asStateFlow()

    private val _taskAlertTone = MutableStateFlow(
        userPrefs.getString("task_alert_tone", "الافتراضي (نغمة إنجاز)") ?: "الافتراضي (نغمة إنجاز)"
    )
    val taskAlertTone: StateFlow<String> = _taskAlertTone.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(true)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    private val _selectedVoicePersona = MutableStateFlow("لهجة نجدية وقورة ورسمية")
    val selectedVoicePersona: StateFlow<String> = _selectedVoicePersona.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleLanguage() {
        _isEnglish.value = !_isEnglish.value
    }

    fun selectThemePalette(palette: AppThemePalette) {
        _themePalette.value = palette
    }

    fun selectDesignStyle(style: AppDesignStyle) {
        _designStyle.value = style
    }

    fun setAppFontScale(scale: Float) {
        _appFontScale.value = scale
    }

    fun setAppDensityScale(scale: Float) {
        _appDensityScale.value = scale
    }

    fun setCustomWallpaperUri(uri: String?) {
        _customWallpaperUri.value = uri
    }

    fun setWallpaperDimAlpha(alpha: Float) {
        _wallpaperDimAlpha.value = alpha
    }

    fun clearCustomWallpaper() {
        _customWallpaperUri.value = null
    }

    fun updateStarryNightSettings(update: (StarryNightSettings) -> StarryNightSettings) {
        _starryNightSettings.value = update(_starryNightSettings.value)
    }

    fun toggleStarryNightEnabled() {
        _starryNightSettings.value = _starryNightSettings.value.copy(
            isEnabled = !_starryNightSettings.value.isEnabled
        )
    }

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

    fun setUserProfileImageUri(uri: String?) {
        _userProfileImageUri.value = uri
        userPrefs.edit().putString("user_profile_image_uri", uri).apply()
    }

    fun setScheduleAlertsEnabled(enabled: Boolean) {
        _scheduleAlertsEnabled.value = enabled
        userPrefs.edit().putBoolean("schedule_alerts_enabled", enabled).apply()
    }

    fun setScheduleAlertTone(tone: String) {
        _scheduleAlertTone.value = tone
        userPrefs.edit().putString("schedule_alert_tone", tone).apply()
    }

    fun setTaskAlertsEnabled(enabled: Boolean) {
        _taskAlertsEnabled.value = enabled
        userPrefs.edit().putBoolean("task_alerts_enabled", enabled).apply()
    }

    fun setTaskAlertTone(tone: String) {
        _taskAlertTone.value = tone
        userPrefs.edit().putString("task_alert_tone", tone).apply()
    }

    fun toggleBiometric() {
        _biometricEnabled.value = !_biometricEnabled.value
    }

    fun setVoicePersona(persona: String) {
        _selectedVoicePersona.value = persona
    }
}

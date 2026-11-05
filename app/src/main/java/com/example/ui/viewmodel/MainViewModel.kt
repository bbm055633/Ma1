package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalendarEventEntity
import com.example.data.local.ExpenseEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TaskEntity
import com.example.data.local.PrayerEntity
import com.example.data.local.UserEntity
import com.example.data.repository.ZheenRepository
import com.example.domain.models.AgentInfo
import com.example.domain.models.ProductServiceItem
import com.example.domain.models.SaudiPaymentMethod
import com.example.domain.models.SubscriptionPlan
import com.example.domain.models.TopUpPackage
import com.example.domain.prayer.CityLocation
import com.example.ui.screens.worship.AttendanceColumn
import com.example.ui.theme.AppDesignStyle
import com.example.ui.theme.AppThemePalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Sub-ViewModels
    val prayerViewModel = PrayerViewModel(application)
    val chatViewModel = ChatViewModel(application)
    val calendarViewModel = CalendarViewModel(application)
    val themeViewModel = ThemeViewModel(application)
    val walletViewModel = WalletViewModel(application)

    val repository: ZheenRepository = prayerViewModel.repository
    val prayerService = prayerViewModel.prayerService

    // Navigation state
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _activeScreen = MutableStateFlow<ActiveScreen>(ActiveScreen.Login)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    private val _userName = MutableStateFlow("فهد بن محمد القحطاني")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("fahad.alqahtani@enterprise.sa")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    // Delegates from PrayerViewModel
    val selectedDate = prayerViewModel.selectedDate
    val prayerState = prayerViewModel.prayerState
    val selectedPrayerState = prayerViewModel.selectedPrayerState
    val allPrayers = prayerViewModel.allPrayers
    val prayerStreaksState = prayerViewModel.prayerStreaksState
    val selectedCity = prayerViewModel.selectedCity
    val prayerTimes = prayerViewModel.prayerTimes
    val hijriDate = prayerViewModel.hijriDate
    val isPrayerAlertsEnabled = prayerViewModel.isPrayerAlertsEnabled
    val calculationMethod = prayerViewModel.calculationMethod
    val iqamaDurationMinutes = prayerViewModel.iqamaDurationMinutes
    val azanSoundType = prayerViewModel.azanSoundType
    val fajrAttendance = prayerViewModel.fajrAttendance
    val dhuhrAttendance = prayerViewModel.dhuhrAttendance
    val asrAttendance = prayerViewModel.asrAttendance
    val maghribAttendance = prayerViewModel.maghribAttendance
    val ishaAttendance = prayerViewModel.ishaAttendance
    val dailyProgress = prayerViewModel.dailyProgress
    val isDailyCelebrationActive = prayerViewModel.isDailyCelebrationActive
    val notificationSound = prayerViewModel.notificationSound
    val isDndDuringWorship = prayerViewModel.isDndDuringWorship
    val dndSelectedPrayers = prayerViewModel.dndSelectedPrayers
    val dndDurationMinutes = prayerViewModel.dndDurationMinutes
    val isMorningAdhkarReminderEnabled = prayerViewModel.isMorningAdhkarReminderEnabled
    val morningAdhkarReminderTime = prayerViewModel.morningAdhkarReminderTime
    val khatmahState = prayerViewModel.khatmahState
    val morningAdhkar = prayerViewModel.morningAdhkar
    val eveningAdhkar = prayerViewModel.eveningAdhkar
    val repetitionCount = prayerViewModel.repetitionCount
    val repetitionTarget = prayerViewModel.repetitionTarget
    val repetitionLap = prayerViewModel.repetitionLap
    val repetitionDhikrText = prayerViewModel.repetitionDhikrText

    // Delegates from ChatViewModel
    val homeChatMessages = chatViewModel.homeChatMessages
    val isHomeGenerating = chatViewModel.isHomeGenerating
    val isVoiceActive = chatViewModel.isVoiceActive
    val agentChatMessages = chatViewModel.agentChatMessages
    val isAgentGenerating = chatViewModel.isAgentGenerating
    val generatedProductDoc = chatViewModel.generatedProductDoc
    val isProductGenerating = chatViewModel.isProductGenerating

    // Delegates from CalendarViewModel
    val calendarEvents = calendarViewModel.calendarEvents
    val expenses = calendarViewModel.expenses
    val notes = calendarViewModel.notes
    val tasks = calendarViewModel.tasks
    val calendarSelectedDay = calendarViewModel.calendarSelectedDay
    val calendarSelectedMonth = calendarViewModel.calendarSelectedMonth
    val calendarSelectedYear = calendarViewModel.calendarSelectedYear
    val calendarViewMode = calendarViewModel.calendarViewMode

    // Delegates from ThemeViewModel
    val isDarkMode = themeViewModel.isDarkMode
    val isEnglish = themeViewModel.isEnglish
    val themePalette = themeViewModel.themePalette
    val designStyle = themeViewModel.designStyle
    val appFontScale = themeViewModel.appFontScale
    val appDensityScale = themeViewModel.appDensityScale
    val customWallpaperUri = themeViewModel.customWallpaperUri
    val wallpaperDimAlpha = themeViewModel.wallpaperDimAlpha
    val themeSettings = themeViewModel.themeSettings
    val starryNightSettings = themeViewModel.starryNightSettings
    val userPrimaryColorHex = themeViewModel.userPrimaryColorHex
    val userPrimaryColor = themeViewModel.userPrimaryColor
    val userProfileImageUri = themeViewModel.userProfileImageUri
    val scheduleAlertsEnabled = themeViewModel.scheduleAlertsEnabled
    val scheduleAlertTone = themeViewModel.scheduleAlertTone
    val taskAlertsEnabled = themeViewModel.taskAlertsEnabled
    val taskAlertTone = themeViewModel.taskAlertTone
    val biometricEnabled = themeViewModel.biometricEnabled
    val selectedVoicePersona = themeViewModel.selectedVoicePersona

    // Delegates from WalletViewModel
    val walletBalanceSar = walletViewModel.walletBalanceSar
    val smartPoints = walletViewModel.smartPoints
    val currentSubscription = walletViewModel.currentSubscription
    val paymentTransactions = walletViewModel.paymentTransactions

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

    fun setUserName(name: String) {
        if (name.isNotBlank()) _userName.value = name
    }

    fun setUserEmail(email: String) {
        if (email.isNotBlank()) _userEmail.value = email
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

    // Prayer Delegation Methods
    fun setSelectedDate(dateStr: String) = prayerViewModel.setSelectedDate(dateStr)
    fun saveOrUpdatePrayer(prayer: PrayerEntity) = prayerViewModel.saveOrUpdatePrayer(prayer)
    fun onPrayerEvent(event: PrayerUiEvent) = prayerViewModel.onPrayerEvent(event)
    fun setCity(city: CityLocation) = prayerViewModel.setCity(city)
    fun detectCurrentLocation(context: android.content.Context) = prayerViewModel.detectCurrentLocation(context)
    fun refreshPrayerTimes() = prayerViewModel.refreshPrayerTimes()
    fun togglePrayerAlerts() = prayerViewModel.togglePrayerAlerts()
    fun setPrayerAlertsEnabled(enabled: Boolean) = prayerViewModel.setPrayerAlertsEnabled(enabled)
    fun setCalculationMethod(method: String) = prayerViewModel.setCalculationMethod(method)
    fun setIqamaDurationMinutes(minutes: Int) = prayerViewModel.setIqamaDurationMinutes(minutes)
    fun setAzanSoundType(type: String) = prayerViewModel.setAzanSoundType(type)
    fun setFajrAttendance(attendance: AttendanceColumn) = prayerViewModel.setFajrAttendance(attendance)
    fun setDhuhrAttendance(attendance: AttendanceColumn) = prayerViewModel.setDhuhrAttendance(attendance)
    fun setAsrAttendance(attendance: AttendanceColumn) = prayerViewModel.setAsrAttendance(attendance)
    fun setMaghribAttendance(attendance: AttendanceColumn) = prayerViewModel.setMaghribAttendance(attendance)
    fun setIshaAttendance(attendance: AttendanceColumn) = prayerViewModel.setIshaAttendance(attendance)
    fun recalculateDailyProgress() = prayerViewModel.recalculateDailyProgress(calendarEvents.value)
    fun toggleDailyCelebration() = prayerViewModel.toggleDailyCelebration()
    fun setNotificationSound(sound: String) = prayerViewModel.setNotificationSound(sound)
    fun setDndDuringWorshipEnabled(enabled: Boolean) = prayerViewModel.setDndDuringWorshipEnabled(enabled)
    fun toggleDndPrayer(prayerName: String) = prayerViewModel.toggleDndPrayer(prayerName)
    fun setDndDurationMinutes(minutes: Int) = prayerViewModel.setDndDurationMinutes(minutes)
    fun setMorningAdhkarReminderEnabled(enabled: Boolean) = prayerViewModel.setMorningAdhkarReminderEnabled(enabled)
    fun setMorningAdhkarReminderTime(timeStr: String) = prayerViewModel.setMorningAdhkarReminderTime(timeStr)
    fun incrementKhatmahPage() = prayerViewModel.incrementKhatmahPage()
    fun decrementKhatmahPage() = prayerViewModel.decrementKhatmahPage()
    fun updateCurrentPage(page: Int) = prayerViewModel.updateCurrentPage(page)
    fun toggleQuranPrayerPortion(prayerName: String) = prayerViewModel.toggleQuranPrayerPortion(prayerName)
    fun incrementAdhkar(isMorning: Boolean, itemId: Int) = prayerViewModel.incrementAdhkar(isMorning, itemId)
    fun resetAdhkar(isMorning: Boolean) = prayerViewModel.resetAdhkar(isMorning)
    fun incrementRepetition() = prayerViewModel.incrementRepetition()
    fun decrementRepetition() = prayerViewModel.decrementRepetition()
    fun resetRepetition() = prayerViewModel.resetRepetition()
    fun setRepetitionTarget(target: Int) = prayerViewModel.setRepetitionTarget(target)
    fun setRepetitionDhikrText(text: String) = prayerViewModel.setRepetitionDhikrText(text)

    // Chat Delegation Methods
    fun sendHomePrompt(prompt: String) = chatViewModel.sendHomePrompt(prompt, prayerTimes.value)
    fun toggleVoiceChat() = chatViewModel.toggleVoiceChat(prayerTimes.value)
    fun sendAgentPrompt(agent: AgentInfo, prompt: String) = chatViewModel.sendAgentPrompt(agent, prompt, selectedCity.value, prayerTimes.value)
    fun getMessagesForAgent(agentId: String) = chatViewModel.getMessagesForAgent(agentId)
    fun generateProduct(product: ProductServiceItem, inputs: Map<String, String>) = chatViewModel.generateProduct(product, inputs)
    fun clearGeneratedProduct() = chatViewModel.clearGeneratedProduct()

    // Calendar Delegation Methods
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
    ) = calendarViewModel.addEvent(
        title = title,
        description = description,
        category = category,
        startHour = startHour,
        startMin = startMin,
        endHour = endHour,
        endMin = endMin,
        location = location,
        day = day,
        month = month,
        year = year,
        city = selectedCity.value
    )

    fun deleteEvent(event: CalendarEventEntity) = calendarViewModel.deleteEvent(event)
    fun toggleEventCompleted(event: CalendarEventEntity) = calendarViewModel.toggleEventCompleted(event)
    fun addExpense(title: String, amount: Double, category: String, isCorporate: Boolean, notes: String = "") = calendarViewModel.addExpense(title, amount, category, isCorporate, notes)
    fun deleteExpense(expense: ExpenseEntity) = calendarViewModel.deleteExpense(expense)
    fun addNote(title: String, content: String, agentTag: String) = calendarViewModel.addNote(title, content, agentTag)
    fun addNoteWithSmartLinking(title: String, content: String, category: String, reminderDateMillis: Long? = null) = calendarViewModel.addNoteWithSmartLinking(title, content, category, reminderDateMillis, selectedCity.value)
    fun deleteNote(note: NoteEntity) = calendarViewModel.deleteNote(note)
    fun insertTask(task: TaskEntity) = calendarViewModel.insertTask(task)
    fun updateTask(task: TaskEntity) = calendarViewModel.updateTask(task)
    fun deleteTask(task: TaskEntity) = calendarViewModel.deleteTask(task)
    fun toggleTaskCompleted(task: TaskEntity) = calendarViewModel.toggleTaskCompleted(task)
    fun syncTaskToCalendar(task: TaskEntity) = calendarViewModel.syncTaskToCalendar(task)
    fun setCalendarViewMode(mode: String) = calendarViewModel.setCalendarViewMode(mode)
    fun setCalendarSelectedDay(day: Int) = calendarViewModel.setCalendarSelectedDay(day)
    fun nextCalendarMonth() = calendarViewModel.nextCalendarMonth()
    fun prevCalendarMonth() = calendarViewModel.prevCalendarMonth()
    fun goToToday() = calendarViewModel.goToToday()

    // Theme Delegation Methods
    fun toggleDarkMode() = themeViewModel.toggleDarkMode()
    fun toggleLanguage() = themeViewModel.toggleLanguage()
    fun selectThemePalette(palette: AppThemePalette) = themeViewModel.selectThemePalette(palette)
    fun selectDesignStyle(style: AppDesignStyle) = themeViewModel.selectDesignStyle(style)
    fun setAppFontScale(scale: Float) = themeViewModel.setAppFontScale(scale)
    fun setAppDensityScale(scale: Float) = themeViewModel.setAppDensityScale(scale)
    fun setCustomWallpaperUri(uri: String?) = themeViewModel.setCustomWallpaperUri(uri)
    fun setWallpaperDimAlpha(alpha: Float) = themeViewModel.setWallpaperDimAlpha(alpha)
    fun clearCustomWallpaper() = themeViewModel.clearCustomWallpaper()
    fun updateStarryNightSettings(update: (StarryNightSettings) -> StarryNightSettings) = themeViewModel.updateStarryNightSettings(update)
    fun toggleStarryNightEnabled() = themeViewModel.toggleStarryNightEnabled()
    fun setUserPrimaryColor(hex: String) = themeViewModel.setUserPrimaryColor(hex)
    fun setUserProfileImageUri(uri: String?) = themeViewModel.setUserProfileImageUri(uri)
    fun setScheduleAlertsEnabled(enabled: Boolean) = themeViewModel.setScheduleAlertsEnabled(enabled)
    fun setScheduleAlertTone(tone: String) = themeViewModel.setScheduleAlertTone(tone)
    fun setTaskAlertsEnabled(enabled: Boolean) = themeViewModel.setTaskAlertsEnabled(enabled)
    fun setTaskAlertTone(tone: String) = themeViewModel.setTaskAlertTone(tone)
    fun toggleBiometric() = themeViewModel.toggleBiometric()
    fun setVoicePersona(persona: String) = themeViewModel.setVoicePersona(persona)

    // Wallet Delegation Methods
    fun processTopUp(packageItem: TopUpPackage, method: SaudiPaymentMethod, cardLast4: String = "4210") = walletViewModel.processTopUp(packageItem, method, cardLast4)
    fun processSubscriptionUpgrade(plan: SubscriptionPlan, method: SaudiPaymentMethod, cardLast4: String = "4210") = walletViewModel.processSubscriptionUpgrade(plan, method, cardLast4)

    override fun onCleared() {
        super.onCleared()
        prayerViewModel.cleanup()
        chatViewModel.cleanup()
    }
}

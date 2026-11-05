package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CalendarEventEntity
import com.example.data.local.PrayerEntity
import com.example.data.repository.ZheenRepository
import com.example.domain.models.AdhkarItem
import com.example.domain.models.AgentDataDefaults
import com.example.domain.models.QuranKhatmahState
import com.example.domain.prayer.CityLocation
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.domain.prayer.PrayerTimes
import com.example.ui.screens.worship.AttendanceColumn
import com.example.service.PrayerTimeService
import com.example.service.ZheenWorkManagerScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = ZheenRepository(database)
    val prayerService = PrayerTimeService.getInstance(application)
    private val prefs = application.getSharedPreferences("fahad_ai_prefs", android.content.Context.MODE_PRIVATE)
    private val userPrefs = application.getSharedPreferences("zheen_user_prefs", android.content.Context.MODE_PRIVATE)

    private val _selectedDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _optimisticPrayers = MutableStateFlow<Map<String, PrayerEntity>>(emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    val prayerState: StateFlow<List<PrayerEntity>> = selectedDate.flatMapLatest { date ->
        repository.getPrayersForDate(date).map { list ->
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
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
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
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

    private val _selectedCity = MutableStateFlow(PrayerTimeCalculator.SAUDI_CITIES[0])
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val prayerTimes: StateFlow<PrayerTimes> = flow {
        while (true) {
            emit(Unit)
            kotlinx.coroutines.delay(60000L)
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

    private val _isPrayerAlertsEnabled = MutableStateFlow(userPrefs.getBoolean("prayer_alerts_enabled", true))
    val isPrayerAlertsEnabled: StateFlow<Boolean> = _isPrayerAlertsEnabled.asStateFlow()

    private val _calculationMethod = MutableStateFlow(
        userPrefs.getString("calculation_method", "تقويم أم القرى (المملكة العربية السعودية)") ?: "تقويم أم القرى (المملكة العربية السعودية)"
    )
    val calculationMethod: StateFlow<String> = _calculationMethod.asStateFlow()

    private val _iqamaDurationMinutes = MutableStateFlow(userPrefs.getInt("iqama_duration_minutes", 15))
    val iqamaDurationMinutes: StateFlow<Int> = _iqamaDurationMinutes.asStateFlow()

    private val _azanSoundType = MutableStateFlow(
        userPrefs.getString("azan_sound_type", "صوت المؤذن (الأذان الكامل)") ?: "صوت المؤذن (الأذان الكامل)"
    )
    val azanSoundType: StateFlow<String> = _azanSoundType.asStateFlow()

    // Daily Prayers Attendance Tracking
    private val _fajrAttendance = MutableStateFlow(AttendanceColumn.NONE)
    val fajrAttendance: StateFlow<AttendanceColumn> = _fajrAttendance.asStateFlow()

    private val _dhuhrAttendance = MutableStateFlow(AttendanceColumn.NONE)
    val dhuhrAttendance: StateFlow<AttendanceColumn> = _dhuhrAttendance.asStateFlow()

    private val _asrAttendance = MutableStateFlow(AttendanceColumn.NONE)
    val asrAttendance: StateFlow<AttendanceColumn> = _asrAttendance.asStateFlow()

    private val _maghribAttendance = MutableStateFlow(AttendanceColumn.NONE)
    val maghribAttendance: StateFlow<AttendanceColumn> = _maghribAttendance.asStateFlow()

    private val _ishaAttendance = MutableStateFlow(AttendanceColumn.NONE)
    val ishaAttendance: StateFlow<AttendanceColumn> = _ishaAttendance.asStateFlow()

    private val _dailyProgress = MutableStateFlow(0.0f)
    val dailyProgress: StateFlow<Float> = _dailyProgress.asStateFlow()

    private val _isDailyCelebrationActive = MutableStateFlow(false)
    val isDailyCelebrationActive: StateFlow<Boolean> = _isDailyCelebrationActive.asStateFlow()

    // DND & Reminders
    private val _notificationSound = MutableStateFlow("default")
    val notificationSound: StateFlow<String> = _notificationSound.asStateFlow()

    private val _isDndDuringWorship = MutableStateFlow(true)
    val isDndDuringWorship: StateFlow<Boolean> = _isDndDuringWorship.asStateFlow()

    private val _dndSelectedPrayers = MutableStateFlow(setOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء"))
    val dndSelectedPrayers: StateFlow<Set<String>> = _dndSelectedPrayers.asStateFlow()

    private val _dndDurationMinutes = MutableStateFlow(20)
    val dndDurationMinutes: StateFlow<Int> = _dndDurationMinutes.asStateFlow()

    private val _isMorningAdhkarReminderEnabled = MutableStateFlow(
        prefs.getBoolean("morning_adhkar_reminder_enabled", true)
    )
    val isMorningAdhkarReminderEnabled: StateFlow<Boolean> = _isMorningAdhkarReminderEnabled.asStateFlow()

    private val _morningAdhkarReminderTime = MutableStateFlow(
        prefs.getString("morning_adhkar_reminder_time", "08:00 AM") ?: "08:00 AM"
    )
    val morningAdhkarReminderTime: StateFlow<String> = _morningAdhkarReminderTime.asStateFlow()

    // Khatmah & Adhkar
    private val _khatmahState = MutableStateFlow(QuranKhatmahState())
    val khatmahState: StateFlow<QuranKhatmahState> = _khatmahState.asStateFlow()

    private val _morningAdhkar = MutableStateFlow(AgentDataDefaults.MORNING_ADHKAR.map { it.copy() })
    val morningAdhkar: StateFlow<List<AdhkarItem>> = _morningAdhkar.asStateFlow()

    private val _eveningAdhkar = MutableStateFlow(AgentDataDefaults.EVENING_ADHKAR.map { it.copy() })
    val eveningAdhkar: StateFlow<List<AdhkarItem>> = _eveningAdhkar.asStateFlow()

    // Repetition counter
    private val _repetitionCount = MutableStateFlow(0)
    val repetitionCount: StateFlow<Int> = _repetitionCount.asStateFlow()

    private val _repetitionTarget = MutableStateFlow(33)
    val repetitionTarget: StateFlow<Int> = _repetitionTarget.asStateFlow()

    private val _repetitionLap = MutableStateFlow(0)
    val repetitionLap: StateFlow<Int> = _repetitionLap.asStateFlow()

    private val _repetitionDhikrText = MutableStateFlow("سُبْحَانَ اللَّهِ")
    val repetitionDhikrText: StateFlow<String> = _repetitionDhikrText.asStateFlow()

    init {
        viewModelScope.launch {
            selectedPrayerState.collect { prayer ->
                val progressStr = prayer?.morningAdhkarProgress ?: ""
                val countsMap = mutableMapOf<Int, Int>()
                if (progressStr.isNotBlank()) {
                    progressStr.split(",").forEach { pair ->
                        val parts = pair.split(":")
                        if (parts.size == 2) {
                            val id = parts[0].toIntOrNull()
                            val cnt = parts[1].toIntOrNull()
                            if (id != null && cnt != null) {
                                countsMap[id] = cnt
                            }
                        }
                    }
                }
                _morningAdhkar.value = AgentDataDefaults.MORNING_ADHKAR.map { defaultItem ->
                    val savedCount = countsMap[defaultItem.id] ?: 0
                    defaultItem.copy(currentCount = savedCount)
                }
            }
        }
    }

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
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
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

    fun setCity(city: CityLocation) {
        _selectedCity.value = city
        refreshPrayerTimes()
    }

    fun detectCurrentLocation(context: android.content.Context) {
        val resolved = PrayerTimeCalculator.resolveSaudiLocation(context)
        _selectedCity.value = resolved
        refreshPrayerTimes()
    }

    fun refreshPrayerTimes() {
        val now = Calendar.getInstance()
        _hijriDate.value = prayerService.getHijriInfo(now)
    }

    fun togglePrayerAlerts() {
        val newValue = !_isPrayerAlertsEnabled.value
        _isPrayerAlertsEnabled.value = newValue
        userPrefs.edit().putBoolean("prayer_alerts_enabled", newValue).apply()
    }

    fun setPrayerAlertsEnabled(enabled: Boolean) {
        _isPrayerAlertsEnabled.value = enabled
        userPrefs.edit().putBoolean("prayer_alerts_enabled", enabled).apply()
    }

    fun setCalculationMethod(method: String) {
        _calculationMethod.value = method
        userPrefs.edit().putString("calculation_method", method).apply()
    }

    fun setIqamaDurationMinutes(minutes: Int) {
        _iqamaDurationMinutes.value = minutes
        userPrefs.edit().putInt("iqama_duration_minutes", minutes).apply()
    }

    fun setAzanSoundType(type: String) {
        _azanSoundType.value = type
        userPrefs.edit().putString("azan_sound_type", type).apply()
    }

    fun setFajrAttendance(attendance: AttendanceColumn) {
        _fajrAttendance.value = attendance
        recalculateDailyProgress(emptyList())
    }

    fun setDhuhrAttendance(attendance: AttendanceColumn) {
        _dhuhrAttendance.value = attendance
        recalculateDailyProgress(emptyList())
    }

    fun setAsrAttendance(attendance: AttendanceColumn) {
        _asrAttendance.value = attendance
        recalculateDailyProgress(emptyList())
    }

    fun setMaghribAttendance(attendance: AttendanceColumn) {
        _maghribAttendance.value = attendance
        recalculateDailyProgress(emptyList())
    }

    fun setIshaAttendance(attendance: AttendanceColumn) {
        _ishaAttendance.value = attendance
        recalculateDailyProgress(emptyList())
    }

    fun recalculateDailyProgress(todayEvents: List<CalendarEventEntity> = emptyList()) {
        val prayersCompleted = listOf(
            _fajrAttendance.value,
            _dhuhrAttendance.value,
            _asrAttendance.value,
            _maghribAttendance.value,
            _ishaAttendance.value
        ).count { it != AttendanceColumn.NONE }

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
            recalculateDailyProgress(emptyList())
        }
    }

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

    fun setMorningAdhkarReminderEnabled(enabled: Boolean) {
        _isMorningAdhkarReminderEnabled.value = enabled
        prefs.edit().putBoolean("morning_adhkar_reminder_enabled", enabled).apply()
        if (enabled) {
            val (h, m) = parseReminderTime(_morningAdhkarReminderTime.value)
            ZheenWorkManagerScheduler.scheduleMorningAdhkarReminder(getApplication(), h, m)
        } else {
            try {
                androidx.work.WorkManager.getInstance(getApplication()).cancelUniqueWork("zheen_morning_adhkar_reminder")
            } catch (_: Exception) {}
        }
    }

    fun setMorningAdhkarReminderTime(timeStr: String) {
        _morningAdhkarReminderTime.value = timeStr
        prefs.edit().putString("morning_adhkar_reminder_time", timeStr).apply()
        if (_isMorningAdhkarReminderEnabled.value) {
            val (h, m) = parseReminderTime(timeStr)
            ZheenWorkManagerScheduler.scheduleMorningAdhkarReminder(getApplication(), h, m)
        }
    }

    private fun parseReminderTime(timeStr: String): Pair<Int, Int> {
        return try {
            val parts = timeStr.trim().split(" ", ":")
            var hour = parts[0].toInt()
            val min = parts[1].toInt()
            val isPm = timeStr.contains("PM", ignoreCase = true) || timeStr.contains("م", ignoreCase = true)
            val isAm = timeStr.contains("AM", ignoreCase = true) || timeStr.contains("ص", ignoreCase = true)
            if (isPm && hour < 12) hour += 12
            if (isAm && hour == 12) hour = 0
            Pair(hour, min)
        } catch (e: Exception) {
            Pair(8, 0)
        }
    }

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
            persistMorningAdhkarProgress(list)
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
            val list = _morningAdhkar.value.map { it.copy(currentCount = 0) }
            _morningAdhkar.value = list
            persistMorningAdhkarProgress(list)
        } else {
            _eveningAdhkar.value = _eveningAdhkar.value.map { it.copy(currentCount = 0) }
        }
    }

    private fun persistMorningAdhkarProgress(list: List<AdhkarItem>) {
        val currentDate = selectedDate.value
        val progressStr = list.joinToString(",") { "${it.id}:${it.currentCount}" }
        val isAllCompleted = list.all { it.currentCount >= it.targetCount } && list.isNotEmpty()

        viewModelScope.launch {
            val currentPrayers = prayerState.value
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val existing = if (currentDate == todayStr) {
                _optimisticPrayers.value[currentDate] ?: currentPrayers.firstOrNull() ?: PrayerEntity(date = currentDate)
            } else {
                currentPrayers.firstOrNull() ?: PrayerEntity(date = currentDate)
            }

            val updated = existing.copy(
                morningAdhkarProgress = progressStr,
                morningAdhkar = isAllCompleted
            )
            if (currentDate == todayStr) {
                _optimisticPrayers.value = _optimisticPrayers.value + (currentDate to updated)
            }
            saveOrUpdatePrayer(updated)
        }
    }

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
}

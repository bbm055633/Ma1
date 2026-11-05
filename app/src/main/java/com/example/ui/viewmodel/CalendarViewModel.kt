package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CalendarEventEntity
import com.example.data.local.ExpenseEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TaskEntity
import com.example.data.repository.ZheenRepository
import com.example.domain.prayer.CityLocation
import com.example.domain.prayer.PrayerTimeCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = ZheenRepository(database)

    val calendarEvents: StateFlow<List<CalendarEventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calendar selected date
    private val _calendarSelectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    private val _calendarSelectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    private val _calendarSelectedDay = MutableStateFlow(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    val calendarSelectedDay: StateFlow<Int> = _calendarSelectedDay.asStateFlow()
    val calendarSelectedMonth: StateFlow<Int> = _calendarSelectedMonth.asStateFlow()
    val calendarSelectedYear: StateFlow<Int> = _calendarSelectedYear.asStateFlow()

    private val _calendarViewMode = MutableStateFlow("MONTH") // MONTH, WEEK, DAY, LIST
    val calendarViewMode: StateFlow<String> = _calendarViewMode.asStateFlow()

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
        year: Int? = null,
        city: CityLocation = PrayerTimeCalculator.SAUDI_CITIES[0]
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
            city
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
        category: String,
        reminderDateMillis: Long? = null,
        city: CityLocation = PrayerTimeCalculator.SAUDI_CITIES[0]
    ) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(
                    title = title,
                    content = content,
                    agentTag = if (category == "WORK") "BUSINESS" else "SECRETARY"
                )
            )
            if (reminderDateMillis != null) {
                val cal = Calendar.getInstance().apply { timeInMillis = reminderDateMillis }
                val startMillis = cal.timeInMillis
                val endMillis = startMillis + 3600000L
                val conflict = PrayerTimeCalculator.checkPrayerConflict(startMillis, endMillis, city)

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
}

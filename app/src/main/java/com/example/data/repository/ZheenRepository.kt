package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.CalendarEventEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ExpenseEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TaskEntity
import com.example.data.local.PrayerEntity
import com.example.data.local.UserEntity
import com.example.domain.prayer.PrayerTimeCalculator
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class ZheenRepository(private val database: AppDatabase) {

    val calendarDao = database.calendarDao()
    val expenseDao = database.expenseDao()
    val noteDao = database.noteDao()
    val taskDao = database.taskDao()
    val chatDao = database.chatDao()
    val prayerDao = database.prayerDao()
    val userDao = database.userDao()

    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun getDefaultUser() = userDao.getDefaultUser()

    fun hashPassword(password: String): String {
        return try {
            val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            password
        }
    }

    val allEvents: Flow<List<CalendarEventEntity>> = calendarDao.getAllEvents()
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getPrayersForDate(date: String): Flow<List<PrayerEntity>> =
        prayerDao.getPrayersForDate(date)

    fun getAllPrayers(): Flow<List<PrayerEntity>> =
        prayerDao.getAllPrayers()

    suspend fun insertPrayer(prayer: PrayerEntity): Long =
        prayerDao.insertPrayer(prayer)

    fun getMessagesForAgent(agentId: String): Flow<List<ChatMessageEntity>> =
        chatDao.getMessagesForAgent(agentId)

    suspend fun insertEvent(event: CalendarEventEntity): Long {
        val warning = PrayerTimeCalculator.checkPrayerConflict(event.startTimeMillis, event.endTimeMillis)
        val finalEvent = if (warning != null) {
            event.copy(isPrayerOverlapWarning = true)
        } else {
            event
        }
        return calendarDao.insertEvent(finalEvent)
    }

    suspend fun updateEvent(event: CalendarEventEntity) {
        calendarDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: CalendarEventEntity) {
        calendarDao.deleteEvent(event)
    }

    suspend fun deleteEventById(id: Long) {
        calendarDao.deleteById(id)
    }

    suspend fun insertExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun insertNote(note: NoteEntity): Long {
        return noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }

    suspend fun insertTask(task: TaskEntity): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
    }

    suspend fun insertChatMessage(agentId: String, sender: String, content: String): Long {
        return chatDao.insertMessage(
            ChatMessageEntity(
                agentId = agentId,
                sender = sender,
                content = content
            )
        )
    }

    suspend fun seedInitialDataIfEmpty() {
        try {
            val defaultEmail = "fahad.alqahtani@enterprise.sa"
            val user = userDao.getUserByEmail(defaultEmail)
            if (user == null) {
                userDao.insertUser(
                    UserEntity(
                        email = defaultEmail,
                        name = "فهد بن محمد القحطاني",
                        passwordHash = hashPassword("password"),
                        isDefault = true
                    )
                )
            }

            if (taskDao.getTaskCount() == 0) {
                val now = System.currentTimeMillis()
                taskDao.insertTask(
                    TaskEntity(
                        title = "مراجعة ميزانية الربع الثالث مع المستشار المالي",
                        description = "اعتماد بنود الصرف ومطابقة الفواتير مع معايير ZATCA",
                        priority = "HIGH",
                        category = "FINANCE",
                        isCompleted = false,
                        dueDateMillis = now + 86400000L
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "تحديث خارطة طريق الذكاء الاصطناعي لمنتجات المؤسسة",
                        description = "اجتماع مع فريق المطورين لمراجعة نماذج Gemini والتكامل السحابي",
                        priority = "HIGH",
                        category = "PROJECT",
                        isCompleted = true,
                        dueDateMillis = now - 86400000L
                    )
                )
            }

            if (calendarDao.getEventCount() == 0) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 10)
                cal.set(Calendar.MINUTE, 0)
                val start = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 11)
                cal.set(Calendar.MINUTE, 30)
                val end = cal.timeInMillis
                calendarDao.insertEvent(
                    CalendarEventEntity(
                        title = "اجتماع مجلس الإدارة ومراجعة الخطط التشغيلية",
                        description = "مناقشة توسع أعمال المؤسسة ومبادرات التحول الرقمي",
                        startTimeMillis = start,
                        endTimeMillis = end,
                        category = "MEETING",
                        location = "برج المملكة، الرياض - القاعة التنفيذية"
                    )
                )
            }
        } catch (_: Exception) {}
    }
}

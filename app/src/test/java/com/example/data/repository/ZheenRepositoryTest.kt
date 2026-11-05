package com.example.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [ZheenRepository] covering Room database CRUD operations
 * using an in-memory SQLite database.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ZheenRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: ZheenRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ZheenRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testUserOperations() = runBlocking {
        val email = "fahad.test@enterprise.sa"
        val name = "فهد القحطاني"
        val rawPassword = "securePassword123"
        val hashed = repository.hashPassword(rawPassword)

        val newUser = UserEntity(
            email = email,
            name = name,
            passwordHash = hashed
        )

        repository.insertUser(newUser)

        val retrieved = repository.getUserByEmail(email)
        assertNotNull("User should be found by email", retrieved)
        assertEquals(email, retrieved?.email)
        assertEquals(name, retrieved?.name)
        assertEquals(hashed, retrieved?.passwordHash)
    }

    @Test
    fun testPasswordHashingConsistency() {
        val hash1 = repository.hashPassword("Password123!")
        val hash2 = repository.hashPassword("Password123!")
        val hash3 = repository.hashPassword("DifferentPass")

        assertEquals("Same password should produce identical SHA-256 hashes", hash1, hash2)
        assertNotEquals("Different passwords should produce different hashes", hash1, hash3)
    }

    @Test
    fun testTaskCrudOperations() = runBlocking {
        val task = TaskEntity(
            title = "اختبار نظام المهام",
            description = "وصف المهمة للتأكد من قاعدة البيانات Local",
            priority = "HIGH",
            category = "WORK",
            isCompleted = false
        )

        val id = repository.insertTask(task)
        assertTrue("Inserted task ID should be positive", id > 0)

        val tasksList = repository.allTasks.first()
        assertTrue("Tasks list should contain the newly added task", tasksList.any { it.title == task.title })

        val insertedTask = tasksList.first { it.title == task.title }
        val updatedTask = insertedTask.copy(isCompleted = true, priority = "MEDIUM")
        repository.updateTask(updatedTask)

        val updatedList = repository.allTasks.first()
        val foundUpdated = updatedList.first { it.id == insertedTask.id }
        assertTrue("Task completed state should be updated to true", foundUpdated.isCompleted)
        assertEquals("MEDIUM", foundUpdated.priority)

        repository.deleteTask(foundUpdated)
        val listAfterDelete = repository.allTasks.first()
        assertFalse("Task should be removed after deletion", listAfterDelete.any { it.id == insertedTask.id })
    }

    @Test
    fun testEventInsertAndDelete() = runBlocking {
        val event = CalendarEventEntity(
            title = "اجتماع الربع الثالث",
            description = "مناقشة الخطة الاستراتيجية",
            category = "MEETING",
            startTimeMillis = 1700000000000L,
            endTimeMillis = 1700003600000L,
            location = "الرياض - المقر الرئيسي"
        )

        val id = repository.insertEvent(event)
        assertTrue("Inserted event ID should be positive", id > 0)

        val eventsList = repository.allEvents.first()
        assertTrue("Events list should include inserted event", eventsList.any { it.title == event.title })

        val insertedEvent = eventsList.first { it.title == event.title }
        repository.deleteEvent(insertedEvent)

        val remainingEvents = repository.allEvents.first()
        assertFalse("Deleted event should no longer exist", remainingEvents.any { it.id == insertedEvent.id })
    }

    @Test
    fun testExpenseCrudOperations() = runBlocking {
        val expense = ExpenseEntity(
            title = "رسوم تراخيص السحابة",
            amount = 1250.0,
            category = "INFRASTRUCTURE",
            isCorporate = true,
            notes = "الفاتورة الشهرية"
        )

        val id = repository.insertExpense(expense)
        assertTrue("Expense ID should be > 0", id > 0)

        val expenses = repository.allExpenses.first()
        val found = expenses.firstOrNull { it.id == id }
        assertNotNull("Inserted expense should be retrieved", found)
        assertEquals(1250.0, found?.amount ?: 0.0, 0.01)

        if (found != null) {
            repository.deleteExpense(found)
        }
        val afterDelete = repository.allExpenses.first()
        assertFalse("Expense should be deleted", afterDelete.any { it.id == id })
    }

    @Test
    fun testPrayerOperations() = runBlocking {
        val dateStr = "2026-11-05"
        val prayer = PrayerEntity(
            date = dateStr,
            fajr = "IN_MOSQUE"
        )

        repository.insertPrayer(prayer)

        val datePrayers = repository.getPrayersForDate(dateStr).first()
        assertTrue("Should return prayer entry for date", datePrayers.any { it.date == dateStr && it.fajr == "IN_MOSQUE" })
    }
}

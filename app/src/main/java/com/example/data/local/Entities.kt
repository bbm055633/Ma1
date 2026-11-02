package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val category: String = "WORK", // WORK, PRAYER, FASTING, PERSONAL, MEETING
    val location: String = "",
    val isCompleted: Boolean = false,
    val isPrayerOverlapWarning: Boolean = false
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val vatRate: Double = 0.15, // 15% Saudi VAT
    val category: String, // تسويق, رواتب, ضيافة, مشتريات, تقنية, تشغيل
    val isCorporate: Boolean = true,
    val dateMillis: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val agentTag: String = "SECRETARY", // SECRETARY, LEGAL, HR, BUSINESS, FINANCE
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDateMillis: Long = System.currentTimeMillis() + 86400000L,
    val priority: String = "HIGH", // HIGH, MEDIUM, LOW
    val category: String = "PROJECT", // PROJECT, MEETING, PERSONAL, FINANCE
    val isCompleted: Boolean = false,
    val assignedAgent: String = "Mobeen AI"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val agentId: String, // general, secretary, business, accountant, financial, sales, hr, executive, legal, travel
    val sender: String, // user, agent
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "prayers")
data class PrayerEntity(
    @PrimaryKey val date: String,
    val fajr: String = "NONE",
    val fajrQ: Boolean = false,
    val fajrB: Boolean = false,
    val dhuhr: String = "NONE",
    val dhuhrQ: Boolean = false,
    val dhuhrB: Boolean = false,
    val asr: String = "NONE",
    val asrQ: Boolean = false,
    val asrB: Boolean = false,
    val maghrib: String = "NONE",
    val maghribQ: Boolean = false,
    val maghribB: Boolean = false,
    val isha: String = "NONE",
    val ishaQ: Boolean = false,
    val ishaB: Boolean = false,
    val duha: Boolean = false,
    val morningAdhkar: Boolean = false,
    val eveningAdhkar: Boolean = false,
    val qiyam: Boolean = false,
    val fasting: Boolean = false
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val passwordHash: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "hijri_worship_schedules")
data class HijriWorshipScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, // e.g., "صيام الأيام البيض", "غرة رمضان", "صيام عاشوراء"
    val description: String,
    val hijriDay: Int,
    val hijriMonthName: String, // e.g. "رمضان", "شعبان", "محرم"
    val worshipType: String, // "FASTING", "PRAYER", "OCCASION"
    val reminderTimestamp: Long, // Epoch millis for notification alert
    val isAlertEnabled: Boolean = true
)

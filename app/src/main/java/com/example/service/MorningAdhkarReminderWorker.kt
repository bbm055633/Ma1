package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.domain.models.AgentDataDefaults
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MorningAdhkarReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val prefs = appContext.getSharedPreferences("fahad_ai_prefs", Context.MODE_PRIVATE)
            val isReminderEnabled = prefs.getBoolean("morning_adhkar_reminder_enabled", true)
            if (!isReminderEnabled) {
                return Result.success()
            }

            ZheenNotificationHelper.createNotificationChannels(appContext)

            val database = AppDatabase.getDatabase(appContext)
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

            val prayerList = database.prayerDao().getPrayersForDate(todayStr).firstOrNull() ?: emptyList()
            val prayer = prayerList.firstOrNull()

            val defaultItems = AgentDataDefaults.MORNING_ADHKAR
            val isFullyCompleted = prayer?.morningAdhkar ?: false

            if (isFullyCompleted) {
                return Result.success()
            }

            // Parse progress string
            val countsMap = mutableMapOf<Int, Int>()
            prayer?.morningAdhkarProgress?.let { progressStr ->
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
            }

            val uncompletedCount = defaultItems.count { item ->
                val curr = countsMap[item.id] ?: 0
                curr < item.targetCount
            }

            if (uncompletedCount > 0) {
                ZheenNotificationHelper.showSpiritualReminderNotification(
                    context = appContext,
                    notificationId = 3001,
                    title = "تذكير أذكار الصباح ☀️",
                    message = "لم تكتمل أذكار الصباح بعد. متبقي لديك $uncompletedCount أذكار ليتم حصنك اليومي."
                )
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}

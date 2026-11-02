package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            ZheenNotificationHelper.createNotificationChannels(appContext)

            val database = AppDatabase.getDatabase(appContext)
            val nowMillis = System.currentTimeMillis()
            val windowEndMillis = nowMillis + (45 * 60 * 1000L) // Next 45 minutes

            val allEvents = database.calendarDao().getAllEvents().firstOrNull() ?: emptyList()
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val upcomingEvents = allEvents.filter { event ->
                !event.isCompleted &&
                        event.startTimeMillis >= (nowMillis - 5 * 60 * 1000L) &&
                        event.startTimeMillis <= windowEndMillis
            }

            upcomingEvents.forEachIndexed { index, event ->
                val timeStr = timeFormat.format(event.startTimeMillis)
                val diffMinutes = ((event.startTimeMillis - nowMillis) / (60 * 1000L)).coerceAtLeast(0)

                val timingDesc = if (diffMinutes <= 5) {
                    "يبدأ خلال $diffMinutes دقائق ($timeStr)"
                } else {
                    "يبدأ بعد $diffMinutes دقيقة ($timeStr)"
                }

                ZheenNotificationHelper.showTaskReminderNotification(
                    context = appContext,
                    notificationId = 2000 + event.id.toInt() + index,
                    taskTitle = event.title,
                    timeDescription = timingDesc,
                    location = event.location
                )
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}

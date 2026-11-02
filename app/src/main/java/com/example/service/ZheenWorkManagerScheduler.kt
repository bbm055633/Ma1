package com.example.service

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

object ZheenWorkManagerScheduler {

    private const val DAILY_PRAYER_SCHEDULER_TAG = "zheen_daily_prayer_scheduler"
    private const val PRAYER_PERIODIC_WORK_TAG = "zheen_prayer_periodic_work"
    private const val TASK_PERIODIC_WORK_TAG = "zheen_task_periodic_work"

    private fun getWorkManagerSafely(context: Context): WorkManager? {
        return try {
            WorkManager.getInstance(context)
        } catch (_: Exception) {
            null
        }
    }

    fun scheduleBackgroundServices(context: Context) {
        // Initialize Notification Channels
        ZheenNotificationHelper.createNotificationChannels(context)

        val workManager = getWorkManagerSafely(context) ?: return

        try {
            // 1. Daily Location-based Prayer Calculator & Alert Scheduler scheduled at 02:00 AM daily (قبل أذان الفجر)
            val nowCal = java.util.Calendar.getInstance()
            val targetCal = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 2)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
                if (before(nowCal)) {
                    add(java.util.Calendar.DAY_OF_YEAR, 1)
                }
            }
            val delayTo2AmMillis = targetCal.timeInMillis - nowCal.timeInMillis

            val daily2AmSchedulerRequest = PeriodicWorkRequestBuilder<DailyPrayerSchedulerWorker>(
                24, TimeUnit.HOURS,
                30, TimeUnit.MINUTES
            )
                .setInitialDelay(delayTo2AmMillis, TimeUnit.MILLISECONDS)
                .addTag(DAILY_PRAYER_SCHEDULER_TAG)
                .build()

            workManager.enqueueUniquePeriodicWork(
                DAILY_PRAYER_SCHEDULER_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                daily2AmSchedulerRequest
            )

            // Trigger immediate calculation & enqueueing of today's 5-minute alerts based on location
            val initialDailySchedule = OneTimeWorkRequestBuilder<DailyPrayerSchedulerWorker>().build()
            workManager.enqueueUniqueWork(
                "initial_daily_prayer_schedule",
                ExistingWorkPolicy.REPLACE,
                initialDailySchedule
            )

            // 2. Periodic Task Reminder Work (every 15 minutes for calendar events)
            val taskWorkRequest = PeriodicWorkRequestBuilder<TaskReminderWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .addTag(TASK_PERIODIC_WORK_TAG)
                .build()

            workManager.enqueueUniquePeriodicWork(
                TASK_PERIODIC_WORK_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                taskWorkRequest
            )

            // 3. Daily Morning Adhkar Reminder Work
            scheduleMorningAdhkarReminder(context)
        } catch (_: Exception) {
        }
    }

    fun scheduleMorningAdhkarReminder(context: Context, hour: Int = 8, minute: Int = 0) {
        val workManager = getWorkManagerSafely(context) ?: return
        try {
            val nowCal = java.util.Calendar.getInstance()
            val targetCal = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, hour)
                set(java.util.Calendar.MINUTE, minute)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
                if (before(nowCal)) {
                    add(java.util.Calendar.DAY_OF_YEAR, 1)
                }
            }
            val delayMillis = targetCal.timeInMillis - nowCal.timeInMillis

            val adhkarRequest = PeriodicWorkRequestBuilder<MorningAdhkarReminderWorker>(
                24, TimeUnit.HOURS,
                30, TimeUnit.MINUTES
            )
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .addTag("zheen_morning_adhkar_reminder")
                .build()

            workManager.enqueueUniquePeriodicWork(
                "zheen_morning_adhkar_reminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                adhkarRequest
            )
        } catch (_: Exception) {
        }
    }

    /**
     * Trigger immediate 5-minute pre-prayer alert and silent mode toggle for test/verification.
     */
    fun trigger5MinPrePrayerAlertTest(
        context: Context,
        prayerName: String = "الظهر",
        prayerTime: String = "12:15 م"
    ) {
        val workManager = getWorkManagerSafely(context) ?: return
        try {
            val testAlertWork = OneTimeWorkRequestBuilder<PrayerPreAlertWorker>()
                .setInputData(
                    workDataOf(
                        PrayerPreAlertWorker.KEY_PRAYER_NAME to prayerName,
                        PrayerPreAlertWorker.KEY_PRAYER_TIME to prayerTime,
                        PrayerPreAlertWorker.KEY_AUTO_SILENT to true,
                        PrayerPreAlertWorker.KEY_RESTORE_DELAY_MINS to 2L // 2 minutes for test
                    )
                )
                .build()

            workManager.enqueue(testAlertWork)
        } catch (_: Exception) {
        }
    }

    /**
     * Trigger an immediate background check for prayer & task reminders.
     */
    fun triggerImmediateCheck(context: Context) {
        val workManager = getWorkManagerSafely(context) ?: return
        try {
            val dailyNow = OneTimeWorkRequestBuilder<DailyPrayerSchedulerWorker>().build()
            val taskNow = OneTimeWorkRequestBuilder<TaskReminderWorker>().build()
            workManager.enqueue(dailyNow)
            workManager.enqueue(taskNow)
        } catch (_: Exception) {
        }
    }

    fun cancelAllServices(context: Context) {
        val workManager = getWorkManagerSafely(context) ?: return
        try {
            workManager.cancelUniqueWork(DAILY_PRAYER_SCHEDULER_TAG)
            workManager.cancelUniqueWork(PRAYER_PERIODIC_WORK_TAG)
            workManager.cancelUniqueWork(TASK_PERIODIC_WORK_TAG)
        } catch (_: Exception) {
        }
    }
}

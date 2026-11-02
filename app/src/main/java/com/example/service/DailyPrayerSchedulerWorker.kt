package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.domain.prayer.PrayerTimeCalculator
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailyPrayerSchedulerWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val workManager = WorkManager.getInstance(appContext)
            val nowMillis = System.currentTimeMillis()

            // 1. Resolve Saudi Arabia location
            val location = PrayerTimeCalculator.resolveSaudiLocation(appContext)

            // 2. Calculate today's prayer schedule
            val scheduleToday = PrayerTimeCalculator.calculatePrayerScheduleForDay(
                city = location,
                calendar = Calendar.getInstance()
            )

            // 3. Schedule 5-minute pre-alert workers for each prayer
            scheduleToday.forEach { item ->
                val delayMillis = item.alert5MinBeforeMillis - nowMillis

                if (delayMillis > 0) {
                    val preAlertWork = OneTimeWorkRequestBuilder<PrayerPreAlertWorker>()
                        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                        .setInputData(
                            workDataOf(
                                PrayerPreAlertWorker.KEY_PRAYER_NAME to item.name,
                                PrayerPreAlertWorker.KEY_PRAYER_TIME to item.formattedTime,
                                PrayerPreAlertWorker.KEY_AUTO_SILENT to true,
                                PrayerPreAlertWorker.KEY_RESTORE_DELAY_MINS to 35L
                            )
                        )
                        .addTag("prayer_pre_alert_${item.name}")
                        .build()

                    workManager.enqueueUniqueWork(
                        "unique_prayer_alert_${item.name}",
                        ExistingWorkPolicy.REPLACE,
                        preAlertWork
                    )
                } else if (nowMillis in item.alert5MinBeforeMillis..item.prayerTimeMillis) {
                    // We are currently in the 5-minute window right before prayer! Trigger immediately
                    val immediateAlert = OneTimeWorkRequestBuilder<PrayerPreAlertWorker>()
                        .setInputData(
                            workDataOf(
                                PrayerPreAlertWorker.KEY_PRAYER_NAME to item.name,
                                PrayerPreAlertWorker.KEY_PRAYER_TIME to item.formattedTime,
                                PrayerPreAlertWorker.KEY_AUTO_SILENT to true,
                                PrayerPreAlertWorker.KEY_RESTORE_DELAY_MINS to 35L
                            )
                        )
                        .build()

                    workManager.enqueue(immediateAlert)
                }
            }

            // Also check tomorrow's Fajr if all prayers today have elapsed
            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            val scheduleTomorrow = PrayerTimeCalculator.calculatePrayerScheduleForDay(
                city = location,
                calendar = tomorrow
            )
            val tomorrowFajr = scheduleTomorrow.firstOrNull { it.name == "الفجر" }
            if (tomorrowFajr != null) {
                val delayTomorrowFajr = tomorrowFajr.alert5MinBeforeMillis - nowMillis
                if (delayTomorrowFajr > 0) {
                    val tomorrowFajrWork = OneTimeWorkRequestBuilder<PrayerPreAlertWorker>()
                        .setInitialDelay(delayTomorrowFajr, TimeUnit.MILLISECONDS)
                        .setInputData(
                            workDataOf(
                                PrayerPreAlertWorker.KEY_PRAYER_NAME to "الفجر",
                                PrayerPreAlertWorker.KEY_PRAYER_TIME to tomorrowFajr.formattedTime,
                                PrayerPreAlertWorker.KEY_AUTO_SILENT to true,
                                PrayerPreAlertWorker.KEY_RESTORE_DELAY_MINS to 35L
                            )
                        )
                        .build()

                    workManager.enqueueUniqueWork(
                        "unique_prayer_alert_tomorrow_fajr",
                        ExistingWorkPolicy.REPLACE,
                        tomorrowFajrWork
                    )
                }
            }

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }
}

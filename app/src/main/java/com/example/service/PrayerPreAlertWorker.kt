package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class PrayerPreAlertWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_PRAYER_NAME = "prayer_name"
        const val KEY_PRAYER_TIME = "prayer_time"
        const val KEY_AUTO_SILENT = "auto_silent"
        const val KEY_RESTORE_DELAY_MINS = "restore_delay_mins"
    }

    override suspend fun doWork(): Result {
        return try {
            ZheenNotificationHelper.createNotificationChannels(appContext)

            val prayerName = inputData.getString(KEY_PRAYER_NAME) ?: "الصلاة"
            val prayerTime = inputData.getString(KEY_PRAYER_TIME) ?: ""
            val autoSilent = inputData.getBoolean(KEY_AUTO_SILENT, true)
            val restoreDelayMins = inputData.getLong(KEY_RESTORE_DELAY_MINS, 35L)

            // 1. Trigger local notification 5 minutes before prayer
            val notificationTitle = "اقتراب موعد صلاة $prayerName بعد 5 دقائق 🕌"
            val notificationMessage = if (prayerTime.isNotBlank()) {
                "متبقي 5 دقائق على رفع أذان $prayerName ($prayerTime). استعد للوضوء والصلاة."
            } else {
                "متبقي 5 دقائق على موعد أذان $prayerName. استعد للوضوء والصلاة."
            }

            ZheenNotificationHelper.showPrayerNotification(
                context = appContext,
                notificationId = prayerName.hashCode(),
                title = notificationTitle,
                message = "$notificationMessage تم تفعيل وضع الصامت التلقائي قبل الإقامة.",
                isDndAlert = autoSilent
            )

            // 2. Adhere to silent mode toggling requirement
            if (autoSilent) {
                // Switch to silent / vibrate mode
                SilentModeManager.enableSilentMode(appContext)

                // Schedule RestoreRingerWorker to automatically restore normal mode after prayer concludes
                val restoreWork = OneTimeWorkRequestBuilder<RestoreRingerWorker>()
                    .setInitialDelay(restoreDelayMins, TimeUnit.MINUTES)
                    .setInputData(
                        workDataOf(
                            RestoreRingerWorker.KEY_PRAYER_NAME to prayerName
                        )
                    )
                    .addTag("restore_ringer_$prayerName")
                    .build()

                WorkManager.getInstance(appContext).enqueue(restoreWork)
            }

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }
}

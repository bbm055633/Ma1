package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class RestoreRingerWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_PRAYER_NAME = "prayer_name"
    }

    override suspend fun doWork(): Result {
        return try {
            val prayerName = inputData.getString(KEY_PRAYER_NAME) ?: "الصلاة"

            // 1. Restore device to normal sound mode
            SilentModeManager.restoreNormalMode(appContext)

            // 2. Notify user that prayer period concluded and sound is restored
            ZheenNotificationHelper.showPrayerNotification(
                context = appContext,
                notificationId = (prayerName + "_restored").hashCode(),
                title = "انتهت فترة صلاة $prayerName 🔔",
                message = "تقبّل الله طاعتكم. تم استعادة الوضع العام لصوت الهاتف تلقائياً.",
                isDndAlert = false
            )

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }
}

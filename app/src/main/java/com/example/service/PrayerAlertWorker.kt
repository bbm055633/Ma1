package com.example.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.domain.prayer.PrayerTimeCalculator
import java.util.Calendar

class PrayerAlertWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            ZheenNotificationHelper.createNotificationChannels(appContext)
            val now = Calendar.getInstance()
            val prayerTimes = PrayerTimeCalculator.calculatePrayerTimes(calendar = now)
            val hijriInfo = PrayerTimeCalculator.getEstimatedHijriDate(now)

            val minutesUntilNext = prayerTimes.minutesUntilNext
            val nextPrayer = prayerTimes.nextPrayerName

            // 1. Alert 15 minutes before Azan
            if (minutesUntilNext in 10..15) {
                ZheenNotificationHelper.showPrayerNotification(
                    context = appContext,
                    notificationId = 1001,
                    title = "اقتراب موعد صلاة $nextPrayer 🕌",
                    message = "متبقي حوالي $minutesUntilNext دقيقة على أذان $nextPrayer (${prayerTimes.nextPrayerTime}). استعد للوضوء والصلاة."
                )
            }
            // 2. Alert at Azan or Iqamah window
            else if (minutesUntilNext in 0..5) {
                ZheenNotificationHelper.showPrayerNotification(
                    context = appContext,
                    notificationId = 1002,
                    title = "حان الآن موعد أذان $nextPrayer 🕌",
                    message = "الله أكبر، الله أكبر. دخل وقت صلاة $nextPrayer. تم تفعيل الوضع الصامت التلقائي قبل الإقامة.",
                    isDndAlert = true
                )
            }

            // 3. Fasting Reminders: Suhoor / Iftar
            if (hijriInfo.fastingTitle != null) {
                val currentHour = now.get(Calendar.HOUR_OF_DAY)
                val currentMin = now.get(Calendar.MINUTE)

                // Suhoor reminder in early morning (around 4:00 - 4:30 AM)
                if (currentHour == 4 && currentMin in 0..20) {
                    ZheenNotificationHelper.showSpiritualReminderNotification(
                        context = appContext,
                        notificationId = 1003,
                        title = "تذكير السحور والنية 🌙",
                        message = "اقترب موعد أذان الفجر (${prayerTimes.fajr}). تذكير بتناول السحور وتجديد نية ${hijriInfo.fastingTitle}."
                    )
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}

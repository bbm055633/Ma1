package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.domain.prayer.PrayerTimeCalculator
import java.util.Calendar

/**
 * مجدول تنبيهات مواقيت الصلاة والأذان عبر AlarmManager بدقة عالية
 * باستخدام setExactAndAllowWhileIdle للعمل أثناء قفل الشاشة ووضع السكون.
 */
object PrayerAlarmScheduler {

    fun scheduleAllPrayers(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // التحقق من صلاحية الجدولة الدقيقة على أندرويد 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // إذا لم تكن الصلاحية ممنوحة نكتفي بالجدولة عبر WorkManager
                return
            }
        }

        val city = PrayerTimeCalculator.resolveSaudiLocation(context)
        val today = Calendar.getInstance()
        val schedule = PrayerTimeCalculator.calculatePrayerScheduleForDay(city, today)

        val nowMillis = System.currentTimeMillis()

        schedule.forEachIndexed { index, prayerItem ->
            val prayerTimeMillis = prayerItem.prayerTimeMillis

            // جدولة الصلوات التي لم يحن وقتها بعد لليوم الحالي
            if (prayerTimeMillis > nowMillis) {
                val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                    putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayerItem.name)
                    putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_TIME, prayerItem.formattedTime)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    100 + index,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        prayerTimeMillis,
                        pendingIntent
                    )
                } catch (_: SecurityException) {
                    // في حال رفض النظام الجدولة الدقيقة
                }
            }
        }
    }

    fun cancelAllPrayerAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        for (i in 0..6) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                100 + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}

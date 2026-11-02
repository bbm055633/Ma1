package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import sa.com.fahada.R

/**
 * مستقبل تنبيهات الأذان عبر AlarmManager بدقة عالية.
 * يشغل الأذان الكامل أو التنبيه القصير أو الصامت وفق تفضيل المستخدم في الإعدادات.
 */
class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "الصلاة"
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME) ?: ""

        val prefs = context.getSharedPreferences("hayati_prayer_prefs", Context.MODE_PRIVATE)
        val alertMode = prefs.getString("adhan_alert_mode", "FULL_ADHAN") ?: "FULL_ADHAN"
        val muadhinName = prefs.getString("selected_muadhin", "الحرم المكي") ?: "الحرم المكي"

        // 1. عرض الإشعار في القناة المناسبة
        when (alertMode) {
            "FULL_ADHAN" -> {
                ZheenNotificationHelper.showAdhanFullNotification(
                    context = context,
                    notificationId = getNotificationIdForPrayer(prayerName),
                    prayerName = prayerName,
                    prayerTime = prayerTime,
                    muadhin = muadhinName
                )
                playAdhanAudio(context)
            }
            "SHORT_ALERT" -> {
                ZheenNotificationHelper.showPrayerNotification(
                    context = context,
                    notificationId = getNotificationIdForPrayer(prayerName),
                    title = "حان الآن موعد أذان $prayerName 🕌",
                    message = "دخل وقت صلاة $prayerName ($prayerTime). استعد لأداء الصلاة."
                )
            }
            "SILENT" -> {
                ZheenNotificationHelper.showSilentPrayerNotification(
                    context = context,
                    notificationId = getNotificationIdForPrayer(prayerName),
                    title = "حان موعد صلاة $prayerName 🔕",
                    message = "دخل وقت الصلاة ($prayerTime). تم تفعيل التنبيه الصامت."
                )
            }
        }

        // 2. إعادة جدولة الصلوات للأيام القادمة تلقائياً
        PrayerAlarmScheduler.scheduleAllPrayers(context)
    }

    private fun playAdhanAudio(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Hayati:AdhanWakeLock"
        )
        wakeLock?.acquire(180_000L) // 3 minutes timeout

        try {
            stopAdhan()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                val afd = context.resources.openRawResourceFd(R.raw.adhan)
                if (afd != null) {
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                    prepare()
                    start()
                    setOnCompletionListener {
                        stopAdhan()
                        if (wakeLock?.isHeld == true) wakeLock.release()
                    }
                } else {
                    if (wakeLock?.isHeld == true) wakeLock.release()
                }
            }
        } catch (_: Exception) {
            if (wakeLock?.isHeld == true) wakeLock.release()
        }
    }

    private fun getNotificationIdForPrayer(prayerName: String): Int {
        return when (prayerName) {
            "الفجر" -> 2001
            "الظهر" -> 2002
            "العصر" -> 2003
            "المغرب" -> 2004
            "العشاء" -> 2005
            else -> 2000
        }
    }

    companion object {
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_PRAYER_TIME = "extra_prayer_time"

        private var mediaPlayer: MediaPlayer? = null

        fun stopAdhan() {
            try {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                }
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (_: Exception) {
                mediaPlayer = null
            }
        }
    }
}

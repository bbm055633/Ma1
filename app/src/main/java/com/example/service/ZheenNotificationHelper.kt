package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import sa.com.fahada.R

object ZheenNotificationHelper {

    const val CHANNEL_ADHAN_FULL = "hayati_adhan_full_channel"
    const val CHANNEL_PRAYER = "zheen_prayer_channel"
    const val CHANNEL_TASKS = "zheen_tasks_channel"
    const val CHANNEL_SPIRITUAL = "zheen_spiritual_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val adhanSoundUri = android.net.Uri.parse("android.resource://${context.packageName}/${R.raw.adhan}")
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                .build()

            val adhanChannel = NotificationChannel(
                CHANNEL_ADHAN_FULL,
                "صوت الأذان الكامل عند الصلاة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعار صوتي كامل للأذان بصوت المؤذن المختار عند دخول الوقت"
                setSound(adhanSoundUri, audioAttributes)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER,
                "تنبيهات مواقيت الصلاة والأذان",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات دخول وقت الصلاة والتنبيه المسبق والوضع الصامت"
                enableVibration(true)
            }

            val tasksChannel = NotificationChannel(
                CHANNEL_TASKS,
                "تذكيرات المواعيد والمهام",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات تذكيرية باجتماعات العمل ومواعيد الأجندة اليومية"
                enableVibration(true)
            }

            val spiritualChannel = NotificationChannel(
                CHANNEL_SPIRITUAL,
                "تذكيرات الصيام والورد القرآني",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات السحور والإفطار والورد القرآني اليومي وأذكار الصباح والمساء"
            }

            manager.createNotificationChannels(listOf(adhanChannel, prayerChannel, tasksChannel, spiritualChannel))
        }
    }

    fun showPrayerNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        isDndAlert: Boolean = false
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_PRAYER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setColor(0xFF0F766E.toInt()) // Emerald Primary
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        if (isDndAlert) {
            builder.setSubText("وضع الصامت التلقائي مفعّل 🔕")
        }

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled when permission is not yet granted
        }
    }

    fun showTaskReminderNotification(
        context: Context,
        notificationId: Int,
        taskTitle: String,
        timeDescription: String,
        location: String = ""
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullMessage = buildString {
            append("الموعد: ").append(timeDescription)
            if (location.isNotBlank()) {
                append("\nالموقع: ").append(location)
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_TASKS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("تذكير موعد: $taskTitle")
            .setContentText(fullMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fullMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFF0F766E.toInt())
            .setCategory(NotificationCompat.CATEGORY_EVENT)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled
        }
    }

    fun showSpiritualReminderNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_SPIRITUAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFFF59E0B.toInt()) // Gold
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled
        }
    }

    fun showAdhanFullNotification(
        context: Context,
        notificationId: Int,
        prayerName: String,
        prayerTime: String,
        muadhin: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "الله أكبر • أذان $prayerName 🕌"
        val message = "حان الآن موعد أذان $prayerName ($prayerTime) • بصوت مؤذن $muadhin"

        val builder = NotificationCompat.Builder(context, CHANNEL_ADHAN_FULL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFF064E3B.toInt()) // Emerald Green
            .setOngoing(false)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled when permission not granted
        }
    }

    fun showSilentPrayerNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_PRAYER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSilent(true)
            .setColor(0xFF064E3B.toInt())

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Handled
        }
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun openNotificationSettingsIntent(context: Context): Intent {
        return Intent().apply {
            action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
            } else {
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                data = android.net.Uri.parse("package:${context.packageName}")
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}

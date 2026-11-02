package com.example.service

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log

object SilentModeManager {
    private const val TAG = "SilentModeManager"
    private const val PREFS_NAME = "silent_mode_prefs"
    private const val KEY_PREVIOUS_RINGER_MODE = "previous_ringer_mode"

    @Volatile
    private var inMemoryPreviousRingerMode: Int? = null

    /**
     * Toggles device to silent mode or vibration mode during prayer/iqamah.
     * Records and stores the original ringer mode before changing it.
     *
     * @param context Android context
     * @param targetMode Optional custom target mode (default SILENT or VIBRATE depending on policy)
     * @return true if mode was changed successfully, false otherwise.
     */
    fun enableSilentMode(context: Context, targetMode: Int? = null): Boolean {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            if (audioManager == null) {
                Log.e(TAG, "فشل الحصول على AudioManager لتفعيل الوضع الصامت")
                return false
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            // حفظ وضع الصوت الأصلي للجهاز قبل التغيير
            val currentMode = audioManager.ringerMode
            inMemoryPreviousRingerMode = currentMode
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_PREVIOUS_RINGER_MODE, currentMode)
                .apply()
            Log.d(TAG, "تم حفظ وضع الصوت الأصلي: $currentMode")

            val desiredMode = targetMode ?: run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (notificationManager?.isNotificationPolicyAccessGranted == true) {
                        AudioManager.RINGER_MODE_SILENT
                    } else {
                        AudioManager.RINGER_MODE_VIBRATE
                    }
                } else {
                    AudioManager.RINGER_MODE_SILENT
                }
            }

            audioManager.ringerMode = desiredMode
            Log.i(TAG, "تم تفعيل الوضع الصامت بنجاح إلى: $desiredMode")
            true
        } catch (e: Exception) {
            Log.e(TAG, "خطأ أثناء تفعيل الوضع الصامت: ${e.message}", e)
            false
        }
    }

    /**
     * Restores device to its exact original ringer mode recorded before prayer,
     * defaulting to NORMAL if no prior mode was stored.
     *
     * @param context Android context
     * @param fallbackMode Mode to use if original mode cannot be determined (defaults to RINGER_MODE_NORMAL)
     * @return true if mode was restored successfully, false otherwise.
     */
    fun restoreNormalMode(context: Context, fallbackMode: Int = AudioManager.RINGER_MODE_NORMAL): Boolean {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            if (audioManager == null) {
                Log.e(TAG, "فشل الحصول على AudioManager لاستعادة وضع الصوت")
                return false
            }

            // استرجاع الوضع الأصلي بدقة
            val savedMode = inMemoryPreviousRingerMode ?: run {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                if (prefs.contains(KEY_PREVIOUS_RINGER_MODE)) {
                    prefs.getInt(KEY_PREVIOUS_RINGER_MODE, fallbackMode)
                } else {
                    fallbackMode
                }
            }

            audioManager.ringerMode = savedMode
            Log.i(TAG, "تم استعادة وضع الصوت الأصلي للجهاز بنجاح: $savedMode")

            // مسح القيمة المخزنة بعد الاستعادة
            inMemoryPreviousRingerMode = null
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_PREVIOUS_RINGER_MODE)
                .apply()

            true
        } catch (e: Exception) {
            Log.e(TAG, "خطأ أثناء استعادة وضع الصوت الأصلي: ${e.message}", e)
            false
        }
    }

    fun isSilentModeActive(context: Context): Boolean {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.ringerMode != AudioManager.RINGER_MODE_NORMAL
        } catch (e: Exception) {
            Log.e(TAG, "خطأ أثناء فحص حالة الصوت: ${e.message}", e)
            false
        }
    }

    fun getStoredPreviousRingerMode(context: Context): Int? {
        return inMemoryPreviousRingerMode ?: run {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            if (prefs.contains(KEY_PREVIOUS_RINGER_MODE)) {
                prefs.getInt(KEY_PREVIOUS_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL)
            } else {
                null
            }
        }
    }
}

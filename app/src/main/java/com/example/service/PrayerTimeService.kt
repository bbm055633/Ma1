package com.example.service

import android.content.Context
import com.example.domain.prayer.CityLocation
import com.example.domain.prayer.HijriDateInfo
import com.example.domain.prayer.PrayerScheduleItem
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.domain.prayer.PrayerTimes
import java.util.Calendar

/**
 * Service that calculates daily prayer times based on the user's location in Saudi Arabia
 * using reliable astronomical algorithms (Umm Al-Qura standard) and schedules WorkManager jobs
 * for 5-minute pre-prayer notifications and silent mode management.
 */
interface PrayerTimeService {

    /**
     * Obtains the resolved location for the user in Saudi Arabia (via GPS/Network or fallback city).
     */
    fun getResolvedLocation(): CityLocation

    /**
     * Calculates daily prayer times (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha, Next Prayer countdown).
     */
    fun calculatePrayerTimes(
        city: CityLocation = getResolvedLocation(),
        calendar: Calendar = Calendar.getInstance()
    ): PrayerTimes

    /**
     * Computes the exact millisecond schedule for all 5 prayers, including the 5-minute pre-alert timestamps.
     */
    fun calculatePrayerSchedule(
        city: CityLocation = getResolvedLocation(),
        calendar: Calendar = Calendar.getInstance()
    ): List<PrayerScheduleItem>

    /**
     * Retrieves current Hijri date information and fasting days (White Days, Mondays, Thursdays).
     */
    fun getHijriInfo(calendar: Calendar = Calendar.getInstance()): HijriDateInfo

    /**
     * Schedules periodic and daily WorkManager background jobs for prayer notifications.
     */
    fun scheduleWorkManagerAlerts()

    /**
     * Triggers an immediate 5-minute pre-prayer alert and silent mode toggle for test or preview.
     */
    fun triggerImmediateAlertTest(prayerName: String, prayerTime: String)

    /**
     * Cancels all scheduled WorkManager prayer jobs.
     */
    fun cancelAllAlerts()

    companion object {
        @Volatile
        private var INSTANCE: PrayerTimeService? = null

        fun getInstance(context: Context): PrayerTimeService {
            return INSTANCE ?: synchronized(this) {
                val instance = DefaultPrayerTimeService(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Default implementation of PrayerTimeService adhering to Umm Al-Qura algorithms
 * and Android WorkManager architecture.
 */
class DefaultPrayerTimeService(private val context: Context) : PrayerTimeService {

    override fun getResolvedLocation(): CityLocation {
        return PrayerTimeCalculator.resolveSaudiLocation(context)
    }

    override fun calculatePrayerTimes(city: CityLocation, calendar: Calendar): PrayerTimes {
        return PrayerTimeCalculator.calculatePrayerTimes(city, calendar)
    }

    override fun calculatePrayerSchedule(city: CityLocation, calendar: Calendar): List<PrayerScheduleItem> {
        return PrayerTimeCalculator.calculatePrayerScheduleForDay(city, calendar)
    }

    override fun getHijriInfo(calendar: Calendar): HijriDateInfo {
        return PrayerTimeCalculator.getEstimatedHijriDate(calendar)
    }

    override fun scheduleWorkManagerAlerts() {
        ZheenWorkManagerScheduler.scheduleBackgroundServices(context)
    }

    override fun triggerImmediateAlertTest(prayerName: String, prayerTime: String) {
        ZheenWorkManagerScheduler.trigger5MinPrePrayerAlertTest(context, prayerName, prayerTime)
    }

    override fun cancelAllAlerts() {
        ZheenWorkManagerScheduler.cancelAllServices(context)
    }
}

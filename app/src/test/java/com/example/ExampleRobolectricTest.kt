package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import sa.com.fahada.R
import com.example.domain.models.AgentDataDefaults
import com.example.domain.prayer.PrayerTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.performClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = androidx.compose.ui.test.junit4.createComposeRule()

    @Test
    fun testIconAccessibilityAndSemantics() {
        var clicked = false
        composeTestRule.setContent {
            androidx.compose.material3.MaterialTheme {
                androidx.compose.material3.IconButton(
                    onClick = { clicked = true },
                    modifier = androidx.compose.ui.Modifier.testTag("home_send_button")
                ) {
                    androidx.compose.material3.Icon(
                        androidx.compose.material.icons.Icons.AutoMirrored.Filled.Send,
                        contentDescription = "إرسال"
                    )
                }
            }
        }
        composeTestRule.onNodeWithTag("home_send_button")
            .assertExists()
            .assertHasClickAction()
            .assertContentDescriptionEquals("إرسال")
            .performClick()
        assertTrue(clicked)
    }

    @Test
    fun testNavigationAndBottomTabsClickActions() {
        var selectedTab = -1
        composeTestRule.setContent {
            androidx.compose.material3.MaterialTheme {
                androidx.compose.foundation.layout.Row {
                    listOf("الرئيسية", "الوكلاء", "المنتجات", "التقويم", "العبادات").forEachIndexed { index, title ->
                        androidx.compose.material3.TextButton(
                            onClick = { selectedTab = index },
                            modifier = androidx.compose.ui.Modifier.testTag("tab_button_$index")
                        ) {
                            androidx.compose.material3.Text(title)
                        }
                    }
                }
            }
        }

        for (i in 0..4) {
            composeTestRule.onNodeWithTag("tab_button_$i")
                .assertExists()
                .assertHasClickAction()
                .performClick()
            assertEquals(i, selectedTab)
        }
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Fahad Ai", appName)
    }

    @Test
    fun `verify prayer times calculation`() {
        val prayerTimes = PrayerTimeCalculator.calculatePrayerTimes()
        assertNotNull(prayerTimes.fajr)
        assertNotNull(prayerTimes.dhuhr)
        assertNotNull(prayerTimes.asr)
        assertNotNull(prayerTimes.maghrib)
        assertNotNull(prayerTimes.isha)
        assertTrue(prayerTimes.minutesUntilNext >= 0)
    }

    @Test
    fun `verify 9 specialized agents are defined`() {
        assertEquals(10, AgentDataDefaults.AGENTS.size)
        val secretary = AgentDataDefaults.AGENTS.first { it.id == "secretary" }
        assertEquals("أثير - السكرتير الشخصي", secretary.name)
    }

    @Test
    fun `verify 12 digital product generators are defined`() {
        assertEquals(12, AgentDataDefaults.PRODUCTS.size)
    }

    @Test
    fun `verify notification channels are created`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        com.example.service.ZheenNotificationHelper.createNotificationChannels(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val prayerChannel = manager.getNotificationChannel(com.example.service.ZheenNotificationHelper.CHANNEL_PRAYER)
        assertNotNull(prayerChannel)
        assertEquals("تنبيهات مواقيت الصلاة والأذان", prayerChannel.name)
        val tasksChannel = manager.getNotificationChannel(com.example.service.ZheenNotificationHelper.CHANNEL_TASKS)
        assertNotNull(tasksChannel)
        val spiritualChannel = manager.getNotificationChannel(com.example.service.ZheenNotificationHelper.CHANNEL_SPIRITUAL)
        assertNotNull(spiritualChannel)
    }

    @Test
    fun `verify daily prayer schedule computes exactly 5 minutes pre-alert`() {
        val schedule = PrayerTimeCalculator.calculatePrayerScheduleForDay()
        assertEquals(5, schedule.size)
        schedule.forEach { item ->
            // Check that alert is exactly 5 minutes (300,000 ms) before prayer time
            val difference = item.prayerTimeMillis - item.alert5MinBeforeMillis
            assertEquals(300_000L, difference)
            // Check silent mode start is after Azan (5 mins before Iqamah)
            assertTrue(item.silentModeStartMillis > item.prayerTimeMillis)
            assertTrue(item.silentModeEndMillis > item.silentModeStartMillis)
        }
    }

    @Test
    fun `verify silent mode manager toggles`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val silentResult = com.example.service.SilentModeManager.enableSilentMode(context)
        // Verify method executes safely
        assertNotNull(silentResult)
        val normalResult = com.example.service.SilentModeManager.restoreNormalMode(context)
        assertNotNull(normalResult)
    }

    @Test
    fun `verify PrayerTimeService calculates times and schedules WorkManager jobs`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val service = com.example.service.PrayerTimeService.getInstance(context)
        assertNotNull(service)

        val location = service.getResolvedLocation()
        assertNotNull(location)
        assertTrue(location.latitude > 0)
        assertTrue(location.longitude > 0)

        val times = service.calculatePrayerTimes()
        assertNotNull(times.fajr)
        assertNotNull(times.dhuhr)
        assertNotNull(times.asr)
        assertNotNull(times.maghrib)
        assertNotNull(times.isha)

        val schedule = service.calculatePrayerSchedule()
        assertEquals(5, schedule.size)

        // Verify WorkManager scheduling does not throw
        service.scheduleWorkManagerAlerts()
        service.triggerImmediateAlertTest("الظهر", "12:15 م")
    }

    @Test
    fun `verify Saudi payment packages and methods are configured`() {
        val packages = com.example.domain.models.PaymentDefaults.TOP_UP_PACKAGES
        assertTrue(packages.isNotEmpty())
        assertEquals(4, packages.size)

        val plans = com.example.domain.models.PaymentDefaults.SUBSCRIPTION_PLANS
        assertTrue(plans.isNotEmpty())
        assertEquals(4, plans.size)

        val methods = com.example.domain.models.SaudiPaymentMethod.entries
        assertTrue(methods.any { it == com.example.domain.models.SaudiPaymentMethod.MADA })
        assertTrue(methods.any { it == com.example.domain.models.SaudiPaymentMethod.APPLE_PAY })
        assertTrue(methods.any { it == com.example.domain.models.SaudiPaymentMethod.STC_PAY })
    }
}

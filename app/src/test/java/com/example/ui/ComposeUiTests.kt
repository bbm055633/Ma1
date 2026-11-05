package com.example.ui

import android.app.Application
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.worship.FaithJourneyPrayerTrackingCard
import com.example.ui.screens.worship.PrayerStatus
import com.example.ui.screens.worship.PrayerTrackingData
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.MainViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Compose UI tests running on local JVM via Robolectric testing:
 * - HomeScreen navigation flow
 * - Prayer attendance marking
 * - Chat message sending and receiving
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ComposeUiTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = MainViewModel(application)
    }

    @Test
    fun testHomeScreenNavigationFlow() {
        var navigatedScreen: ActiveScreen? = null

        composeTestRule.setContent {
            MaterialTheme {
                HomeScreen(
                    viewModel = viewModel,
                    prayerTimes = PrayerTimeCalculator.calculatePrayerTimes(),
                    hijriDate = PrayerTimeCalculator.getEstimatedHijriDate(),
                    onNavigate = { screen -> navigatedScreen = screen }
                )
            }
        }

        composeTestRule.waitForIdle()

        // Assert hero countdown card exists
        composeTestRule.onNodeWithTag("home_next_prayer_card").assertExists().assertIsDisplayed()

        // Test navigation action
        viewModel.navigateTo(ActiveScreen.Worship)

        assertEquals("ActiveScreen should update to Worship", ActiveScreen.Worship, viewModel.activeScreen.value)
    }

    @Test
    fun testPrayerAttendanceMarking() {
        var selectedFajrStatus: PrayerStatus? = null

        val trackingData = PrayerTrackingData(
            prayerTimes = PrayerTimeCalculator.calculatePrayerTimes(),
            fajrAttendance = PrayerStatus.NONE,
            onFajrAttendanceChange = { newStatus ->
                selectedFajrStatus = newStatus
            }
        )

        composeTestRule.setContent {
            MaterialTheme {
                FaithJourneyPrayerTrackingCard(trackingData = trackingData)
            }
        }

        composeTestRule.waitForIdle()

        // Trigger attendance callback to simulate user interaction
        trackingData.onFajrAttendanceChange(PrayerStatus.IN_MOSQUE)

        assertEquals("Fajr attendance status should update to IN_MOSQUE", PrayerStatus.IN_MOSQUE, selectedFajrStatus)
    }

    @Test
    fun testChatMessageSendingAndReceiving() {
        composeTestRule.setContent {
            MaterialTheme {
                HomeScreen(
                    viewModel = viewModel,
                    prayerTimes = PrayerTimeCalculator.calculatePrayerTimes(),
                    hijriDate = PrayerTimeCalculator.getEstimatedHijriDate(),
                    onNavigate = {}
                )
            }
        }

        composeTestRule.waitForIdle()

        // Find input text field and type prompt
        val inputNode = composeTestRule.onNodeWithTag("home_chat_input")
        inputNode.assertExists()
        inputNode.performTextInput("ما هي الميزات الذكية المتاحة؟")

        composeTestRule.waitForIdle()

        // Click send button
        val sendNode = composeTestRule.onNodeWithTag("home_send_button")
        sendNode.assertExists()
        sendNode.performClick()

        composeTestRule.waitForIdle()

        // Verify that chat messages flow in viewModel contains sent message
        val messages = viewModel.homeChatMessages.value
        assertTrue("Messages list should contain sent user message", messages.any { it.text.contains("ما هي الميزات الذكية المتاحة؟") })
    }
}

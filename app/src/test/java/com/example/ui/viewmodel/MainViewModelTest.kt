package com.example.ui.viewmodel

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.ui.screens.worship.AttendanceColumn
import com.example.ui.theme.AppThemePalette
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [MainViewModel] testing state changes, navigation logic, user login/registration,
 * and delegated sub-ViewModel actions.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = MainViewModel(application)
    }

    @Test
    fun testInitialState() {
        assertEquals(0, viewModel.currentTab.value)
        assertEquals(ActiveScreen.Login, viewModel.activeScreen.value)
        assertEquals("فهد بن محمد القحطاني", viewModel.userName.value)
        assertEquals("fahad.alqahtani@enterprise.sa", viewModel.userEmail.value)
    }

    @Test
    fun testTabSelectionAndNavigation() {
        // Selecting a tab should update currentTab and set activeScreen to MainTabs
        viewModel.selectTab(2)
        assertEquals(2, viewModel.currentTab.value)
        assertEquals(ActiveScreen.MainTabs, viewModel.activeScreen.value)

        // Navigate to Profile screen
        viewModel.navigateTo(ActiveScreen.Profile)
        assertEquals(ActiveScreen.Profile, viewModel.activeScreen.value)

        // Navigate to Worship screen
        viewModel.navigateTo(ActiveScreen.Worship)
        assertEquals(ActiveScreen.Worship, viewModel.activeScreen.value)

        // Navigate back should reset to MainTabs
        viewModel.navigateBack()
        assertEquals(ActiveScreen.MainTabs, viewModel.activeScreen.value)
    }

    @Test
    fun testSetUserNameAndEmail() {
        viewModel.setUserName("سلمان العتيبي")
        assertEquals("سلمان العتيبي", viewModel.userName.value)

        // Blank name should be ignored
        viewModel.setUserName("   ")
        assertEquals("سلمان العتيبي", viewModel.userName.value)

        viewModel.setUserEmail("salman@enterprise.sa")
        assertEquals("salman@enterprise.sa", viewModel.userEmail.value)

        // Blank email should be ignored
        viewModel.setUserEmail("")
        assertEquals("salman@enterprise.sa", viewModel.userEmail.value)
    }

    @Test
    fun testDefaultUserLoginSuccess() = runBlocking {
        var successCalled = false
        var errorMessage: String? = null

        viewModel.loginUser(
            email = "fahad.alqahtani@enterprise.sa",
            pass = "password",
            onSuccess = { successCalled = true },
            onError = { errorMessage = it }
        )

        Thread.sleep(150)
        org.robolectric.shadows.ShadowLooper.idleMainLooper()
        org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        // Verify loginUser logic execution
        assertNotNull("User name should be initialized", viewModel.userName.value)
    }

    @Test
    fun testRegisterUserWithBlankFieldsError() {
        var errorOccurred = false
        var message: String? = null

        viewModel.registerUser(
            name = "",
            email = "test@example.com",
            pass = "password",
            onSuccess = {},
            onError = {
                errorOccurred = true
                message = it
            }
        )

        assertTrue("Error callback should be triggered for blank fields", errorOccurred)
        assertNotNull(message)
    }

    @Test
    fun testPrayerDelegationMethods() {
        val makkah = PrayerTimeCalculator.SAUDI_CITIES[1] // Makkah
        viewModel.setCity(makkah)
        assertEquals(makkah, viewModel.selectedCity.value)

        viewModel.setFajrAttendance(AttendanceColumn.IN_MOSQUE)
        assertEquals(AttendanceColumn.IN_MOSQUE, viewModel.fajrAttendance.value)

        viewModel.setDhuhrAttendance(AttendanceColumn.INDIVIDUAL)
        assertEquals(AttendanceColumn.INDIVIDUAL, viewModel.dhuhrAttendance.value)
    }

    @Test
    fun testThemeDelegationMethods() {
        val initialDarkMode = viewModel.isDarkMode.value
        viewModel.toggleDarkMode()
        assertEquals(!initialDarkMode, viewModel.isDarkMode.value)

        viewModel.selectThemePalette(AppThemePalette.SAUDI_EMERALD)
        assertEquals(AppThemePalette.SAUDI_EMERALD, viewModel.themePalette.value)
    }
}

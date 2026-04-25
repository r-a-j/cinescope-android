package com.example.cinescopesurat.ui.viewmodel

import app.cash.turbine.test
import com.example.cinescopesurat.data.AppTheme
import com.example.cinescopesurat.data.ThemePreferences
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val themePreferences = mockk<ThemePreferences>(relaxed = true)
    private lateinit var viewModel: ThemeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(AppCompatDelegate::class)
        every { AppCompatDelegate.setDefaultNightMode(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `themeState reflects preferences`() = runTest {
        every { themePreferences.themeFlow } returns flowOf(AppTheme.DARK)
        viewModel = ThemeViewModel(themePreferences)

        viewModel.themeState.test {
            // Initial value from stateIn
            assertEquals(AppTheme.SYSTEM, awaitItem())
            // Value from themePreferences.themeFlow
            assertEquals(AppTheme.DARK, awaitItem())
        }
    }

    @Test
    fun `setTheme calls saveTheme and updates AppCompatDelegate`() = runTest {
        viewModel = ThemeViewModel(themePreferences)
        viewModel.setTheme(AppTheme.DARK)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { themePreferences.saveTheme(AppTheme.DARK) }
        coVerify { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
    }
}

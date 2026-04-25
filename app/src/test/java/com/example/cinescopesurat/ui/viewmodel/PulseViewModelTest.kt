package com.example.cinescopesurat.ui.viewmodel

import app.cash.turbine.test
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.data.repository.MediaRepository
import io.mockk.every
import io.mockk.mockk
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
class PulseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<MediaRepository>()
    private lateinit var viewModel: PulseViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState initially emits loading then trending movies`() = runTest {
        val movies = listOf(MediaItem(1, "Test Movie", "8.5"))
        every { repository.getTrendingMovies() } returns flowOf(movies)

        viewModel = PulseViewModel(repository)

        viewModel.uiState.test {
            // Initial value (isLoading = true)
            val firstItem = awaitItem()
            assertEquals(true, firstItem.isLoading)
            
            // Values from repository (mapped state)
            val secondItem = awaitItem()
            assertEquals(movies, secondItem.trendingMovies)
            assertEquals(false, secondItem.isLoading)
        }
    }
}

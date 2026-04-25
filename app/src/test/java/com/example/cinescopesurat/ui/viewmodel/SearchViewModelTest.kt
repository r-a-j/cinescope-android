package com.example.cinescopesurat.ui.viewmodel

import app.cash.turbine.test
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.data.model.Person
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<MediaRepository>()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        viewModel = SearchViewModel(repository)
        assertEquals("", viewModel.uiState.value.query)
        assertEquals(true, viewModel.uiState.value.results.isEmpty())
    }

    @Test
    fun `onQueryChanged updates query and fetches results`() = runTest {
        val movies = listOf(MediaItem(1, "Inception", "8.8"))
        val tvShows = emptyList<MediaItem>()
        val people = emptyList<Person>()
        
        every { repository.searchMovies("Inc") } returns flowOf(movies)
        every { repository.searchTvShows("Inc") } returns flowOf(tvShows)
        every { repository.searchPeople("Inc") } returns flowOf(people)

        viewModel = SearchViewModel(repository)
        
        viewModel.uiState.test {
            // Initial state
            assertEquals("", awaitItem().query)
            
            viewModel.onQueryChanged("Inc")
            
            // Loading state
            val loadingItem = awaitItem()
            assertEquals("Inc", loadingItem.query)
            assertEquals(true, loadingItem.isLoading)
            
            // Results state
            val resultItem = awaitItem()
            val movieResult = resultItem.results.first() as SearchResult.Movie
            assertEquals("Inception", movieResult.item.title)
            assertEquals(false, resultItem.isLoading)
        }
    }
}

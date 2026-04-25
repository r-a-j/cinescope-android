package com.example.cinescopesurat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.data.model.Person
import com.example.cinescopesurat.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchResult {
    data class Movie(val item: MediaItem) : SearchResult
    data class TvShow(val item: MediaItem) : SearchResult
    data class PersonResult(val person: Person) : SearchResult
}

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val isAiSearchEnabled: Boolean = false,
    val showAiTrigger: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, isLoading = true, isAiSearchEnabled = false) }
        search(newQuery)
    }

    fun triggerAiSearch() {
        _uiState.update { it.copy(isLoading = true, isAiSearchEnabled = true, showAiTrigger = false) }
        // TODO: Implement actual AI search logic with GenAI/Vertex
        viewModelScope.launch {
            delay(1500) // Simulate AI thinking
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun search(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false, showAiTrigger = false) }
            return
        }

        viewModelScope.launch {
            combine(
                repository.searchMovies(query),
                repository.searchTvShows(query),
                repository.searchPeople(query)
            ) { movies, tvShows, people ->
                val mixedResults = mutableListOf<SearchResult>()
                
                // Simple ranking: exact matches or starts-with first
                // For now, just interleaving or prioritizing based on simple logic
                mixedResults.addAll(movies.map { SearchResult.Movie(it) })
                mixedResults.addAll(tvShows.map { SearchResult.TvShow(it) })
                mixedResults.addAll(people.map { SearchResult.PersonResult(it) })

                // Sort by title/name length as a proxy for "simpler/more direct" match
                mixedResults.sortedBy { 
                    when(it) {
                        is SearchResult.Movie -> it.item.title.length
                        is SearchResult.TvShow -> it.item.title.length
                        is SearchResult.PersonResult -> it.person.name.length
                    }
                }
            }.collect { mixed ->
                _uiState.update { 
                    it.copy(
                        results = mixed, 
                        isLoading = false,
                        showAiTrigger = mixed.size < 3 // Show AI if few results
                    ) 
                }
            }
        }
    }
}

package com.example.cinescopesurat.ui.viewmodel

import androidx.compose.ui.graphics.Color
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
    val trendingMovies: List<SearchResult.Movie> = emptyList(),
    val trendingPeople: List<SearchResult.PersonResult> = emptyList(),
    val categories: List<Pair<String, Color>> = emptyList(),
    val oracleThoughts: String = "",
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

    init {
        loadDiscoveryData()
    }

    private fun loadDiscoveryData() {
        viewModelScope.launch {
            combine(
                repository.getTrendingMovies(),
                repository.searchPeople("")
            ) { movies, people ->
                _uiState.update { 
                    it.copy(
                        trendingMovies = movies.take(6).map { m -> SearchResult.Movie(m) },
                        trendingPeople = people.take(8).map { p -> SearchResult.PersonResult(p) },
                        categories = listOf(
                            "Sci-Fi" to Color(0xFFD2A8FF),
                            "Action" to Color(0xFFF85149),
                            "Drama" to Color(0xFF79C0FF),
                            "Indie" to Color(0xFFFF9F0A)
                        )
                    )
                }
            }.collect()
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, isLoading = true, isAiSearchEnabled = false) }
        search(newQuery)
    }

    fun triggerAiSearch() {
        _uiState.update { it.copy(isLoading = true, isAiSearchEnabled = true, showAiTrigger = false) }
        
        viewModelScope.launch {
            val thoughts = listOf(
                "Consulting the archives...",
                "Deciphering your cinematic intent...",
                "Scanning the multiverse of stories...",
                "Synthesizing the perfect recommendation...",
                "Almost there. The Oracle has spoken."
            )
            
            for (thought in thoughts) {
                _uiState.update { it.copy(oracleThoughts = thought) }
                delay(800)
            }
            
            _uiState.update { it.copy(isLoading = false, oracleThoughts = "") }
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

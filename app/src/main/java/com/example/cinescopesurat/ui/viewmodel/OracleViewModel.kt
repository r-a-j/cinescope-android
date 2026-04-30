package com.example.cinescopesurat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

data class OracleUiState(
    val oraclesChoice: MediaItem? = null,
    val prophecies: List<Pair<String, Int>> = emptyList(), // Genre and count
    val vibeInsights: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isSpinning: Boolean = false,
    val randomThought: String = "",
    val spinsLeft: Int = 2, // 2 manual spins left after the first auto-spin
    val lastSpinDate: String? = null,
    val error: String? = null
)

@HiltViewModel
class OracleViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OracleUiState())
    val uiState: StateFlow<OracleUiState> = _uiState.asStateFlow()

    init {
        val today = todayDateString()
        _uiState.update { it.copy(lastSpinDate = today) }
        loadOracleData()
    }

    private fun todayDateString(): String = java.time.LocalDate.now().toString()

    fun spinAgain() {
        val today = todayDateString()
        val state = _uiState.value

        // Re-entrancy protection & Limit check
        if (state.isSpinning || state.isLoading) return

        viewModelScope.launch {
            if (state.lastSpinDate != today) {
                // New day reset
                _uiState.update { 
                    it.copy(
                        isSpinning = true, 
                        spinsLeft = 2, 
                        lastSpinDate = today,
                        error = null 
                    ) 
                }
                delay(1200)
                loadOracleDataInternal()
            } else if (state.spinsLeft > 0) {
                // Consume a spin
                _uiState.update { 
                    it.copy(
                        isSpinning = true, 
                        spinsLeft = state.spinsLeft - 1,
                        error = null
                    ) 
                }
                delay(1200)
                loadOracleDataInternal()
            }
        }
    }

    private fun loadOracleData() {
        viewModelScope.launch {
            loadOracleDataInternal()
        }
    }

    private suspend fun loadOracleDataInternal() {
        try {
            val movies = mutableListOf<MediaItem>()
            repository.getTrendingMovies().first().let { trendingMovies ->
                movies.addAll(trendingMovies)
            }

            if (movies.isEmpty()) {
                throw Exception("The Oracle's archives are empty today.")
            }

            // Pick a random "oracle's choice" from trending
            val choice = movies.shuffled().first()
            
            // Generate prophecies
            val prophecies = listOf(
                "SCI-FI" to movies.size,
                "ACTION" to (movies.size - 1),
                "DRAMA" to (movies.size - 2),
                "INDIE" to (movies.size / 2)
            ).filter { it.second > 0 }

            // Generate vibe insights
            val insights = listOf(
                "The multiverse speaks: ${choice.title} resonates with the zeitgeist.",
                "Your cinematic aura aligns with ${choice.title}—a prestige experience awaits.",
                "The Oracle has decoded your essence: prepare for revelations.",
                "Trending across dimensions: ${choice.title} shatters expectations.",
                "A constellation of stories converges here. ${choice.title} leads the way.",
                "The archives suggest: you are ready for this."
            )

            val thoughts = listOf(
                "Consulting the archives...",
                "Deciphering your cinematic intent...",
                "Synthesizing the perfect prophecy...",
                "The Oracle has spoken.",
                "Multiverse calculations complete...",
                "Your destiny awaits..."
            )

            _uiState.update {
                it.copy(
                    oraclesChoice = choice,
                    prophecies = prophecies,
                    vibeInsights = insights,
                    randomThought = thoughts.random(),
                    isLoading = false,
                    isSpinning = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSpinning = false,
                    error = e.message ?: "The Oracle is momentarily obscured."
                )
            }
        }
    }
}

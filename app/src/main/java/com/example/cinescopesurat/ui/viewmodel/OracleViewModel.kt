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
    val error: String? = null,
    // Oracle Ritual Upgrade: New fields
    val currentRoll: Int = 0, // 0, 1, 2 - tracks sequence progression
    val rollHistory: List<MediaItem> = emptyList(), // Previous rolls in this session
    val isDestinyLocked: Boolean = false // True when 3rd roll is complete
)

@HiltViewModel
class OracleViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OracleUiState())
    val uiState: StateFlow<OracleUiState> = _uiState.asStateFlow()

    // Re-entrancy protection: track if a roll is in progress
    private var isRolling = false

    init {
        val today = todayDateString()
        _uiState.update { it.copy(lastSpinDate = today) }
        loadOracleData()
    }

    private fun todayDateString(): String = java.time.LocalDate.now().toString()

    fun spinAgain() {
        val today = todayDateString()
        val state = _uiState.value

        // Re-entrancy protection: block double-taps
        if (isRolling || state.isSpinning || state.isLoading || state.isDestinyLocked) return

        // Check if we've reached 3 rolls (Destiny Locked)
        if (state.currentRoll >= 2) {
            _uiState.update { it.copy(isDestinyLocked = true) }
            return
        }

        viewModelScope.launch {
            isRolling = true

            try {
                if (state.lastSpinDate != today) {
                    // New day reset: reset sequence
                    _uiState.update {
                        it.copy(
                            isSpinning = true,
                            currentRoll = 0,
                            rollHistory = emptyList(),
                            isDestinyLocked = false,
                            lastSpinDate = today,
                            error = null
                        )
                    }
                    delay(1200)
                    loadOracleDataInternal()
                } else {
                    // Continue sequence: advance to next roll
                    val nextRoll = state.currentRoll + 1
                    _uiState.update {
                        it.copy(
                            isSpinning = true,
                            currentRoll = nextRoll,
                            error = null
                        )
                    }
                    delay(1200)
                    loadOracleDataInternal()
                }
            } finally {
                isRolling = false
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
            val state = _uiState.value

            // Get data sources: prioritize Vault, fall back to Trending
            val vaultMovies = repository.getVaultMovies().first()
            val trendingMovies = repository.getTrendingMovies().first()

            // Data source priority: use Vault if >= 3 items, else use Trending
            val dataSource = if (vaultMovies.size >= 3) vaultMovies else trendingMovies

            if (dataSource.isEmpty()) {
                throw Exception("The Oracle's archives are empty today.")
            }

            // Enforce uniqueness: exclude movies already in rollHistory
            val availableMovies = dataSource.filter { movie ->
                !state.rollHistory.any { it.id == movie.id }
            }

            if (availableMovies.isEmpty()) {
                throw Exception("No new destinies remain to be revealed. Return tomorrow.")
            }

            // Pick a random choice from available unique movies
            val choice = availableMovies.shuffled().first()

            // Lock destiny on 3rd roll
            val isDestinyLocked = state.currentRoll >= 2

            // Generate prophecies
            val prophecies = listOf(
                "SCI-FI" to availableMovies.size,
                "ACTION" to (availableMovies.size - 1),
                "DRAMA" to (availableMovies.size - 2),
                "INDIE" to (availableMovies.size / 2)
            ).filter { it.second > 0 }

            // Generate vibe insights based on roll count
            val insights = when (state.currentRoll) {
                0 -> listOf(
                    "The multiverse speaks: ${choice.title} resonates with the zeitgeist.",
                    "Your cinematic aura aligns with ${choice.title}—a prestige experience awaits.",
                    "The Oracle has decoded your essence: prepare for revelations.",
                    "First vision: ${choice.title} emerges from the void."
                )
                1 -> listOf(
                    "A second path converges: ${choice.title}.",
                    "The threads of fate intertwine. ${choice.title} awaits.",
                    "Deeper into the archives: ${choice.title} calls.",
                    "The veil thins. Another revelation: ${choice.title}."
                )
                else -> listOf(
                    "The final truth crystallizes: ${choice.title}.",
                    "Your destiny is sealed. ${choice.title} is your chosen path.",
                    "The Oracle's wisdom culminates here. Heed this final vision.",
                    "The threads converge. Your cinematic fate: ${choice.title}."
                )
            }

            val thoughts = listOf(
                "Consulting the archives...",
                "Deciphering your cinematic intent...",
                "Synthesizing the perfect prophecy...",
                "The Oracle has spoken.",
                "Multiverse calculations complete...",
                "Your destiny awaits..."
            )

            // Update roll history with the new choice
            val newHistory = state.rollHistory.toMutableList().apply { add(choice) }

            _uiState.update {
                it.copy(
                    oraclesChoice = choice,
                    prophecies = prophecies,
                    vibeInsights = insights,
                    randomThought = thoughts.random(),
                    rollHistory = newHistory,
                    isDestinyLocked = isDestinyLocked,
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

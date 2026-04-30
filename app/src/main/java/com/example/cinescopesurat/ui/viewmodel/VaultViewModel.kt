package com.example.cinescopesurat.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class MovieVibe {
    LOW_STAKES,
    HIGH_ENERGY
}

data class VaultUiState(
    val selectedVibe: MovieVibe = MovieVibe.LOW_STAKES,
    val timeLimitMinutes: Int = 85,
    val matchCount: Int = 42
)

@HiltViewModel
class VaultViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    fun updateVibe(vibe: MovieVibe) {
        _uiState.update { 
            it.copy(
                selectedVibe = vibe,
                matchCount = calculateMatches(vibe, it.timeLimitMinutes)
            ) 
        }
    }

    fun updateTimeLimit(minutes: Float) {
        val mins = minutes.toInt()
        _uiState.update { 
            it.copy(
                timeLimitMinutes = mins,
                matchCount = calculateMatches(it.selectedVibe, mins)
            ) 
        }
    }

    private fun calculateMatches(vibe: MovieVibe, minutes: Int): Int {
        // Simulated logic: lower time or higher energy usually means fewer matches
        val base = if (vibe == MovieVibe.LOW_STAKES) 50 else 30
        return (base * (minutes / 120f) * (1.0 + (minutes % 10) / 10.0)).toInt().coerceAtLeast(0)
    }
}

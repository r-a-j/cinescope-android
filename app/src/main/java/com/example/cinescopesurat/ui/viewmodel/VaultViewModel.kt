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

enum class VaultContext {
    UNIFIED_LIST,
    PHYSICAL_SHELF,
    PURGATORY
}

enum class LibraryItemType {
    WATCHING,
    ADAPTATION,
    UNWATCHED
}

data class LibraryItem(
    val id: String,
    val title: String,
    val posterUrl: String,
    val type: LibraryItemType,
    val progress: Float? = null,
    val episodeInfo: String? = null,
    val duration: String? = null,
    val hasAdaptationLink: Boolean = false,
    val rating: Double? = null,
    val isFeatured: Boolean = false,
    val lastAccessed: String? = null // e.g., "2h ago"
)

enum class MaintenanceBannerType {
    ALERT,
    INFO
}

data class MaintenanceBanner(
    val title: String,
    val description: String,
    val type: MaintenanceBannerType,
    val isVisible: Boolean = true
)

data class VaultUiState(
    val selectedVibe: MovieVibe = MovieVibe.LOW_STAKES,
    val timeLimitMinutes: Int = 85,
    val matchCount: Int = 42,
    val selectedContext: VaultContext = VaultContext.UNIFIED_LIST,
    val maintenanceBanners: List<MaintenanceBanner> = listOf(
        MaintenanceBanner(
            title = "Watchlist Bankruptcy",
            description = "Your watchlist has grown too large. Time to curate!",
            type = MaintenanceBannerType.ALERT,
            isVisible = true
        ),
        MaintenanceBanner(
            title = "Franchise GPS",
            description = "Track your progress across franchises and series.",
            type = MaintenanceBannerType.INFO,
            isVisible = true
        )
    ),
    val libraryItems: List<LibraryItem> = listOf(
        LibraryItem(
            id = "dune",
            title = "Dune: Part Two",
            posterUrl = "https://example.com/dune2.jpg",
            type = LibraryItemType.ADAPTATION,
            hasAdaptationLink = true,
            rating = 8.9,
            isFeatured = true,
            lastAccessed = "Just now"
        ),
        LibraryItem(
            id = "sopranos",
            title = "The Sopranos",
            posterUrl = "https://example.com/sopranos.jpg",
            type = LibraryItemType.WATCHING,
            progress = 0.65f,
            episodeInfo = "S04E02",
            rating = 9.2
        ),
        LibraryItem(
            id = "three_body",
            title = "3 Body Problem",
            posterUrl = "https://example.com/threebody.jpg",
            type = LibraryItemType.ADAPTATION,
            hasAdaptationLink = true,
            rating = 7.8
        ),
        LibraryItem(
            id = "heat",
            title = "Heat",
            posterUrl = "https://example.com/heat.jpg",
            type = LibraryItemType.UNWATCHED,
            duration = "2h 15m",
            rating = 8.3
        ),
        LibraryItem(
            id = "oppenheimer",
            title = "Oppenheimer",
            posterUrl = "https://example.com/oppenheimer.jpg",
            type = LibraryItemType.UNWATCHED,
            duration = "3h 0m",
            rating = 8.4
        ),
        LibraryItem(
            id = "succession",
            title = "Succession",
            posterUrl = "https://example.com/succession.jpg",
            type = LibraryItemType.WATCHING,
            progress = 0.9f,
            episodeInfo = "S04E10",
            rating = 8.9
        )
    )
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

    fun updateContext(context: VaultContext) {
        _uiState.update { it.copy(selectedContext = context) }
    }

    private fun calculateMatches(vibe: MovieVibe, minutes: Int): Int {
        // Simulated logic: lower time or higher energy usually means fewer matches
        val base = if (vibe == MovieVibe.LOW_STAKES) 50 else 30
        return (base * (minutes / 120f) * (1.0 + (minutes % 10) / 10.0)).toInt().coerceAtLeast(0)
    }
}

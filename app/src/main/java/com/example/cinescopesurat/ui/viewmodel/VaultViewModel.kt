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

enum class VaultMediaType {
    MOVIES,
    TV_SHOWS
}

enum class VaultStatus {
    WATCHLIST,
    WATCHED
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
    val mediaType: VaultMediaType,
    val status: VaultStatus,
    val progress: Float? = null,
    val episodeInfo: String? = null,
    val duration: String? = null,
    val hasAdaptationLink: Boolean = false,
    val rating: Double? = null,
    val isFeatured: Boolean = false,
    val lastAccessed: String? = null,
    val whyFeatured: String? = null // e.g., "Trending in your circle"
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
    val selectedMediaType: VaultMediaType = VaultMediaType.MOVIES,
    val selectedStatus: VaultStatus = VaultStatus.WATCHLIST,
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
            mediaType = VaultMediaType.MOVIES,
            status = VaultStatus.WATCHLIST,
            hasAdaptationLink = true,
            rating = 8.9,
            isFeatured = true,
            lastAccessed = "Just now",
            whyFeatured = "THE ORACLE'S TOP PICK"
        ),
        LibraryItem(
            id = "sopranos",
            title = "The Sopranos",
            posterUrl = "https://example.com/sopranos.jpg",
            type = LibraryItemType.WATCHING,
            mediaType = VaultMediaType.TV_SHOWS,
            status = VaultStatus.WATCHLIST,
            progress = 0.65f,
            episodeInfo = "S04E02",
            rating = 9.2
        ),
        LibraryItem(
            id = "three_body",
            title = "3 Body Problem",
            posterUrl = "https://example.com/threebody.jpg",
            type = LibraryItemType.ADAPTATION,
            mediaType = VaultMediaType.TV_SHOWS,
            status = VaultStatus.WATCHLIST,
            hasAdaptationLink = true,
            rating = 7.8
        ),
        LibraryItem(
            id = "heat",
            title = "Heat",
            posterUrl = "https://example.com/heat.jpg",
            type = LibraryItemType.UNWATCHED,
            mediaType = VaultMediaType.MOVIES,
            status = VaultStatus.WATCHED,
            duration = "2h 15m",
            rating = 8.3
        ),
        LibraryItem(
            id = "oppenheimer",
            title = "Oppenheimer",
            posterUrl = "https://example.com/oppenheimer.jpg",
            type = LibraryItemType.UNWATCHED,
            mediaType = VaultMediaType.MOVIES,
            status = VaultStatus.WATCHED,
            duration = "3h 0m",
            rating = 8.4
        ),
        LibraryItem(
            id = "succession",
            title = "Succession",
            posterUrl = "https://example.com/succession.jpg",
            type = LibraryItemType.WATCHING,
            mediaType = VaultMediaType.TV_SHOWS,
            status = VaultStatus.WATCHED,
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

    fun updateMediaType(mediaType: VaultMediaType) {
        _uiState.update { it.copy(selectedMediaType = mediaType) }
    }

    fun updateStatus(status: VaultStatus) {
        _uiState.update { it.copy(selectedStatus = status) }
    }

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

    fun toggleItemStatus(itemId: String) {
        _uiState.update { state ->
            val updatedItems = state.libraryItems.map { item ->
                if (item.id == itemId) {
                    val newStatus = if (item.status == VaultStatus.WATCHLIST) VaultStatus.WATCHED else VaultStatus.WATCHLIST
                    item.copy(status = newStatus)
                } else {
                    item
                }
            }
            state.copy(libraryItems = updatedItems)
        }
    }

    fun removeItem(itemId: String) {
        _uiState.update { state ->
            val updatedItems = state.libraryItems.filter { it.id != itemId }
            state.copy(libraryItems = updatedItems)
        }
    }

    private fun calculateMatches(vibe: MovieVibe, minutes: Int): Int {
        val base = if (vibe == MovieVibe.LOW_STAKES) 50 else 30
        return (base * (minutes / 120f) * (1.0 + (minutes % 10) / 10.0)).toInt().coerceAtLeast(0)
    }
}

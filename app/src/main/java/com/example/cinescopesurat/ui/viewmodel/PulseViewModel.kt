package com.example.cinescopesurat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PulseUiState(
    val trendingMovies: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class PulseViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    val uiState: StateFlow<PulseUiState> = repository.getTrendingMovies()
        .map { movies -> PulseUiState(trendingMovies = movies) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PulseUiState(isLoading = true)
        )
}

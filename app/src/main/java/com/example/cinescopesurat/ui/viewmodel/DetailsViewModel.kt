package com.example.cinescopesurat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.data.model.Person
import com.example.cinescopesurat.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailsUiState {
    object Loading : DetailsUiState
    data class MovieSuccess(val movie: MediaItem) : DetailsUiState
    data class TvShowSuccess(val tvShow: MediaItem) : DetailsUiState
    data class PersonSuccess(val person: Person) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    fun loadMovie(id: Int) {
        viewModelScope.launch {
            repository.getMovieById(id)
                .map { movie ->
                    if (movie != null) DetailsUiState.MovieSuccess(movie)
                    else DetailsUiState.Error("Movie not found")
                }
                .catch { emit(DetailsUiState.Error(it.message ?: "Unknown error")) }
                .collect { _uiState.value = it }
        }
    }

    fun loadTvShow(id: Int) {
        viewModelScope.launch {
            repository.getTvShowById(id)
                .map { tvShow ->
                    if (tvShow != null) DetailsUiState.TvShowSuccess(tvShow)
                    else DetailsUiState.Error("TV Show not found")
                }
                .catch { emit(DetailsUiState.Error(it.message ?: "Unknown error")) }
                .collect { _uiState.value = it }
        }
    }

    fun loadPerson(id: Int) {
        viewModelScope.launch {
            repository.getPersonById(id)
                .map { person ->
                    if (person != null) DetailsUiState.PersonSuccess(person)
                    else DetailsUiState.Error("Person not found")
                }
                .catch { emit(DetailsUiState.Error(it.message ?: "Unknown error")) }
                .collect { _uiState.value = it }
        }
    }
}

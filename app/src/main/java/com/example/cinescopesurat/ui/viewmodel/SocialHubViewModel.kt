package com.example.cinescopesurat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SyncState {
    IDLE,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class SocialHubUiState(
    val syncState: SyncState = SyncState.IDLE,
    val connectedFriend: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class SocialHubViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SocialHubUiState())
    val uiState: StateFlow<SocialHubUiState> = _uiState.asStateFlow()

    fun startSync() {
        val state = _uiState.value
        if (state.syncState == SyncState.CONNECTING || state.syncState == SyncState.CONNECTED) return

        viewModelScope.launch {
            _uiState.update { it.copy(syncState = SyncState.CONNECTING, errorMessage = null) }
            
            // Simulated connection logic
            delay(3000)
            
            // Randomly succeed or fail for demo purposes
            if ((1..10).random() > 2) {
                _uiState.update { 
                    it.copy(
                        syncState = SyncState.CONNECTED, 
                        connectedFriend = "Alex M."
                    ) 
                }
            } else {
                _uiState.update { 
                    it.copy(
                        syncState = SyncState.ERROR, 
                        errorMessage = "No nearby friends found."
                    ) 
                }
            }
        }
    }

    fun disconnect() {
        _uiState.update { SocialHubUiState() }
    }
}

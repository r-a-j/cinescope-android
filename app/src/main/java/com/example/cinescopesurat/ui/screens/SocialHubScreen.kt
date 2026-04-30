package com.example.cinescopesurat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cinescopesurat.ui.components.CompromiseEngineCard
import com.example.cinescopesurat.ui.viewmodel.SocialHubViewModel

@Composable
fun SocialHubScreen(
    viewModel: SocialHubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 140.dp, bottom = 120.dp)
        ) {
            
            CompromiseEngineCard(
                syncState = uiState.syncState,
                connectedFriend = uiState.connectedFriend,
                errorMessage = uiState.errorMessage,
                onConnect = { viewModel.startSync() },
                onDisconnect = { viewModel.disconnect() }
            )
            
            // Future sections like "Nearby Friends", "Global Trending", etc.
        }
    }
}

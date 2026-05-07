package com.example.cinescopesurat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import com.example.cinescopesurat.ui.components.*
import androidx.compose.foundation.lazy.LazyListScope
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import com.example.cinescopesurat.ui.viewmodel.LibraryItem
import com.example.cinescopesurat.ui.viewmodel.VaultViewModel

@Composable
fun VaultScreen(
    viewModel: VaultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp)
            ) {
                item {
                    ContextSwitcher(
                        selectedContext = uiState.selectedContext,
                        onContextSelected = { viewModel.updateContext(it) }
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(uiState.maintenanceBanners) { banner ->
                    MaintenanceBanner(banner = banner)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    FitMyScheduleCard(
                        selectedVibe = uiState.selectedVibe,
                        timeLimit = uiState.timeLimitMinutes,
                        matchCount = uiState.matchCount,
                        onVibeSelected = { viewModel.updateVibe(it) },
                        onTimeLimitChanged = { viewModel.updateTimeLimit(it) }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    SectionHeader(
                        tag = "YOUR LIBRARY",
                        title = "Intelligent Collection"
                    )
                }

                intelligentLibraryGrid(uiState.libraryItems)
            }

            PhysicalBridgeFAB(
                onScanClick = { /* TODO: Implement UPC scanning */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .padding(bottom = 80.dp) // Offset for bottom nav
            )
        }
    }
}

private fun LazyListScope.intelligentLibraryGrid(libraryItems: List<LibraryItem>) {
    var i = 0
    while (i < libraryItems.size) {
        val item = libraryItems[i]
        if (item.isFeatured) {
            item {
                LibraryGridItem(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
            i++
        } else {
            val rowItems = mutableListOf<LibraryItem>()
            repeat(3) {
                if (i < libraryItems.size && !libraryItems[i].isFeatured) {
                    rowItems.add(libraryItems[i])
                    i++
                }
            }

            if (rowItems.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { rowItem ->
                            LibraryGridItem(
                                item = rowItem,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "VaultScreen")
@Composable
private fun VaultScreenPreview() {
    CinescopeTheme {
        VaultScreen()
    }
}


package com.example.cinescopesurat.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import com.example.cinescopesurat.ui.components.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import com.example.cinescopesurat.ui.viewmodel.LibraryItem
import com.example.cinescopesurat.ui.viewmodel.VaultMediaType
import com.example.cinescopesurat.ui.viewmodel.VaultStatus
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
                contentPadding = PaddingValues(top = 80.dp, bottom = 120.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "THE VAULT",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = (-2).sp
                        )
                        Text(
                            text = "Your personal media command center.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                item {
                    VaultCategoryTabs(
                        selectedType = uiState.selectedMediaType,
                        onTypeSelected = { viewModel.updateMediaType(it) }
                    )
                }

                item {
                    VaultStatusChips(
                        selectedStatus = uiState.selectedStatus,
                        onStatusSelected = { viewModel.updateStatus(it) }
                    )
                }

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

                val filteredItems = uiState.libraryItems.filter {
                    it.mediaType == uiState.selectedMediaType && it.status == uiState.selectedStatus
                }

                if (filteredItems.isEmpty()) {
                    item {
                        EmptyVaultState(uiState.selectedStatus)
                    }
                } else {
                    intelligentLibraryGrid(
                        libraryItems = filteredItems,
                        onStatusToggle = { viewModel.toggleItemStatus(it) },
                        onRemove = { viewModel.removeItem(it) }
                    )
                }
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

@Composable
private fun VaultCategoryTabs(
    selectedType: VaultMediaType,
    onTypeSelected: (VaultMediaType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        VaultCategoryTab(
            label = "Movies",
            selected = selectedType == VaultMediaType.MOVIES,
            onClick = { onTypeSelected(VaultMediaType.MOVIES) },
            modifier = Modifier.weight(1f)
        )
        VaultCategoryTab(
            label = "TV Shows",
            selected = selectedType == VaultMediaType.TV_SHOWS,
            onClick = { onTypeSelected(VaultMediaType.TV_SHOWS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun VaultCategoryTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        label = "tabContent"
    )

    Column(
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Black,
            color = contentColor,
            letterSpacing = 1.sp
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(width = 24.dp, height = 3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun VaultStatusChips(
    selectedStatus: VaultStatus,
    onStatusSelected: (VaultStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        VaultStatusChip(
            label = "Watchlist",
            selected = selectedStatus == VaultStatus.WATCHLIST,
            onClick = { onStatusSelected(VaultStatus.WATCHLIST) }
        )
        VaultStatusChip(
            label = "Watched",
            selected = selectedStatus == VaultStatus.WATCHED,
            onClick = { onStatusSelected(VaultStatus.WATCHED) }
        )
    }
}

@Composable
private fun VaultStatusChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
        label = "chipBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        label = "chipContent"
    )
    val borderColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun EmptyVaultState(status: VaultStatus) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp, horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (status == VaultStatus.WATCHLIST) Icons.Default.BookmarkBorder else Icons.Default.CheckCircleOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (status == VaultStatus.WATCHLIST) "Your Watchlist is empty" else "Nothing watched yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Text(
            text = "Go find your next favorite movie!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: Route back to discovery */ },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Explore Discovery",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun LazyListScope.intelligentLibraryGrid(
    libraryItems: List<LibraryItem>,
    onStatusToggle: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    // 1. ACTIVE SESSIONS (Resume Watching)
    val watchingItems = libraryItems.filter { (it.progress ?: 0f) > 0f }
    if (watchingItems.isNotEmpty()) {
        item {
            SectionHeader(tag = "RESUME", title = "Active Sessions")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                this.items(watchingItems) { item ->
                    LibraryGridItem(
                        item = item,
                        onStatusToggle = { onStatusToggle(item.id) },
                        onRemove = { onRemove(item.id) },
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }
    }

    // 2. FEATURED HERO & GRID
    var i = 0
    while (i < libraryItems.size) {
        val item = libraryItems[i]
        if (item.isFeatured) {
            item {
                FeaturedLibraryCard(
                    item = item,
                    onStatusToggle = { onStatusToggle(item.id) },
                    onRemove = { onRemove(item.id) },
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
            i++
        } else if ((item.progress ?: 0f) > 0f) {
            // Already shown in Active Sessions, skip in main grid unless it's a specific view
            i++
        } else {
            val rowItems = mutableListOf<LibraryItem>()
            repeat(3) {
                if (i < libraryItems.size && !libraryItems[i].isFeatured && (libraryItems[i].progress ?: 0f) == 0f) {
                    rowItems.add(libraryItems[i])
                    i++
                } else if (i < libraryItems.size && (libraryItems[i].isFeatured || (libraryItems[i].progress ?: 0f) > 0f)) {
                    // Break if we hit a featured item or a watching item we've already handled
                    return@repeat
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
                                onStatusToggle = { onStatusToggle(rowItem.id) },
                                onRemove = { onRemove(rowItem.id) },
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

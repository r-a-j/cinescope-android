package com.example.cinescopesurat.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.ui.tooling.preview.Preview
import com.example.cinescopesurat.ui.components.*
import com.example.cinescopesurat.ui.theme.BoldOrange
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import com.example.cinescopesurat.ui.viewmodel.*
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState

@Composable
fun VaultScreen(
    viewModel: VaultViewModel = hiltViewModel(),
    liquidState: LiquidState = rememberLiquidState()
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
                // 1. BRAND HEADER & MISSION CONTROL
                item {
                    VaultCommandHeader(insights = uiState.insights)
                }

                // 2. ACTIVE MISSIONS (Promoted for priority)
                val watchingItems = uiState.libraryItems.filter { (it.progress ?: 0f) > 0f }
                if (watchingItems.isNotEmpty()) {
                    item {
                        SectionHeader(tag = "CONTINUE", title = "Active Missions")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            items(watchingItems) { item ->
                                LibraryGridItem(
                                    item = item,
                                    onStatusToggle = { viewModel.toggleItemStatus(item.id) },
                                    onRemove = { viewModel.removeItem(item.id) },
                                    modifier = Modifier.width(180.dp)
                                )
                            }
                        }
                    }
                }

                // 3. MASTER CONTROLS (The Matrix)
                item {
                    VaultManagementMatrix(
                        selectedType = uiState.selectedMediaType,
                        selectedStatus = uiState.selectedStatus,
                        selectedContext = uiState.selectedContext,
                        onTypeSelected = { viewModel.updateMediaType(it) },
                        onStatusSelected = { viewModel.updateStatus(it) },
                        onContextSelected = { viewModel.updateContext(it) },
                        liquidState = liquidState
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                // 4. MAINTENANCE ENGINE (High-Fidelity Action Cards)
                items(uiState.maintenanceBanners) { banner ->
                    VaultMissionCard(banner = banner)
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // 5. THE INTELLIGENT COLLECTION (The Dynamic Grid)
                val filteredItems = uiState.libraryItems.filter {
                    it.mediaType == uiState.selectedMediaType && it.status == uiState.selectedStatus && (it.progress ?: 0f) == 0f
                }

                if (filteredItems.isEmpty() && watchingItems.isEmpty()) {
                    item { EmptyVaultState(uiState.selectedStatus) }
                } else if (filteredItems.isNotEmpty()) {
                    item {
                        SectionHeader(
                            tag = uiState.selectedStatus.name,
                            title = if (uiState.selectedStatus == VaultStatus.WATCHLIST) "Curated Watchlist" else "Watched Archive"
                        )
                    }
                    intelligentLibraryGrid(
                        libraryItems = filteredItems,
                        onStatusToggle = { viewModel.toggleItemStatus(it) },
                        onRemove = { viewModel.removeItem(it) }
                    )
                }
            }

            // PHYSICAL BRIDGE ACTION
            PhysicalBridgeFAB(
                onScanClick = { /* TODO: UPC Scan Engine */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .padding(bottom = 80.dp)
            )
        }
    }
}

@Composable
private fun VaultCommandHeader(insights: List<VaultInsight>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "THE VAULT",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-3).sp,
                    lineHeight = 56.sp
                )
                Text(
                    text = "Mission Control",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // CURATE TOGGLE
                Surface(
                    onClick = { /* TODO */ },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Tune, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))

                // VAULT HEALTH INDICATOR
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 0.72f },
                        modifier = Modifier.size(42.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        strokeWidth = 5.dp,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text(
                        "72%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        
        // INTELLIGENCE MODULES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IntelligenceModule(
                insight = insights[0],
                modifier = Modifier.weight(1.6f),
                isHighlight = true
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IntelligenceModule(insight = insights[1])
                IntelligenceModule(insight = insights[2])
            }
        }
    }
}

@Composable
private fun IntelligenceModule(
    insight: VaultInsight,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    Surface(
        modifier = modifier.height(if (isHighlight) 140.dp else 64.dp),
        shape = RoundedCornerShape(28.dp),
        color = if (isHighlight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = if (!isHighlight) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) else null
    ) {
        Column(
            modifier = Modifier.padding(if (isHighlight) 24.dp else 16.dp),
            verticalArrangement = if (isHighlight) Arrangement.SpaceBetween else Arrangement.Center
        ) {
            Text(
                insight.label, 
                style = MaterialTheme.typography.labelSmall, 
                color = if (isHighlight) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
            
            if (isHighlight) {
                Column {
                    Text(
                        insight.value,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = (-2).sp
                    )
                    insight.trend?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                Text(
                    insight.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun VaultManagementMatrix(
    selectedType: VaultMediaType,
    selectedStatus: VaultStatus,
    selectedContext: VaultContext,
    onTypeSelected: (VaultMediaType) -> Unit,
    onStatusSelected: (VaultStatus) -> Unit,
    onContextSelected: (VaultContext) -> Unit,
    liquidState: LiquidState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ROW 1: PRIMARY CATEGORIES (High Fidelity Tabs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MatrixTab(
                label = "MOVIES",
                icon = Icons.Default.Movie,
                selected = selectedType == VaultMediaType.MOVIES,
                onClick = { onTypeSelected(VaultMediaType.MOVIES) },
                modifier = Modifier.weight(1f)
            )
            MatrixTab(
                label = "TV SERIES",
                icon = Icons.Default.Tv,
                selected = selectedType == VaultMediaType.TV_SHOWS,
                onClick = { onTypeSelected(VaultMediaType.TV_SHOWS) },
                modifier = Modifier.weight(1f)
            )
        }

        // ROW 2: SUB-FILTERS (Status & Context)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // STATUS TOGGLE (Watchlist vs Watched)
            StatusPill(
                selectedStatus = selectedStatus,
                onStatusSelected = onStatusSelected,
                modifier = Modifier.weight(1f)
            )
            
            // CONTEXT SELECTOR (The Physical Gear)
            ContextGlassDial(
                selectedContext = selectedContext,
                onContextSelected = onContextSelected,
                liquidState = liquidState,
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}

@Composable
private fun MatrixTab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "scale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(300, easing = LinearOutSlowInEasing),
        label = "bg"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        label = "content"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = modifier
            .fillMaxHeight()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = contentColor,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun StatusPill(
    selectedStatus: VaultStatus,
    onStatusSelected: (VaultStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Box(modifier = Modifier.padding(4.dp)) {
            val isWatchlist = selectedStatus == VaultStatus.WATCHLIST
            
            // THE SLIDING "LIQUID" BLOB
            val bias by animateFloatAsState(
                targetValue = if (isWatchlist) -1f else 1f,
                animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow),
                label = "bias"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .align(BiasAlignment(horizontalBias = bias, verticalBias = 0f))
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isWatchlist) BoldOrange else MaterialTheme.colorScheme.secondary)
            )

            Row {
                StatusPillItem(
                    label = "W/L", 
                    selected = isWatchlist, 
                    onClick = { onStatusSelected(VaultStatus.WATCHLIST) }, 
                    modifier = Modifier.weight(1f)
                )
                StatusPillItem(
                    label = "DONE", 
                    selected = !isWatchlist, 
                    onClick = { onStatusSelected(VaultStatus.WATCHED) }, 
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatusPillItem(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelSmall, 
            fontWeight = FontWeight.Black, 
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun VaultMissionCard(banner: MaintenanceBanner) {
    val isAlert = banner.type == MaintenanceBannerType.ALERT
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .height(100.dp),
        shape = RoundedCornerShape(28.dp),
        color = if (isAlert) Color(0xFF8B0000).copy(alpha = 0.9f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (!isAlert) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) else null,
        onClick = { /* TODO: Launch Mission */ }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isAlert) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAlert) Icons.Default.Warning else Icons.Default.GpsFixed,
                    contentDescription = null,
                    tint = if (isAlert) Color.White else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = banner.title.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = if (isAlert) Color.White else MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )
                Text(
                    text = banner.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isAlert) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isAlert) Color.White.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun ContextGlassDial(
    selectedContext: VaultContext,
    onContextSelected: (VaultContext) -> Unit,
    liquidState: LiquidState,
    modifier: Modifier = Modifier
) {
    val glassColor = CinescopeTheme.customColors.glassBackground
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .liquid(liquidState) {
                frost = 6.dp
                tint = glassColor.copy(alpha = 0.12f)
            }
            .padding(4.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            VaultContext.entries.forEach { context ->
                val isSelected = selectedContext == context
                val icon = when(context) {
                    VaultContext.UNIFIED_LIST -> Icons.Default.AllInclusive
                    VaultContext.PHYSICAL_SHELF -> Icons.AutoMirrored.Filled.LibraryBooks
                    VaultContext.PURGATORY -> Icons.Default.Cyclone
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onContextSelected(context) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyVaultState(status: VaultStatus) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (status == VaultStatus.WATCHLIST) Icons.Default.Inventory2 else Icons.Default.TaskAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (status == VaultStatus.WATCHLIST) "Vault Initialized" else "Archive Empty",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
        Text(
            text = "Acquire cinema from the Oracle to populate.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun LazyListScope.intelligentLibraryGrid(
    libraryItems: List<LibraryItem>,
    onStatusToggle: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    // PRIMARY GRID (Featured & Regular)
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
        } else {
            val rowItems = mutableListOf<LibraryItem>()
            repeat(3) {
                if (i < libraryItems.size && !libraryItems[i].isFeatured) {
                    rowItems.add(libraryItems[i])
                    i++
                } else if (i < libraryItems.size && libraryItems[i].isFeatured) {
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

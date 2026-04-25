package com.example.cinescopesurat.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import com.example.cinescopesurat.ui.viewmodel.SearchResult
import com.example.cinescopesurat.ui.viewmodel.SearchViewModel
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.liquefiable

@Composable
fun SearchScreen(
    liquidState: LiquidState = rememberLiquidState(),
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AiPulseBackground(enabled = uiState.isAiSearchEnabled)

        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                SearchLoadingState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxSize()
                        .liquefiable(liquidState),
                    contentPadding = PaddingValues(
                        top = 120.dp,
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 120.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.showAiTrigger && !uiState.isAiSearchEnabled) {
                        item(span = { GridItemSpan(4) }) {
                            AiSearchTriggerCard(onTrigger = { viewModel.triggerAiSearch() })
                        }
                    }

                    itemsIndexed(
                        items = uiState.results,
                        key = { _, result -> 
                            when(result) {
                                is SearchResult.Movie -> "m_${result.item.id}"
                                is SearchResult.TvShow -> "t_${result.item.id}"
                                is SearchResult.PersonResult -> "p_${result.person.id}"
                            }
                        }
                    ) { index, result ->
                        SleekGridResultCard(result, index)
                    }

                    if (uiState.query.isNotEmpty() && uiState.results.isEmpty()) {
                        item(span = { GridItemSpan(4) }) {
                            SearchEmptyState(query = uiState.query) { viewModel.triggerAiSearch() }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                GlassSearchBar(
                    query = uiState.query,
                    onQueryChanged = { viewModel.onQueryChanged(it) },
                    isAiMode = uiState.isAiSearchEnabled,
                    liquidState = liquidState
                )
            }
        }
    }
}

@Composable
fun AiPulseBackground(enabled: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "aiPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    if (enabled) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                            Color.Transparent
                        )
                    )
                )
                .blur(100.dp)
        )
    }
}

@Composable
fun GlassSearchBar(
    query: String, 
    onQueryChanged: (String) -> Unit, 
    isAiMode: Boolean,
    liquidState: LiquidState
) {
    val customColors = CinescopeTheme.customColors
    val activeColor = MaterialTheme.colorScheme.primary
    
    val borderColor by animateColorAsState(
        targetValue = if (isAiMode) activeColor else Color.Transparent,
        label = "border"
    )

    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .liquid(liquidState) {
                    frost = 3.dp
                    refraction = 0.25f
                    curve = 0.25f
                    edge = 0.0f
                    saturation = 1.4f
                    dispersion = 0.08f
                    tint = if (isAiMode) activeColor.copy(alpha = 0.05f) else customColors.glassBackground.copy(alpha = 0.05f)
                }
                .border(
                    width = if (isAiMode) 1.dp else 0.dp,
                    color = borderColor.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        )

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (isAiMode) Icons.Default.AutoAwesome else Icons.Default.Search,
                contentDescription = null,
                tint = if (isAiMode) activeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = query,
                onValueChange = onQueryChanged,
                placeholder = { 
                    Text(
                        if (isAiMode) "Ask the Oracle anything..." else "Search movies, TV, cast...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyLarge
                    ) 
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )
            
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(
                    onClick = { onQueryChanged("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AiSearchTriggerCard(onTrigger: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                    )
                )
            )
            .clickable { onTrigger() }
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "NOT FINDING IT?",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    "Ask the Oracle AI",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Try describing the plot or vibe instead of the title.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun SleekGridResultCard(result: SearchResult, index: Int) {
    val (title, icon, color, imageRes) = when (result) {
        is SearchResult.Movie -> Quadruple(result.item.title, Icons.Default.Movie, Color(0xFFF85149), result.item.posterRes)
        is SearchResult.TvShow -> Quadruple(result.item.title, Icons.Default.Tv, Color(0xFF79C0FF), result.item.posterRes)
        is SearchResult.PersonResult -> Quadruple(result.person.name, Icons.Default.Person, Color(0xFFD2A8FF), result.person.imageRes)
    }

    // 1. CASCADING ANIMATION
    var isVisible by remember { mutableStateOf(false) }
    
    // We use animateFloatAsState but with a specific spec to ensure it starts from 0
    val entryAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = (index % 8) * 40),
        label = "entryAlpha"
    )
    val entrySlide by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 30.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "entrySlide"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // 2. INTERACTIVE FEEDBACK
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )

    Box(
        modifier = Modifier
            .aspectRatio(0.7f)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
                translationY = entrySlide.toPx()
                alpha = entryAlpha
            }
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { /* Navigate */ }
                )
            }
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (imageRes == com.example.cinescopesurat.R.drawable.placeholder) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    modifier = Modifier.graphicsLayer { this.alpha = 1f }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.3f))
                .blur(4.dp)
        )
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(16.dp)
        )
    }
}

@Composable
fun SearchLoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Consulting the Oracle...",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun SearchEmptyState(query: String, onAiTrigger: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No direct matches for \"$query\"",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAiTrigger,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("TRY ORACLE AI SEARCH", fontWeight = FontWeight.ExtraBold)
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

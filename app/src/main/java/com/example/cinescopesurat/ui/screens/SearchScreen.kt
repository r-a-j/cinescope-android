package com.example.cinescopesurat.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    viewModel: SearchViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit = {},
    onTvShowClick: (Int) -> Unit = {},
    onPersonClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    
    // Dedicated liquid state for the search bar to avoid recursive sampling from NavHost
    val barLiquidState = rememberLiquidState()
    
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardOpen = keyboardHeight > 0
    
    val barBottomPadding by animateDpAsState(
        targetValue = if (isKeyboardOpen) 12.dp else 112.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "barBottomPadding"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // SOURCE: Everything behind the search bar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .liquefiable(barLiquidState)
            ) {
                AiPulseBackground(enabled = uiState.isAiSearchEnabled)

                if (uiState.isLoading && uiState.isAiSearchEnabled) {
                    OracleLoadingState(uiState.oracleThoughts)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 240.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ZERO STATE: DISCOVERY DASHBOARD
                        if (uiState.query.isEmpty()) {
                            // Section: Header & Categories combined for tighter spacing
                            item(span = { GridItemSpan(4) }) {
                                Column(modifier = Modifier.padding(top = 16.dp)) {
                                    Text(
                                        "DISCOVERY",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = com.example.cinescopesurat.ui.theme.DiscoveryCyan,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 3.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        "CINESCOPE RECOMMENDED",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = (-1).sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    
                                    val messages = remember {
                                        listOf(
                                            "Curated stories and rising talent, handpicked for your cinematic journey.",
                                            "Discover hidden gems and global hits, tailored to your unique taste.",
                                            "Your next favorite story is just a scroll away. Explore the archives.",
                                            "Dive into a multiverse of cinema, from indie favorites to epic sagas.",
                                            "The Oracle has synthesized these recommendations specifically for you.",
                                            "Sleek interface, cinematic soul. Welcome to the future of discovery.",
                                            "Finding the perfect watch is an art. Let the Oracle be your guide.",
                                            "From the golden age to tomorrow's classics, it's all here.",
                                            "Wait, have you seen the latest trending talent? Scroll down.",
                                            "Unlocking a world of narrative excellence. One frame at a time."
                                        )
                                    }
                                    
                                    var messageIndex by remember { mutableIntStateOf(0) }
                                    
                                    LaunchedEffect(Unit) {
                                        while (true) {
                                            kotlinx.coroutines.delay(10000)
                                            messageIndex = (messageIndex + 1) % messages.size
                                        }
                                    }

                                    AnimatedContent(
                                        targetState = messages[messageIndex],
                                        transitionSpec = {
                                            (fadeIn(tween(800)) + 
                                             scaleIn(initialScale = 0.8f, animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) +
                                             expandVertically(expandFrom = Alignment.Top))
                                            .togetherWith(
                                                fadeOut(tween(400)) + 
                                                scaleOut(targetScale = 1.1f)
                                            )
                                        },
                                        label = "rotatingSubtext"
                                    ) { text ->
                                        // Materialize from blur
                                        var blurTrigger by remember(text) { mutableStateOf(16f) }
                                        LaunchedEffect(text) { blurTrigger = 0f }
                                        
                                        val animatedBlur by animateFloatAsState(
                                            targetValue = blurTrigger,
                                            animationSpec = tween(1200, easing = EaseOutExpo),
                                            label = "blur"
                                        )

                                        Column {
                                            Text(
                                                text = text,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                                modifier = Modifier
                                                    .padding(top = 4.dp, end = 48.dp)
                                                    .blur(animatedBlur.dp)
                                                    .graphicsLayer { 
                                                        alpha = (1f - (animatedBlur / 16f)).coerceIn(0f, 1f)
                                                        scaleX = 1f + (animatedBlur / 32f)
                                                    }
                                            )
                                            
                                            // THE LIQUID SOUL UNDERLINE: High-velocity elastic motion
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 10.dp)
                                                    .height(1.5.dp)
                                                    .fillMaxWidth(0.4f)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Brush.linearGradient(
                                                            listOf(
                                                                com.example.cinescopesurat.ui.theme.DiscoveryCyan,
                                                                com.example.cinescopesurat.ui.theme.OracleBlue,
                                                                Color.Transparent
                                                            )
                                                        )
                                                    )
                                                    .graphicsLayer {
                                                        // Liquid stretch based on blur
                                                        scaleX = 1.5f - (animatedBlur / 16f)
                                                        alpha = 1f - (animatedBlur / 16f)
                                                    }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(bottom = 12.dp)
                                    ) {
                                        items(uiState.categories) { (name, color) ->
                                            SuggestionChip(
                                                onClick = { viewModel.onQueryChanged(name) },
                                                label = { Text(name, fontWeight = FontWeight.Bold) },
                                                colors = SuggestionChipDefaults.suggestionChipColors(
                                                    containerColor = color.copy(alpha = 0.1f),
                                                    labelColor = color
                                                ),
                                                border = SuggestionChipDefaults.suggestionChipBorder(
                                                    enabled = true,
                                                    borderColor = color.copy(alpha = 0.3f)
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Section: Main Discovery Grid
                            itemsIndexed(
                                items = uiState.trendingMovies,
                                span = { index, _ ->
                                    val span = when (index) {
                                        0, 1 -> 2 // Two medium cards at the top
                                        else -> 1 // Rest are small
                                    }
                                    GridItemSpan(span)
                                }
                            ) { index, result ->
                                SleekGridResultCard(
                                    result = result,
                                    index = index,
                                    onMovieClick = { id -> focusManager.clearFocus(); onMovieClick(id) },
                                    onTvShowClick = { id -> focusManager.clearFocus(); onTvShowClick(id) },
                                    onPersonClick = { id -> focusManager.clearFocus(); onPersonClick(id) }
                                )
                            }
                        }

                        if (uiState.showAiTrigger && !uiState.isAiSearchEnabled && uiState.query.isNotEmpty() && uiState.results.isNotEmpty()) {
                            item(span = { GridItemSpan(4) }) {
                                Box(modifier = Modifier.padding(top = 16.dp)) {
                                    AiSearchTriggerCard(onTrigger = { viewModel.triggerAiSearch() })
                                }
                            }
                        }

                        if (uiState.query.isNotEmpty()) {
                            itemsIndexed(
                                items = uiState.results,
                                key = { _, result -> 
                                    when(result) {
                                        is SearchResult.Movie -> "m_${result.item.id}"
                                        is SearchResult.TvShow -> "t_${result.item.id}"
                                        is SearchResult.PersonResult -> "p_${result.person.id}"
                                    }
                                },
                                span = { index, _ -> 
                                    // CINEMATIC STAGGERED RHYTHM
                                    val span = when (index % 5) {
                                        0 -> 2 // Anchor
                                        1 -> 2 // Pair
                                        else -> 1 // Triplets
                                    }
                                    GridItemSpan(span)
                                }
                            ) { _, result ->
                                CinematicCompactCard(
                                    result = result,
                                    onMovieClick = { id -> focusManager.clearFocus(); onMovieClick(id) },
                                    onTvShowClick = { id -> focusManager.clearFocus(); onTvShowClick(id) },
                                    onPersonClick = { id -> focusManager.clearFocus(); onPersonClick(id) }
                                )
                            }
                        }

                        if (uiState.query.isNotEmpty() && uiState.results.isEmpty() && !uiState.isLoading) {
                            item(span = { GridItemSpan(4) }) {
                                SearchEmptyState(query = uiState.query) { viewModel.triggerAiSearch() }
                            }
                        }
                    }
                }
            }

            // CONSUMER: The Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .imePadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = barBottomPadding)
            ) {
                GlassSearchBar(
                    query = uiState.query,
                    onQueryChanged = { viewModel.onQueryChanged(it) },
                    isAiMode = uiState.isAiSearchEnabled,
                    liquidState = barLiquidState
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
    liquidState: LiquidState,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    val activeColor = if (isAiMode) com.example.cinescopesurat.ui.theme.OracleBlue else com.example.cinescopesurat.ui.theme.DiscoveryCyan
    val focusManager = LocalFocusManager.current
    
    val contentColor by animateColorAsState(
        targetValue = if (isAiMode) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "contentColor"
    )

    val height by animateDpAsState(
        targetValue = if (isAiMode) 80.dp else 64.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "height"
    )

    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .onFocusChanged { onFocusChanged(it.isFocused) },
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
                    tint = activeColor.copy(alpha = 0.15f)
                }
        )

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (isAiMode) Icons.Default.AutoAwesome else Icons.Default.Search,
                contentDescription = null,
                tint = if (isAiMode) activeColor else contentColor.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = query,
                onValueChange = onQueryChanged,
                placeholder = { 
                    Text(
                        if (isAiMode) "Ask the Oracle anything..." else "Search movies, TV, cast...",
                        color = contentColor.copy(alpha = 0.5f),
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
                    onClick = { 
                        onQueryChanged("")
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = contentColor.copy(alpha = 0.4f),
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
            .padding(top = 8.dp)
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
fun CinematicCompactCard(
    result: SearchResult,
    onMovieClick: (Int) -> Unit = {},
    onTvShowClick: (Int) -> Unit = {},
    onPersonClick: (Int) -> Unit = {}
) {
    val (title, icon, color, imageRes) = when (result) {
        is SearchResult.Movie -> Quadruple(result.item.title, Icons.Default.Movie, Color(0xFFF85149), result.item.posterRes)
        is SearchResult.TvShow -> Quadruple(result.item.title, Icons.Default.Tv, Color(0xFF79C0FF), result.item.posterRes)
        is SearchResult.PersonResult -> Quadruple(result.person.name, Icons.Default.Person, Color(0xFFD2A8FF), result.person.imageRes)
    }

    val isPlaceholder = imageRes == com.example.cinescopesurat.R.drawable.placeholder || 
                        imageRes == com.example.cinescopesurat.R.drawable.placeholder_backdrop

    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "press"
    )

    Box(
        modifier = Modifier
            .aspectRatio(0.8f) // Slightly wider than standard posters for grid density
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { 
                        when (result) {
                            is SearchResult.Movie -> onMovieClick(result.item.id)
                            is SearchResult.TvShow -> onTvShowClick(result.item.id)
                            is SearchResult.PersonResult -> onPersonClick(result.person.id)
                        }
                    }
                )
            }
    ) {
        // THE HERO: FULL POSTER
        if (!isPlaceholder) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
            }
        }

        // AMBIENT GRADIENT (Bottom-up for text legibility)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.5f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.8f)
                    )
                )
        )

        // MINIMAL INFO OVERLAY
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 2,
                lineHeight = 12.sp
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon, 
                    null, 
                    tint = color, 
                    modifier = Modifier.size(10.dp)
                )
                if (result is SearchResult.Movie || result is SearchResult.TvShow) {
                    val rating = if (result is SearchResult.Movie) result.item.rating else (result as SearchResult.TvShow).item.rating
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = rating,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CompactResultCard(
    result: SearchResult,
    onMovieClick: (Int) -> Unit = {},
    onTvShowClick: (Int) -> Unit = {},
    onPersonClick: (Int) -> Unit = {}
) {
    val (title, icon, color, imageRes) = when (result) {
        is SearchResult.Movie -> Quadruple(result.item.title, Icons.Default.Movie, Color(0xFFF85149), result.item.posterRes)
        is SearchResult.TvShow -> Quadruple(result.item.title, Icons.Default.Tv, Color(0xFF79C0FF), result.item.posterRes)
        is SearchResult.PersonResult -> Quadruple(result.person.name, Icons.Default.Person, Color(0xFFD2A8FF), result.person.imageRes)
    }

    val isPlaceholder = imageRes == com.example.cinescopesurat.R.drawable.placeholder || 
                        imageRes == com.example.cinescopesurat.R.drawable.placeholder_backdrop

    Surface(
        onClick = {
            when (result) {
                is SearchResult.Movie -> onMovieClick(result.item.id)
                is SearchResult.TvShow -> onTvShowClick(result.item.id)
                is SearchResult.PersonResult -> onPersonClick(result.person.id)
            }
        },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (!isPlaceholder) {
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when(result) {
                        is SearchResult.Movie -> "MOVIE"
                        is SearchResult.TvShow -> "TV SHOW"
                        is SearchResult.PersonResult -> result.person.role.uppercase()
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (result is SearchResult.Movie || result is SearchResult.TvShow) {
                    val rating = if (result is SearchResult.Movie) result.item.rating else (result as SearchResult.TvShow).item.rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rating,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SleekGridResultCard(
    result: SearchResult,
    index: Int,
    onMovieClick: (Int) -> Unit = {},
    onTvShowClick: (Int) -> Unit = {},
    onPersonClick: (Int) -> Unit = {}
) {
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

    val isPlaceholder = imageRes == com.example.cinescopesurat.R.drawable.placeholder || 
                        imageRes == com.example.cinescopesurat.R.drawable.placeholder_backdrop

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
                    onTap = { 
                        when (result) {
                            is SearchResult.Movie -> onMovieClick(result.item.id)
                            is SearchResult.TvShow -> onTvShowClick(result.item.id)
                            is SearchResult.PersonResult -> onPersonClick(result.person.id)
                        }
                    }
                )
            }
    ) {
        if (!isPlaceholder) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Overlay for information
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isPlaceholder) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                    else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isPlaceholder) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color.copy(alpha = 0.6f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        maxLines = 2
                    )
                }
            }
        }

        // Type Icon (Top Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.3f))
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
fun OracleLoadingState(thought: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "oracleGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // BACKGROUND AMBIENT GLOW (More expansive and soft)
        Box(
            modifier = Modifier
                .size(400.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f * glowAlpha),
                            Color.Transparent
                        )
                    )
                )
                .blur(80.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-160).dp) // Move significantly up to avoid keyboard/searchbar overlap
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp) // Slightly larger for elegance
                        .graphicsLayer {
                            val pulse = 0.9f + (glowAlpha * 0.2f)
                            scaleX = pulse
                            scaleY = pulse
                            rotationZ = glowAlpha * 15f // Subtler rotation
                        },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            AnimatedContent(
                targetState = thought,
                transitionSpec = {
                    (fadeIn(tween(1000)) + slideInVertically(tween(1000)) { it / 2 }) togetherWith
                    (fadeOut(tween(1000)) + slideOutVertically(tween(1000)) { -it / 2 })
                },
                label = "thought"
            ) { text ->
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp, // Even more "Oracle" like
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }
}

@Composable
fun SearchLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
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

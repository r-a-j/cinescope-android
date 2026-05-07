package com.example.cinescopesurat.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.*
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TileMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.ui.components.SectionHeader
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import com.example.cinescopesurat.ui.viewmodel.OracleViewModel
import com.example.cinescopesurat.ui.viewmodel.OracleUiState
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState

@Composable
fun OracleScreen(
    onMovieClick: (Int) -> Unit = {},
    viewModel: OracleViewModel = hiltViewModel(),
    liquidState: LiquidState = rememberLiquidState(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val isScrolling by remember {
        derivedStateOf { listState.isScrollInProgress }
    }

    OracleScreenContent(
        uiState = uiState,
        listState = listState,
        isScrolling = isScrolling,
        liquidState = liquidState,
        onMovieClick = onMovieClick,
    ) { viewModel.spinAgain() }
}

@Composable
fun OracleScreenContent(
    uiState: OracleUiState,
    listState: LazyListState,
    isScrolling: Boolean,
    liquidState: LiquidState,
    onMovieClick: (Int) -> Unit,
    onSpinAgain: () -> Unit,
) {
    // Hoist the animations so the whole screen reacts to the spin
    val chargeAlpha by animateFloatAsState(
        targetValue = if (uiState.isSpinning) 1f else 0f,
        animationSpec = tween(600, easing = EaseInOutCubic),
        label = "chargeAlpha"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        // Deep space dark background so the aurora pops perfectly
        color = Color(0xFF0F1115)
    ) {
        if (uiState.error != null) {
            OracleErrorState(message = uiState.error) { onSpinAgain() }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {

        // --- THE GLOBAL AETHER (Optimized Static Atmosphere) ---
        AtmosphericSoul(
            tint = when {
                uiState.isDestinyLocked -> Color(0xFFFFD700) // Fate Gold
                uiState.currentRoll == 1 -> Color(0xFF7C3AED) // Convergence Violet
                else -> Color(0xFF00BCD4) // Revelation Cyan
            },
            chargeAlpha = chargeAlpha
        )

        // --- THE FOREGROUND UI ---
                // Sits cleanly ON TOP of the Aurora.
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // ORACLE HERO
                    item(key = "oracle_hero") {
                        uiState.oraclesChoice?.let { movie ->
                            OracleHeroSection(
                                movie = movie,
                                isSpinning = uiState.isSpinning,
                                currentRoll = uiState.currentRoll,
                                isDestinyLocked = uiState.isDestinyLocked,
                                isScrolling = isScrolling,
                                liquidState = liquidState,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    // RITUAL PROGRESS INDICATOR
                    item(key = "ritual_progress") {
                        RitualProgressIndicator(
                            currentRoll = uiState.currentRoll,
                            isDestinyLocked = uiState.isDestinyLocked
                        )
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    // SPIN AGAIN BUTTON
                    item(key = "spin_section") {
                        SpinAgainSection(
                            isDestinyLocked = uiState.isDestinyLocked,
                            currentRoll = uiState.currentRoll,
                            isSpinning = uiState.isSpinning,
                            liquidState = liquidState,
                            onSpin = onSpinAgain
                        )
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }

                    // PROPHECIES
                    if (uiState.prophecies.isNotEmpty()) {
                        item { SectionHeader("PROPHECIES", "THE ORACLE'S WISDOM") }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .height(140.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                uiState.prophecies.take(2).forEachIndexed { index, (genre, count) ->
                                    OracleProphecyTile(
                                        label = genre,
                                        count = count,
                                        isPrimary = index == 0,
                                        isScrolling = isScrolling,
                                        modifier = Modifier.weight(1f).fillMaxHeight()
                                    )
                                }
                            }
                        }

                        if (uiState.prophecies.size > 2) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                        .height(120.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    uiState.prophecies.asSequence().drop(2).take(3).forEach { (genre, count) ->
                                        OracleProphecyTile(
                                            label = genre,
                                            count = count,
                                            isScrolling = isScrolling,
                                            modifier = Modifier.weight(1f).fillMaxHeight()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }

                    // VIBE INSIGHTS
                    if (uiState.vibeInsights.isNotEmpty()) {
                        item { SectionHeader("VIBE CHECK", "TODAY'S INSIGHTS") }

                        items(uiState.vibeInsights) { insight ->
                            VibeInsightCard(
                                insight = insight,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // ROLL HISTORY
                    if (uiState.rollHistory.size > 1) {
                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                            SectionHeader("HISTORIC VISIONS", "YOUR PAST ROLLS")
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        items(
                            items = uiState.rollHistory.dropLast(1).reversed(),
                            key = { it.id }
                        ) { movie ->
                            HistoryMovieTile(
                                movie = movie,
                                onMovieClick = onMovieClick,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AtmosphericSoul(
    tint: Color,
    chargeAlpha: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Static Aether Waves - Reactive to spinning "Charge"
        val alphaBoost = 0.05f * chargeAlpha
        
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, height * 0.15f)
            cubicTo(
                width * 0.4f, height * (0.05f - (alphaBoost * 0.5f)),
                width * 0.6f, height * (0.35f + (alphaBoost * 0.5f)),
                width, height * 0.25f
            )
            lineTo(width, 0f)
            lineTo(0f, 0f)
            close()
        }
        
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(tint.copy(alpha = 0.15f + alphaBoost), Color.Transparent)
            )
        )
        
        // Deep Ritual Wave
        val path2 = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, height * 0.6f)
            quadraticTo(
                width * 0.5f, height * (0.9f + alphaBoost),
                width, height * 0.5f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        
        drawPath(
            path = path2,
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, tint.copy(alpha = 0.08f + alphaBoost))
            )
        )
    }
}

@Composable
fun OracleErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "DESTINY OBSCURED",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("CONSULT AGAIN", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RitualProgressIndicator(
    currentRoll: Int,
    isDestinyLocked: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                val fillWeight by animateFloatAsState(
                    targetValue = when (currentRoll) {
                        0 -> 0.01f
                        1 -> 0.5f
                        else -> 1f
                    },
                    animationSpec = tween(800, easing = EaseInOutCubic),
                    label = "trackFill"
                )

                if (fillWeight > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(fillWeight)
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF00BCD4),
                                        if (isDestinyLocked) Color(0xFFFFD700) else Color(0xFF7C3AED)
                                    )
                                )
                            )
                    )
                }
                if (fillWeight < 1f) {
                    Spacer(modifier = Modifier.weight(1f - fillWeight))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isActive = index == currentRoll
                    val isCompleted = index < currentRoll

                    val nodeColor = when {
                        isActive && isDestinyLocked -> Color(0xFFFFD700)
                        isActive -> Color.White
                        isCompleted -> Color(0xFF7C3AED)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    // Performance: Use graphicsLayer for scale instead of animating DP size (which triggers layout)
                    val targetScale = if (isActive) 1.5f else 1.0f
                    val animatedScale by animateFloatAsState(
                        targetValue = targetScale,
                        animationSpec = spring(Spring.DampingRatioMediumBouncy),
                        label = "nodeScale"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                            }
                            .clip(CircleShape)
                            .background(nodeColor)
                            .border(
                                width = if (isActive) 1.dp else 0.dp,
                                color = if (isActive && !isDestinyLocked) Color(0xFF00BCD4) else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                isDestinyLocked -> "FATE CRYSTALLIZED"
                currentRoll == 0 -> "Awaiting first revelation"
                currentRoll == 1 -> "Path converges"
                else -> "Final threshold"
            }.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isDestinyLocked) Color(0xFFFFD700) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun HistoryMovieTile(
    movie: MediaItem,
    modifier: Modifier = Modifier,
    onMovieClick: (Int) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "historyScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onMovieClick(movie.id) },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = movie.posterUrl ?: com.example.cinescopesurat.R.drawable.placeholder_backdrop,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(com.example.cinescopesurat.R.drawable.placeholder_backdrop),
                error = painterResource(com.example.cinescopesurat.R.drawable.placeholder_backdrop)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    movie.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    "A vision from your past",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun OracleHeroSection(
    movie: MediaItem,
    isSpinning: Boolean = false,
    currentRoll: Int = 0,
    isDestinyLocked: Boolean = false,
    isScrolling: Boolean = false,
    liquidState: LiquidState,
    onMovieClick: (Int) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val infiniteTransition = rememberInfiniteTransition(label = "oracleHeroAnimations")

    val spinPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinPhase"
    )

    val edgeOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "edgeOffset"
    )

    // Performance: Use a slightly lower max blur for better performance on mid-range devices
    val visionBlur by animateDpAsState(
        targetValue = if (isSpinning) 16.dp else 0.dp,
        animationSpec = tween(
            durationMillis = if (isSpinning) 800 else 1200,
            easing = FastOutSlowInEasing
        ),
        label = "visionBlur"
    )

    val destinyGlowAlpha by animateFloatAsState(
        targetValue = if (isDestinyLocked) 1f else 0f,
        animationSpec = tween(1200, easing = EaseInOutCubic),
        label = "destinyGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isDestinyLocked && !isSpinning
            ) { onMovieClick(movie.id) },
        contentAlignment = Alignment.Center
    ) {
        // Optimized Image Loading and Blurring
        Crossfade(
            targetState = movie,
            animationSpec = tween(1000),
            label = "movieCrossfade",
            modifier = Modifier.fillMaxSize()
        ) { currentMovie ->
            AsyncImage(
                model = currentMovie.backdropUrl ?: com.example.cinescopesurat.R.drawable.placeholder_backdrop,
                contentDescription = currentMovie.title,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // High Performance Blur: Use BlurEffect directly in graphicsLayer
                        // to avoid recomposition when the blur radius changes.
                        if (visionBlur > 0.dp) {
                            renderEffect = BlurEffect(
                                visionBlur.toPx(),
                                visionBlur.toPx(),
                                TileMode.Clamp
                            )
                        }
                    },
                contentScale = ContentScale.Crop,
                colorFilter = if (isSpinning) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.3f) }) else null
            )
        }

        // Seamless Fade Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.5f to MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                        1.0f to MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                    )
                )
        )

        // Spin shimmer effect - Optimized with graphicsLayer to avoid recomposition
        if (isSpinning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                // Use size.width to make the shimmer relative to container size
                                start = androidx.compose.ui.geometry.Offset(x = (spinPhase * size.width * 2f) - size.width, y = 0f),
                                end = androidx.compose.ui.geometry.Offset(x = (spinPhase * size.width * 2f), y = size.height)
                            )
                        )
                    }
            )
        }

        // The Sleek Animated Golden Ring (Optimized Compositing)
        if (isDestinyLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Use CompositingStrategy.Offscreen ONLY when destiny is locked
                        // and use the explicit API for modern Compose performance.
                        compositingStrategy = CompositingStrategy.Offscreen
                        alpha = destinyGlowAlpha
                    }
                    .drawWithContent {
                        drawContent()
                        // Erasure mask without alpha = 0.99f hacks
                        drawRect(
                            brush = Brush.verticalGradient(
                                0.0f to Color.Transparent,
                                0.5f to Color.Transparent,
                                0.7f to Color.Black,
                                1.0f to Color.Black
                            ),
                            blendMode = androidx.compose.ui.graphics.BlendMode.DstIn
                        )
                    }
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFFFFD700).copy(alpha = 0.4f),
                                Color.White,
                                Color(0xFFFFD700).copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(edgeOffset - 800f, edgeOffset - 800f),
                            end = androidx.compose.ui.geometry.Offset(edgeOffset, edgeOffset)
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            )
        }

        // Glass Frosted Card Content
        val glassColor = CinescopeTheme.customColors.glassBackground
        
        // Performance: Skip liquid shader while scrolling
        val modifierWithLiquid = if (!isScrolling) {
            Modifier.liquid(liquidState) {
                frost = 4.dp
                tint = glassColor.copy(alpha = 0.05f)
            }
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(glassColor.copy(alpha = 0.1f)) // Slightly more opaque when liquid is off
                .graphicsLayer {
                    // Use a slightly lower blur for the card backdrop for performance
                    renderEffect = BlurEffect(12f, 12f, TileMode.Clamp)
                }
                .then(modifierWithLiquid)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            if (isDestinyLocked) {
                val goldGradient = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFDF73), Color(0xFFFFF8DC), Color(0xFFD4AF37))
                )

                Text(
                    text = "✦ YOUR FATE IS SEALED ✦",
                    style = androidx.compose.ui.text.TextStyle(
                        brush = goldGradient,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            blurRadius = 8f
                        )
                    ),
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 6.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
            } else {
                Text(
                    when (currentRoll) {
                        0 -> "ORACLE FIRST VISION"
                        1 -> "ORACLE SECOND PATH"
                        else -> "ORACLE FINAL REVELATION"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = when (currentRoll) {
                        0 -> Color(0xFF00BCD4).copy(alpha = 0.8f)
                        1 -> Color(0xFF7C3AED).copy(alpha = 0.8f)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    },
                    letterSpacing = 3.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Text(
                movie.title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                lineHeight = 40.sp,
                color = Color.White,
                maxLines = 2,
                textAlign = if (isDestinyLocked) androidx.compose.ui.text.style.TextAlign.Center else androidx.compose.ui.text.style.TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isDestinyLocked) Arrangement.Center else Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.Black.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDestinyLocked) Color(0xFFFFD700).copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isDestinyLocked) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            movie.rating,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun OracleProphecyTile(
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    isScrolling: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Performance: Pause infinite animations during scroll
    val bounceOffset = if (!isScrolling) {
        val infiniteTransition = rememberInfiniteTransition(label = "prophecyBounce$label")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 4f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutQuad),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bounceOffset"
        ).value
    } else {
        0f
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "propScale"
    )

    // Optimized brush creation: remember the brush
    val baseColor = remember(label, isPrimary) {
        when {
            isPrimary && label == "SCI-FI" -> Color(0xFF7C3AED)
            label == "ACTION" -> Color(0xFFF85149)
            label == "DRAMA" -> Color(0xFF79C0FF)
            label == "INDIE" -> Color(0xFF4ADE80)
            else -> Color(0xFF303030)
        }
    }
    
    val tileBrush = remember(baseColor) {
        Brush.linearGradient(
            listOf(
                baseColor.copy(alpha = 0.8f),
                baseColor.copy(alpha = 0.5f)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(tileBrush)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = bounceOffset
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {}
            .padding(20.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$count prophecies",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 0.5.sp
            )
        }
    }
}


@Composable
fun VibeInsightCard(
    insight: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "insightScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {},
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Text(
                insight,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
                lineHeight = 22.sp
            )
        }
    }
}


@Composable
fun SpinAgainSection(
    isDestinyLocked: Boolean,
    currentRoll: Int,
    isSpinning: Boolean,
    liquidState: LiquidState,
    onSpin: () -> Unit
) {
    val spinsRemaining = maxOf(0, 2 - currentRoll)

    val buttonText = when {
        isDestinyLocked -> "DESTINY SEALED"
        currentRoll >= 2 -> "FINAL REVELATION"
        else -> "SPIN FOR DESTINY"
    }
    val subtext = when {
        isDestinyLocked -> "Your fate is written. Return tomorrow."
        currentRoll >= 2 -> "One last glimpse into the aether..."
        else -> "Rolls remaining: $spinsRemaining"
    }
    val buttonColor = if (currentRoll >= 2 && !isDestinyLocked) Color(0xFF7C3AED) else Color(0xFFFFB300)
    val isButtonEnabled = !isDestinyLocked

    val chargeAlpha by animateFloatAsState(
        targetValue = if (isSpinning) 1f else 0f,
        animationSpec = tween(600, easing = EaseInOutCubic),
        label = "chargeAlpha"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val buttonScale by animateFloatAsState(
        targetValue = when {
            isPressed && isButtonEnabled -> 0.94f
            isSpinning && isButtonEnabled -> 0.97f
            else -> 1f
        },
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "buttonScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- LIQUID GLASS LENS ---
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(64.dp)
                .graphicsLayer {
                    scaleX = buttonScale
                    scaleY = buttonScale
                }
                // 1. Clip first to constrain the liquid effect
                .clip(RoundedCornerShape(36.dp))
                // 2. Apply the heavy frosted liquid glass shader matching BottomNavBar
                .liquid(liquidState) {
                    frost = 5.dp
                    refraction = 0.25f
                    curve = 1.0f
                    edge = 0.0f
                    saturation = 1.4f
                    dispersion = 0.40f
                    contrast = 1.25f
                    // Dynamically tint the glass based on button state
                    tint = if (isButtonEnabled) buttonColor.copy(alpha = 0.15f) else Color.DarkGray.copy(alpha = 0.15f)
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = isButtonEnabled
                ) { onSpin() }
                // 3. Add a sharp rim light to define the physical edge of the glass
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(36.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSpinning && isButtonEnabled) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = chargeAlpha * 0.2f))
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
            ) {
                Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                    if (isSpinning && isButtonEnabled) {
                        Icon(
                            Icons.Default.Autorenew, contentDescription = null, tint = Color.White,
                            modifier = Modifier.size(24.dp).graphicsLayer { rotationZ = (chargeAlpha * 1000f) % 360f }
                        )
                    } else {
                        Icon(
                            if (isButtonEnabled) Icons.Default.Casino else Icons.Default.LockClock,
                            contentDescription = null, tint = if (isButtonEnabled) buttonColor else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    buttonText, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp,
                    color = if (isButtonEnabled) Color.White else Color.Gray, fontSize = 15.sp,
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.weight(1f))

                if (isButtonEnabled) {
                    Surface(
                        shape = RoundedCornerShape(20.dp), color = Color.Black.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(
                            spinsRemaining.toString(), fontWeight = FontWeight.Black, color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            subtext, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    CinescopeTheme {
            OracleScreenContent(
                uiState = OracleUiState(
                    oraclesChoice = MediaItem(1, "Inception", "8.8"),
                    prophecies = listOf("Action" to 5, "Sci-Fi" to 3),
                    vibeInsights = listOf("Mind-bending", "Thrilling")
                ),
                listState = rememberLazyListState(),
                isScrolling = false,
                liquidState = rememberLiquidState(),
                onMovieClick = {},
            ) { }
    }
}
package com.example.cinescopesurat.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.ui.components.SectionHeader
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import com.example.cinescopesurat.ui.viewmodel.OracleViewModel
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.error != null) {
            OracleErrorState(message = uiState.error!!) {
                viewModel.spinAgain()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 0.dp, bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ORACLE HERO - Oracle's Choice with in-place spinning animation
                uiState.oraclesChoice?.let { movie ->
                    OracleHeroSection(
                        movie = movie,
                        isSpinning = uiState.isSpinning,
                        currentRoll = uiState.currentRoll,
                        isDestinyLocked = uiState.isDestinyLocked,
                        liquidState = liquidState,
                        onMovieClick = onMovieClick
                    )
                }

                // RITUAL PROGRESS INDICATOR
                RitualProgressIndicator(
                    currentRoll = uiState.currentRoll,
                    isDestinyLocked = uiState.isDestinyLocked
                )

                Spacer(modifier = Modifier.height(24.dp))

                // SPIN AGAIN BUTTON & LIMIT
                SpinAgainSection(
                    isDestinyLocked = uiState.isDestinyLocked,
                    currentRoll = uiState.currentRoll,
                    isSpinning = uiState.isSpinning,
                ) {
                    viewModel.spinAgain()
                }

                Spacer(modifier = Modifier.height(40.dp))

                // PROPHECIES - Genre Tiles
                if (uiState.prophecies.isNotEmpty()) {
                    SectionHeader("PROPHECIES", "THE ORACLE'S WISDOM")
                    PropheciesBentoGrid(uiState.prophecies)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // VIBE INSIGHTS
                if (uiState.vibeInsights.isNotEmpty()) {
                    SectionHeader("VIBE CHECK", "TODAY'S INSIGHTS")
                    VibeInsightsSection(uiState.vibeInsights)
                }

                // ROLL HISTORY - Previous rolls displayed as subdued tiles
                if (uiState.rollHistory.size > 1) {
                    Spacer(modifier = Modifier.height(40.dp))
                    SectionHeader("HISTORIC VISIONS", "YOUR PAST ROLLS")
                    RollHistoryGrid(uiState.rollHistory.dropLast(1), onMovieClick = onMovieClick)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
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
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ritual stages labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isCompleted = index < currentRoll
                    val isActive = index == currentRoll

                    val stageColor = when {
                        isActive && isDestinyLocked -> Color(0xFFFFD700)
                        isActive -> Color(0xFF00BCD4)
                        isCompleted -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }

                    // Stage icon circle
                    val scale by animateFloatAsState(
                        targetValue = if (isActive) 1.2f else 1f,
                        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                        label = "stageScale$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(CircleShape)
                            .background(stageColor.copy(alpha = 0.15f))
                            .border(
                                width = if (isActive) 2.5.dp else 1.5.dp,
                                color = stageColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            when (index) {
                                0 -> Icons.Default.Visibility
                                1 -> Icons.Default.AutoAwesome
                                else -> Icons.Default.Star
                            },
                            contentDescription = null,
                            tint = stageColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        when (index) {
                            0 -> "First Vision"
                            1 -> "Second Path"
                            else -> "Final Revelation"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = stageColor,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar with connectors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val isActive = index <= currentRoll
                val isCurrentRoll = index == currentRoll
                val glowColor = when {
                    isCurrentRoll && isDestinyLocked -> Color(0xFFFFD700)
                    isCurrentRoll -> Color(0xFF00BCD4)
                    isActive -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                val width by animateFloatAsState(
                    targetValue = if (isActive) 1f else 0.4f,
                    animationSpec = tween(600, easing = EaseInOutCubic),
                    label = "progressWidth$index"
                )

                Box(
                    modifier = Modifier
                        .weight(width)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isActive) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        glowColor.copy(alpha = 0.4f),
                                        glowColor
                                    )
                                )
                            } else {
                                Brush.verticalGradient(
                                    colors = listOf(
                                        glowColor.copy(alpha = 0.3f),
                                        glowColor.copy(alpha = 0.15f)
                                    )
                                )
                            }
                        )
                        .shadow(
                            elevation = if (isCurrentRoll) 8.dp else 0.dp,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status text
        Text(
            text = when {
                isDestinyLocked -> "✦ DESTINY SEALED ✦"
                currentRoll == 0 -> "Awaiting first revelation..."
                currentRoll == 1 -> "Path converges..."
                else -> "Fate crystallizes..."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun RollHistoryGrid(
    history: List<MediaItem>,
    onMovieClick: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        history.forEach { movie ->
            HistoryMovieTile(movie = movie, onMovieClick = onMovieClick)
        }
    }
}

@Composable
fun HistoryMovieTile(
    movie: MediaItem,
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
        modifier = Modifier
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
    liquidState: LiquidState,
    onMovieClick: (Int) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 1. RESTORED: Needed for the white shimmer sweep over the card
    val infiniteTransition = rememberInfiniteTransition(label = "oracleShimmer")
    val spinPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinPhase"
    )

    // 2. The Breathing Scale
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.96f
            isSpinning -> 0.92f
            isDestinyLocked -> 1.02f
            else -> 1f
        },
        animationSpec = tween(1200, easing = EaseInOutCubic),
        label = "heroScale"
    )

    // 3. The Deep Blur
    val visionBlur by animateDpAsState(
        targetValue = if (isSpinning) 24.dp else 0.dp,
        animationSpec = tween(
            durationMillis = if (isSpinning) 800 else 1200,
            easing = FastOutSlowInEasing
        ),
        label = "visionBlur"
    )

    // 4. Destiny Glow
    val destinyGlowAlpha by animateFloatAsState(
        targetValue = if (isDestinyLocked) 0.8f else 0f,
        animationSpec = tween(800),
        label = "destinyGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isDestinyLocked || !isSpinning
            ) { onMovieClick(movie.id) }
    ) {
        // 5. FIXED: Crossfade with fully qualified R references (Removed the duplicate AsyncImage)
        Crossfade(
            targetState = movie,
            animationSpec = tween(1000),
            label = "movieCrossfade"
        ) { currentMovie ->
            AsyncImage(
                model = currentMovie.backdropUrl ?: com.example.cinescopesurat.R.drawable.placeholder_backdrop,
                contentDescription = currentMovie.title,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(visionBlur),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(com.example.cinescopesurat.R.drawable.placeholder_backdrop),
                error = painterResource(com.example.cinescopesurat.R.drawable.placeholder_backdrop),
                colorFilter = if (isSpinning) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.3f) }) else null
            )
        }

        // Dynamic cinematic gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.5f to MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                        1.0f to MaterialTheme.colorScheme.background
                    )
                )
        )

        // Spin shimmer effect overlay
        if (isSpinning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0f),
                                Color.White.copy(alpha = 0.15f),
                                Color.White.copy(alpha = 0f)
                            ),
                            start = androidx.compose.ui.geometry.Offset(
                                x = (spinPhase * 1000f),
                                y = 0f
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                x = (spinPhase * 1000f) + 500f,
                                y = 500f
                            )
                        )
                    )
            )
        }

        // Destiny Locked glow effect
        if (isDestinyLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        color = Color(0xFFFFD700).copy(alpha = destinyGlowAlpha * 0.6f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .blur(12.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = Color(0xFFFFD700).copy(alpha = destinyGlowAlpha),
                        shape = RoundedCornerShape(24.dp)
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700).copy(alpha = destinyGlowAlpha * 0.1f),
                                Color.Transparent
                            ),
                            radius = 600f
                        )
                    )
            )
        }

        // Glass Frosted Card
        val glassColor = CinescopeTheme.customColors.glassBackground
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(glassColor.copy(alpha = 0.06f))
                .blur(12.dp)
                .liquid(liquidState) {
                    frost = 5.dp
                    tint = glassColor.copy(alpha = 0.08f)
                }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                when {
                    isDestinyLocked -> "✦ YOUR FATE IS SEALED ✦"
                    currentRoll == 0 -> "ORACLE FIRST VISION"
                    currentRoll == 1 -> "ORACLE SECOND PATH"
                    else -> "ORACLE FINAL REVELATION"
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = when {
                    isDestinyLocked -> Color(0xFFFFD700)
                    currentRoll == 0 -> Color(0xFF00BCD4)
                    currentRoll == 1 -> Color(0xFF7C3AED)
                    else -> MaterialTheme.colorScheme.primary
                },
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                movie.title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.5).sp,
                lineHeight = 40.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            movie.rating,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                AnimatedVisibility(
                    visible = !isDestinyLocked,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PropheciesBentoGrid(prophecies: List<Pair<String, Int>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // First Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            prophecies.take(2).forEachIndexed { index, (genre, count) ->
                OracleProphecyTile(
                    label = genre,
                    count = count,
                    isPrimary = index == 0,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        // Second Row
        if (prophecies.size > 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                prophecies.asSequence().drop(2).take(3).forEach { (genre, count) ->
                    OracleProphecyTile(
                        label = genre,
                        count = count,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
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
    isPrimary: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "prophecyBounce$label")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceOffset"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "propScale"
    )

    val baseColor = when {
        (isPrimary && label == "SCI-FI") -> Color(0xFF7C3AED)
        label == "ACTION" -> Color(0xFFF85149)
        label == "DRAMA" -> Color(0xFF79C0FF)
        label == "INDIE" -> Color(0xFF4ADE80)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        baseColor.copy(alpha = 0.8f),
                        baseColor.copy(alpha = 0.5f)
                    )
                )
            )
            .graphicsLayer {
                scaleX = scale
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
fun VibeInsightsSection(insights: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        insights.forEach { insight ->
            VibeInsightCard(insight)
        }
    }
}

@Composable
fun VibeInsightCard(insight: String) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "insightScale"
    )

    Surface(
        modifier = Modifier
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
    onSpin: () -> Unit
) {
    val spinsRemaining = maxOf(0, 2 - currentRoll)

    // Determine button state and styling
    val buttonText: String
    val subtext: String
    val buttonColor: Color
    val isButtonEnabled: Boolean

    when {
        isDestinyLocked -> {
            buttonText = "DESTINY SEALED"
            subtext = "Your fate is written. Return tomorrow."
            buttonColor = Color(0xFFFFB300)
            isButtonEnabled = false
        }
        currentRoll >= 2 -> {
            buttonText = "FINAL REVELATION"
            subtext = "One last glimpse into the aether..."
            buttonColor = Color(0xFF7C3AED)
            isButtonEnabled = true
        }
        else -> {
            buttonText = "SPIN FOR DESTINY"
            subtext = "Rolls remaining: $spinsRemaining"
            buttonColor = Color(0xFFFFB300)
            isButtonEnabled = true
        }
    }

    val chargeAlpha by animateFloatAsState(
        targetValue = if (isSpinning) 0.7f else 0.2f,
        animationSpec = tween(600, easing = EaseInOutCubic),
        label = "chargeAlpha"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isSpinning && isButtonEnabled) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "buttonScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Enhanced button with layered effects
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(64.dp)
                .graphicsLayer {
                    scaleX = buttonScale
                    scaleY = buttonScale
                }
                .clickable(enabled = isButtonEnabled) { onSpin() }
        ) {
            // Outer glow (only when active)
            if (isButtonEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(36.dp))
                        .background(buttonColor.copy(alpha = 0.15f))
                        .blur(8.dp),
                    contentAlignment = Alignment.Center
                ) {}
            }

            // Main button container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(36.dp))
                    .background(
                        if (isButtonEnabled) {
                            Brush.linearGradient(
                                colors = listOf(
                                    buttonColor,
                                    buttonColor.copy(alpha = 0.85f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    buttonColor.copy(alpha = 0.3f),
                                    buttonColor.copy(alpha = 0.15f)
                                )
                            )
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = if (isDestinyLocked) Color(0xFFFFD700) else buttonColor,
                        shape = RoundedCornerShape(36.dp)
                    )
                    .shadow(
                        elevation = if (isButtonEnabled) 12.dp else 0.dp,
                        shape = RoundedCornerShape(36.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Charge-up shimmer background
                if (isSpinning && isButtonEnabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(34.dp))
                            .background(Color.White.copy(alpha = chargeAlpha * 0.3f))
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    // Icon with animation
                    Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        if (isSpinning && isButtonEnabled) {
                            Icon(
                                Icons.Default.Autorenew,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer {
                                        rotationZ = (chargeAlpha * 360f) % 360f
                                    }
                            )
                        } else {
                            Icon(
                                if (isButtonEnabled) Icons.Default.Casino else Icons.Default.LockClock,
                                contentDescription = null,
                                tint = if (isButtonEnabled) Color.Black else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Text(
                        buttonText,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = if (isButtonEnabled) Color.Black else Color.Gray,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Remaining rolls indicator
                    if (isButtonEnabled) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Black.copy(alpha = 0.2f)
                        ) {
                            Text(
                                spinsRemaining.toString(),
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Enhanced subtext with context
        Text(
            subtext,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

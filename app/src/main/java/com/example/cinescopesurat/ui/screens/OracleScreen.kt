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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
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

    OracleScreenContent(
        uiState = uiState,
        liquidState = liquidState,
        onMovieClick = onMovieClick,
        onSpinAgain = { viewModel.spinAgain() }
    )
}

@Composable
fun OracleScreenContent(
    uiState: OracleUiState,
    liquidState: LiquidState,
    onMovieClick: (Int) -> Unit,
    onSpinAgain: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.error != null) {
            OracleErrorState(message = uiState.error) {
                onSpinAgain()
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
                    onSpinAgain()
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
            .padding(horizontal = 32.dp, vertical = 24.dp), // Tighter padding to blend with card
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Seamless thread line with nodes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // The track background line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            )

            // The filled track line
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
                            .shadow(if (currentRoll > 0) 8.dp else 0.dp, spotColor = Color(0xFF00BCD4))
                    )
                }
                if (fillWeight < 1f) {
                    Spacer(modifier = Modifier.weight(1f - fillWeight))
                }
            }

            // The Nodes
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

                    val nodeSize by animateDpAsState(
                        targetValue = if (isActive) 12.dp else 8.dp,
                        animationSpec = spring(Spring.DampingRatioMediumBouncy),
                        label = "nodeSize"
                    )

                    Box(
                        modifier = Modifier
                            .size(nodeSize)
                            .clip(CircleShape)
                            .background(nodeColor)
                            .border(
                                width = if (isActive) 2.dp else 0.dp,
                                color = if (isActive && !isDestinyLocked) Color(0xFF00BCD4) else Color.Transparent,
                                shape = CircleShape
                            )
                            .shadow(if (isActive || isCompleted) 12.dp else 0.dp, spotColor = nodeColor)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clean, minimalist status text
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

    val visionBlur by animateDpAsState(
        targetValue = if (isSpinning) 24.dp else 0.dp,
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

    // THE MASTER CONTAINER
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
            .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 32.dp, bottomEnd = 32.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isDestinyLocked || !isSpinning
            ) { onMovieClick(movie.id) },
        contentAlignment = Alignment.Center
    ) {
        // --- THICK YELLOW AURA REMOVED ENTIRELY ---

        // The Main Movie Image
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
                    .blur(visionBlur),
                contentScale = ContentScale.Crop,
                colorFilter = if (isSpinning) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.3f) }) else null
            )
        }

        // Seamless Fade Gradient (Melts the bottom into your app background)
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

        // Spin shimmer effect
        if (isSpinning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(x = (spinPhase * 1000f), y = 0f),
                            end = androidx.compose.ui.geometry.Offset(x = (spinPhase * 1000f) + 500f, y = 500f)
                        )
                    )
            )
        }

        // The Sleek Animated Golden Ring (With Top-Half Dissolve Mask)
        if (isDestinyLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Forces Compose to render this specific box into an offscreen buffer
                        // so we can apply the erasure mask cleanly.
                        alpha = 0.99f
                    }
                    .drawWithContent {
                        drawContent() // 1. Draws the border first

                        // 2. Draws the erasure mask over it
                        drawRect(
                            brush = Brush.verticalGradient(
                                0.0f to Color.Transparent, // Top is completely erased
                                0.5f to Color.Transparent, // Stays erased until exactly 50%
                                0.7f to Color.Black,       // Smoothly fades into existence
                                1.0f to Color.Black        // Bottom is fully visible
                            ),
                            blendMode = androidx.compose.ui.graphics.BlendMode.DstIn
                        )
                    }
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFFFFD700).copy(alpha = destinyGlowAlpha * 0.4f),
                                Color.White.copy(alpha = destinyGlowAlpha),
                                Color(0xFFFFD700).copy(alpha = destinyGlowAlpha * 0.4f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(edgeOffset - 800f, edgeOffset - 800f),
                            end = androidx.compose.ui.geometry.Offset(edgeOffset, edgeOffset)
                        ),
                        // Match the bottom-only rounded corners from earlier
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            )
        }

        // Glass Frosted Card Content
        val glassColor = CinescopeTheme.customColors.glassBackground
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(glassColor.copy(alpha = 0.03f))
                .blur(16.dp)
                .liquid(liquidState) {
                    frost = 5.dp
                    tint = glassColor.copy(alpha = 0.05f)
                }
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
            liquidState = rememberLiquidState(),
            onMovieClick = {},
            onSpinAgain = {}
        )
    }
}
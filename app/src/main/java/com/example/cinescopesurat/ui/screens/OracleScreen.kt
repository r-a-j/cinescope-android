package com.example.cinescopesurat.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
    liquidState: LiquidState = rememberLiquidState()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.error != null) {
            OracleErrorState(
                message = uiState.error!!,
                onRetry = { viewModel.spinAgain() }
            )
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
                    onSpin = { viewModel.spinAgain() }
                )

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
        verticalArrangement = Arrangement.Center
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
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val isActive = index <= currentRoll
                val isCurrentRoll = index == currentRoll
                val glowColor = when {
                    isCurrentRoll && isDestinyLocked -> Color(0xFFFFD700) // Gold when locked
                    isCurrentRoll -> Color(0xFF00BCD4) // Cyan for active
                    isActive -> MaterialTheme.colorScheme.primary // Primary for completed
                    else -> MaterialTheme.colorScheme.surfaceVariant // Grey for pending
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(glowColor.copy(alpha = 0.3f))
                        .border(
                            width = if (isCurrentRoll) 1.5.dp else 1.dp,
                            color = glowColor,
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when {
                isDestinyLocked -> "Destiny Sealed"
                currentRoll == 0 -> "First Vision"
                currentRoll == 1 -> "Second Path"
                else -> "Final Revelation"
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
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

    // In-place liquid morphing spin animation
    val infiniteTransition = rememberInfiniteTransition(label = "oracleSpinMorph")
    val spinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heroRotation"
    )

    val actualRotationZ = if (isSpinning) spinRotation else 0f

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "oracleHeroScale"
    )

    val destinyGlowAlpha by animateFloatAsState(
        targetValue = if (isDestinyLocked) 0.8f else 0f,
        animationSpec = tween(600),
        label = "destinyGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = actualRotationZ
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isDestinyLocked
            ) { onMovieClick(movie.id) }
    ) {
        // Backdrop
        AsyncImage(
            model = movie.backdropUrl ?: com.example.cinescopesurat.R.drawable.placeholder_backdrop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(com.example.cinescopesurat.R.drawable.placeholder_backdrop),
            error = painterResource(com.example.cinescopesurat.R.drawable.placeholder_backdrop)
        )

        // Cinematic Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.2f to Color.Transparent,
                        0.6f to MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        1.0f to MaterialTheme.colorScheme.background
                    )
                )
        )

        // Destiny Locked Glow Border (only visible when locked)
        if (isDestinyLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFFD700).copy(alpha = destinyGlowAlpha),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .blur(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "✦ DESTINY LOCKED ✦",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD700).copy(alpha = destinyGlowAlpha * 0.8f),
                    letterSpacing = 2.sp,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
                )
            }
        }

        // Glass Frosted Card with Oracle Styling
        val glassColor = CinescopeTheme.customColors.glassBackground

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(glassColor.copy(alpha = 0.08f))
                .blur(10.dp)
                .liquid(liquidState) {
                    frost = 4.dp
                    tint = glassColor.copy(alpha = 0.1f)
                }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "The Oracle Has Spoken",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                movie.title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.5).sp,
                lineHeight = 40.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.primary
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
                prophecies.drop(2).take(3).forEach { (genre, count) ->
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
    isPrimary: Boolean = false,
    modifier: Modifier = Modifier
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
        isPrimary && label == "SCI-FI" -> Color(0xFF7C3AED)
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
    val enabled = !isDestinyLocked
    val buttonText = when {
        isDestinyLocked -> "DESTINY SEALED"
        currentRoll >= 2 -> "FINAL REVELATION"
        else -> "SPIN FOR DESTINY (${3 - currentRoll} left)"
    }

    val chargeAlpha by animateFloatAsState(
        targetValue = if (isSpinning) 0.6f else 0.2f,
        animationSpec = tween(1000),
        label = "chargeAlpha"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(60.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFFFFB300).copy(alpha = if (enabled) 1f else 0.15f))
                .border(
                    width = 2.dp,
                    color = if (isDestinyLocked) Color(0xFFFFD700) else Color(0xFFFFB300),
                    shape = RoundedCornerShape(32.dp)
                )
                .shadow(if (enabled) 12.dp else 0.dp, RoundedCornerShape(32.dp))
                .clickable(enabled = enabled) { onSpin() }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Charge-up background
            if (isSpinning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFFFFD700).copy(alpha = chargeAlpha))
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    if (enabled && !isDestinyLocked) Icons.Default.Casino else Icons.Default.LockClock,
                    contentDescription = null,
                    tint = if (enabled) Color.Black else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    buttonText,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = if (enabled) Color.Black else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            if (isDestinyLocked) "Your fate is sealed. Return tomorrow for new visions." else "The Oracle grants 3 spins per night.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
    }
}

package com.example.cinescopesurat.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
        AnimatedContent(
            targetState = uiState.isLoading || uiState.isSpinning,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith
                        fadeOut(animationSpec = tween(500))
            },
            label = "oracleStateTransition"
        ) { isDisplayingLoading ->
            if (isDisplayingLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    OracleLoadingAnimation(uiState.randomThought)
                }
            } else if (uiState.error != null) {
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
                    // ORACLE HERO - Oracle's Choice
                    uiState.oraclesChoice?.let { movie ->
                        OracleHeroSection(
                            movie = movie,
                            liquidState = liquidState,
                            onMovieClick = onMovieClick
                        )
                    }

                    // SPIN AGAIN BUTTON & LIMIT
                    Spacer(modifier = Modifier.height(24.dp))
                    SpinAgainSection(
                        spinsLeft = uiState.spinsLeft,
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

                    Spacer(modifier = Modifier.height(32.dp))
                }
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
            "THE VOID WHISPERS...",
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
fun OracleLoadingAnimation(thought: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "oracleGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha)
                )
                .blur(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            thought,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "⋯ ⋯ ⋯",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            letterSpacing = 6.sp
        )
    }
}

@Composable
fun OracleHeroSection(
    movie: MediaItem,
    liquidState: LiquidState,
    onMovieClick: (Int) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "oracleHeroScale"
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
                indication = null
            ) { onMovieClick(movie.id) }
    ) {
        // Backdrop
        Image(
            painter = painterResource(movie.backdropRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
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
fun SpinAgainSection(spinsLeft: Int, onSpin: () -> Unit) {
    val enabled = spinsLeft > 0
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onSpin,
            enabled = enabled,
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFB300),
                contentColor = Color.Black,
                disabledContainerColor = Color(0xFFFFB300).copy(alpha = 0.15f),
                disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(60.dp)
                .shadow(if (enabled) 12.dp else 0.dp, RoundedCornerShape(32.dp))
        ) {
            Icon(
                if (enabled) Icons.Default.Casino else Icons.Default.LockClock, 
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            val buttonText = if (enabled) {
                "Spin Again ($spinsLeft left)"
            } else {
                "Out of Wisdom for Tonight"
            }
            Text(buttonText, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            if (enabled) "The Oracle grants 3 spins per night." else "Return when the stars align (tomorrow).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
    }
}

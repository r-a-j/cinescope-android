package com.example.cinescopesurat.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.cinescopesurat.ui.viewmodel.PulseViewModel
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState

@Composable
fun PulseScreen(
    onMovieClick: (Int) -> Unit = {},
    viewModel: PulseViewModel = hiltViewModel(),
    liquidState: LiquidState = rememberLiquidState()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sampleMovies = remember(uiState.trendingMovies) { uiState.trendingMovies }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp) // Seamless start behind the glass
        ) {
            // CINEMATIC HERO
            if (sampleMovies.isNotEmpty()) {
                HeroSpotlight(sampleMovies.first(), liquidState, onMovieClick)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // LIVE PULSE SECTION
            SectionHeader("LIVE PULSE", "TRENDING NOW")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = sampleMovies,
                    key = { it.id }, // Critical for scroll performance
                    contentType = { "movie_card" } // Helps in view pool recycling
                ) { movie ->
                    // Optimization: Remember the click lambda to prevent unnecessary PulseMovieCard recomposition
                    val onClick = remember(movie.id) { { onMovieClick(movie.id) } }
                    PulseMovieCard(movie = movie, onClick = onClick)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // DISCOVER BENTO
            SectionHeader("DISCOVER", "GENRES & MORE")
            DiscoverBentoGrid()

            Spacer(modifier = Modifier.height(32.dp))

            // RECENTLY ADDED
            SectionHeader("RECENT", "NEW ARRIVALS")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = sampleMovies.reversed(),
                    key = { "recent_${it.id}" },
                    contentType = { "movie_card_small" }
                ) { movie ->
                    val onClick = remember(movie.id) { { onMovieClick(movie.id) } }
                    PulseMovieCard(movie, small = true, onClick = onClick)
                }
            }
        }
    }
}

@Composable
fun HeroSpotlight(
    movie: MediaItem,
    liquidState: LiquidState,
    onClick: (Int) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "heroScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(580.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick(movie.id) }
    ) {
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
                        0.3f to Color.Transparent,
                        0.7f to MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                        1.0f to MaterialTheme.colorScheme.background
                    )
                )
        )

        // GLASS FROSTED CARD
        val glassColor = CinescopeTheme.customColors.glassBackground
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .liquid(liquidState) {
                    frost = 8.dp
                    refraction = 0.15f
                    curve = 0.1f
                    tint = glassColor.copy(alpha = 0.15f)
                }
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "FEATURED",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "IMDb ${movie.rating}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = movie.title.uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    lineHeight = 46.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WATCH TRAILER", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PulseMovieCard(movie: MediaItem, small: Boolean = false, onClick: () -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessVeryLow),
        label = "cardScale"
    )

    val width = if (small) 140.dp else 190.dp
    val height = if (small) 210.dp else 280.dp

    Column(
        modifier = Modifier
            .width(width)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = movie.posterUrl ?: com.example.cinescopesurat.R.drawable.placeholder,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(com.example.cinescopesurat.R.drawable.placeholder),
                error = painterResource(com.example.cinescopesurat.R.drawable.placeholder)
            )

            // Glass Rating Tag
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        movie.rating,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            movie.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            letterSpacing = (-0.5).sp
        )
        Text(
            movie.type.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DiscoverBentoGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Large Featured Tile
            Box(
                modifier = Modifier
                    .weight(1.6f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .clickable { }
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Text(
                        "CURATED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 2.sp
                    )
                    Text(
                        "ORACLE'S\nCHOICE",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        lineHeight = 32.sp,
                        letterSpacing = (-1).sp
                    )
                }
            }

            // Vertical Stack
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BentoSmallTile("SCI-FI", MaterialTheme.colorScheme.secondaryContainer, Modifier.weight(1f))
                BentoSmallTile("ACTION", MaterialTheme.colorScheme.tertiaryContainer, Modifier.weight(1f))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BentoSmallTile("HORROR", Color(0xFF2D2D2D), Modifier.weight(1f), isDark = true)
            BentoSmallTile("DRAMA", MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
            BentoSmallTile("COMEDY", MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), Modifier.weight(0.8f))
        }
    }
}

@Composable
fun BentoSmallTile(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(color)
            .clickable { }
            .padding(20.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface,
            letterSpacing = 1.sp
        )
    }
}

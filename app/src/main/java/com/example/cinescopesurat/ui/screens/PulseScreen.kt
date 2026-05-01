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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.ui.components.SectionHeader
import com.example.cinescopesurat.ui.viewmodel.PulseViewModel

@Composable
fun PulseScreen(
    onMovieClick: (Int) -> Unit = {},
    viewModel: PulseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sampleMovies = uiState.trendingMovies

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
                HeroSpotlight(sampleMovies.first(), onMovieClick)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // LIVE PULSE SECTION
            SectionHeader("LIVE PULSE", "TRENDING NOW")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleMovies) { movie ->
                    PulseMovieCard(movie) { onMovieClick(movie.id) }
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
                items(sampleMovies.reversed()) { movie ->
                    PulseMovieCard(movie, small = true) { onMovieClick(movie.id) }
                }
            }
        }
    }
}

@Composable
fun HeroSpotlight(
    movie: MediaItem,
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
            .height(340.dp) // Taller, more immersive hero section
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick(movie.id) }
    ) {
        Image(
            painter = painterResource(movie.backdropRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Cinematic Multi-Stage Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.3f), // Subtle dim at the top for header readability
                        0.4f to Color.Transparent,
                        0.7f to MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        1.0f to MaterialTheme.colorScheme.background
                    )
                )
        )

        // CONTENT SECTION
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 40.dp)
                .fillMaxWidth()
        ) {
            // METADATA TAGS
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
                    tint = Color(0xFFFFD700), // Gold
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "IMDb ${movie.rating}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TITLE
            Text(
                text = movie.title.uppercase(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.5).sp,
                lineHeight = 48.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ACTIONS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("WATCH TRAILER", fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Favorite",
                        tint = Color.White
                    )
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
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
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
                indication = null
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Image(
                painter = painterResource(movie.posterRes),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
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

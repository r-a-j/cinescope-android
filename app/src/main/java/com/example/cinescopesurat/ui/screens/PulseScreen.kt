package com.example.cinescopesurat.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.cinescopesurat.data.model.MediaItem
import com.example.cinescopesurat.ui.viewmodel.PulseViewModel

@Composable
fun PulseScreen(viewModel: PulseViewModel = hiltViewModel()) {
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
                .padding(bottom = 120.dp)
        ) {
            // CINEMATIC HERO
            if (sampleMovies.isNotEmpty()) {
                HeroSpotlight(sampleMovies.first())
            }

        Spacer(modifier = Modifier.height(32.dp))

        // LIVE PULSE SECTION
        PulseSectionHeader("LIVE PULSE", "TRENDING NOW")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleMovies) { movie ->
                PulseMovieCard(movie)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // DISCOVER BENTO
        PulseSectionHeader("DISCOVER", "GENRES & MORE")
        DiscoverBentoGrid()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // RECENTLY ADDED
        PulseSectionHeader("RECENT", "NEW ARRIVALS")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleMovies.reversed()) { movie ->
                PulseMovieCard(movie, small = true)
            }
        }
    }
}
}

@Composable
fun HeroSpotlight(movie: MediaItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
    ) {
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
                        0.4f to Color.Transparent,
                        0.7f to MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        1.0f to MaterialTheme.colorScheme.background
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "PREMIUM",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "IMDb ${movie.rating}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = movie.title.uppercase(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                lineHeight = 44.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("WATCH TRAILER", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Favorite")
                }
            }
        }
    }
}

@Composable
fun PulseSectionHeader(tag: String, title: String) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            tag,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp
        )
    }
}

@Composable
fun PulseMovieCard(movie: MediaItem, small: Boolean = false) {
    val width = if (small) 140.dp else 180.dp
    val height = if (small) 200.dp else 260.dp
    
    Column(modifier = Modifier.width(width)) {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Image(
                painter = painterResource(movie.posterRes),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Rating Tag
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    movie.rating,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            movie.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            movie.type.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun DiscoverBentoGrid() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Large Tile
        Box(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { }
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    "TOP 50",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "MOST WATCHED",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 24.sp
                )
            }
        }
        
        // Vertical Stack
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BentoSmallTile("SCI-FI", MaterialTheme.colorScheme.secondaryContainer, Modifier.weight(1f))
            BentoSmallTile("ACTION", MaterialTheme.colorScheme.tertiaryContainer, Modifier.weight(1f))
        }
    }
}

@Composable
fun BentoSmallTile(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(color)
            .clickable { }
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

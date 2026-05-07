package com.example.cinescopesurat.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinescopesurat.ui.viewmodel.MovieVibe

@Composable
fun FitMyScheduleCard(
    selectedVibe: MovieVibe,
    timeLimit: Int,
    matchCount: Int,
    onVibeSelected: (MovieVibe) -> Unit,
    onTimeLimitChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Text(
            "FIT MY SCHEDULE",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = (-1).sp
        )
        Text(
            "Movies that respect your time constraints.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // VIBE SELECTOR TABS
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            VibeTab(
                label = "Low-stakes",
                selected = selectedVibe == MovieVibe.LOW_STAKES,
                onClick = { onVibeSelected(MovieVibe.LOW_STAKES) }
            )
            VibeTab(
                label = "High-Energy",
                selected = selectedVibe == MovieVibe.HIGH_ENERGY,
                onClick = { onVibeSelected(MovieVibe.HIGH_ENERGY) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // MAIN TIME CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(40.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                .padding(32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = timeLimit.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 80.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "minutes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Slider(
                    value = timeLimit.toFloat(),
                    onValueChange = onTimeLimitChanged,
                    valueRange = 60f..240f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "$matchCount movies match your current vibe",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun VibeTab(label: String, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "tabBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        animationSpec = tween(300),
        label = "tabContent"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
        animationSpec = tween(300),
        label = "tabBorder"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.height(48.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = contentColor,
                letterSpacing = 1.2.sp
            )
        }
    }
}

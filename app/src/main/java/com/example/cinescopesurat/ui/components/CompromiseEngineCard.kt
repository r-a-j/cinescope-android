package com.example.cinescopesurat.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinescopesurat.ui.theme.ActionRed
import com.example.cinescopesurat.ui.viewmodel.SyncState

@Composable
fun CompromiseEngineCard(
    syncState: SyncState,
    connectedFriend: String?,
    errorMessage: String?,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF161618))
            .padding(24.dp)
    ) {
        // RADAR ANIMATION BACKGROUND
        if (syncState == SyncState.CONNECTING) {
            RadarAnimation()
        }

        Column {
            Text(
                "COMPROMISE ENGINE",
                style = MaterialTheme.typography.headlineSmall,
                color = ActionRed,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Can't decide what to watch?\nFind the middle ground.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // INNER SYNC CARD
            Surface(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    if (syncState == SyncState.ERROR) Color.Red.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = ActionRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            val titleText = when (syncState) {
                                SyncState.CONNECTED -> "Synced with $connectedFriend"
                                SyncState.ERROR -> "Sync Failed"
                                else -> "Sync with a friend"
                            }
                            Text(
                                titleText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "NFC / QR / SHARE LINK",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.4f),
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (syncState == SyncState.ERROR) {
                        Text(
                            errorMessage ?: "Unknown error",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Button(
                        onClick = { if (syncState == SyncState.CONNECTED) onDisconnect() else onConnect() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (syncState == SyncState.CONNECTED) Color.DarkGray else ActionRed,
                            contentColor = Color.White
                        ),
                        enabled = syncState != SyncState.CONNECTING
                    ) {
                        AnimatedContent(
                            targetState = syncState,
                            label = "buttonContent"
                        ) { state ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (state == SyncState.CONNECTED) Icons.Default.Refresh else Icons.Default.Link,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    when (state) {
                                        SyncState.CONNECTING -> "Connecting..."
                                        SyncState.CONNECTED -> "Disconnect"
                                        SyncState.ERROR -> "Try Again"
                                        else -> "Connect"
                                    },
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RadarAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing, delayMillis = 1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        RadarCircle(pulse1)
        RadarCircle(pulse2)
    }
}

@Composable
fun RadarCircle(progress: Float) {
    Box(
        modifier = Modifier
            .size(300.dp * progress)
            .graphicsLayer { alpha = 1f - progress }
            .border(2.dp, ActionRed.copy(alpha = 0.5f), CircleShape)
    )
}

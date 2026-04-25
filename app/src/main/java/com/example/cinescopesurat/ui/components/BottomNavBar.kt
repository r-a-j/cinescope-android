package com.example.cinescopesurat.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cinescopesurat.ui.navigation.bottomNavItems
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid

@Composable
fun BottomNavBar(
    navController: NavController,
    liquidState: LiquidState
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination
    
    val selectedIndex = bottomNavItems.indexOfFirst { item ->
        currentDestination?.hasRoute(item.route::class) ?: false
    }

    val customColors = CinescopeTheme.customColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
        ) {
            val itemWidth = maxWidth / bottomNavItems.size
            val density = LocalDensity.current
            
            // Glass Background Layer (Clipped)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .liquid(liquidState) {
                        frost = 3.dp
                        refraction = 0.25f
                        curve = 0.25f
                        saturation = 1.4f
                        contrast = 1.2f
                        dispersion = 0.08f
                        tint = customColors.glassBackground.copy(alpha = 0.05f)
                    }
            )

            // DRAMATIC "SOUL" MOTION: Leading and Trailing edges move at different speeds
            val leftTarget = if (selectedIndex != -1) itemWidth * selectedIndex else 0.dp
            val rightTarget = if (selectedIndex != -1) itemWidth * (selectedIndex + 1) else itemWidth

            val leftEdge by animateDpAsState(
                targetValue = leftTarget,
                animationSpec = spring(stiffness = 150f, dampingRatio = 0.8f),
                label = "leftEdge"
            )
            val rightEdge by animateDpAsState(
                targetValue = rightTarget,
                animationSpec = spring(stiffness = 250f, dampingRatio = 0.7f),
                label = "rightEdge"
            )

            val activeColor = if (selectedIndex != -1) bottomNavItems[selectedIndex].glowColor else Color.White

            // The "Soul" - Liquid Elastic Indicator (Stays inside the bar)
            Box(
                modifier = Modifier
                    .offset { IntOffset(with(density) { leftEdge.toPx() }.toInt(), 0) }
                    .width(rightEdge - leftEdge)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Main Glow "Engine"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    activeColor.copy(alpha = 0.45f),
                                    activeColor.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .blur(16.dp)
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                bottomNavItems.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 2.0f else 1f,
                        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                    )
                    
                    val translationY by animateDpAsState(
                        targetValue = if (isSelected) (-24).dp else 0.dp,
                        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                    )
                    
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) item.glowColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )

                    val haloAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0f,
                        animationSpec = tween(400)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isSelected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Standing Out: Elegant Glow Halo
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .graphicsLayer { this.translationY = translationY.toPx() }
                                    .background(
                                        Brush.radialGradient(
                                            listOf(item.glowColor.copy(alpha = 0.35f * haloAlpha), Color.Transparent)
                                        )
                                    )
                                    .blur(10.dp)
                            )
                        }

                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = iconColor,
                            modifier = Modifier
                                .size(26.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.translationY = translationY.toPx()
                                }
                        )
                    }
                }
            }
        }
    }
}

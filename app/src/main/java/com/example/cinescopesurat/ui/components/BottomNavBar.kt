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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
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
    var barSize by remember { mutableStateOf(IntSize.Zero) }

    // Use the item's specific glow color if selected, otherwise fallback to primary
    val activeBrandColor = if (selectedIndex != -1) {
        bottomNavItems[selectedIndex].glowColor
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // GLASS BACKGROUND LAYER
        Box(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .onSizeChanged { barSize = it }
                .clip(CircleShape)
                .liquid(liquidState) {
                    frost = 3.dp
                    refraction = 0.25f
                    curve = 0.25f
                    edge = 0.0f
                    saturation = 1.4f
                    dispersion = 0.08f
                    tint = if (selectedIndex != -1) {
                        activeBrandColor.copy(alpha = 0.15f)
                    } else {
                        customColors.glassBackground.copy(alpha = 0.05f)
                    }
                }
        )

        // INTERACTION & ICON LAYER
        Box(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
        ) {
            if (barSize.width > 0) {
                val itemWidthPx = barSize.width.toFloat() / bottomNavItems.size

                val currentActiveColor = if (selectedIndex != -1) activeBrandColor else Color.White

                // Optimized Soul Indicator
                LiquidSoulIndicator(
                    selectedIndex = selectedIndex,
                    itemWidthPx = itemWidthPx,
                    activeColor = currentActiveColor
                )

                Row(modifier = Modifier.fillMaxSize()) {
                    bottomNavItems.forEachIndexed { index, item ->
                        val isSelected = selectedIndex == index

                        // Optimized GPU-Accelerated Animations
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 2.0f else 1f,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                            label = "scale"
                        )

                        val translationY by animateDpAsState(
                            targetValue = if (isSelected) (-24).dp else 0.dp,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                            label = "transY"
                        )

                        val iconColor by animateColorAsState(
                            targetValue = if (isSelected) activeBrandColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            label = "color"
                        )

                        val haloAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 1f else 0f,
                            animationSpec = tween(400),
                            label = "halo"
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
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .graphicsLayer { this.translationY = translationY.toPx() }
                                        .background(
                                            Brush.radialGradient(
                                                listOf(activeBrandColor.copy(alpha = 0.35f * haloAlpha), Color.Transparent)
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
}

@Composable
private fun LiquidSoulIndicator(
    selectedIndex: Int,
    itemWidthPx: Float,
    activeColor: Color
) {
    val density = LocalDensity.current

    // SOUL MOTION: Elastic stretching on tabs switch
    val leftTarget = if (selectedIndex != -1) itemWidthPx * selectedIndex else 0f
    val rightTarget = if (selectedIndex != -1) itemWidthPx * (selectedIndex + 1) else itemWidthPx

    val leftEdge by animateFloatAsState(
        targetValue = leftTarget,
        animationSpec = spring(stiffness = 150f, dampingRatio = 0.8f),
        label = "left"
    )
    val rightEdge by animateFloatAsState(
        targetValue = rightTarget,
        animationSpec = spring(stiffness = 250f, dampingRatio = 0.7f),
        label = "right"
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .graphicsLayer {
                translationX = leftEdge
            }
            .width(with(density) { (rightEdge - leftEdge).toDp() })
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
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
}

package com.example.cinescopesurat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.RectangleShape
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid

/**
 * Optimized Apple-style Progressive Blur Header.
 * Uses 5 overlapping layers for high performance and smooth visual transition.
 */
@Composable
fun ProgressiveBlurHeader(
    liquidState: LiquidState,
    modifier: Modifier = Modifier,
    height: Dp = 140.dp,
    tintColor: Color? = null,
    intensity: Float = 1f
) {
    if (intensity <= 0.01f) return
    
    val customColors = com.example.cinescopesurat.ui.theme.CinescopeTheme.customColors
    val glassTint = tintColor ?: customColors.glassBackground.copy(alpha = 0.03f)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer { 
                alpha = intensity.coerceIn(0f, 1f)
            }
    ) {
        // LAYERED PROGRESSIVE BLUR (5 Layers for Performance)
        // We use a non-linear distribution of blur radii for a more natural "fade"
        
        // 1. Heavy Base (64dp) - Covers the top most part
        BlurLayer(liquidState, 64.dp, 0.0f, 0.4f)
        
        // 2. Strong (32dp) - Deep overlap
        BlurLayer(liquidState, 32.dp, 0.2f, 0.6f)
        
        // 3. Medium (16.dp)
        BlurLayer(liquidState, 16.dp, 0.4f, 0.8f)
        
        // 4. Soft (8.dp)
        BlurLayer(liquidState, 8.dp, 0.6f, 0.9f)
        
        // 5. Hint (2.dp) - The final "shimmer" before clear
        BlurLayer(liquidState, 2.dp, 0.8f, 1.0f)
        
        // Ambient glass tint that provides the "surface" feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to glassTint,
                        0.6f to glassTint.copy(alpha = 0.01f),
                        1f to Color.Transparent
                    )
                )
        )
    }
}

@Composable
private fun BlurLayer(
    liquidState: LiquidState,
    radius: Dp,
    stop1: Float,
    stop2: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Offscreen strategy is expensive, but required for BlendMode.DstIn.
            // With 5 layers, modern devices should handle this at 60fps.
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .liquid(liquidState) {
                frost = radius
                refraction = 0.0f
                curve = 0.0f
                edge = 0.0f
                shape = RectangleShape
                tint = Color.Transparent
            }
            .drawWithContent {
                drawContent()
                // Use a smooth quadratic-like fade for the alpha mask
                drawRect(
                    brush = Brush.verticalGradient(
                        0f to Color.Black,
                        stop1 to Color.Black,
                        stop2 to Color.Transparent,
                        1f to Color.Transparent
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
    )
}

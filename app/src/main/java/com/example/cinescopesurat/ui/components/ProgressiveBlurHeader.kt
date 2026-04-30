package com.example.cinescopesurat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.drawWithCache
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid

/**
 * Industry-Standard Progressive Blur Header.
 * Optimized for 60fps performance using 3-layer multiplexing and zero-recomposition draw logic.
 */
@Composable
fun ProgressiveBlurHeader(
    liquidState: LiquidState,
    modifier: Modifier = Modifier,
    height: Dp = 150.dp, // Premium tall header
    intensityProvider: () -> Float
) {
    val customColors = com.example.cinescopesurat.ui.theme.CinescopeTheme.customColors
    val glassTint = customColors.glassBackground.copy(alpha = 0.04f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                // Moving all animation logic to the DRAW phase to eliminate lag
                alpha = intensityProvider().coerceIn(0f, 1f)
                // Single parent buffer for the entire effect group
                compositingStrategy = CompositingStrategy.Offscreen
            }
    ) {
        // LAYER 1: PEAK HALO (64dp Blur + Light Dispersion)
        // This creates the "Halo" effect where content colors bleed beautifully at the top
        BlurLayer(
            liquidState = liquidState, 
            radius = 64.dp, 
            stop1 = 0.0f, 
            stop2 = 0.45f,
            dispersion = 0.4f,
            refraction = 0.2f
        )

        // LAYER 2: THE GLASS CORE (16dp Blur)
        // The main body of the blur, providing a consistent frosted look
        BlurLayer(
            liquidState = liquidState, 
            radius = 16.dp, 
            stop1 = 0.30f, 
            stop2 = 0.75f
        )

        // LAYER 3: THE SOFT ENTRY (2dp Blur)
        // High-frequency detail for seamless integration with the content area
        BlurLayer(
            liquidState = liquidState, 
            radius = 2.dp, 
            stop1 = 0.65f, 
            stop2 = 1.0f
        )

        // PEAK GLASS HIGHLIGHT (Subtle light reflection at the top edge)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to customColors.glassHighlight.copy(alpha = 0.1f),
                        0.3f to Color.Transparent
                    )
                )
        )

        // AMBIENT GLASS TINT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to glassTint,
                        0.5f to glassTint.copy(alpha = 0.01f),
                        0.9f to Color.Transparent
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
    stop2: Float,
    dispersion: Float = 0f,
    refraction: Float = 0f
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .liquid(liquidState) {
                frost = radius
                this.dispersion = dispersion
                this.refraction = refraction
                curve = 0.0f
                edge = 0.0f
                shape = RectangleShape
                tint = Color.Transparent
            }
            .drawWithCache {
                // Pre-allocate the mask to avoid frame-time allocations (Garbage Collection spikes)
                val maskBrush = Brush.verticalGradient(
                    0.0f to Color.Black,
                    stop1 to Color.Black,
                    // Quadratic easing to hide layer seams
                    (stop1 + (stop2 - stop1) * 0.4f) to Color.Black.copy(alpha = 0.8f),
                    stop2 to Color.Transparent
                )
                onDrawWithContent {
                    drawContent()
                    // Apply alpha mask to the blurred slice
                    drawRect(brush = maskBrush, blendMode = BlendMode.DstIn)
                }
            }
    )
}

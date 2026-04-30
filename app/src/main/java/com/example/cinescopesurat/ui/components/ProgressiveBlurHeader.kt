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
 * High-Performance Apple-style Progressive Blur Header.
 * Uses a refined multiplexing strategy with overlapping sigmoid gradients
 * to eliminate banding and "white hue" artifacts.
 */
@Composable
fun ProgressiveBlurHeader(
    liquidState: LiquidState,
    modifier: Modifier = Modifier,
    height: Dp = 140.dp,
    intensityProvider: () -> Float
) {
    // 0. Remove manual tints. Let content color bleed purely through optics.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                alpha = intensityProvider().coerceIn(0f, 1f)
                compositingStrategy = CompositingStrategy.Offscreen
            }
    ) {
        // 1. High-Fidelity Logarithmic Stack (Indistinguishable from true progressive blur)
        // We use carefully calculated overlaps to remove 'separation layers'.
        
        // Logarithmic steps provide the most natural visual falloff.
        BlurLayer(liquidState, 64.dp, 0.0f, 0.55f, 1.4f, 0.3f) // Heavy peak
        BlurLayer(liquidState, 32.dp, 0.15f, 0.75f, 1.2f, 0.15f) // Smooth mid
        BlurLayer(liquidState, 12.dp, 0.40f, 0.90f, 1.1f, 0.05f) // Subtle edge
        BlurLayer(liquidState, 2.dp, 0.70f, 1.00f, 1.0f, 0.0f) // Crystal integration
    }
}

@Composable
private fun BlurLayer(
    liquidState: LiquidState,
    radius: Dp,
    startPoint: Float,
    endPoint: Float,
    saturation: Float = 1.0f,
    dispersion: Float = 0.0f
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .liquid(liquidState) {
                frost = radius
                this.saturation = saturation
                this.dispersion = dispersion
                refraction = 0.1f // Consistent micro-distortion for a "glassy" texture
                curve = 0.0f
                edge = 0.0f
                shape = RectangleShape
                tint = Color.Transparent
            }
            .drawWithCache {
                // Sigma-weighted gradient mask removes visible 'lines'
                val mask = Brush.verticalGradient(
                    0.0f to Color.Black,
                    startPoint to Color.Black,
                    (startPoint + (endPoint - startPoint) * 0.45f) to Color.Black.copy(alpha = 0.6f),
                    endPoint to Color.Transparent
                )
                onDrawWithContent {
                    drawContent()
                    drawRect(brush = mask, blendMode = BlendMode.DstIn)
                }
            }
    )
}

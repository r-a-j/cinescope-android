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
    height: Dp = 70.dp,
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
        // 1. Hyper-Fidelity Hyper-Intensity Stack
        // Compressed for a 70dp height to keep status bar heavily blurred while freeing content.
        BlurLayer(liquidState, 96.dp, 0.0f, 0.70f, 1.8f, 0.4f) // Hyper-Peak
        BlurLayer(liquidState, 48.dp, 0.15f, 0.85f, 1.5f, 0.2f) // Smooth mid
        BlurLayer(liquidState, 16.dp, 0.45f, 0.95f, 1.2f, 0.1f) // Edge integration
        BlurLayer(liquidState, 2.dp, 0.75f, 1.00f, 1.1f, 0.0f) // Final blend
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

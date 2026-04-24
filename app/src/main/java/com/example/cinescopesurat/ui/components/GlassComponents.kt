package com.example.cinescopesurat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cinescopesurat.ui.theme.CinescopeTheme

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val customColors = CinescopeTheme.customColors
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        customColors.glassHighlight,
                        customColors.glassBackground
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        customColors.glassHighlight.copy(alpha = customColors.glassHighlight.alpha * 1.5f),
                        customColors.glassHighlight.copy(alpha = 0.1f),
                        customColors.glassHighlight,
                        customColors.glassHighlight.copy(alpha = 0.05f),
                        customColors.glassHighlight.copy(alpha = 1.2f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        content = content
    )
}

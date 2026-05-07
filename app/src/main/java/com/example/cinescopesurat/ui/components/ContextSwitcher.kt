package com.example.cinescopesurat.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinescopesurat.ui.viewmodel.VaultContext

@Composable
fun ContextSwitcher(
    selectedContext: VaultContext,
    onContextSelected: (VaultContext) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ContextTab(
            label = "Unified List",
            selected = selectedContext == VaultContext.UNIFIED_LIST,
            onClick = { onContextSelected(VaultContext.UNIFIED_LIST) },
            modifier = Modifier.weight(1f)
        )
        ContextTab(
            label = "Physical Shelf",
            selected = selectedContext == VaultContext.PHYSICAL_SHELF,
            onClick = { onContextSelected(VaultContext.PHYSICAL_SHELF) },
            modifier = Modifier.weight(1f)
        )
        ContextTab(
            label = "Purgatory",
            selected = selectedContext == VaultContext.PURGATORY,
            onClick = { onContextSelected(VaultContext.PURGATORY) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ContextTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(300),
        label = "tabBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        animationSpec = tween(300),
        label = "tabContent"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(46.dp),
        color = backgroundColor,
        modifier = modifier.height(48.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

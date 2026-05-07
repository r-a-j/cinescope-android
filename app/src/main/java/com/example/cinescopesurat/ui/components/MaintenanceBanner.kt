package com.example.cinescopesurat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinescopesurat.ui.viewmodel.MaintenanceBanner
import com.example.cinescopesurat.ui.viewmodel.MaintenanceBannerType

@Composable
fun MaintenanceBanner(
    banner: MaintenanceBanner,
    modifier: Modifier = Modifier
) {
    if (!banner.isVisible) return

    val backgroundColor = when (banner.type) {
        MaintenanceBannerType.ALERT -> MaterialTheme.colorScheme.tertiary
        MaintenanceBannerType.INFO -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when (banner.type) {
        MaintenanceBannerType.ALERT -> MaterialTheme.colorScheme.onTertiary
        MaintenanceBannerType.INFO -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = banner.title.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = contentColor,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = banner.description,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

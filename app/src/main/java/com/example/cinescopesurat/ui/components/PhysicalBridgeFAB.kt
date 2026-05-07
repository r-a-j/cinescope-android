package com.example.cinescopesurat.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cinescopesurat.ui.theme.CinematicCrimson

@Composable
fun PhysicalBridgeFAB(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onScanClick,
        shape = CircleShape,
        containerColor = CinematicCrimson,
        contentColor = androidx.compose.ui.graphics.Color.White,
        modifier = modifier.size(64.dp)
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = "Scan UPC",
            modifier = Modifier.size(28.dp)
        )
    }
}

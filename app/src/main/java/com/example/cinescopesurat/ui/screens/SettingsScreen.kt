package com.example.cinescopesurat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cinescopesurat.data.AppTheme
import com.example.cinescopesurat.ui.viewmodel.ThemeViewModel

@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val currentTheme by themeViewModel.themeState.collectAsStateWithLifecycle()
    var adultContentEnabled by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 120.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 2026 BOLD HEADER
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(40.dp))

            // APPEARANCE SECTION
            FutureSectionHeader("APPEARANCE")
            FutureThemeSelector(
                currentTheme = currentTheme,
                onThemeSelected = { themeViewModel.setTheme(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // CONTENT & PRIVACY
            FutureSectionHeader("CONTENT & PRIVACY")
            FutureActionTile(
                icon = Icons.Default.VpnLock,
                title = "Adult Content",
                subtitle = "Allow restricted content in search",
                trailing = {
                    Switch(
                        checked = adultContentEnabled,
                        onCheckedChange = { adultContentEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // DATA MANAGEMENT
            FutureSectionHeader("DATA MANAGEMENT")
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                FutureListItem("Clear Watched", Icons.Default.VisibilityOff) { /* TODO */ }
                FutureListItem("Clear Watchlist", Icons.Default.DeleteSweep) { /* TODO */ }
                FutureListItem("Clear Cache", Icons.Default.CleaningServices) { /* TODO */ }
                FutureListItem("Backup Data Locally", Icons.Default.Backup) { /* TODO */ }
                FutureListItem("Restore from Backup", Icons.Default.SettingsBackupRestore, isLast = true) { /* TODO */ }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ABOUT & SUPPORT
            FutureSectionHeader("ABOUT & SUPPORT")
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                FutureListItem("Source Code", Icons.Default.Code) { /* TODO */ }
                FutureListItem("Send Feedback", Icons.Default.Feedback) { /* TODO */ }
                FutureListItem("Developer Website", Icons.Default.Language, isLast = true) { /* TODO */ }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // VERSION INFO
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CINESCOPE CORE",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "VERSION 1.0.0 (2026.04.24)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun FutureSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
        letterSpacing = 1.sp
    )
}

@Composable
fun FutureThemeSelector(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val themes = listOf(
            Triple(AppTheme.LIGHT, "Light", Icons.Default.LightMode),
            Triple(AppTheme.DARK, "Dark", Icons.Default.DarkMode),
            Triple(AppTheme.SYSTEM, "Auto", Icons.Default.HdrAuto)
        )

        themes.forEach { (theme, label, icon) ->
            val isSelected = currentTheme == theme
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    .clickable { onThemeSelected(theme) }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        icon, 
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun FutureActionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            trailing()
        }
    }
}

@Composable
fun FutureListItem(
    title: String,
    icon: ImageVector,
    isLast: Boolean = false,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(14.dp)
            )
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
        }
    }
}

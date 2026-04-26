package com.example.cinescopesurat.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

import com.example.cinescopesurat.ui.theme.ActionRed
import com.example.cinescopesurat.ui.theme.OracleBlue
import com.example.cinescopesurat.ui.theme.SoulPurple
import com.example.cinescopesurat.ui.theme.CinematicCrimson
import com.example.cinescopesurat.ui.theme.DiscoveryCyan
import com.example.cinescopesurat.ui.theme.SettingsSlate
import com.example.cinescopesurat.ui.theme.IdentityOrange

@Serializable
sealed interface Route {
    @Serializable object Pulse : Route
    @Serializable object Oracle : Route
    @Serializable object Vault : Route
    @Serializable object SocialHub : Route
    @Serializable object Identity : Route
    @Serializable object Search : Route
    @Serializable object Settings : Route
    @Serializable data class MovieDetails(val id: Int) : Route
    @Serializable data class TvShowDetails(val id: Int) : Route
    @Serializable data class PersonDetails(val id: Int) : Route
}

data class BottomNavItem(
    val route: Any,
    val title: String,
    val icon: ImageVector,
    val glowColor: Color
)

val bottomNavItems = listOf(
    BottomNavItem(Route.Pulse, "Pulse", Icons.Default.Bolt, ActionRed),
    BottomNavItem(Route.Oracle, "Oracle", Icons.Default.AutoAwesome, OracleBlue),
    BottomNavItem(Route.Vault, "Vault", Icons.Default.VideoLibrary, CinematicCrimson),
    BottomNavItem(Route.SocialHub, "Social Hub", Icons.Default.Group, SoulPurple),
    BottomNavItem(Route.Identity, "Identity", Icons.Default.Person, IdentityOrange),
    BottomNavItem(Route.Search, "Search", Icons.Default.Search, DiscoveryCyan),
    BottomNavItem(Route.Settings, "Settings", Icons.Default.Settings, SettingsSlate)
)

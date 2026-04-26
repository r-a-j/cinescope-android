package com.example.cinescopesurat.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

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
    BottomNavItem(Route.Pulse, "Pulse", Icons.Default.Bolt, Color(0xFFF85149)),
    BottomNavItem(Route.Oracle, "Oracle", Icons.Default.AutoAwesome, Color(0xFF79C0FF)),
    BottomNavItem(Route.Vault, "Vault", Icons.Default.VideoLibrary, Color(0xFFE50914)),
    BottomNavItem(Route.SocialHub, "Social Hub", Icons.Default.Group, Color(0xFFD2A8FF)),
    BottomNavItem(Route.Identity, "Identity", Icons.Default.Person, Color(0xFFF0883E)),
    BottomNavItem(Route.Search, "Search", Icons.Default.Search, Color.White),
    BottomNavItem(Route.Settings, "Settings", Icons.Default.Settings, Color.White)
)

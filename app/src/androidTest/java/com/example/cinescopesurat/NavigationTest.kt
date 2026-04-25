package com.example.cinescopesurat

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cinescopesurat.data.AppTheme
import com.example.cinescopesurat.ui.components.BottomNavBar
import com.example.cinescopesurat.ui.navigation.Route
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import io.github.fletchmckee.liquid.rememberLiquidState
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bottomNavBar_displaysAllItems() {
        composeTestRule.setContent {
            CinescopeTheme(appTheme = AppTheme.SYSTEM) {
                TestNavHost()
            }
        }

        // Verify that all bottom nav items are displayed
        // Pulse, Oracle, Vault, Social Hub, Identity
        composeTestRule.onNodeWithContentDescription("Pulse").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Oracle").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Vault").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Social Hub").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Identity").assertIsDisplayed()
    }

    @Test
    fun bottomNavBar_navigationWorks() {
        composeTestRule.setContent {
            CinescopeTheme(appTheme = AppTheme.SYSTEM) {
                TestNavHost()
            }
        }

        // Initially on Pulse screen
        composeTestRule.onNodeWithText("Pulse Screen").assertIsDisplayed()

        // Click on Oracle
        composeTestRule.onNodeWithContentDescription("Oracle").performClick()
        composeTestRule.onNodeWithText("Oracle Screen").assertIsDisplayed()

        // Click on Vault
        composeTestRule.onNodeWithContentDescription("Vault").performClick()
        composeTestRule.onNodeWithText("Vault Screen").assertIsDisplayed()

        // Click on Social Hub
        composeTestRule.onNodeWithContentDescription("Social Hub").performClick()
        composeTestRule.onNodeWithText("Social Hub Screen").assertIsDisplayed()

        // Click on Identity
        composeTestRule.onNodeWithContentDescription("Identity").performClick()
        composeTestRule.onNodeWithText("Identity Screen").assertIsDisplayed()
    }
}

@Composable
fun TestNavHost() {
    val navController = rememberNavController()
    val liquidState = rememberLiquidState()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Route.Pulse,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<Route.Pulse> { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Pulse Screen") } }
            composable<Route.Oracle> { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Oracle Screen") } }
            composable<Route.Vault> { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Vault Screen") } }
            composable<Route.SocialHub> { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Social Hub Screen") } }
            composable<Route.Identity> { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Identity Screen") } }
            composable<Route.Search> { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Search Screen") } }
            composable<Route.Settings> { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Settings Screen") } }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController, liquidState)
        }
    }
}

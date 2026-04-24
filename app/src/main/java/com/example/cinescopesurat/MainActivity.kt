package com.example.cinescopesurat

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cinescopesurat.ui.components.BottomNavBar
import com.example.cinescopesurat.ui.navigation.Route
import com.example.cinescopesurat.ui.screens.PulseScreen
import com.example.cinescopesurat.ui.screens.SettingsScreen
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import com.example.cinescopesurat.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplashScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        lifecycleScope.launch {
            delay(1000)
            keepSplashScreen = false
        }

        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val appTheme by themeViewModel.themeState.collectAsStateWithLifecycle()

            CinescopeTheme(appTheme = appTheme) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Route.Pulse,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<Route.Pulse> { PulseScreen() }
            composable<Route.Oracle> { PlaceholderScreen("Oracle") }
            composable<Route.Vault> { PlaceholderScreen("Vault") }
            composable<Route.SocialHub> { PlaceholderScreen("Social Hub") }
            composable<Route.Identity> { PlaceholderScreen("Identity") }
            composable<Route.Search> { PlaceholderScreen("Search") }
            composable<Route.Settings> { SettingsScreen() }
        }
        
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = "$name Screen", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

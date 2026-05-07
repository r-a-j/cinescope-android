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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.geometry.Offset
import com.example.cinescopesurat.ui.components.BottomNavBar
import com.example.cinescopesurat.ui.components.ProgressiveBlurHeader
import com.example.cinescopesurat.ui.navigation.Route
import com.example.cinescopesurat.ui.navigation.bottomNavItems
import com.example.cinescopesurat.ui.screens.SocialHubScreen
import com.example.cinescopesurat.ui.screens.VaultScreen
import com.example.cinescopesurat.ui.screens.PulseScreen
import com.example.cinescopesurat.ui.screens.SearchScreen
import com.example.cinescopesurat.ui.screens.SettingsScreen
import com.example.cinescopesurat.ui.screens.MovieDetailsScreen
import com.example.cinescopesurat.ui.screens.TvShowDetailsScreen
import com.example.cinescopesurat.ui.screens.PersonDetailsScreen
import com.example.cinescopesurat.ui.screens.OracleScreen
import com.example.cinescopesurat.ui.theme.CinescopeTheme
import com.example.cinescopesurat.ui.viewmodel.ThemeViewModel
import androidx.compose.animation.*
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.rememberLiquidState
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
    val liquidState = rememberLiquidState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route ?: Route.Pulse::class.qualifiedName ?: ""

    // Persist scroll offsets keyed by route using a Bundle-safe format
    val scrollOffsetsState = rememberSaveable(
        saver = Saver<MutableState<Map<String, Float>>, ArrayList<List<Any>>>(
            save = { state -> 
                ArrayList(state.value.entries.map { listOf(it.key, it.value) })
            },
            restore = { saved ->
                mutableStateOf(saved.associate { it[0] as String to it[1] as Float })
            }
        )
    ) { 
        mutableStateOf(emptyMap()) 
    }
    var scrollOffsets by scrollOffsetsState

    val nestedScrollConnection = remember(currentRoute) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val currentOffset = scrollOffsets[currentRoute] ?: 0f
                var newOffset = currentOffset
                
                // Blur the header on any downward scroll (consumed.y < 0)
                if (consumed.y < 0) {
                    newOffset = (currentOffset + consumed.y).coerceIn(-120f, 0f)
                }
                // Clear the header ONLY when upward scroll is unconsumed (meaning child reached top)
                if (available.y > 0) {
                    newOffset = (currentOffset + available.y).coerceIn(-120f, 0f)
                }

                if (newOffset != currentOffset) {
                    scrollOffsets = scrollOffsets.toMutableMap().apply {
                        put(currentRoute, newOffset)
                    }
                }

                return Offset.Zero
            }
        }
    }

    // Configurable threshold (10%) and range
    val blurThreshold = 0.1f
    val blurRange = 70f
    
    val blurIntensityProvider = {
        val offset = scrollOffsets[currentRoute] ?: 0f
        val rawIntensity = (-offset / blurRange).coerceIn(0f, 1f)
        if (rawIntensity < blurThreshold) {
            0f
        } else {
            ((rawIntensity - blurThreshold) / (1f - blurThreshold)).coerceIn(0f, 1f)
        }
    }

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hasRoute(item.route::class) ?: false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        NavHost(
            navController = navController,
            startDestination = Route.Pulse,
            modifier = Modifier
                .fillMaxSize()
                .liquefiable(liquidState),
        ) {
            composable<Route.Pulse> { 
                PulseScreen(
                    onMovieClick = { id -> navController.navigate(Route.MovieDetails(id)) }
                )
            }
            composable<Route.Oracle> {
                OracleScreen(
                    onMovieClick = { id -> navController.navigate(Route.MovieDetails(id)) },
                    liquidState = liquidState
                )
            }
            composable<Route.Vault> { VaultScreen() }
            composable<Route.SocialHub> { SocialHubScreen() }
            composable<Route.Identity> { PlaceholderScreen("Identity") }
            composable<Route.Search> { SearchScreen(
                onMovieClick = { id -> navController.navigate(Route.MovieDetails(id)) },
                onTvShowClick = { id -> navController.navigate(Route.TvShowDetails(id)) },
                onPersonClick = { id -> navController.navigate(Route.PersonDetails(id)) }
            ) }
            composable<Route.Settings> { SettingsScreen() }
            composable<Route.MovieDetails> { backStackEntry ->
                val details = backStackEntry.toRoute<Route.MovieDetails>()
                MovieDetailsScreen(id = details.id, onBack = { navController.popBackStack() })
            }
            composable<Route.TvShowDetails> { backStackEntry ->
                val details = backStackEntry.toRoute<Route.TvShowDetails>()
                TvShowDetailsScreen(id = details.id, onBack = { navController.popBackStack() })
            }
            composable<Route.PersonDetails> { backStackEntry ->
                val details = backStackEntry.toRoute<Route.PersonDetails>()
                PersonDetailsScreen(id = details.id, onBack = { navController.popBackStack() })
            }
        }
        
        // TOP PROGRESSIVE BLUR (Apple-style)
        ProgressiveBlurHeader(
            liquidState = liquidState,
            intensityProvider = blurIntensityProvider
        )
        
        AnimatedVisibility(
            visible = showBottomBar,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavBar(navController, liquidState)
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

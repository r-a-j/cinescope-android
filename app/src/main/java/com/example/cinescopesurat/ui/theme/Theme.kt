package com.example.cinescopesurat.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import com.example.cinescopesurat.data.AppTheme

@Immutable
data class CinescopeCustomColors(
    val glassBackground: Color = Color.Unspecified,
    val glassHighlight: Color = Color.Unspecified
)

val LocalCinescopeCustomColors = staticCompositionLocalOf { CinescopeCustomColors() }

private val DarkColorScheme = darkColorScheme(
    primary = CinematicCrimson,
    onPrimary = Color.White,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = BoldOrange,
    onSecondary = Color.Black,
    tertiary = DeepRichRed,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = CinematicCrimson,
    onPrimary = Color.White,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = BoldOrange,
    onSecondary = Color.White,
    tertiary = DeepRichRed,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface
)

@Composable
fun CinescopeTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val dynamicScheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            dynamicScheme.copy(
                primary = CinematicCrimson,
                onPrimary = Color.White,
                primaryContainer = if (darkTheme) DarkPrimaryContainer else LightPrimaryContainer,
                onPrimaryContainer = if (darkTheme) DarkOnPrimaryContainer else LightOnPrimaryContainer,
                secondary = BoldOrange,
                onSecondary = if (darkTheme) Color.Black else Color.White,
                tertiary = DeepRichRed,
                onTertiary = Color.White
            )
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) {
        CinescopeCustomColors(
            glassBackground = DarkGlassBackground,
            glassHighlight = DarkGlassHighlight
        )
    } else {
        CinescopeCustomColors(
            glassBackground = LightGlassBackground,
            glassHighlight = LightGlassHighlight
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalCinescopeCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object CinescopeTheme {
    val customColors: CinescopeCustomColors
        @Composable
        get() = LocalCinescopeCustomColors.current
}

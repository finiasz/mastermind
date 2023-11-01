package net.finiasz.mastermind.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    surface = Color.White,
    onSurface = Color.Black,
    outline = Color.LightGray,
    primary = LightChiffre,
    onPrimary = Color.White,
    secondary = LightOp,
    onSecondary = Color.Black,
    tertiary = LightGreen,
    onTertiary = Color.Black,
    tertiaryContainer = DarkGreen,
    error = LightReset,
    onError = Color.Black,
    surfaceVariant = Color(0xffffffff),
    outlineVariant = LightSolve,
    primaryContainer = Color(0xffdddddd),
)

private val DarkColorScheme = darkColorScheme(
    surface = Color.Black,
    onSurface = Color(0xffdddddd),
    outline = Color.DarkGray,
    primary = LightChiffre,
    onPrimary = Color(0xffdddddd),
    secondary = LightOp,
    onSecondary = Color.Black,
    tertiary = DarkGreen,
    onTertiary = Color(0xffdddddd),
    tertiaryContainer = LightGreen,
    error = DarkReset,
    onError = Color(0xffdddddd),
    surfaceVariant = Color(0xff333333),
    outlineVariant = DarkSolve,
    primaryContainer = Color(0xff333333),
)

@Composable
fun MastermindTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
    )
}
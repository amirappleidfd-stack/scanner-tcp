package com.railwaypingtester.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BlueAccent,
    secondary = PurpleGradientStart,
    tertiary = YellowTesting,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkCardBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = RedOffline,
    errorContainer = RedOffline.copy(alpha = 0.2f)
)

private val AppTypography = Typography()

@Composable
fun RailwayPingTesterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
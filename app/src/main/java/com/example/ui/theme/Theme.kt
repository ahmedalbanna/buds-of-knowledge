package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KidsPrimary,
    onPrimary = Color.White,
    primaryContainer = KidsAccentYellow,
    onPrimaryContainer = Color(0xFF3E2723),
    secondary = KidsSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1F5FE),
    tertiary = KidsTertiary,
    onTertiary = Color.White,
    background = KidsBackgroundLight,
    onBackground = Color(0xFF212121),
    surface = KidsSurfaceLight,
    onSurface = Color(0xFF212121),
    surfaceVariant = KidsCardBg
)

private val DarkColorScheme = darkColorScheme(
    primary = KidsDarkPrimary,
    secondary = KidsDarkSecondary,
    background = KidsDarkBackground,
    surface = KidsDarkSurface
)

@Composable
fun BaraemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = KidsPrimary,
    onPrimary = KidsOnPrimary,
    primaryContainer = KidsPrimaryContainer,
    onPrimaryContainer = KidsOnPrimaryContainer,
    secondary = KidsSecondary,
    onSecondary = KidsOnSecondary,
    secondaryContainer = KidsSecondaryContainer,
    onSecondaryContainer = KidsOnSecondaryContainer,
    tertiary = KidsTertiary,
    onTertiary = KidsOnTertiary,
    tertiaryContainer = KidsTertiaryContainer,
    onTertiaryContainer = KidsOnTertiaryContainer,
    background = KidsBackground,
    onBackground = KidsOnBackground,
    surface = KidsSurface,
    onSurface = KidsOnSurface,
    surfaceVariant = KidsSurfaceVariant,
    onSurfaceVariant = KidsOnSurfaceVariant,
    error = KidsError,
    onError = KidsOnError,
    errorContainer = KidsErrorContainer,
    onErrorContainer = KidsOnErrorContainer,
    outline = KidsOutline,
    outlineVariant = KidsOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = KidsDarkPrimary,
    onPrimary = KidsDarkOnPrimary,
    primaryContainer = KidsDarkPrimaryContainer,
    onPrimaryContainer = KidsDarkOnPrimaryContainer,
    secondary = KidsDarkSecondary,
    onSecondary = KidsDarkOnSecondary,
    secondaryContainer = KidsDarkSecondaryContainer,
    onSecondaryContainer = KidsDarkOnSecondaryContainer,
    tertiary = KidsDarkTertiary,
    onTertiary = KidsDarkOnTertiary,
    tertiaryContainer = KidsDarkTertiaryContainer,
    onTertiaryContainer = KidsDarkOnTertiaryContainer,
    background = KidsDarkBackground,
    onBackground = KidsDarkOnBackground,
    surface = KidsDarkSurface,
    onSurface = KidsDarkOnSurface,
    surfaceVariant = KidsDarkSurfaceVariant,
    onSurfaceVariant = KidsDarkOnSurfaceVariant,
    error = KidsDarkError,
    onError = KidsDarkOnError,
    errorContainer = KidsDarkErrorContainer,
    onErrorContainer = KidsDarkOnErrorContainer,
    outline = KidsDarkOutline,
    outlineVariant = KidsDarkOutlineVariant
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

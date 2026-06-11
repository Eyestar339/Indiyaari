package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IndiyaariColorScheme = darkColorScheme(
    primary = ElegantDarkPrimary,
    onPrimary = ElegantDarkOnPrimary,
    primaryContainer = ElegantDarkPrimaryContainer,
    onPrimaryContainer = ElegantDarkOnPrimaryContainer,
    secondary = ElegantDarkSecondary,
    onSecondary = ElegantDarkOnSecondary,
    secondaryContainer = ElegantDarkSecondaryContainer,
    onSecondaryContainer = ElegantDarkOnSecondaryContainer,
    tertiary = ElegantDarkTertiary,
    background = ElegantDarkBg,
    onBackground = ElegantDarkOnBg,
    surface = ElegantDarkSurface,
    onSurface = ElegantDarkOnBg,
    surfaceVariant = ElegantDarkSecondaryContainer,
    onSurfaceVariant = ElegantDarkMuted,
    outline = ElegantDarkBorder
)

private val IndiyaariLightColorScheme = darkColorScheme(
    primary = ElegantDarkPrimary,
    onPrimary = ElegantDarkOnPrimary,
    primaryContainer = ElegantDarkPrimaryContainer,
    onPrimaryContainer = ElegantDarkOnPrimaryContainer,
    secondary = ElegantDarkSecondary,
    onSecondary = ElegantDarkOnSecondary,
    secondaryContainer = ElegantDarkSecondaryContainer,
    onSecondaryContainer = ElegantDarkOnSecondaryContainer,
    tertiary = ElegantDarkTertiary,
    background = ElegantDarkBg,
    onBackground = ElegantDarkOnBg,
    surface = ElegantDarkSurface,
    onSurface = ElegantDarkOnBg,
    surfaceVariant = ElegantDarkSecondaryContainer,
    onSurfaceVariant = ElegantDarkMuted,
    outline = ElegantDarkBorder
)

@Composable
fun IndiyaariTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Avoid dynamic system colors to enforce our beautiful cohesive Indian Saffron/Emerald branding
    val colors = if (darkTheme) IndiyaariColorScheme else IndiyaariLightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}

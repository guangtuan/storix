package com.storix.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = Mint,
    secondary = Sand,
    tertiary = Rose,
    background = NightNavy,
    surface = SlateBlue,
    onPrimary = NightNavy,
    onSecondary = Ink,
    onBackground = Sand,
    onSurface = Sand
)

private val LightColors = lightColorScheme(
    primary = SlateBlue,
    secondary = Mint,
    tertiary = Rose,
    background = SoftSurface,
    surface = Sand,
    onPrimary = SoftSurface,
    onSecondary = Ink,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun StorixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(context)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

package com.storix.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = TelegramBlue,
    secondary = TelegramBlueDark,
    tertiary = TelegramBlueLight,
    background = TelegramBackgroundDark,
    surface = TelegramSurfaceDark,
    surfaceVariant = Color(0xFF2B3A4A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF5F7FA),
    onSurface = Color(0xFFF5F7FA),
    onSurfaceVariant = Color(0xFFA6B3C2)
)

private val LightColors = lightColorScheme(
    primary = TelegramBlue,
    secondary = TelegramBlueDark,
    tertiary = TelegramBlueLight,
    background = TelegramBackground,
    surface = TelegramSurface,
    surfaceVariant = Color(0xFFE8EDF2),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TelegramText,
    onSurface = TelegramText,
    onSurfaceVariant = TelegramSubtext
)

@Composable
fun StorixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

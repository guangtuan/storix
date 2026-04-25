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
    primary = StorixGreen,
    secondary = StorixGreenDark,
    tertiary = StorixGreenLight,
    background = StorixCreamDark,
    surface = StorixSurfaceDark,
    surfaceVariant = Color(0xFF2A3030),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF5F2EE),
    onSurface = Color(0xFFF5F2EE),
    onSurfaceVariant = Color(0xFFA6B3AB)
)

private val LightColors = lightColorScheme(
    primary = StorixGreen,
    secondary = StorixGreenDark,
    tertiary = StorixGreenLight,
    background = StorixCream,
    surface = StorixSurface,
    surfaceVariant = Color(0xFFE5EDE9),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = StorixText,
    onSurface = StorixText,
    onSurfaceVariant = StorixSubtext
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

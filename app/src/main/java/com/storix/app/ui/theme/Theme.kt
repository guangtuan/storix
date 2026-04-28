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

enum class ThemePreset(val displayName: String, val swatch: Color) {
    MANJARO(displayName = "雾松", swatch = Color(0xFF86A398)),
    MINT(displayName = "薄荷", swatch = Color(0xFF92B6AA)),
    OCEAN(displayName = "雾蓝", swatch = Color(0xFF9CB2D2)),
    SUNSET(displayName = "陶土", swatch = Color(0xFFE1A38E)),
    LAVENDER(displayName = "灰紫", swatch = Color(0xFFB58BEE)),
    AMBER(displayName = "沙金", swatch = Color(0xFFD5B27D)),
    ROSE(displayName = "雾粉", swatch = Color(0xFFD7A0B3)),
    SLATE(displayName = "石板", swatch = Color(0xFF9EADBE))
}

private data class ThemeTones(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val lightBackground: Color,
    val lightSurface: Color,
    val lightSurfaceVariant: Color,
    val lightOnSurfaceVariant: Color,
    val darkBackground: Color,
    val darkSurface: Color,
    val darkSurfaceVariant: Color,
    val darkOnSurfaceVariant: Color
)

private fun tonesFor(preset: ThemePreset): ThemeTones {
    return when (preset) {
        ThemePreset.MANJARO -> ThemeTones(
            primary = Color(0xFF86A398),
            secondary = Color(0xFFD9E6DF),
            tertiary = Color(0xFFFDE8E2),
            lightBackground = Color(0xFFF5F0F8),
            lightSurface = Color(0xFFFFFCFF),
            lightSurfaceVariant = Color(0xFFF2EEF6),
            lightOnSurfaceVariant = Color(0xFF756F7E),
            darkBackground = Color(0xFF18171D),
            darkSurface = Color(0xFF23212A),
            darkSurfaceVariant = Color(0xFF2D2934),
            darkOnSurfaceVariant = Color(0xFFB8B2C2)
        )
        ThemePreset.MINT -> ThemeTones(
            primary = Color(0xFF92B6AA),
            secondary = Color(0xFFDFF0EA),
            tertiary = Color(0xFFFDE8E2),
            lightBackground = Color(0xFFF5F0F8),
            lightSurface = Color(0xFFFFFCFF),
            lightSurfaceVariant = Color(0xFFF1EEF7),
            lightOnSurfaceVariant = Color(0xFF726E7B),
            darkBackground = Color(0xFF18171D),
            darkSurface = Color(0xFF23212A),
            darkSurfaceVariant = Color(0xFF2D2934),
            darkOnSurfaceVariant = Color(0xFFB7B2C0)
        )
        ThemePreset.OCEAN -> ThemeTones(
            primary = Color(0xFF9CB2D2),
            secondary = Color(0xFFE2ECFA),
            tertiary = Color(0xFFFCEADF),
            lightBackground = Color(0xFFF4F1F9),
            lightSurface = Color(0xFFFFFCFF),
            lightSurfaceVariant = Color(0xFFF1EEF8),
            lightOnSurfaceVariant = Color(0xFF71707B),
            darkBackground = Color(0xFF17171D),
            darkSurface = Color(0xFF22232A),
            darkSurfaceVariant = Color(0xFF2C2E36),
            darkOnSurfaceVariant = Color(0xFFB6B4BE)
        )
        ThemePreset.SUNSET -> ThemeTones(
            primary = Color(0xFFE1A38E),
            secondary = Color(0xFFF6D8CB),
            tertiary = Color(0xFFF4E4FF),
            lightBackground = Color(0xFFF7F1F8),
            lightSurface = Color(0xFFFFFCFF),
            lightSurfaceVariant = Color(0xFFF7EEF2),
            lightOnSurfaceVariant = Color(0xFF7C6F75),
            darkBackground = Color(0xFF1A171A),
            darkSurface = Color(0xFF262228),
            darkSurfaceVariant = Color(0xFF312B31),
            darkOnSurfaceVariant = Color(0xFFC0B2BB)
        )
        ThemePreset.LAVENDER -> ThemeTones(
            primary = Color(0xFFB58BEE),
            secondary = Color(0xFFE7D8FD),
            tertiary = Color(0xFFFFE8E0),
            lightBackground = Color(0xFFF5F0FB),
            lightSurface = Color(0xFFFFFCFF),
            lightSurfaceVariant = Color(0xFFF2EDF9),
            lightOnSurfaceVariant = Color(0xFF736D80),
            darkBackground = Color(0xFF17151C),
            darkSurface = Color(0xFF231F28),
            darkSurfaceVariant = Color(0xFF2D2833),
            darkOnSurfaceVariant = Color(0xFFB8B2C4)
        )
        ThemePreset.AMBER -> ThemeTones(
            primary = Color(0xFFD5B27D),
            secondary = Color(0xFFF6E6C7),
            tertiary = Color(0xFFF5E1FF),
            lightBackground = Color(0xFFF7F2F8),
            lightSurface = Color(0xFFFFFCFF),
            lightSurfaceVariant = Color(0xFFF5EEF6),
            lightOnSurfaceVariant = Color(0xFF7A717A),
            darkBackground = Color(0xFF1A1719),
            darkSurface = Color(0xFF252124),
            darkSurfaceVariant = Color(0xFF312A2F),
            darkOnSurfaceVariant = Color(0xFFC0B5BC)
        )
        ThemePreset.ROSE -> ThemeTones(
            primary = Color(0xFFD7A0B3),
            secondary = Color(0xFFF4D9E3),
            tertiary = Color(0xFFFFEAE2),
            lightBackground = Color(0xFFF7F1F7),
            lightSurface = Color(0xFFFFFCFF),
            lightSurfaceVariant = Color(0xFFF7EDF4),
            lightOnSurfaceVariant = Color(0xFF7B7078),
            darkBackground = Color(0xFF1A161B),
            darkSurface = Color(0xFF251F24),
            darkSurfaceVariant = Color(0xFF302830),
            darkOnSurfaceVariant = Color(0xFFC0B4BD)
        )
        ThemePreset.SLATE -> ThemeTones(
            primary = Color(0xFF9EADBE),
            secondary = Color(0xFFE2E9F1),
            tertiary = Color(0xFFFFE9E4),
            lightBackground = Color(0xFFF4F0F8),
            lightSurface = Color(0xFFFFFCFF),
            lightSurfaceVariant = Color(0xFFF1EDF5),
            lightOnSurfaceVariant = Color(0xFF726F79),
            darkBackground = Color(0xFF17171C),
            darkSurface = Color(0xFF212128),
            darkSurfaceVariant = Color(0xFF2A2A33),
            darkOnSurfaceVariant = Color(0xFFB5B5C0)
        )
    }
}

private fun lightColors(tones: ThemeTones) = lightColorScheme(
    primary = tones.primary,
    secondary = tones.secondary,
    tertiary = tones.tertiary,
    primaryContainer = tones.tertiary,
    secondaryContainer = tones.lightSurfaceVariant,
    background = tones.lightBackground,
    surface = tones.lightSurface,
    surfaceVariant = tones.lightSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color(0xFF2B2434),
    onPrimaryContainer = Color(0xFF241F2B),
    onSecondaryContainer = Color(0xFF241F2B),
    onBackground = Color(0xFF241F2B),
    onSurface = Color(0xFF241F2B),
    onSurfaceVariant = tones.lightOnSurfaceVariant,
    outline = tones.lightOnSurfaceVariant.copy(alpha = 0.18f),
    outlineVariant = Color.White.copy(alpha = 0.56f),
    surfaceTint = Color.White.copy(alpha = 0.8f)
)

private fun darkColors(tones: ThemeTones) = darkColorScheme(
    primary = tones.primary,
    secondary = tones.secondary,
    tertiary = tones.tertiary,
    primaryContainer = tones.darkSurfaceVariant,
    secondaryContainer = tones.darkSurface,
    background = tones.darkBackground,
    surface = tones.darkSurface,
    surfaceVariant = tones.darkSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color(0xFFF5F1F8),
    onPrimaryContainer = Color(0xFFF3F5F4),
    onSecondaryContainer = Color(0xFFF3F5F4),
    onBackground = Color(0xFFF3F5F4),
    onSurface = Color(0xFFF3F5F4),
    onSurfaceVariant = tones.darkOnSurfaceVariant,
    outline = tones.darkOnSurfaceVariant.copy(alpha = 0.22f),
    outlineVariant = tones.darkSurfaceVariant,
    surfaceTint = Color.White.copy(alpha = 0.08f)
)

@Composable
fun StorixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    themePreset: ThemePreset = ThemePreset.MANJARO,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val tones = tonesFor(themePreset)
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(context)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(context)
        darkTheme -> darkColors(tones)
        else -> lightColors(tones)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

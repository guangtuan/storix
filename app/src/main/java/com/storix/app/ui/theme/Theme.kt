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
    MANJARO(displayName = "雾松", swatch = Color(0xFF6D8A7A)),
    MINT(displayName = "薄荷", swatch = Color(0xFF7B9B92)),
    OCEAN(displayName = "雾蓝", swatch = Color(0xFF748A9B)),
    SUNSET(displayName = "陶土", swatch = Color(0xFFB8836C)),
    LAVENDER(displayName = "灰紫", swatch = Color(0xFF8A84A7)),
    AMBER(displayName = "沙金", swatch = Color(0xFFB49A70)),
    ROSE(displayName = "雾粉", swatch = Color(0xFFB58491)),
    SLATE(displayName = "石板", swatch = Color(0xFF6D808C))
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
            primary = Color(0xFF6D8A7A),
            secondary = Color(0xFF8FA39A),
            tertiary = Color(0xFFE6EEE8),
            lightBackground = Color(0xFFFAFAF8),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF1F3EF),
            lightOnSurfaceVariant = Color(0xFF6A726D),
            darkBackground = Color(0xFF141715),
            darkSurface = Color(0xFF1B1F1D),
            darkSurfaceVariant = Color(0xFF252A27),
            darkOnSurfaceVariant = Color(0xFFAAB2AD)
        )
        ThemePreset.MINT -> ThemeTones(
            primary = Color(0xFF7B9B92),
            secondary = Color(0xFF94AAA4),
            tertiary = Color(0xFFE8EFEC),
            lightBackground = Color(0xFFFAFAF8),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF0F3F1),
            lightOnSurfaceVariant = Color(0xFF67726E),
            darkBackground = Color(0xFF131716),
            darkSurface = Color(0xFF1A1F1D),
            darkSurfaceVariant = Color(0xFF242A27),
            darkOnSurfaceVariant = Color(0xFFA7B1AD)
        )
        ThemePreset.OCEAN -> ThemeTones(
            primary = Color(0xFF748A9B),
            secondary = Color(0xFF8E9FAD),
            tertiary = Color(0xFFE7EDF1),
            lightBackground = Color(0xFFFAFAFA),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF0F2F4),
            lightOnSurfaceVariant = Color(0xFF68727A),
            darkBackground = Color(0xFF14171A),
            darkSurface = Color(0xFF1B2023),
            darkSurfaceVariant = Color(0xFF252B30),
            darkOnSurfaceVariant = Color(0xFFA9B1B8)
        )
        ThemePreset.SUNSET -> ThemeTones(
            primary = Color(0xFFB8836C),
            secondary = Color(0xFFC69A87),
            tertiary = Color(0xFFF4E9E3),
            lightBackground = Color(0xFFFBF9F8),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF4EFEB),
            lightOnSurfaceVariant = Color(0xFF756A64),
            darkBackground = Color(0xFF171412),
            darkSurface = Color(0xFF1F1B19),
            darkSurfaceVariant = Color(0xFF2A2522),
            darkOnSurfaceVariant = Color(0xFFB8AEA9)
        )
        ThemePreset.LAVENDER -> ThemeTones(
            primary = Color(0xFF8A84A7),
            secondary = Color(0xFF9E97B8),
            tertiary = Color(0xFFECEAF3),
            lightBackground = Color(0xFFFAFAFA),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF1F0F5),
            lightOnSurfaceVariant = Color(0xFF706C7F),
            darkBackground = Color(0xFF151418),
            darkSurface = Color(0xFF1D1B20),
            darkSurfaceVariant = Color(0xFF27252B),
            darkOnSurfaceVariant = Color(0xFFB0AEB9)
        )
        ThemePreset.AMBER -> ThemeTones(
            primary = Color(0xFFB49A70),
            secondary = Color(0xFFC2AA83),
            tertiary = Color(0xFFF3ECDD),
            lightBackground = Color(0xFFFBFAF7),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF4F0E7),
            lightOnSurfaceVariant = Color(0xFF746B5E),
            darkBackground = Color(0xFF161513),
            darkSurface = Color(0xFF1E1C1A),
            darkSurfaceVariant = Color(0xFF282522),
            darkOnSurfaceVariant = Color(0xFFB8B0A4)
        )
        ThemePreset.ROSE -> ThemeTones(
            primary = Color(0xFFB58491),
            secondary = Color(0xFFC49CA5),
            tertiary = Color(0xFFF3E8EB),
            lightBackground = Color(0xFFFBFAFA),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF4EFF1),
            lightOnSurfaceVariant = Color(0xFF756B6F),
            darkBackground = Color(0xFF171416),
            darkSurface = Color(0xFF1F1B1D),
            darkSurfaceVariant = Color(0xFF2A2527),
            darkOnSurfaceVariant = Color(0xFFB8ADB1)
        )
        ThemePreset.SLATE -> ThemeTones(
            primary = Color(0xFF6D808C),
            secondary = Color(0xFF8898A1),
            tertiary = Color(0xFFE8ECEF),
            lightBackground = Color(0xFFFAFAFA),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF0F2F3),
            lightOnSurfaceVariant = Color(0xFF697278),
            darkBackground = Color(0xFF141618),
            darkSurface = Color(0xFF1B1F21),
            darkSurfaceVariant = Color(0xFF252A2D),
            darkOnSurfaceVariant = Color(0xFFA8B0B4)
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
    onSecondary = Color.White,
    onPrimaryContainer = Color(0xFF1C2120),
    onSecondaryContainer = Color(0xFF1C2120),
    onBackground = Color(0xFF1C2120),
    onSurface = Color(0xFF1C2120),
    onSurfaceVariant = tones.lightOnSurfaceVariant,
    outline = tones.lightOnSurfaceVariant.copy(alpha = 0.18f),
    outlineVariant = tones.lightSurfaceVariant
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
    onSecondary = Color.White,
    onPrimaryContainer = Color(0xFFF3F5F4),
    onSecondaryContainer = Color(0xFFF3F5F4),
    onBackground = Color(0xFFF3F5F4),
    onSurface = Color(0xFFF3F5F4),
    onSurfaceVariant = tones.darkOnSurfaceVariant,
    outline = tones.darkOnSurfaceVariant.copy(alpha = 0.22f),
    outlineVariant = tones.darkSurfaceVariant
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

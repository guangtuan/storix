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
    MANJARO(displayName = "Manjaro", swatch = Color(0xFF35BF5C)),
    MINT(displayName = "薄荷", swatch = Color(0xFF2FAE8F)),
    OCEAN(displayName = "海蓝", swatch = Color(0xFF1B89C9)),
    SUNSET(displayName = "落日", swatch = Color(0xFFE56D3D)),
    LAVENDER(displayName = "薰衣草", swatch = Color(0xFF7C73E6)),
    AMBER(displayName = "琥珀", swatch = Color(0xFFCD8B00)),
    ROSE(displayName = "玫瑰", swatch = Color(0xFFC94A75)),
    SLATE(displayName = "石板", swatch = Color(0xFF3B7A90))
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
            primary = Color(0xFF34BE5B),
            secondary = Color(0xFF16A085),
            tertiary = Color(0xFF35BFA4),
            lightBackground = Color(0xFFF1F8F4),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFDCEFE8),
            lightOnSurfaceVariant = Color(0xFF4F6761),
            darkBackground = Color(0xFF2D3136),
            darkSurface = Color(0xFF3F484F),
            darkSurfaceVariant = Color(0xFF353D44),
            darkOnSurfaceVariant = Color(0xFFAFC1CD)
        )
        ThemePreset.MINT -> ThemeTones(
            primary = Color(0xFF2FAE8F),
            secondary = Color(0xFF1E8C72),
            tertiary = Color(0xFFC8EFE6),
            lightBackground = Color(0xFFF0FAF7),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFD9EFE9),
            lightOnSurfaceVariant = Color(0xFF4F6E67),
            darkBackground = Color(0xFF101916),
            darkSurface = Color(0xFF182521),
            darkSurfaceVariant = Color(0xFF253833),
            darkOnSurfaceVariant = Color(0xFFA4C5BD)
        )
        ThemePreset.OCEAN -> ThemeTones(
            primary = Color(0xFF1B89C9),
            secondary = Color(0xFF146DA1),
            tertiary = Color(0xFFCDE8F8),
            lightBackground = Color(0xFFF0F7FC),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFDCEBF5),
            lightOnSurfaceVariant = Color(0xFF4F6677),
            darkBackground = Color(0xFF10161B),
            darkSurface = Color(0xFF18212A),
            darkSurfaceVariant = Color(0xFF243140),
            darkOnSurfaceVariant = Color(0xFFA3B8C9)
        )
        ThemePreset.SUNSET -> ThemeTones(
            primary = Color(0xFFE56D3D),
            secondary = Color(0xFFBE5327),
            tertiary = Color(0xFFFFDFC9),
            lightBackground = Color(0xFFFEF5F0),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF8E3D6),
            lightOnSurfaceVariant = Color(0xFF7A6053),
            darkBackground = Color(0xFF1B1310),
            darkSurface = Color(0xFF2A1D18),
            darkSurfaceVariant = Color(0xFF3B2A22),
            darkOnSurfaceVariant = Color(0xFFD0B0A0)
        )
        ThemePreset.LAVENDER -> ThemeTones(
            primary = Color(0xFF7C73E6),
            secondary = Color(0xFF645CC2),
            tertiary = Color(0xFFE4E1FF),
            lightBackground = Color(0xFFF5F3FE),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFE8E4F8),
            lightOnSurfaceVariant = Color(0xFF655F80),
            darkBackground = Color(0xFF151321),
            darkSurface = Color(0xFF211D33),
            darkSurfaceVariant = Color(0xFF2F2948),
            darkOnSurfaceVariant = Color(0xFFBCB6DE)
        )
        ThemePreset.AMBER -> ThemeTones(
            primary = Color(0xFFCD8B00),
            secondary = Color(0xFFAA7300),
            tertiary = Color(0xFFFFE8B8),
            lightBackground = Color(0xFFFFF8EB),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF7E8CA),
            lightOnSurfaceVariant = Color(0xFF7C6846),
            darkBackground = Color(0xFF1B160D),
            darkSurface = Color(0xFF2A2114),
            darkSurfaceVariant = Color(0xFF3B2F1C),
            darkOnSurfaceVariant = Color(0xFFD1BE98)
        )
        ThemePreset.ROSE -> ThemeTones(
            primary = Color(0xFFC94A75),
            secondary = Color(0xFFA73A60),
            tertiary = Color(0xFFF7D6E2),
            lightBackground = Color(0xFFFFF3F7),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF4DDE6),
            lightOnSurfaceVariant = Color(0xFF7D5D69),
            darkBackground = Color(0xFF1B1116),
            darkSurface = Color(0xFF2A1A21),
            darkSurfaceVariant = Color(0xFF3A2430),
            darkOnSurfaceVariant = Color(0xFFD0AFBC)
        )
        ThemePreset.SLATE -> ThemeTones(
            primary = Color(0xFF3B7A90),
            secondary = Color(0xFF305F71),
            tertiary = Color(0xFFCFE7EE),
            lightBackground = Color(0xFFF2F7F9),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFDDE8EC),
            lightOnSurfaceVariant = Color(0xFF596A72),
            darkBackground = Color(0xFF10161A),
            darkSurface = Color(0xFF182229),
            darkSurfaceVariant = Color(0xFF24323B),
            darkOnSurfaceVariant = Color(0xFFA8BBC4)
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
    outline = tones.lightOnSurfaceVariant.copy(alpha = 0.34f),
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
    outline = tones.darkOnSurfaceVariant.copy(alpha = 0.4f),
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

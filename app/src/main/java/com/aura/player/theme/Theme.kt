package com.aura.player.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AuraDarkColorScheme = darkColorScheme(
    primary = AuraPrimary,
    onPrimary = AuraOnPrimary,
    primaryContainer = AuraPrimaryContainer,
    onPrimaryContainer = AuraOnPrimaryContainer,
    secondary = AuraSecondary,
    onSecondary = AuraOnSecondary,
    secondaryContainer = AuraSecondaryContainer,
    onSecondaryContainer = AuraOnSecondaryContainer,
    tertiary = AuraTertiary,
    onTertiary = AuraOnTertiary,
    tertiaryContainer = AuraTertiaryContainer,
    onTertiaryContainer = AuraOnTertiaryContainer,
    background = AuraBackground,
    onBackground = AuraOnSurface,
    surface = AuraSurface,
    onSurface = AuraOnSurface,
    surfaceVariant = AuraSurfaceVariant,
    onSurfaceVariant = AuraOnSurfaceVariant,
    surfaceContainerLowest = AuraSurfaceContainerLowest,
    surfaceContainerLow = AuraSurfaceContainerLow,
    surfaceContainer = AuraSurfaceContainer,
    surfaceContainerHigh = AuraSurfaceContainerHigh,
    surfaceContainerHighest = AuraSurfaceContainerHighest,
    surfaceBright = AuraSurfaceBright,
    surfaceDim = AuraSurfaceDim,
    outline = AuraOutline,
    outlineVariant = AuraOutlineVariant,
    error = AuraError,
    onError = AuraOnError,
    errorContainer = AuraErrorContainer,
    onErrorContainer = AuraOnErrorContainer
)

@Composable
fun AuraPlayerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AuraDarkColorScheme,
        typography = Typography,
        content = content
    )
}

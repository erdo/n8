package com.kmpfoo.android.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.random.Random
import androidx.core.graphics.toColorInt

/**
 * https://m3.material.io/theme-builder
 */

val md_theme_light_primary = Color(0xFF5754A8)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFE2DFFF)
val md_theme_light_onPrimaryContainer = Color(0xFF110563)
val md_theme_light_secondary = Color(0xFF4F56A9)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFE0E0FF)
val md_theme_light_onSecondaryContainer = Color(0xFF030865)
val md_theme_light_tertiary = Color(0xFF006590)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFC8E6FF)
val md_theme_light_onTertiaryContainer = Color(0xFF001E2F)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF1C1B1F)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF1C1B1F)
val md_theme_light_surfaceVariant = Color(0xFFE4E1EC)
val md_theme_light_onSurfaceVariant = Color(0xFF47464F)
val md_theme_light_outline = Color(0xFF787680)
val md_theme_light_inverseOnSurface = Color(0xFFF3EFF4)
val md_theme_light_inverseSurface = Color(0xFF313034)
val md_theme_light_inversePrimary = Color(0xFFC3C0FF)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF5754A8)
val md_theme_light_outlineVariant = Color(0xFFC8C5D0)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFC3C0FF)
val md_theme_dark_onPrimary = Color(0xFF282377)
val md_theme_dark_primaryContainer = Color(0xFF3F3C8F)
val md_theme_dark_onPrimaryContainer = Color(0xFFE2DFFF)
val md_theme_dark_secondary = Color(0xFFBEC2FF)
val md_theme_dark_onSecondary = Color(0xFF1E2578)
val md_theme_dark_secondaryContainer = Color(0xFF373E90)
val md_theme_dark_onSecondaryContainer = Color(0xFFE0E0FF)
val md_theme_dark_tertiary = Color(0xFF88CEFF)
val md_theme_dark_onTertiary = Color(0xFF00344D)
val md_theme_dark_tertiaryContainer = Color(0xFF004C6E)
val md_theme_dark_onTertiaryContainer = Color(0xFFC8E6FF)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF1C1B1F)
val md_theme_dark_onBackground = Color(0xFFE5E1E6)
val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_onSurface = Color(0xFFE5E1E6)
val md_theme_dark_surfaceVariant = Color(0xFF47464F)
val md_theme_dark_onSurfaceVariant = Color(0xFFC8C5D0)
val md_theme_dark_outline = Color(0xFF928F9A)
val md_theme_dark_inverseOnSurface = Color(0xFF1C1B1F)
val md_theme_dark_inverseSurface = Color(0xFFE5E1E6)
val md_theme_dark_inversePrimary = Color(0xFF5754A8)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFC3C0FF)
val md_theme_dark_outlineVariant = Color(0xFF47464F)
val md_theme_dark_scrim = Color(0xFF000000)

val seed = Color(0xFF7C7ABA)



fun randomColorHex(): String {
    val r = Random.nextInt(0, 256)
    val g = Random.nextInt(0, 256)
    val b = Random.nextInt(0, 256)
    return String.format("#%02X%02X%02X", r, g, b)
}

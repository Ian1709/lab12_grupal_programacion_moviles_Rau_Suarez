package com.tecsup.pokemon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FirePalette = darkColorScheme(
    primary = Color(0xFFFF7043),
    onPrimary = Color(0xFF28120D),
    secondary = Color(0xFFFFC857),
    background = Color(0xFF111113),
    surface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFF29292E),
    onSurface = Color(0xFFF5F3F1),
    onSurfaceVariant = Color(0xFFBDB8B4)
)

private val GrassPalette = darkColorScheme(
    primary = Color(0xFF66BB6A), onPrimary = Color(0xFF102713), secondary = Color(0xFFA5D6A7),
    background = Color(0xFF101411), surface = Color(0xFF191E1A), surfaceVariant = Color(0xFF252D26),
    onSurface = Color(0xFFF1F5F1), onSurfaceVariant = Color(0xFFB8C2B9)
)

private val WaterPalette = darkColorScheme(
    primary = Color(0xFF42A5F5), onPrimary = Color(0xFF081A2A), secondary = Color(0xFF80DEEA),
    background = Color(0xFF101419), surface = Color(0xFF191E24), surfaceVariant = Color(0xFF252C34),
    onSurface = Color(0xFFF1F4F8), onSurfaceVariant = Color(0xFFB8C2CC)
)

@Composable
fun PokemonTheme(type: String = "fire", content: @Composable () -> Unit) {
    val palette = when (type) { "grass" -> GrassPalette; "water" -> WaterPalette; else -> FirePalette }
    MaterialTheme(colorScheme = palette, content = content)
}

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

@Composable
fun PokemonTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = FirePalette, content = content)
}

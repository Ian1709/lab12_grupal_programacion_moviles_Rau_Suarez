package com.tecsup.pokemon.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.ui.components.PokemonArtwork
import com.tecsup.pokemon.domain.model.Pokemon
import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.ui.components.ErrorPanel
import com.tecsup.pokemon.ui.components.LoadingPanel

@Composable
fun PokemonDetailScreen(
    state: UiState<Pokemon>,
    onBack: () -> Unit,
    onToggleFavorite: (Pokemon) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = onBack) { Text("‹  Volver al catálogo") }
        when (state) {
            UiState.Loading -> LoadingPanel(Modifier.weight(1f), "Cargando detalle...")
            is UiState.Error -> ErrorPanel(state.message, onRetry, Modifier.weight(1f))
            is UiState.Success -> {
                val pokemon = state.data
                Column(
                    Modifier.fillMaxSize().padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PokemonArtwork(pokemon, Modifier.size(190.dp))
                    Spacer(Modifier.height(18.dp))
                    Text("#${pokemon.id.toString().padStart(3, '0')}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(pokemon.name.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
                    Text(pokemon.types.joinToString("  ·  ") { it.replaceFirstChar { c -> c.uppercase() } }, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(28.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("ALTURA", pokemon.height?.let { "${it / 10.0} m" } ?: "—", Modifier.weight(1f))
                        StatCard("PESO", pokemon.weight?.let { "${it / 10.0} kg" } ?: "—", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { onToggleFavorite(pokemon) }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (pokemon.isFavorite) "Quitar de favoritos" else "Añadir a favoritos")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Card(modifier) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}



package com.tecsup.pokemon.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.domain.model.Pokemon
import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.ui.components.EmptyPanel
import com.tecsup.pokemon.ui.components.ErrorPanel
import com.tecsup.pokemon.ui.components.LoadingPanel
import com.tecsup.pokemon.ui.components.PokemonArtwork

@Composable
fun PokemonListScreen(
    state: UiState<List<Pokemon>>,
    onSelectPokemon: (Pokemon) -> Unit,
    onToggleFavorite: (Pokemon) -> Unit,
    onOpenFavorites: () -> Unit,
    onRetry: () -> Unit,
    isOffline: Boolean = false,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    Scaffold(modifier = modifier, topBar = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("POKÉDEX", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("Tipo fuego", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Row {
                TextButton(onClick = onOpenFavorites) { Text("♥ Favoritos") }
                TextButton(onClick = onLogout) { Text("Salir") }
            }
        }
    }) { insets ->
        Column(Modifier.fillMaxSize().padding(insets).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar Pokémon") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            Spacer(Modifier.height(12.dp))
            if (isOffline) {
                Text(
                    "Sin conexión: mostrando los datos guardados en este dispositivo.",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            when (state) {
                UiState.Loading -> LoadingPanel(Modifier.weight(1f))
                is UiState.Error -> ErrorPanel(state.message, onRetry, Modifier.weight(1f))
                is UiState.Success -> {
                    val filtered = state.data.filter { it.name.contains(query.trim(), ignoreCase = true) }
                    if (filtered.isEmpty()) {
                        EmptyPanel(if (query.isBlank()) "No hay Pokémon guardados todavía." else "No encontramos coincidencias.", Modifier.weight(1f))
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)) {
                            items(filtered, key = { it.id }) { pokemon ->
                                PokemonCard(pokemon, onSelectPokemon, onToggleFavorite)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonCard(
    pokemon: Pokemon,
    onSelect: (Pokemon) -> Unit,
    onToggleFavorite: (Pokemon) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelect(pokemon) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            PokemonArtwork(pokemon, Modifier.size(72.dp))
            Column(Modifier.weight(1f)) {
                Text("#${pokemon.id.toString().padStart(3, '0')}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(pokemon.name.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(pokemon.types.joinToString(" · ") { it.replaceFirstChar { c -> c.uppercase() } }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = { onToggleFavorite(pokemon) }) {
                Text(if (pokemon.isFavorite) "♥" else "♡", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}





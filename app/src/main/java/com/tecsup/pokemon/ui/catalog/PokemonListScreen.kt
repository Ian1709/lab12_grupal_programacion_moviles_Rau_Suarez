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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    isAdmin: Boolean = false,
    onOpenAdmin: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    selectedType: String = "fire",
    onSelectType: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    Scaffold(modifier = modifier, topBar = {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("POKÉDEX", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Tipo ${typeLabel(selectedType).lowercase()}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onOpenProfile) { Text("◉  Perfil") }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("fire", "grass", "water").forEach { type ->
                    FilterChip(selected = selectedType == type, onClick = { onSelectType(type) }, label = { Text("${typeEmoji(type)} ${typeLabel(type)}") })
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onOpenFavorites) { Text("♥ Favoritos") }
                if (isAdmin) TextButton(onClick = onOpenAdmin) { Text("Panel admin") }
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
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                trailingIcon = {
                    if (query.isNotEmpty()) TextButton(onClick = { query = "" }) { Text("Limpiar") }
                }
            )
            Spacer(Modifier.height(12.dp))
            CollectionHero(
                count = (state as? UiState.Success)?.data?.size,
                type = selectedType,
                modifier = Modifier.fillMaxWidth()
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
                                PokemonCard(pokemon, onSelectPokemon, onToggleFavorite, selectedType)
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
    onToggleFavorite: (Pokemon) -> Unit,
    type: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelect(pokemon) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                Modifier.size(78.dp).clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(cardGradient(type))),
                contentAlignment = Alignment.Center
            ) { PokemonArtwork(pokemon, Modifier.size(70.dp)) }
            Column(Modifier.weight(1f)) {
                val numberLabel = pokemon.dexNumber.takeIf { it > 0 }?.let { "N.º ${it.toString().padStart(3, '0')}" } ?: "N.º —"
                val generationLabel = pokemon.generation.takeIf { it > 0 }?.let { "  ·  GENERACIÓN $it" }.orEmpty()
                Text(numberLabel + generationLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(pokemon.name.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(pokemon.types.joinToString(" · ") { it.replaceFirstChar { c -> c.uppercase() } }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            TextButton(
                onClick = { onToggleFavorite(pokemon) },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (pokemon.isFavorite) "♥" else "♡", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
private fun CollectionHero(count: Int?, type: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            Modifier.fillMaxWidth()
                .background(Brush.linearGradient(heroGradient(type)))
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("COLECCIÓN ${typeLabel(type).uppercase()}", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = .76f), fontWeight = FontWeight.Bold)
                Text("Tu próxima aventura", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Black)
                Text(
                    count?.let { "$it Pokémon listos para descubrir" } ?: "Descubre criaturas de tipo ${typeLabel(type).lowercase()} de todas las regiones",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = .86f)
                )
            }
            Text("◉", style = MaterialTheme.typography.displayMedium, color = Color.White.copy(alpha = .88f), fontWeight = FontWeight.Black)
        }
    }
}

private fun typeLabel(type: String): String = when (type) { "grass" -> "Planta"; "water" -> "Agua"; else -> "Fuego" }
private fun typeEmoji(type: String): String = when (type) { "grass" -> "🌿"; "water" -> "💧"; else -> "🔥" }
private fun heroGradient(type: String): List<Color> = when (type) {
    "grass" -> listOf(Color(0xFF43A047), Color(0xFF287849), Color(0xFF174A3A))
    "water" -> listOf(Color(0xFF2684D9), Color(0xFF2467A8), Color(0xFF183C68))
    else -> listOf(Color(0xFFE95A3C), Color(0xFF8E302E), Color(0xFF48212E))
}
private fun cardGradient(type: String): List<Color> = when (type) {
    "grass" -> listOf(Color(0xFF244A31), Color(0xFF292D2A))
    "water" -> listOf(Color(0xFF203D59), Color(0xFF292D32))
    else -> listOf(Color(0xFF542720), Color(0xFF29252A))
}





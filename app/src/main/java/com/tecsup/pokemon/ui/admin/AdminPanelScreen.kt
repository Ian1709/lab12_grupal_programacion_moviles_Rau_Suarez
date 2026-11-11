package com.tecsup.pokemon.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.domain.model.PokemonStats
import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.ui.components.LoadingPanel

@Composable
fun AdminPanelScreen(
    stats: UiState<PokemonStats>,
    isSyncing: Boolean,
    message: String?,
    onBack: () -> Unit,
    onSynchronize: () -> Unit,
    onClearCache: () -> Unit,
    selectedType: String = "fire",
    modifier: Modifier = Modifier
) {
    var confirmClear by remember { mutableStateOf(false) }
    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("¿Limpiar los datos locales?") },
            text = { Text("Se borrarán el catálogo y los favoritos guardados en este dispositivo. Luego podrás sincronizarlos otra vez.") },
            confirmButton = {
                TextButton(onClick = { confirmClear = false; onClearCache() }) { Text("Limpiar todo") }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancelar") } }
        )
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) { Text("‹ Volver al catálogo") }
        Text("CONTROL DEL CATÁLOGO", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text("Panel administrador", style = MaterialTheme.typography.headlineMedium)
        Text("Mantén actualizada la colección de tipo ${selectedTypeLabel(selectedType)}.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        when (stats) {
            UiState.Loading -> LoadingPanel(Modifier.fillMaxWidth().height(120.dp), "Cargando estadísticas...")
            is UiState.Error -> Text(stats.message, color = MaterialTheme.colorScheme.error)
            is UiState.Success -> {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Pokémon", stats.data.total, Modifier.weight(1f))
                    MetricCard("Favoritos", stats.data.favorites, Modifier.weight(1f))
                }
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Sincronización", style = MaterialTheme.typography.titleMedium)
                Text("Descarga los datos de PokeAPI y los guarda en Room para consultarlos sin conexión.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (message != null) Text(message, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Button(onClick = onSynchronize, enabled = !isSyncing, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text(if (isSyncing) "Sincronizando…" else "Sincronizar tipo ${selectedTypeLabel(selectedType)}")
        }
        OutlinedButton(onClick = { confirmClear = true }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Limpiar todos los datos locales")
        }
        Spacer(Modifier.height(8.dp))
    }
}

private fun selectedTypeLabel(type: String): String = when (type) { "grass" -> "planta"; "water" -> "agua"; else -> "fuego" }

@Composable
private fun MetricCard(label: String, value: Int, modifier: Modifier = Modifier) {
    Card(
        modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value.toString(), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

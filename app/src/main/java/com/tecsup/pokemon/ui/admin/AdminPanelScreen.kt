package com.tecsup.pokemon.ui.admin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onClick = onBack) { Text("‹ Volver al catálogo") }
        Text("Panel de administración", style = MaterialTheme.typography.headlineSmall)
        when (stats) {
            UiState.Loading -> LoadingPanel(Modifier.weight(1f), "Cargando estadísticas...")
            is UiState.Error -> Text(stats.message, color = MaterialTheme.colorScheme.error)
            is UiState.Success -> {
                StatRow("Pokémon guardados", stats.data.total)
                StatRow("Favoritos", stats.data.favorites)
            }
        }
        if (message != null) Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = onSynchronize, enabled = !isSyncing, modifier = Modifier.fillMaxWidth()) {
            Text(if (isSyncing) "Sincronizando..." else "Sincronizar catálogo")
        }
        OutlinedButton(onClick = onClearCache, modifier = Modifier.fillMaxWidth()) {
            Text("Limpiar caché (conservar favoritos)")
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun StatRow(label: String, value: Int) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value.toString(), style = MaterialTheme.typography.headlineMedium)
        }
    }
}

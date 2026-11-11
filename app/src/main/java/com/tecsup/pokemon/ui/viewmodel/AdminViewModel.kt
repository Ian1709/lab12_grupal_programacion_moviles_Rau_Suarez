package com.tecsup.pokemon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.domain.model.PokemonStats
import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.domain.repository.PokemonRepository
import com.tecsup.pokemon.domain.repository.SyncNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: PokemonRepository,
    private val notifier: SyncNotifier
) : ViewModel() {
    private val _stats = MutableStateFlow<UiState<PokemonStats>>(UiState.Loading)
    val stats: StateFlow<UiState<PokemonStats>> = _stats.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeStats()
                .catch { _stats.value = UiState.Error(it.message ?: "No se pudieron leer las estadísticas.") }
                .collect { _stats.value = UiState.Success(it) }
        }
    }

    fun synchronize(type: String = "fire") {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            _message.value = null
            runCatching { repository.synchronize(type) }
                .onSuccess { count ->
                    notifier.notifySyncComplete(count)
                    _message.value = "Sincronización completada: $count Pokémon guardados."
                }
                .onFailure { _message.value = it.message ?: "No se pudo sincronizar el catálogo." }
            _isSyncing.value = false
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            runCatching { repository.clearCache() }
                .onSuccess { _message.value = "Caché y favoritos locales eliminados." }
                .onFailure { _message.value = it.message ?: "No se pudo limpiar la caché." }
        }
    }

    companion object {
        fun factory(repository: PokemonRepository, notifier: SyncNotifier) =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminViewModel(repository, notifier) as T
            }
    }
}

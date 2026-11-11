package com.tecsup.pokemon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.domain.model.Pokemon
import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.domain.repository.PokemonRepository
import com.tecsup.pokemon.domain.repository.NetworkMonitor
import com.tecsup.pokemon.domain.repository.SyncNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PokemonListViewModel(
    private val repository: PokemonRepository,
    private val networkMonitor: NetworkMonitor,
    private val notifier: SyncNotifier
) : ViewModel() {
    private val _items = MutableStateFlow<UiState<List<Pokemon>>>(UiState.Loading)
    val items: StateFlow<UiState<List<Pokemon>>> = _items.asStateFlow()

    private val _favorites = MutableStateFlow<UiState<List<Pokemon>>>(UiState.Loading)
    val favorites: StateFlow<UiState<List<Pokemon>>> = _favorites.asStateFlow()

    private val _isOffline = MutableStateFlow(!networkMonitor.isOnline.value)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()
    private var hasCompletedFirstSync = false

    init {
        viewModelScope.launch {
            repository.observePokemon().catch { error ->
                _items.value = UiState.Error(error.message ?: "No se pudo leer el catálogo local.")
            }.collect { rows ->
                _items.value = when {
                    rows.isNotEmpty() -> UiState.Success(rows)
                    !networkMonitor.isOnline.value -> UiState.Error("Sin conexión y no hay datos guardados.")
                    hasCompletedFirstSync -> UiState.Success(emptyList())
                    else -> UiState.Loading
                }
            }
        }
        viewModelScope.launch {
            repository.observeFavorites().catch { error ->
                _favorites.value = UiState.Error(error.message ?: "No se pudieron leer los favoritos.")
            }.collect { _favorites.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _isOffline.value = !online
                if (online) synchronize() else showOfflineIfEmpty()
            }
        }
    }

    fun toggleFavorite(pokemon: Pokemon) {
        viewModelScope.launch {
            repository.setFavorite(pokemon.id, !pokemon.isFavorite)
        }
    }

    fun synchronize(onComplete: (() -> Unit)? = null, onError: ((Throwable) -> Unit)? = null) {
        if (!networkMonitor.isOnline.value) {
            showOfflineIfEmpty()
            onError?.invoke(IllegalStateException("Sin conexión a internet."))
            return
        }
        viewModelScope.launch {
            runCatching { repository.synchronize() }
                .onSuccess { count ->
                    hasCompletedFirstSync = true
                    notifier.notifySyncComplete(count)
                    if (_items.value is UiState.Loading && count == 0) _items.value = UiState.Success(emptyList())
                    onComplete?.invoke()
                }
                .onFailure {
                    hasCompletedFirstSync = true
                    showOfflineIfEmpty(it.message ?: "No se pudo sincronizar.")
                    onError?.invoke(it)
                }
        }
    }

    private fun showOfflineIfEmpty(message: String = "Sin conexión y no hay datos guardados.") {
        val current = (_items.value as? UiState.Success)?.data
        if (current.isNullOrEmpty()) _items.value = UiState.Error(message)
    }

    fun clearCache() {
        viewModelScope.launch { repository.clearCache() }
    }

    companion object {
        fun factory(repository: PokemonRepository, networkMonitor: NetworkMonitor, notifier: SyncNotifier) = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = PokemonListViewModel(repository, networkMonitor, notifier) as T
        }
    }
}

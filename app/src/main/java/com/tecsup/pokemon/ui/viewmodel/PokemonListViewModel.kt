package com.tecsup.pokemon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.domain.model.Pokemon
import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PokemonListViewModel(private val repository: PokemonRepository) : ViewModel() {
    private val _items = MutableStateFlow<UiState<List<Pokemon>>>(UiState.Loading)
    val items: StateFlow<UiState<List<Pokemon>>> = _items.asStateFlow()

    private val _favorites = MutableStateFlow<UiState<List<Pokemon>>>(UiState.Loading)
    val favorites: StateFlow<UiState<List<Pokemon>>> = _favorites.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observePokemon().catch { error ->
                _items.value = UiState.Error(error.message ?: "No se pudo leer el catálogo local.")
            }.collect { _items.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repository.observeFavorites().catch { error ->
                _favorites.value = UiState.Error(error.message ?: "No se pudieron leer los favoritos.")
            }.collect { _favorites.value = UiState.Success(it) }
        }
    }

    fun toggleFavorite(pokemon: Pokemon) {
        viewModelScope.launch {
            repository.setFavorite(pokemon.id, !pokemon.isFavorite)
        }
    }

    fun synchronize(onComplete: (() -> Unit)? = null, onError: ((Throwable) -> Unit)? = null) {
        viewModelScope.launch {
            runCatching { repository.synchronize() }
                .onSuccess { onComplete?.invoke() }
                .onFailure { onError?.invoke(it) }
        }
    }

    fun clearCache() {
        viewModelScope.launch { repository.clearCache() }
    }

    companion object {
        fun factory(repository: PokemonRepository) = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = PokemonListViewModel(repository) as T
        }
    }
}

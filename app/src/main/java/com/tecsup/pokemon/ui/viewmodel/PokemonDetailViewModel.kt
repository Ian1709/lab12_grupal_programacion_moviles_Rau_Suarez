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

class PokemonDetailViewModel(
    private val repository: PokemonRepository,
    private val pokemonId: Int,
    private val pokemonName: String
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<Pokemon>>(UiState.Loading)
    val state: StateFlow<UiState<Pokemon>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observePokemon(pokemonId).catch { error ->
                _state.value = UiState.Error(error.message ?: "No se pudo leer el Pokémon guardado.")
            }.collect { pokemon ->
                if (pokemon == null) _state.value = UiState.Loading
                else _state.value = UiState.Success(pokemon)
            }
        }
        load(pokemonName)
    }

    fun retry() {
        viewModelScope.launch {
            val current = (_state.value as? UiState.Success)?.data
            runCatching { repository.loadDetails(pokemonName) }
                .onFailure { if (current != null) _state.value = UiState.Success(current) else _state.value = UiState.Error(it.message ?: "No se pudo cargar el detalle.") }
        }
    }

    fun load(name: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching { repository.loadDetails(name) }
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure {
                    if (_state.value !is UiState.Success) {
                        _state.value = UiState.Error(it.message ?: "No se pudo cargar el detalle.")
                    }
                }
        }
    }

    companion object {
        fun factory(repository: PokemonRepository, pokemonId: Int, pokemonName: String) =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PokemonDetailViewModel(repository, pokemonId, pokemonName) as T
            }
    }
}

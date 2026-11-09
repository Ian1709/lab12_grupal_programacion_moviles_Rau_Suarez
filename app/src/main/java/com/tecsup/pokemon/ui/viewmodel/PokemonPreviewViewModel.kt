package com.tecsup.pokemon.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tecsup.pokemon.domain.model.Pokemon
import com.tecsup.pokemon.domain.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Datos de muestra para conectar y revisar las pantallas antes de integrar los repositorios. */
class PokemonPreviewViewModel : ViewModel() {
    private val _items = MutableStateFlow<UiState<List<Pokemon>>>(UiState.Success(previewPokemon))
    val items: StateFlow<UiState<List<Pokemon>>> = _items.asStateFlow()

    private val _selected = MutableStateFlow<UiState<Pokemon>>(UiState.Loading)
    val selected: StateFlow<UiState<Pokemon>> = _selected.asStateFlow()

    private val _favorites = MutableStateFlow<List<Pokemon>>(emptyList())
    val favorites: StateFlow<List<Pokemon>> = _favorites.asStateFlow()

    fun select(pokemon: Pokemon) { _selected.value = UiState.Success(pokemon) }

    fun toggleFavorite(pokemon: Pokemon) {
        val current = _items.value.let { (it as? UiState.Success)?.data.orEmpty() }
        val isSaved = _favorites.value.any { it.id == pokemon.id }
        _favorites.value = if (isSaved) _favorites.value.filterNot { it.id == pokemon.id }
            else _favorites.value + pokemon.copy(isFavorite = true)
        _items.value = UiState.Success(current.map {
            if (it.id == pokemon.id) it.copy(isFavorite = !isSaved) else it
        })
        val selected = (_selected.value as? UiState.Success)?.data
        if (selected?.id == pokemon.id) _selected.value = UiState.Success(selected.copy(isFavorite = !isSaved))
    }

    private companion object {
        val previewPokemon = listOf(
            Pokemon(4, "charmander"), Pokemon(5, "charmeleon"), Pokemon(6, "charizard", types = listOf("fire", "flying")),
            Pokemon(37, "vulpix"), Pokemon(38, "ninetales"), Pokemon(58, "growlithe"), Pokemon(59, "arcanine"),
            Pokemon(77, "ponyta"), Pokemon(78, "rapidash"), Pokemon(126, "magmar"), Pokemon(136, "flareon"),
            Pokemon(146, "moltres", types = listOf("fire", "flying")), Pokemon(155, "cyndaquil"), Pokemon(256, "combusken")
        )
    }
}

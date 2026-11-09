package com.tecsup.pokemon.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String? = null,
    val types: List<String> = listOf("fire"),
    val height: Int? = null,
    val weight: Int? = null,
    val isFavorite: Boolean = false
)

data class PokemonStats(
    val total: Int,
    val favorites: Int
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

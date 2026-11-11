package com.tecsup.pokemon.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String? = null,
    val types: List<String> = listOf("fire"),
    val height: Int? = null,
    val weight: Int? = null,
    val isFavorite: Boolean = false,
    val generation: Int = 1
)

data class PokemonDebut(
    val game: String,
    val animeSeason: String
)

fun Pokemon.debut(): PokemonDebut = when (generation) {
    1 -> PokemonDebut("Pokémon Red & Green · 1996", "Liga Añil · Generación I")
    2 -> PokemonDebut("Pokémon Gold & Silver · 1999", "Los viajes Johto · Generación II")
    3 -> PokemonDebut("Pokémon Ruby & Sapphire · 2002", "Pokémon Advanced · Generación III")
    4 -> PokemonDebut("Pokémon Diamond & Pearl · 2006", "Diamond & Pearl · Generación IV")
    5 -> PokemonDebut("Pokémon Black & White · 2010", "Pokémon Black & White · Generación V")
    6 -> PokemonDebut("Pokémon X & Y · 2013", "Pokémon the Series: XY · Generación VI")
    7 -> PokemonDebut("Pokémon Sun & Moon · 2016", "Pokémon the Series: Sun & Moon · Generación VII")
    8 -> PokemonDebut("Pokémon Sword & Shield · 2019", "Pokémon Journeys · Generación VIII")
    9 -> PokemonDebut("Pokémon Scarlet & Violet · 2022", "Pokémon Horizons · Generación IX")
    else -> PokemonDebut("Información de debut no disponible", "Información de temporada no disponible")
}

data class PokemonStats(
    val total: Int,
    val favorites: Int
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

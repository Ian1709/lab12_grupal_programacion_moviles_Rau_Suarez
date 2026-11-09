package com.tecsup.pokemon.domain.repository

import com.tecsup.pokemon.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun observePokemon(): Flow<List<Pokemon>>
    fun observeFavorites(): Flow<List<Pokemon>>
    fun observePokemon(id: Int): Flow<Pokemon?>
    suspend fun synchronize(type: String = "fire")
    suspend fun loadDetails(name: String): Pokemon
    suspend fun setFavorite(id: Int, favorite: Boolean)
    suspend fun clearCache()
}

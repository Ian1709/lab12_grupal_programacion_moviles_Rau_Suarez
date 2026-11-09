package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.data.local.PokemonDao
import com.tecsup.pokemon.data.mapper.toDomain
import com.tecsup.pokemon.data.mapper.toEntity
import com.tecsup.pokemon.data.remote.PokemonApiService
import com.tecsup.pokemon.domain.model.Pokemon
import com.tecsup.pokemon.domain.model.PokemonStats
import com.tecsup.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine

class PokemonRepositoryImpl(
    private val dao: PokemonDao,
    private val api: PokemonApiService
) : PokemonRepository {
    override fun observePokemon(): Flow<List<Pokemon>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<Pokemon>> = dao.observeFavorites().map { rows -> rows.map { it.toDomain() } }

    override fun observePokemon(id: Int): Flow<Pokemon?> = dao.observeById(id).map { it?.toDomain() }

    override fun observeStats(): Flow<PokemonStats> = combine(
        dao.observeCount(), dao.observeFavoriteCount()
    ) { total, favorites -> PokemonStats(total, favorites) }

    override suspend fun synchronize(type: String) {
        val response = api.getPokemonByType(type)
        val catalog = response.pokemon.map { it.pokemon.toEntity() }
        dao.replaceCatalog(catalog)
    }

    override suspend fun loadDetails(name: String): Pokemon {
        val details = api.getPokemonDetail(name.trim().lowercase())
        val currentFavorite = dao.getById(details.id)?.isFavorite ?: false
        val entity = details.toEntity(currentFavorite)
        dao.upsert(entity)
        return entity.toDomain()
    }

    override suspend fun setFavorite(id: Int, favorite: Boolean) {
        dao.setFavorite(id, favorite)
    }

    override suspend fun clearCache() {
        dao.clearNonFavoriteCache()
    }
}

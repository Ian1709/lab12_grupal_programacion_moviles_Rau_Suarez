package com.tecsup.pokemon.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon ORDER BY id")
    fun observeAll(): Flow<List<PokemonEntity>>

    @Query("SELECT pokemon.* FROM pokemon INNER JOIN pokemon_type_catalog ON pokemon.id = pokemon_type_catalog.pokemonId WHERE pokemon_type_catalog.type = :type ORDER BY pokemon.dexNumber, pokemon.name")
    fun observeCatalog(type: String): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE isFavorite = 1 ORDER BY name")
    fun observeFavorites(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<PokemonEntity?>

    @Query("SELECT * FROM pokemon WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PokemonEntity?

    @Query("SELECT COUNT(*) FROM pokemon")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM pokemon WHERE isFavorite = 1")
    fun observeFavoriteCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PokemonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCatalogMemberships(memberships: List<PokemonTypeCatalogEntity>)

    @Query("DELETE FROM pokemon_type_catalog WHERE type = :type")
    suspend fun deleteCatalogMemberships(type: String)

    @Query("DELETE FROM pokemon WHERE isFavorite = 0 AND id NOT IN (SELECT pokemonId FROM pokemon_type_catalog)")
    suspend fun deleteUnreferencedNonFavorites()

    @Query("DELETE FROM pokemon WHERE isFavorite = 0 AND id NOT IN (:ids)")
    suspend fun deleteUnlistedNonFavorites(ids: List<Int>)

    @Transaction
    suspend fun replaceCatalog(entities: List<PokemonEntity>) {
        replaceCatalog("fire", entities)
    }

    @Transaction
    suspend fun replaceCatalog(type: String, entities: List<PokemonEntity>) {
        val ids = entities.map(PokemonEntity::id)
        val favoriteIds = entities.mapNotNull { entity ->
            if (getById(entity.id)?.isFavorite == true) entity.id else null
        }.toSet()
        val merged = entities.map { entity ->
            entity.copy(isFavorite = entity.id in favoriteIds)
        }
        deleteCatalogMemberships(type)
        upsertAll(merged)
        insertCatalogMemberships(ids.map { PokemonTypeCatalogEntity(type, it) })
        deleteUnreferencedNonFavorites()
    }

    @Query("UPDATE pokemon SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Int, favorite: Boolean)

    @Query("DELETE FROM pokemon")
    suspend fun clearAll()
}

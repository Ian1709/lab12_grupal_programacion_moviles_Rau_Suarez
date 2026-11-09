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

    @Query("DELETE FROM pokemon WHERE isFavorite = 0 AND id NOT IN (:ids)")
    suspend fun deleteUnlistedNonFavorites(ids: List<Int>)

    @Transaction
    suspend fun replaceCatalog(entities: List<PokemonEntity>) {
        if (entities.isEmpty()) return
        val favoriteIds = entities.mapNotNull { entity ->
            if (getById(entity.id)?.isFavorite == true) entity.id else null
        }.toSet()
        val merged = entities.map { entity ->
            entity.copy(isFavorite = entity.id in favoriteIds)
        }
        deleteUnlistedNonFavorites(merged.map(PokemonEntity::id))
        upsertAll(merged)
    }

    @Query("UPDATE pokemon SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Int, favorite: Boolean)

    @Query("DELETE FROM pokemon WHERE isFavorite = 0")
    suspend fun clearNonFavoriteCache()
}

package com.tecsup.pokemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val types: List<String>,
    val height: Int?,
    val weight: Int?,
    val isFavorite: Boolean = false
)

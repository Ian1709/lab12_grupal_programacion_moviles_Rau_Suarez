package com.tecsup.pokemon.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "pokemon_type_catalog",
    primaryKeys = ["type", "pokemonId"],
    foreignKeys = [ForeignKey(
        entity = PokemonEntity::class,
        parentColumns = ["id"],
        childColumns = ["pokemonId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("pokemonId")]
)
data class PokemonTypeCatalogEntity(val type: String, val pokemonId: Int)

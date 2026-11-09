package com.tecsup.pokemon.data.local

import androidx.room.TypeConverter

class PokemonConverters {
    @TypeConverter
    fun fromTypes(types: List<String>): String = types.joinToString(",")

    @TypeConverter
    fun toTypes(types: String): List<String> = types.split(',').filter(String::isNotBlank)
}

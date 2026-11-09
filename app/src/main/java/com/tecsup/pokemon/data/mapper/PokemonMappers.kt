package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.local.PokemonEntity
import com.tecsup.pokemon.data.remote.PokemonDetailDto
import com.tecsup.pokemon.data.remote.PokemonReferenceDto
import com.tecsup.pokemon.domain.model.Pokemon

private const val OFFICIAL_ART_BASE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"

fun PokemonEntity.toDomain(): Pokemon = Pokemon(
    id = id,
    name = name,
    imageUrl = imageUrl ?: "$OFFICIAL_ART_BASE$id.png",
    types = types,
    height = height,
    weight = weight,
    isFavorite = isFavorite
)

fun Pokemon.toEntity(): PokemonEntity = PokemonEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    types = types,
    height = height,
    weight = weight,
    isFavorite = isFavorite
)

fun PokemonReferenceDto.toEntity(): PokemonEntity {
    val pokemonId = url.trimEnd('/').substringAfterLast('/').toInt()
    return PokemonEntity(
        id = pokemonId,
        name = name,
        imageUrl = "$OFFICIAL_ART_BASE$pokemonId.png",
        types = listOf("fire"),
        height = null,
        weight = null
    )
}

fun PokemonDetailDto.toEntity(isFavorite: Boolean): PokemonEntity = PokemonEntity(
    id = id,
    name = name,
    imageUrl = sprites?.other?.officialArtwork?.frontDefault ?: "$OFFICIAL_ART_BASE$id.png",
    types = types.map { it.type.name },
    height = height,
    weight = weight,
    isFavorite = isFavorite
)

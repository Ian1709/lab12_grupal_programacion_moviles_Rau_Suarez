package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.local.PokemonEntity
import com.tecsup.pokemon.data.remote.PokemonDetailDto
import com.tecsup.pokemon.data.remote.PokemonReferenceDto
import com.tecsup.pokemon.data.remote.PokemonSpeciesReferenceDto
import com.tecsup.pokemon.domain.model.Pokemon

private const val OFFICIAL_ART_BASE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"

fun PokemonEntity.toDomain(): Pokemon = Pokemon(
    id = id,
    name = name,
    imageUrl = imageUrl ?: "$OFFICIAL_ART_BASE$id.png",
    types = types,
    height = height,
    weight = weight,
    generation = generation,
    dexNumber = dexNumber,
    isFavorite = isFavorite
)

fun Pokemon.toEntity(): PokemonEntity = PokemonEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    types = types,
    height = height,
    weight = weight,
    generation = generation,
    dexNumber = dexNumber,
    isFavorite = isFavorite
)

fun PokemonReferenceDto.toEntity(type: String = "fire"): PokemonEntity {
    val pokemonId = url.trimEnd('/').substringAfterLast('/').toInt()
    return PokemonEntity(
        id = pokemonId,
        name = name,
        imageUrl = "$OFFICIAL_ART_BASE$pokemonId.png",
        types = listOf(type),
        height = null,
        weight = null,
        generation = generationFromId(pokemonId),
        dexNumber = pokemonId
    )
}

fun PokemonDetailDto.toEntity(isFavorite: Boolean, generation: Int): PokemonEntity = PokemonEntity(
    id = id,
    name = name,
    imageUrl = sprites?.other?.officialArtwork?.frontDefault ?: "$OFFICIAL_ART_BASE$id.png",
    types = types.map { it.type.name },
    height = height,
    weight = weight,
    generation = generation,
    isFavorite = isFavorite,
    dexNumber = species.speciesId()
)

fun PokemonSpeciesReferenceDto.speciesId(): Int =
    url.trimEnd('/').substringAfterLast('/').toInt()

fun generationFromId(id: Int): Int = when (id) {
    in 1..151 -> 1
    in 152..251 -> 2
    in 252..386 -> 3
    in 387..493 -> 4
    in 494..649 -> 5
    in 650..721 -> 6
    in 722..809 -> 7
    in 810..905 -> 8
    in 906..1025 -> 9
    else -> 0
}

fun String.generationNumber(): Int = when (substringAfterLast('-').lowercase()) {
    "i", "1" -> 1
    "ii", "2" -> 2
    "iii", "3" -> 3
    "iv", "4" -> 4
    "v", "5" -> 5
    "vi", "6" -> 6
    "vii", "7" -> 7
    "viii", "8" -> 8
    "ix", "9" -> 9
    else -> 1
}

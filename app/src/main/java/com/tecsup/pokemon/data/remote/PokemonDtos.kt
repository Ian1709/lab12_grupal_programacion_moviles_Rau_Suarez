package com.tecsup.pokemon.data.remote

import com.google.gson.annotations.SerializedName

data class PokemonTypeResponseDto(
    @SerializedName("pokemon") val pokemon: List<PokemonSlotDto> = emptyList()
)

data class PokemonSlotDto(
    @SerializedName("pokemon") val pokemon: PokemonReferenceDto
)

data class PokemonReferenceDto(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

data class PokemonDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("species") val species: PokemonSpeciesReferenceDto,
    @SerializedName("types") val types: List<PokemonTypeSlotDto> = emptyList(),
    @SerializedName("sprites") val sprites: PokemonSpritesDto?
)

data class PokemonSpeciesReferenceDto(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

data class PokemonSpeciesDto(
    @SerializedName("generation") val generation: PokemonGenerationReferenceDto
)

data class PokemonGenerationReferenceDto(
    @SerializedName("name") val name: String
)

data class PokemonTypeSlotDto(
    @SerializedName("type") val type: PokemonTypeNameDto
)

data class PokemonTypeNameDto(
    @SerializedName("name") val name: String
)

data class PokemonSpritesDto(
    @SerializedName("other") val other: OtherSpritesDto?
)

data class OtherSpritesDto(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtworkDto?
)

data class OfficialArtworkDto(
    @SerializedName("front_default") val frontDefault: String?
)

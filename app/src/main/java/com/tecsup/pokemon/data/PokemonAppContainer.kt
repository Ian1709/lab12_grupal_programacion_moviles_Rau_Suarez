package com.tecsup.pokemon.data

import android.content.Context
import com.tecsup.pokemon.data.local.PokemonDatabase
import com.tecsup.pokemon.data.remote.PokemonApiService
import com.tecsup.pokemon.data.repository.PokemonRepositoryImpl
import com.tecsup.pokemon.domain.repository.PokemonRepository

class PokemonAppContainer(context: Context) {
    private val database = PokemonDatabase.getInstance(context)
    val pokemonRepository: PokemonRepository = PokemonRepositoryImpl(database.pokemonDao(), PokemonApiService.create())
}

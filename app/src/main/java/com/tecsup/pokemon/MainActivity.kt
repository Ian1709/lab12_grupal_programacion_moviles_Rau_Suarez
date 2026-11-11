package com.tecsup.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tecsup.pokemon.data.PokemonAppContainer
import com.tecsup.pokemon.ui.navigation.PokemonNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = PokemonAppContainer(applicationContext)
        setContent { PokemonNavigation(container.pokemonRepository, container.networkMonitor, container.authRepository, container.syncNotifier) }
    }
}

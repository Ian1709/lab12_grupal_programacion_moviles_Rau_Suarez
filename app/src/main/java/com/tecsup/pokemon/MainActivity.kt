package com.tecsup.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tecsup.pokemon.ui.navigation.PokemonNavigation
import com.tecsup.pokemon.ui.theme.PokemonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PokemonTheme { PokemonNavigation() } }
    }
}

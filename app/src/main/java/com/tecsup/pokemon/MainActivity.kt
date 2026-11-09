package com.tecsup.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.tecsup.pokemon.ui.theme.PokemonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PokemonTheme { Surface { PokemonApp() } } }
    }
}

@Composable
private fun PokemonApp() {
    androidx.compose.material3.Text("Pokédex Fire")
}

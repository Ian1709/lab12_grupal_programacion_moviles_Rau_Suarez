package com.tecsup.pokemon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.pokemon.ui.catalog.PokemonListScreen
import com.tecsup.pokemon.ui.detail.PokemonDetailScreen
import com.tecsup.pokemon.ui.favorites.FavoritesScreen
import com.tecsup.pokemon.ui.login.LoginScreen
import com.tecsup.pokemon.ui.viewmodel.PokemonPreviewViewModel

@Composable
fun PokemonNavigation() {
    val navController = rememberNavController()
    val pokemonViewModel: PokemonPreviewViewModel = viewModel()
    val items by pokemonViewModel.items.collectAsStateWithLifecycle()
    val detail by pokemonViewModel.selected.collectAsStateWithLifecycle()
    val favorites by pokemonViewModel.favorites.collectAsStateWithLifecycle()
    var loginAttempted by rememberSaveable { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                isLoading = false,
                errorMessage = if (loginAttempted) "Firebase todavía no está conectado. Puedes explorar la demo." else null,
                onLogin = { _, _ -> loginAttempted = true },
                onContinueAsGuest = { navController.navigate("catalog") { popUpTo("login") { inclusive = true } } }
            )
        }
        composable("catalog") {
            PokemonListScreen(
                state = items,
                onSelectPokemon = { pokemonViewModel.select(it); navController.navigate("detail/${it.id}") },
                onToggleFavorite = pokemonViewModel::toggleFavorite,
                onOpenFavorites = { navController.navigate("favorites") },
                onRetry = { }
            )
        }
        composable("detail/{pokemonId}", arguments = listOf(navArgument("pokemonId") { type = NavType.IntType })) {
            PokemonDetailScreen(
                state = detail,
                onBack = { navController.popBackStack() },
                onToggleFavorite = pokemonViewModel::toggleFavorite,
                onRetry = { }
            )
        }
        composable("favorites") {
            FavoritesScreen(
                favorites = favorites,
                onBack = { navController.popBackStack() },
                onSelectPokemon = { pokemonViewModel.select(it); navController.navigate("detail/${it.id}") },
                onRemoveFavorite = pokemonViewModel::toggleFavorite
            )
        }
    }
}

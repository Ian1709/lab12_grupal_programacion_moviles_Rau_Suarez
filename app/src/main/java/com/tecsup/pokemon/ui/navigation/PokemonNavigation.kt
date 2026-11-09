package com.tecsup.pokemon.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.pokemon.domain.repository.PokemonRepository
import com.tecsup.pokemon.domain.repository.AuthRepository
import com.tecsup.pokemon.ui.catalog.PokemonListScreen
import com.tecsup.pokemon.ui.detail.PokemonDetailScreen
import com.tecsup.pokemon.ui.favorites.FavoritesScreen
import com.tecsup.pokemon.ui.login.LoginScreen
import com.tecsup.pokemon.ui.viewmodel.PokemonDetailViewModel
import com.tecsup.pokemon.ui.viewmodel.PokemonListViewModel
import com.tecsup.pokemon.ui.viewmodel.AuthViewModel
import com.tecsup.pokemon.domain.model.UiState

@Composable
fun PokemonNavigation(repository: PokemonRepository, networkMonitor: com.tecsup.pokemon.domain.repository.NetworkMonitor, authRepository: AuthRepository) {
    val navController = rememberNavController()
    val pokemonViewModel: PokemonListViewModel = viewModel(factory = PokemonListViewModel.factory(repository, networkMonitor))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(authRepository))
    val session by authViewModel.session.collectAsStateWithLifecycle()
    val items by pokemonViewModel.items.collectAsStateWithLifecycle()
    val favorites by pokemonViewModel.favorites.collectAsStateWithLifecycle()
    val isOffline by pokemonViewModel.isOffline.collectAsStateWithLifecycle()

    LaunchedEffect(session) {
        if (session is UiState.Success) {
            val user = (session as UiState.Success).data
            if (user != null) navController.navigate("catalog") { popUpTo("login") { inclusive = true }; launchSingleTop = true }
            else if (navController.currentDestination?.route != "login") {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                isLoading = session is UiState.Loading,
                errorMessage = (session as? UiState.Error)?.message,
                onLogin = authViewModel::signIn
            )
        }
        composable("catalog") {
            PokemonListScreen(
                state = items,
                onSelectPokemon = { navController.navigate("detail/${it.id}/${it.name}") },
                onToggleFavorite = pokemonViewModel::toggleFavorite,
                onOpenFavorites = { navController.navigate("favorites") },
                onRetry = { pokemonViewModel.synchronize() },
                isOffline = isOffline,
                onLogout = authViewModel::signOut
            )
        }
        composable("detail/{pokemonId}/{pokemonName}", arguments = listOf(
            navArgument("pokemonId") { type = NavType.IntType },
            navArgument("pokemonName") { type = NavType.StringType }
        )) { entry ->
            val pokemonId = entry.arguments?.getInt("pokemonId") ?: 0
            val pokemonName = entry.arguments?.getString("pokemonName").orEmpty()
            val detailViewModel: PokemonDetailViewModel = viewModel(
                key = "pokemon-detail-$pokemonId",
                factory = PokemonDetailViewModel.factory(repository, pokemonId, pokemonName)
            )
            val detail by detailViewModel.state.collectAsStateWithLifecycle()
            PokemonDetailScreen(
                state = detail,
                onBack = { navController.popBackStack() },
                onToggleFavorite = pokemonViewModel::toggleFavorite,
                onRetry = detailViewModel::retry
            )
        }
        composable("favorites") {
            FavoritesScreen(
                favorites = (favorites as? UiState.Success)?.data.orEmpty(),
                onBack = { navController.popBackStack() },
                onSelectPokemon = { navController.navigate("detail/${it.id}/${it.name}") },
                onRemoveFavorite = pokemonViewModel::toggleFavorite
            )
        }
    }
}

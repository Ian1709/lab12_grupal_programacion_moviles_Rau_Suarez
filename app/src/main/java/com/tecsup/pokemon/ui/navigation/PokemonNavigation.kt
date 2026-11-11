package com.tecsup.pokemon.ui.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.pokemon.domain.repository.PokemonRepository
import com.tecsup.pokemon.domain.repository.AuthRepository
import com.tecsup.pokemon.domain.repository.SyncNotifier
import com.tecsup.pokemon.ui.catalog.PokemonListScreen
import com.tecsup.pokemon.ui.detail.PokemonDetailScreen
import com.tecsup.pokemon.ui.favorites.FavoritesScreen
import com.tecsup.pokemon.ui.login.LoginScreen
import com.tecsup.pokemon.ui.viewmodel.PokemonDetailViewModel
import com.tecsup.pokemon.ui.viewmodel.PokemonListViewModel
import com.tecsup.pokemon.ui.viewmodel.AuthViewModel
import com.tecsup.pokemon.ui.viewmodel.AdminViewModel
import com.tecsup.pokemon.ui.admin.AdminPanelScreen
import com.tecsup.pokemon.ui.profile.ProfileScreen
import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.ui.theme.PokemonTheme

@Composable
fun PokemonNavigation(
    repository: PokemonRepository,
    networkMonitor: com.tecsup.pokemon.domain.repository.NetworkMonitor,
    authRepository: AuthRepository,
    syncNotifier: SyncNotifier
) {
    val navController = rememberNavController()
    val pokemonViewModel: PokemonListViewModel = viewModel(factory = PokemonListViewModel.factory(repository, networkMonitor, syncNotifier))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(authRepository))
    val session by authViewModel.session.collectAsStateWithLifecycle()
    val items by pokemonViewModel.items.collectAsStateWithLifecycle()
    val favorites by pokemonViewModel.favorites.collectAsStateWithLifecycle()
    val isOffline by pokemonViewModel.isOffline.collectAsStateWithLifecycle()
    val selectedType by pokemonViewModel.selectedType.collectAsStateWithLifecycle()
    val currentUser = (session as? UiState.Success)?.data
    val context = LocalContext.current
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    LaunchedEffect(session) {
        if (session is UiState.Success) {
            val user = (session as UiState.Success).data
            if (user != null) navController.navigate("catalog") { popUpTo("login") { inclusive = true }; launchSingleTop = true }
            else if (navController.currentDestination?.route != "login") {
                navController.navigate("login") {
                    popUpTo("catalog") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    PokemonTheme(type = selectedType) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                isLoading = session is UiState.Loading,
                errorMessage = (session as? UiState.Error)?.message,
                onLogin = authViewModel::signIn,
                onRegister = authViewModel::signUp
            )
        }
        composable("catalog") {
            PokemonListScreen(
                state = items,
                selectedType = selectedType,
                onSelectType = pokemonViewModel::selectType,
                onSelectPokemon = { navController.navigate("detail/${it.id}/${it.name}") },
                onToggleFavorite = pokemonViewModel::toggleFavorite,
                onOpenFavorites = { navController.navigate("favorites") },
                onRetry = { pokemonViewModel.synchronize() },
                isOffline = isOffline,
                onLogout = authViewModel::signOut,
                isAdmin = currentUser?.role == "admin",
                onOpenAdmin = { if (currentUser?.role == "admin") navController.navigate("admin") },
                onOpenProfile = { navController.navigate("profile") }
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
        composable("profile") {
            val user = currentUser
            if (user == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") { popUpTo("profile") { inclusive = true } }
                }
            } else {
                ProfileScreen(
                    session = user,
                    onBack = { navController.popBackStack() },
                    onOpenAdmin = { if (user.role == "admin") navController.navigate("admin") },
                    onSignOut = authViewModel::signOut
                )
            }
        }
        composable("admin") {
            if (currentUser?.role != "admin") {
                LaunchedEffect(Unit) {
                    navController.navigate("catalog") { popUpTo("admin") { inclusive = true } }
                }
            } else {
                val adminViewModel: AdminViewModel = viewModel(
                    factory = AdminViewModel.factory(repository, syncNotifier)
                )
                val stats by adminViewModel.stats.collectAsStateWithLifecycle()
                val isSyncing by adminViewModel.isSyncing.collectAsStateWithLifecycle()
                val message by adminViewModel.message.collectAsStateWithLifecycle()
                AdminPanelScreen(
                    stats = stats,
                    selectedType = selectedType,
                    isSyncing = isSyncing,
                    message = message,
                    onBack = { navController.popBackStack() },
                    onSynchronize = { adminViewModel.synchronize(selectedType) },
                    onClearCache = adminViewModel::clearCache
                )
            }
        }
    }
    }
}

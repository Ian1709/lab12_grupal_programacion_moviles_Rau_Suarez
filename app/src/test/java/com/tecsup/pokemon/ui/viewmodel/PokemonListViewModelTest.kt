package com.tecsup.pokemon.ui.viewmodel

import com.tecsup.pokemon.domain.model.Pokemon
import com.tecsup.pokemon.domain.model.PokemonStats
import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.domain.repository.NetworkMonitor
import com.tecsup.pokemon.domain.repository.PokemonRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test fun `muestra elementos emitidos desde el repositorio local`() = runTest {
        val pokemon = Pokemon(id = 4, name = "charmander")
        val repository = FakePokemonRepository(items = listOf(pokemon))
        val viewModel = PokemonListViewModel(repository, FakeNetworkMonitor(online = true))
        advanceUntilIdle()

        assertEquals(UiState.Success(listOf(pokemon)), viewModel.items.value)
    }

    @Test fun `muestra error reintentable cuando no hay red ni datos`() = runTest {
        val viewModel = PokemonListViewModel(FakePokemonRepository(), FakeNetworkMonitor(online = false))
        advanceUntilIdle()

        assertTrue(viewModel.items.value is UiState.Error)
    }

    @Test fun `alternar favorito delega el cambio al repositorio`() = runTest {
        val pokemon = Pokemon(id = 4, name = "charmander")
        val repository = FakePokemonRepository(items = listOf(pokemon))
        val viewModel = PokemonListViewModel(repository, FakeNetworkMonitor(online = false))
        advanceUntilIdle()

        viewModel.toggleFavorite(pokemon)
        advanceUntilIdle()

        assertEquals(4 to true, repository.lastFavoriteChange)
    }
}

private class FakeNetworkMonitor(online: Boolean) : NetworkMonitor {
    override val isOnline: StateFlow<Boolean> = MutableStateFlow(online)
}

private class FakePokemonRepository(items: List<Pokemon> = emptyList()) : PokemonRepository {
    private val catalog = MutableStateFlow(items)
    private val initialItems = items
    override fun observePokemon(): Flow<List<Pokemon>> = catalog
    override fun observeFavorites(): Flow<List<Pokemon>> = flowOf(initialItems.filter(Pokemon::isFavorite))
    override fun observePokemon(id: Int): Flow<Pokemon?> = flowOf(initialItems.firstOrNull { it.id == id })
    override fun observeStats(): Flow<PokemonStats> = flowOf(PokemonStats(initialItems.size, initialItems.count(Pokemon::isFavorite)))
    override suspend fun synchronize(type: String) = Unit
    override suspend fun loadDetails(name: String): Pokemon = error("Not used in this test")
    var lastFavoriteChange: Pair<Int, Boolean>? = null
    override suspend fun setFavorite(id: Int, favorite: Boolean) { lastFavoriteChange = id to favorite }
    override suspend fun clearCache() { catalog.value = emptyList() }
}

package com.tecsup.pokemon.ui.viewmodel

import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.domain.model.UserSession
import com.tecsup.pokemon.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test fun `registro exitoso inicia sesion como entrenador normal`() = runTest {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(repository)
        advanceUntilIdle()

        viewModel.signUp("Ash", "ash@example.com", "pikachu123")
        advanceUntilIdle()

        val session = (viewModel.session.value as UiState.Success).data
        assertEquals("Ash", repository.registeredName)
        assertEquals("usuario", session?.role)
        assertEquals("ash@example.com", session?.email)
    }

    @Test fun `error de registro se expone sin abrir el catalogo`() = runTest {
        val viewModel = AuthViewModel(FakeAuthRepository().apply { failRegistration = true })
        advanceUntilIdle()

        viewModel.signUp("Ash", "ash@example.com", "pikachu123")
        advanceUntilIdle()

        assertTrue(viewModel.session.value is UiState.Error)
    }
}

private class FakeAuthRepository : AuthRepository {
    var registeredName: String? = null
    var failRegistration = false

    override suspend fun currentSession(): UserSession? = null
    override suspend fun signIn(email: String, password: String): UserSession = error("Not used in this test")
    override suspend fun signUp(displayName: String, email: String, password: String): UserSession {
        if (failRegistration) error("No se pudo crear la cuenta.")
        registeredName = displayName
        return UserSession(uid = "test-uid", email = email, role = "usuario", displayName = displayName)
    }
    override suspend fun signOut() = Unit
    override suspend fun updateProfilePhoto(photoUri: String): String = photoUri
}

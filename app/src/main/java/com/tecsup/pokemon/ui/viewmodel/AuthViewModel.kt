package com.tecsup.pokemon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.domain.model.UiState
import com.tecsup.pokemon.domain.model.UserSession
import com.tecsup.pokemon.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _session = MutableStateFlow<UiState<UserSession?>>(UiState.Loading)
    val session: StateFlow<UiState<UserSession?>> = _session.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repository.currentSession() }
                .onSuccess { _session.value = UiState.Success(it) }
                .onFailure { _session.value = UiState.Error(it.message ?: "No se pudo recuperar la sesión.") }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _session.value = UiState.Loading
            runCatching { repository.signIn(email, password) }
                .onSuccess { _session.value = UiState.Success(it) }
                .onFailure { _session.value = UiState.Error(it.message ?: "No se pudo iniciar sesión.") }
        }
    }

    fun signUp(displayName: String, email: String, password: String) {
        viewModelScope.launch {
            _session.value = UiState.Loading
            runCatching { repository.signUp(displayName, email, password) }
                .onSuccess { _session.value = UiState.Success(it) }
                .onFailure { _session.value = UiState.Error(it.message ?: "No se pudo crear la cuenta.") }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            runCatching { repository.signOut() }
                .onSuccess { _session.value = UiState.Success(null) }
                .onFailure { _session.value = UiState.Error(it.message ?: "No se pudo cerrar sesión.") }
        }
    }

    fun updateProfilePhoto(photoUri: String) {
        viewModelScope.launch {
            val current = (_session.value as? UiState.Success)?.data ?: return@launch
            runCatching { repository.updateProfilePhoto(photoUri) }
                .onSuccess { _session.value = UiState.Success(current.copy(photoUri = photoUri)) }
        }
    }

    companion object {
        fun factory(repository: AuthRepository) = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(repository) as T
        }
    }
}

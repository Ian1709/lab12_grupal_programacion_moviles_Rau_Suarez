package com.tecsup.pokemon.domain.repository

import com.tecsup.pokemon.domain.model.UserSession

interface AuthRepository {
    suspend fun currentSession(): UserSession?
    suspend fun signIn(email: String, password: String): UserSession
    suspend fun signUp(displayName: String, email: String, password: String): UserSession
    suspend fun signOut()
}

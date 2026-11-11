package com.tecsup.pokemon.domain.model

data class UserSession(
    val uid: String,
    val email: String?,
    val role: String,
    val displayName: String? = null,
    val photoUri: String? = null
)

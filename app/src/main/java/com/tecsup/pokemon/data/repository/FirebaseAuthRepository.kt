package com.tecsup.pokemon.data.repository

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.pokemon.domain.model.UserSession
import com.tecsup.pokemon.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(context: Context) : AuthRepository {
    private val firebaseApp = FirebaseApp.initializeApp(context.applicationContext)
    private val auth = firebaseApp?.let(FirebaseAuth::getInstance)
    private val firestore = firebaseApp?.let(FirebaseFirestore::getInstance)

    override suspend fun currentSession(): UserSession? {
        val user = auth?.currentUser ?: return null
        return try {
            user.toSession()
        } catch (error: Throwable) {
            auth?.signOut()
            throw error
        }
    }

    override suspend fun signIn(email: String, password: String): UserSession {
        val firebaseAuth = requireNotNull(auth) {
            "Firebase no está configurado. Coloca app/google-services.json en este equipo y vuelve a sincronizar Gradle."
        }
        val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
        val user = requireNotNull(result.user) { "Firebase no devolvió el usuario autenticado." }
        return try {
            user.toSession()
        } catch (error: Throwable) {
            firebaseAuth.signOut()
            throw error
        }
    }

    override suspend fun signOut() {
        requireNotNull(auth) { "Firebase no está configurado." }.signOut()
    }

    private suspend fun FirebaseUser.toSession(): UserSession {
        val database = requireNotNull(firestore) { "Firebase no está configurado." }
        val profile = database.collection("usuarios").document(uid).get().await()
        val role = profile.getString("rol")?.trim()?.lowercase()
            ?: throw IllegalStateException("No existe el campo rol en Firestore para este usuario.")
        require(role == "admin" || role == "usuario") { "El rol debe ser admin o usuario." }
        return UserSession(uid = uid, email = email, role = role)
    }
}

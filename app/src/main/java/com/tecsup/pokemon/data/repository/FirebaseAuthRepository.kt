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
    private val appContext = context.applicationContext
    private val firebaseApp = FirebaseApp.initializeApp(appContext)
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

    override suspend fun signUp(displayName: String, email: String, password: String): UserSession {
        val firebaseAuth = requireNotNull(auth) {
            "Firebase no está configurado. Coloca app/google-services.json en este equipo y vuelve a sincronizar Gradle."
        }
        val database = requireNotNull(firestore) { "Firebase no está configurado." }
        val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
        val user = requireNotNull(result.user) { "Firebase no devolvió el usuario creado." }
        return try {
            database.collection("usuarios").document(user.uid).set(
                mapOf("rol" to "usuario", "nombre" to displayName.trim())
            ).await()
            user.toSession()
        } catch (error: Throwable) {
            runCatching { user.delete().await() }
            firebaseAuth.signOut()
            throw error
        }
    }

    override suspend fun signOut() {
        requireNotNull(auth) { "Firebase no está configurado." }.signOut()
    }

    override suspend fun updateProfilePhoto(photoUri: String) {
        val user = requireNotNull(auth?.currentUser) { "Inicia sesión para cambiar tu foto." }
        appContext.getSharedPreferences("profile_photos", Context.MODE_PRIVATE)
            .edit().putString(user.uid, photoUri).apply()
    }

    private suspend fun FirebaseUser.toSession(): UserSession {
        val database = requireNotNull(firestore) { "Firebase no está configurado." }
        val profile = database.collection("usuarios").document(uid).get().await()
        val role = profile.getString("rol")?.trim()?.lowercase()
            ?: throw IllegalStateException("No existe el campo rol en Firestore para este usuario.")
        require(role == "admin" || role == "usuario") { "El rol debe ser admin o usuario." }
        val photoUri = appContext.getSharedPreferences("profile_photos", Context.MODE_PRIVATE).getString(uid, null)
        return UserSession(uid = uid, email = email, role = role, displayName = profile.getString("nombre"), photoUri = photoUri)
    }
}

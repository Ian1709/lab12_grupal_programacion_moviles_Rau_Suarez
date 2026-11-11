package com.tecsup.pokemon.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.R

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (email: String, password: String) -> Unit,
    onRegister: (displayName: String, email: String, password: String) -> Unit,
    onContinueAsGuest: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isRegistering by rememberSaveable { mutableStateOf(false) }
    var validationMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF32130F), Color(0xFF171315), Color(0xFF101114))))
            .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.fillMaxWidth().height(205.dp).clip(RoundedCornerShape(30.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.pokemon_starters_banner),
                contentDescription = "Pokémon iniciales Squirtle, Bulbasaur y Tepig",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Black.copy(alpha = .05f), Color.Black.copy(alpha = .72f)))
                )
            )
            Column(Modifier.align(Alignment.BottomStart).padding(22.dp), horizontalAlignment = Alignment.Start) {
                Text("TU AVENTURA EMPIEZA AQUÍ", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = .9f), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Atrápalos a todos.", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Black)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(if (isRegistering) "Crea tu cuenta de entrenador" else "Bienvenido a la Pokédex", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            if (isRegistering) "Tu cuenta nueva tendrá acceso de entrenador."
            else "Entra y descubre tu equipo de fuego",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1D1C20))
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                if (isRegistering) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it; validationMessage = null },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre de entrenador") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; validationMessage = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; validationMessage = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "Ocultar" else "Ver")
                        }
                    }
                )
                if (isRegistering) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; validationMessage = null },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
                val message = validationMessage ?: errorMessage
                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(message, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = {
                        validationMessage = when {
                            isRegistering && displayName.isBlank() -> "Escribe tu nombre de entrenador."
                            email.isBlank() -> "Escribe tu correo electrónico."
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Escribe un correo electrónico válido."
                            password.isBlank() -> "Escribe tu contraseña."
                            isRegistering && password.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
                            isRegistering && confirmPassword != password -> "Las contraseñas no coinciden."
                            else -> null
                        }
                        if (validationMessage == null) {
                            if (isRegistering) onRegister(displayName.trim(), email.trim(), password)
                            else onLogin(email.trim(), password)
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(Modifier.height(20.dp), strokeWidth = 2.dp)
                    else Text(if (isRegistering) "Crear cuenta de entrenador" else "Iniciar sesión", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        isRegistering = !isRegistering
                        validationMessage = null
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isRegistering) "¿Ya tienes cuenta? Inicia sesión" else "¿Nuevo entrenador? Crear cuenta")
                }
            }
        }
        if (onContinueAsGuest != null) {
            TextButton(onClick = onContinueAsGuest, enabled = !isLoading) {
                Text("Explorar catálogo de demostración")
            }
        }
    }
}


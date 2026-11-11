package com.tecsup.pokemon.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (email: String, password: String) -> Unit,
    onContinueAsGuest: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var validationMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF32130F), Color(0xFF171315), Color(0xFF101114))))
            .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(32.dp))
                .background(Brush.linearGradient(listOf(Color(0xFFEF553F), Color(0xFF9F292B), Color(0xFF521D2D))))
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text("TU AVENTURA EMPIEZA AQUÍ", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = .8f), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Text("Atrápalos\na todos.", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(18.dp))
                PokeballMark(Modifier.align(Alignment.End).height(84.dp))
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Bienvenido a la Pokédex", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Entra y descubre tu equipo de fuego", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1D1C20))
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
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
                val message = validationMessage ?: errorMessage
                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(message, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = {
                        validationMessage = when {
                            email.isBlank() -> "Escribe tu correo electrónico."
                            password.isBlank() -> "Escribe tu contraseña."
                            else -> null
                        }
                        if (validationMessage == null) onLogin(email.trim(), password)
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(Modifier.height(20.dp), strokeWidth = 2.dp)
                    else Text("Iniciar sesión", fontWeight = FontWeight.Bold)
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

@Composable
private fun PokeballMark(modifier: Modifier = Modifier) {
    Box(Modifier.size(84.dp).then(modifier).clip(CircleShape).background(Color.White).padding(6.dp)) {
        Column {
            Box(Modifier.fillMaxWidth().weight(1f).background(Color(0xFFE8453E)))
            Box(Modifier.fillMaxWidth().height(6.dp).background(Color(0xFF222126)))
            Box(Modifier.fillMaxWidth().weight(1f).background(Color(0xFFFFFBF5)))
        }
        Box(Modifier.align(Alignment.Center).clip(CircleShape).background(Color.White).padding(5.dp)) {
            Box(Modifier.clip(CircleShape).background(Color(0xFF242329)).padding(7.dp))
        }
    }
}


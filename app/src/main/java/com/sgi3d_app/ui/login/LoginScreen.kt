package com.sgi3d_app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sgi3d_app.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onLoginSuccess: ((token: String, role: String) -> Unit)? = null
) {
    val viewModel: LoginViewModel = viewModel()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // ✅ succès login (optionnel)
    LaunchedEffect(viewModel.token) {
        val token = viewModel.token
        val role = viewModel.userRole

        if (token != null && role != null) {
            onLoginSuccess?.invoke(token, role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // 🔥 LOGO
        Image(
            painter = painterResource(id = R.drawable.logo_lycee),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔥 TITRE
        Text(
            text = "SGI 3D",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Gestion imprimantes 3D",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🧱 CARD
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // 📧 EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 🔒 PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (passwordVisible)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            Icon(icon, contentDescription = null)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.login(email, password)
                            onLoginClick(email, password) // ✅ garde compatibilité
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 🔘 BUTTON
                Button(
                    onClick = {
                        viewModel.login(email, password)
                        onLoginClick(email, password) // ✅ important
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Se connecter")
                    }
                }

                // ❌ ERREUR STYLÉE
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val message = errorMessage ?: ""

                    // 🔥 Choix icône selon erreur
                    val icon = when {
                        message.contains("email", true) -> Icons.Default.VisibilityOff
                        message.contains("mot de passe", true) -> Icons.Default.Visibility
                        message.contains("connexion", true) -> Icons.Default.VisibilityOff
                        else -> Icons.Default.VisibilityOff
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
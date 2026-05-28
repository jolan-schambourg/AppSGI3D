package com.sgi3d_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.sgi3d_app.data.repository.AuthRepository

import kotlinx.coroutines.launch

import androidx.compose.runtime.*
import kotlinx.coroutines.withTimeout

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    // ===============================
    // États UI
    // ===============================
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var userRole by mutableStateOf<String?>(null)
    var token by mutableStateOf<String?>(null)

    // ===============================
    // LOGIN
    // ===============================
    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = withTimeout(10_000) { // ⏱️ 10 secondes
                    repository.login(email, password)
                }

                if (response != null && response.success) {
                    token = response.token
                    userRole = response.role ?: "user"

                } else {
                    errorMessage = when (response?.message) {
                        "USER_NOT_FOUND" -> "Cet email n'est associé à aucun compte"
                        "WRONG_PASSWORD" -> "Mot de passe incorrect"
                        "INVALID_CREDENTIALS" -> "Email ou mot de passe incorrect"
                        else -> "Identifiants incorrects"
                    }
                }

            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                // ⏱️ Timeout → réseau lent / BDD KO
                errorMessage = "Erreur réseau : serveur trop lent ou connexion à la base de données impossible"

            } catch (e: Exception) {
                // 🌐 Autres erreurs (API down, crash…)
                errorMessage = "Erreur de connexion au serveur"

            } finally {
                isLoading = false
            }
        }
    }
}


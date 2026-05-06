package com.sgi3d_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.sgi3d_app.data.repository.AuthRepository

import kotlinx.coroutines.launch

import androidx.compose.runtime.*

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

    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            isLoading = true

            errorMessage = null

            try {

                val response =
                    repository.login(
                        email,
                        password
                    )

                if (
                    response != null &&
                    response.success
                ) {

                    // ✅ Token reçu

                    token = response.token

                    // ⚠️ role null → valeur par défaut

                    userRole =
                        response.role ?: "user"

                }

                else {

                    errorMessage =
                        response?.message
                            ?: "Identifiants incorrect"

                }

            }

            catch (e: Exception) {

                errorMessage =
                    "Erreur serveur"

            }

            isLoading = false

        }

    }

}
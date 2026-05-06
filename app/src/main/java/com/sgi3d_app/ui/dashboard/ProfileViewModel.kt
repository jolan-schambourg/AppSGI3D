package com.sgi3d_app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sgi3d_app.data.remote.ApiRetrofitInstance
import com.sgi3d_app.data.remote.ProfileData
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

class ProfileViewModel : ViewModel() {

    var profile by mutableStateOf<ProfileData?>(null)

    var isLoading by mutableStateOf(true)

    var errorMessage by mutableStateOf<String?>(null)

    fun loadProfile(token: String) {

        viewModelScope.launch {

            try {

                val response =
                    ApiRetrofitInstance.api
                        .getProfile("Bearer $token")

                if (response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    profile =
                        response.body()?.profile

                    errorMessage = null

                }
                else {

                    errorMessage =
                        response.body()?.message
                            ?: "Erreur serveur"

                }

            } catch (e: Exception) {

                errorMessage =
                    "Erreur connexion"

            }

            isLoading = false

        }

    }

}
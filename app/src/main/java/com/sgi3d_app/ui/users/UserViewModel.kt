package com.sgi3d_app.ui.users

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.sgi3d_app.data.remote.ApiRetrofitInstance
import com.sgi3d_app.data.remote.UserItem
import com.sgi3d_app.data.remote.ToggleUserStatusRequest
import com.sgi3d_app.data.remote.DeleteUserRequest
import com.sgi3d_app.data.remote.UserResponse
import com.sgi3d_app.data.remote.CreateUserRequest

import kotlinx.coroutines.launch



class UserViewModel : ViewModel() {

    // 📄 Liste utilisateurs
    var users by mutableStateOf<List<UserItem>>(emptyList())
        private set

    private val _createSuccess = mutableStateOf(false)
    val createSuccess: State<Boolean> = _createSuccess


    // 🔄 Charger utilisateurs
    fun fetchUsers(token: String) {

        viewModelScope.launch {

            try {

                val response =
                    ApiRetrofitInstance
                        .userApi
                        .getUsers("Bearer $token")

                if (response.isSuccessful) {

                    val body: UserResponse? =
                        response.body()

                    if (
                        body != null &&
                        body.success
                    ) {

                        // ✅ ICI la vraie correction
                        users = body.data

                    }

                }

            }
            catch (e: Exception) {

                e.printStackTrace()

            }

        }

    }


    // 🔴🟢 Activer / Désactiver utilisateur
    fun toggleUserStatus(
        token: String,
        id: Int,
        actif: Int
    ) {

        println("API toggleUserStatus id=$id actif=$actif")

        viewModelScope.launch {

            try {

                val response =
                    ApiRetrofitInstance
                        .userApi
                        .toggleUserStatus(
                            "Bearer $token",
                            ToggleUserStatusRequest(
                                id,
                                actif
                            )
                        )

                println("API RESPONSE: ${response.code()}")

                if (response.isSuccessful) {

                    println("Toggle SUCCESS")

                    fetchUsers(token)

                } else {

                    println("Toggle FAILED")

                }

            }
            catch (e: Exception) {

                println("ERROR toggleUserStatus")
                e.printStackTrace()

            }

        }

    }


    // 🗑 Supprimer utilisateur
    fun deleteUser(
        token: String,
        id: Int
    ) {

        println("API deleteUser id=$id")

        viewModelScope.launch {

            try {

                val response =
                    ApiRetrofitInstance
                        .userApi
                        .deleteUser(
                            "Bearer $token",
                            DeleteUserRequest(id)
                        )

                println("DELETE RESPONSE: ${response.code()}")

                if (response.isSuccessful) {

                    println("Delete SUCCESS")

                    fetchUsers(token)

                } else {

                    println("Delete FAILED")

                }

            }
            catch (e: Exception) {

                println("ERROR deleteUser")
                e.printStackTrace()

            }

        }

    }

    fun createUser(
        token: String,
        nom: String,
        email: String,
        password: String,
        role: String
    ) {

        viewModelScope.launch {

            try {

                println("🧪 CREATE USER CLICK")

                val response =
                    ApiRetrofitInstance.userApi.createUser(
                        "Bearer $token",
                        CreateUserRequest(
                            nom = nom,
                            email = email,
                            password = password,
                            role = role
                        )
                    )

                println("📡 RESPONSE CODE: ${response.code()}")
                println("📦 BODY: ${response.body()}")

                if (
                    response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    println("✅ USER CREATED")

                    _createSuccess.value = true

                } else {

                    println("❌ CREATE FAILED: ${response.body()?.message}")

                }

            } catch (e: Exception) {

                println("💥 EXCEPTION CREATE USER")
                e.printStackTrace()

            }

        }
    }

    fun resetCreateSuccess() {
        _createSuccess.value = false
    }



}
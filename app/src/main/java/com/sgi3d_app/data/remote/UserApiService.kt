package com.sgi3d_app.data.remote

import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    // 📥 Récupérer utilisateurs
    @GET("get_users.php")
    suspend fun getUsers(
        @Header("Authorization") token: String
    ): Response<UserResponse>


    // 🔴🟢 Activer / Désactiver utilisateur
    @POST("toggle_user_status.php")
    suspend fun toggleUserStatus(
        @Header("Authorization") token: String,
        @Body request: ToggleUserStatusRequest
    ): Response<SimpleResponse>


    // 🗑 Supprimer utilisateur
    @POST("delete_user.php")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Body request: DeleteUserRequest
    ): Response<SimpleResponse>

    @POST("create_user.php")
    suspend fun createUser(

        @Header("Authorization")
        token: String,

        @Body
        request: CreateUserRequest

    ): Response<CreateUserResponse>

}
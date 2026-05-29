package com.sgi3d_app.data.remote

// ===============================
// IMPORTS (TOUJOURS EN HAUT)
// ===============================

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

import com.sgi3d_app.data.model.Printer
import com.sgi3d_app.data.Demande


// ===============================
// 🔐 LOGIN
// ===============================

data class LoginRequest(
    val email: String,
    val mot_de_passe: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val role: String?,
    val message: String?
)




// ===============================
// 👤 PROFIL
// ===============================




// ===============================
// 🌐 INTERFACE API
// ===============================

interface ApiService {

    // 🔐 LOGIN
    @POST("login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    // ===============================
    // 🖨 IMPRIMANTES
    // ===============================

    @GET("get_printers.php")
    suspend fun getPrinters(
        @Header("Authorization") token: String
    ): Response<PrintersResponse>


    // ===============================
    // 📄 DEMANDES
    // ===============================

    @GET("get_demandes.php")
    suspend fun getDemandes(
        @Header("Authorization") token: String
    ): Response<DemandeResponse>


    // ===============================
    // 👤 PROFIL
    // ===============================

    @GET("get_profile.php")
    suspend fun getProfile(
        @Header("Authorization")
        token: String
    ): Response<ProfileResponse>

    @POST("toggle_user_status.php")
    suspend fun toggleUserStatus(
        @Header("Authorization") token: String,
        @Body request: ToggleUserStatusRequest
    ): Response<SimpleResponse>


    @POST("delete_user.php")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Body request: DeleteUserRequest
    ): Response<SimpleResponse>

}
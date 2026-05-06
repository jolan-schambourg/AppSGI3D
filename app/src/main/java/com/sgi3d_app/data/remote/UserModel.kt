package com.sgi3d_app.data.remote

data class UserItem(

    val id: Int,
    val nom: String,
    val email: String,
    val role: String,
    val statut: String,
    val actif: Int

)

data class UserResponse(

    val success: Boolean,
    val data: List<UserItem>

)
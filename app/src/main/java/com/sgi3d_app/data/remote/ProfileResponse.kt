package com.sgi3d_app.data.remote


data class ProfileResponse(

    val success: Boolean,

    val profile: ProfileData?,

    val message: String?   // 🔥 AJOUT IMPORTANT

)

data class ProfileData(

    val id: Int,

    val nom: String,

    val email: String,

    val role: String,

    val avatar: String?,

    val actif: Int?,

    val cree_le: String?

)
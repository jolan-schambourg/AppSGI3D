package com.sgi3d_app.data.remote

data class User(
    val id: Int,
    val nom: String,
    val email: String,
    val role: String,
    val actif: Int
)
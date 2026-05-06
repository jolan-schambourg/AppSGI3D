package com.sgi3d_app.data.remote

data class CreateUserRequest(

    val nom: String,
    val email: String,
    val password: String,
    val role: String

)
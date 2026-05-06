package com.sgi3d_app.data.repository

import android.util.Log

import com.sgi3d_app.data.remote.LoginRequest
import com.sgi3d_app.data.remote.LoginResponse
import com.sgi3d_app.data.remote.ApiRetrofitInstance

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse? {

        return try {

            val response =
                ApiRetrofitInstance.api.login(
                    LoginRequest(
                        email = email,
                        mot_de_passe = password
                    )
                )

            if (response.isSuccessful) {

                val body = response.body()

                Log.d("LOGIN_DEBUG",
                    "Response OK: $body")

                body

            }

            else {

                Log.e(
                    "LOGIN_DEBUG",
                    "HTTP Error: ${response.code()}"
                )

                null

            }

        }

        catch (e: Exception) {

            Log.e(
                "LOGIN_DEBUG",
                "Exception: ${e.message}"
            )

            null

        }

    }

}
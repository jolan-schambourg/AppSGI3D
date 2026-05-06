package com.sgi3d_app.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

import org.json.JSONObject
import org.json.JSONArray

import java.io.IOException

import android.os.Handler
import android.os.Looper

object ApiClient {

    private val client = OkHttpClient()

    private const val BASE_URL =
        "http://192.168.0.101/api/"

    // ===============================
    // 🔐 LOGIN
    // ===============================

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        val json = JSONObject()

        json.put("email", email)
        json.put("mot_de_passe", password)

        val mediaType =
            "application/json".toMediaType()

        val body =
            json.toString()
                .toRequestBody(mediaType)

        val request =
            Request.Builder()
                .url(BASE_URL + "login.php")
                .post(body)
                .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onFailure(
                    call: Call,
                    e: IOException
                ) {

                    onResult(false, null)

                }

                override fun onResponse(
                    call: Call,
                    response: Response
                ) {

                    val responseBody =
                        response.body?.string()

                    if (responseBody != null) {

                        val jsonResponse =
                            JSONObject(responseBody)

                        val success =
                            jsonResponse.getBoolean("success")

                        if (success) {

                            val token =
                                jsonResponse.getString("token")

                            onResult(true, token)

                        } else {

                            onResult(false, null)

                        }

                    } else {

                        onResult(false, null)

                    }

                }

            })

    }



    // ===============================
    // 🖨 GET PRINTERS
    // ===============================

    fun getPrinters(
        token: String,
        onResult: (JSONArray?) -> Unit
    ) {

        val request =
            Request.Builder()
                .url(BASE_URL + "get_printers.php")
                .addHeader("Authorization", token)
                .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onFailure(
                    call: Call,
                    e: IOException
                ) {

                    onResult(null)

                }

                override fun onResponse(
                    call: Call,
                    response: Response
                ) {

                    val body =
                        response.body?.string()

                    if (body != null) {

                        try {

                            val json =
                                JSONObject(body)

                            if (json.getBoolean("success")) {

                                val printers =
                                    json.getJSONArray("printers")

                                onResult(printers)

                            } else {

                                onResult(null)

                            }

                        } catch (e: Exception) {

                            onResult(null)

                        }

                    } else {

                        onResult(null)

                    }

                }

            })

    }

    fun changePassword(
        token: String,
        oldPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {

        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("old_password", oldPassword)
            .add("new_password", newPassword)
            .build()

        val request = Request.Builder()
            .url(BASE_URL + "change_password.php")
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                e.printStackTrace()

                android.util.Log.e(
                    "CHANGE_PASSWORD",
                    "Erreur réseau: ${e.message}"
                )

                callback(false, "Erreur réseau")

            }

            override fun onResponse(
                call: Call,
                response: Response
            ) {

                val body =
                    response.body?.string() ?: ""

                /////////////////////////////////////////////////////
                // 🔥 LOG IMPORTANT
                /////////////////////////////////////////////////////

                android.util.Log.d(
                    "CHANGE_PASSWORD",
                    "Réponse serveur: $body"
                )

                try {

                    val json =
                        JSONObject(body)

                    val success =
                        json.getBoolean("success")

                    val message =
                        json.getString("message")

                    val debugArray = json.optJSONArray("debug")

                    if (debugArray != null) {

                        for (i in 0 until debugArray.length()) {

                            android.util.Log.d(
                                "PHP_DEBUG",
                                debugArray.getString(i)
                            )
                        }
                    }

                    /////////////////////////////////////////////////////

                    android.util.Log.d(
                        "CHANGE_PASSWORD",
                        "Success: $success"
                    )

                    android.util.Log.d(
                        "CHANGE_PASSWORD",
                        "Message: $message"
                    )

                    Handler(Looper.getMainLooper()).post {

                        callback(success, message)

                    }

                } catch (e: Exception) {

                    android.util.Log.e(
                        "CHANGE_PASSWORD",
                        "Erreur parsing JSON: ${e.message}"
                    )

                    callback(false, "Erreur serveur")

                }

            }

        })

    }

}
package com.sgi3d_app.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiRetrofitInstance {

    // 🔗 URL serveur
    private const val BASE_URL =
        "http://192.168.0.101/api/"


    // 🔧 Retrofit instance
    private val retrofit: Retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // 🔥 AJOUT ICI
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    }

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .build()


    // 📄 Demandes API
    val requestApi: RequestApiService by lazy {

        retrofit.create(
            RequestApiService::class.java
        )

    }


    // 📜 Historique API
    val historiqueApi: HistoriqueApiService by lazy {

        retrofit.create(
            HistoriqueApiService::class.java
        )

    }


    // 👤 Profil API (si utilisé)
    val api: ApiService by lazy {

        retrofit.create(
            ApiService::class.java
        )

    }

    val userApi: UserApiService by lazy {

        retrofit.create(
            UserApiService::class.java
        )

    }



}
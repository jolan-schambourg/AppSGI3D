package com.sgi3d_app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OctoRetrofitInstance {

    private const val BASE_URL =
        "http://192.168.0.32/"

    val api: OctoPrintApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(OctoPrintApi::class.java)

    }

}
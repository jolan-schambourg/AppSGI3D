package com.sgi3d_app.data.remote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.sgi3d_app.data.remote.FluiddApiService

object FluiddRetrofitInstance {

    fun getApi(baseUrl: String): FluiddApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FluiddApiService::class.java)
    }
}
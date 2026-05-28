package com.sgi3d_app.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface HistoriqueApiService {

    @GET("get_historique.php")
    suspend fun getHistorique(@Header("Authorization") token: String):
            Response<HistoriqueResponse>

}
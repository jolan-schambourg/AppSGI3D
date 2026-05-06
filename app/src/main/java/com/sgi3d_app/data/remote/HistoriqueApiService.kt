package com.sgi3d_app.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface HistoriqueApiService {

    @GET("get_historique.php")
    suspend fun getHistorique():
            Response<HistoriqueResponse>

}
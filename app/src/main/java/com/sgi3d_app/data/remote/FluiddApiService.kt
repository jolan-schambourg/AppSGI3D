package com.sgi3d_app.data.remote
import retrofit2.http.GET
import com.sgi3d_app.data.remote.FluiddResponse

interface FluiddApiService {

    @GET("printer/objects/query?extruder&heater_bed")
    suspend fun getPrinterInfo(): FluiddResponse
}
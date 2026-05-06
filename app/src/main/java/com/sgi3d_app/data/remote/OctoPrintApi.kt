package com.sgi3d_app.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface OctoPrintApi {

    @GET("api/printer")
    suspend fun getPrinterStatus(
        @Header("X-Api-Key")
        apiKey: String
    ): PrinterResponse


    @GET("api/job")
    suspend fun getJobStatus(
        @Header("X-Api-Key")
        apiKey: String
    ): JobResponse


    @POST("api/job")
    suspend fun sendJobCommand(
        @Header("X-Api-Key")
        apiKey: String,
        @Body command: Map<String, String>
    )


    @POST("api/files/local/{file}")
    suspend fun startPrint(
        @Header("X-Api-Key")
        apiKey: String,
        @Path("file") fileName: String,
        @Body command: Map<String, Any>
    )


    @GET("api/settings")
    suspend fun getSettings(
        @Header("X-Api-Key")
        apiKey: String
    ): Map<String, Any>


    @GET("api/files")
    suspend fun getFiles(
        @Header("X-Api-Key")
        apiKey: String
    ): FilesResponse

}


// 🔥 Fonction déplacement (EN DEHORS interface)

fun sendMoveCommand(

    printerIp: String,
    apiKey: String,
    axis: String,
    amount: Int

) {

    Thread {

        try {

            val url =
                java.net.URL(
                    "http://$printerIp/api/printer/command"
                )

            val conn =
                url.openConnection()
                        as java.net.HttpURLConnection

            conn.requestMethod = "POST"

            conn.setRequestProperty(
                "X-Api-Key",
                apiKey
            )

            conn.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            conn.doOutput = true

            val json =

                """
                {
                  "command": "jog",
                  "axis": "$axis",
                  "amount": $amount
                }
                """

            conn.outputStream.write(
                json.toByteArray()
            )

            conn.disconnect()

        }

        catch (e: Exception) {

            e.printStackTrace()

        }

    }.start()

}
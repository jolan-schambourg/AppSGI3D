package com.sgi3d_app.data.remote

import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody



interface RequestApiService {

    // ===============================
    // 📥 Charger les demandes
    // ===============================

    @GET("get_demandes.php")
    suspend fun getDemandes(
        @Header("Authorization") token: String
    ): Response<DemandeResponse>



    // ===============================
    // ✅ ACCEPTER DEMANDE
    // ===============================

    @FormUrlEncoded
    @POST("accepter_demande.php")

    suspend fun accepterDemande(

        @Field("id")
        id: Int,

        @Field("admin_nom")
        adminNom: String,

        @Field("admin_email")
        adminEmail: String,

        @Field("commentaire")
        commentaire: String

    ): Response<SimpleResponse>



    // ===============================
    // ❌ REFUSER DEMANDE
    // ===============================

    @FormUrlEncoded
    @POST("refuser_demande.php")

    suspend fun refuserDemande(

        @Field("id")
        id: Int,

        @Field("admin_nom")
        adminNom: String,

        @Field("admin_email")
        adminEmail: String,

        @Field("commentaire")
        commentaire: String

    ): Response<SimpleResponse>

    @Multipart
    @POST("create_demande.php")
    suspend fun createDemande(
        @Header("Authorization") token: String,

        @Part file: MultipartBody.Part,

        @Part("etudiant_nom") nom: RequestBody,
        @Part("etudiant_email") email: RequestBody,
        @Part("commentaire") commentaire: RequestBody
    ): Response<SimpleResponse>
}
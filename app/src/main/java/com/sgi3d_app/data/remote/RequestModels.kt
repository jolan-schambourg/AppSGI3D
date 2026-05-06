package com.sgi3d_app.data.remote

data class ToggleUserStatusRequest(

    val id: Int,
    val actif: Int

)

data class DeleteUserRequest(

    val id: Int

)

data class SimpleResponse(

    val success: Boolean,
    val message: String?

)



data class DemandeResponse(

    val success: Boolean,

    val data: List<DemandeItem>?

)

data class DemandeItem(

    val id: Int,

    val etudiant_nom: String,

    val etudiant_email: String,

    val fichier_nom: String,

    val commentaire: String?,

    val date_creation: String,

    val status : String?

)


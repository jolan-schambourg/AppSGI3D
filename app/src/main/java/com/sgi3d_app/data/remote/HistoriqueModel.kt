package com.sgi3d_app.data.remote

data class HistoriqueItem(

    val id: Int,
    val etudiant_nom: String,
    val etudiant_email: String,
    val fichier_nom: String,
    val commentaire: String,
    val statut: String,
    val admin_nom: String,
    val commentaire_admin: String,
    val date_traitement: String

)

data class HistoriqueResponse(

    val success: Boolean,
    val data: List<HistoriqueItem>

)
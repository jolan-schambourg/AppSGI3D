package com.sgi3d_app.data

data class Demande(
    val id: Int,
    val etudiant: String,
    val fichier: String,
    val duree: String,
    val statut: String = "En attente"
)
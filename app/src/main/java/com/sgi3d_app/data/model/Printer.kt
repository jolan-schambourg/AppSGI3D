package com.sgi3d_app.data.model

data class Printer(
    val id: Int,
    val nom: String,
    val modele: String?,
    val statut: String,
    val ip: String?,
    val localisation: String?,
    val materiau: String?
)
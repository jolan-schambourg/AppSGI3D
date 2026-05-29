package com.sgi3d_app.data.model

data class PrinterUI(
    val id: Int,
    val nom: String,
    val statut: String,

    val nozzleTemp: String = "--",
    val bedTemp: String = "--",

    val progress: Float = 0f,
    val timeRemaining: String = "-"
)
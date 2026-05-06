package com.sgi3d_app.data.remote

// ===============================
// 🖨 Réponse API OctoPrint /api/printer
// ===============================

data class PrinterResponse(

    val temperature: PrinterTemperature,

    val state: PrinterState

)

data class PrinterTemperature(

    val tool0: Tool0,

    val bed: BedTemp

)

data class Tool0(

    val actual: Double

)

data class BedTemp(

    val actual: Double

)

data class PrinterState(

    val text: String

)
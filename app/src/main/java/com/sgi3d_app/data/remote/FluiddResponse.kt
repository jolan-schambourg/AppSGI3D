package com.sgi3d_app.data.remote

data class FluiddResponse(
    val result: FluiddResult
)

data class FluiddResult(
    val status: FluiddStatus
)

data class FluiddStatus(
    val extruder: Extruder,
    val heater_bed: HeaterBed,
    val print_stats: PrintStats
)

data class Extruder(
    val temperature: Double
)

data class HeaterBed(
    val temperature: Double
)

data class PrintStats(
    val state: String
)
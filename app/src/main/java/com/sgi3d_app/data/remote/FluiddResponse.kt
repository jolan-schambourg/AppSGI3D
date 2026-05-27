data class FluiddResponse(
    val result: ResultData
)

data class ResultData(
    val status: StatusData
)

data class StatusData(
    val extruder: Extruder,
    val heater_bed: HeaterBed
)

data class Extruder(val temperature: Double)
data class HeaterBed(val temperature: Double)
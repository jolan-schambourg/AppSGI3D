package com.sgi3d_app.ui.dashboard

import androidx.compose.runtime.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import android.content.Context

import kotlinx.coroutines.launch

import android.util.Log
import com.sgi3d_app.data.model.Printer

import com.sgi3d_app.data.remote.OctoRetrofitInstance
import com.sgi3d_app.data.remote.OctoFile

import com.sgi3d_app.network.ApiClient

import com.sgi3d_app.ui.utils.cancelPrintNotification
import com.sgi3d_app.ui.utils.showFinishedNotification
import com.sgi3d_app.ui.utils.showPrintNotification

import com.sgi3d_app.data.remote.FluiddRetrofitInstance
import com.sgi3d_app.data.remote.FluiddResponse


class PrinterViewModel : ViewModel() {

    // ===============================
    // OctoPrint
    // ===============================

    data class PrinterDynamic(
        val nozzleTemp: String = "--",
        val bedTemp: String = "--",
        val status: String = "Chargement...",
        val progress: Float = 0f,
        val timeRemaining: String = "-"
    )
    data class PrinterState(
        val nozzleTemp: String = "--",
        val bedTemp: String = "--",
        val status: String = "Chargement...",
        val progress: Float = 0f,
        val timeRemaining: String = "-"
    )

    var printersDynamic by mutableStateOf<Map<String, PrinterDynamic>>(emptyMap())

    var printerStates by mutableStateOf<Map<Int, PrinterState>>(emptyMap())
        private set

    var printerName by mutableStateOf("Chargement...")
        private set

    var octoFiles by mutableStateOf<List<OctoFile>>(emptyList())
        private set

    var timeLeftSeconds by mutableStateOf(0)
        private set


    // ===============================
    // BDD
    // ===============================

    var printerModel by mutableStateOf("Chargement...")
        private set

    var printerLocation by mutableStateOf("Chargement...")
        private set

    var printerMaterial by mutableStateOf("Chargement...")
        private set

    var printerDescription by mutableStateOf("Aucune description")
        private set


    private var lastStatusNotified: String = ""

    var printers by mutableStateOf<List<Printer>>(emptyList())
        private set

    fun fetchPrinters(token: String) {
        ApiClient.getPrinters(token) { array ->
            if (array != null) {
                val list = mutableListOf<Printer>()

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)

                    list.add(
                        Printer(
                            id = obj.getInt("id"),
                            nom = obj.getString("nom"),
                            modele = obj.optString("modele"),
                            statut = obj.getString("statut"),
                            ip = obj.optString("ip"),
                            api_key = obj.optString("api_key"),
                            localisation = obj.optString("localisation"),
                            materiau = obj.optString("materiau"),
                            description = obj.optString("description"),
                            type = obj.optString("type", "octoprint")
                        )
                    )
                }

                printers = list
            }
        }
    }

    fun startRealtimeMonitoring(apiKey: String, printerName: String, context: Context) {
        viewModelScope.launch {

            var lastStatus = ""
            var lastProgress = -1

            while (true) {
                try {
                    val response = OctoRetrofitInstance.api.getJobStatus(apiKey)

                    val status = response.state
                    val progress = ((response.progress.completion ?: 0.0) * 100).toInt()
                    val timeLeft = response.progress.printTimeLeft ?: 0

                    val timeText =
                        if (timeLeft <= 0) "Indisponible"
                        else "${timeLeft / 60} min"

                    // ✅ UPDATE seulement si changement
                    if (status != lastStatus || progress != lastProgress) {

                        when {
                            status.contains("Printing", true) -> {
                                showPrintNotification(
                                    context,
                                    printerName,
                                    timeText,
                                    progress / 100f
                                )
                            }

                            status.contains("Paused", true) -> {
                                cancelPrintNotification(context)
                                showFinishedNotification(
                                    context,
                                    printerName,
                                    "Impression mise en pause"
                                )
                            }

                            status.contains("Cancelling", true) -> {
                                cancelPrintNotification(context)
                                showFinishedNotification(
                                    context,
                                    printerName,
                                    "Impression annulée"
                                )
                            }

                            status.contains("Operational", true) -> {
                                cancelPrintNotification(context)

                                val message =
                                    if (progress >= 95) "Impression terminée"
                                    else "Impression annulée"

                                showFinishedNotification(context, printerName, message)
                                break
                            }
                        }

                        // 🔁 mise à jour mémoire
                        lastStatus = status
                        lastProgress = progress
                    }

                } catch (_: Exception) {}

                kotlinx.coroutines.delay(5000)
            }
        }
    }
    fun formatTime(seconds: Int): String {

        if (seconds <= 0) {

            return "Indisponible"
        }

        val h = seconds / 3600
        val m = (seconds % 3600) / 60

        return if (h > 0) {

            "${h}h ${m}min"

        } else {

            "${m}min"
        }
    }

    // ===============================
    // FICHIERS
    // ===============================

    fun fetchFiles(apiKey: String) {

        viewModelScope.launch {

            try {

                val response =
                    OctoRetrofitInstance.api.getFiles(apiKey)

                octoFiles =
                    response.files.filter {
                        it.type == "machinecode"
                    }

            } catch (_: Exception) {

                octoFiles = emptyList()

            }

        }

    }

    // ===============================
    // TEMP
    // ===============================

    fun fetchPrinterDataDynamic(printer: Printer) {
        viewModelScope.launch {

            try {
                var nozzle = "--"
                var bed = "--"
                var statusText = "Offline"

                when (printer.type) {

                    "octoprint" -> {
                        if (!printer.api_key.isNullOrBlank()) {
                            try {
                                val response = OctoRetrofitInstance.api
                                    .getPrinterStatus(printer.api_key!!)

                                nozzle = "${response.temperature.tool0.actual}°C"
                                bed = "${response.temperature.bed.actual}°C"
                                statusText = response.state.text

                            } catch (e: Exception) {
                                statusText = "Offline"
                            }
                        } else {
                            statusText = "Clé API manquante"
                        }
                    }

                    "fluidd" -> {
                        if (!printer.ip.isNullOrBlank()) {
                            try {
                                val cleanIp = printer.ip!!.replace("http://", "")

                                val response = FluiddRetrofitInstance
                                    .getApi(cleanIp)
                                    .getPrinterInfo()

                                val data = response.result.status

                                nozzle = "${data.extruder.temperature}°C"
                                bed = "${data.heater_bed.temperature}°C"
                                statusText = data.print_stats.state

                            } catch (e: Exception) {
                                statusText = "Offline"
                            }
                        } else {
                            statusText = "IP manquante"
                        }
                    }
                }

                val current = printerStates[printer.id] ?: PrinterState()

                printerStates = printerStates.toMutableMap().apply {
                    put(
                        printer.id,
                        current.copy(
                            nozzleTemp = nozzle,
                            bedTemp = bed,
                            status = statusText
                        )
                    )
                }

            } catch (e: Exception) {
                printerStates = printerStates.toMutableMap().apply {
                    put(printer.id, PrinterState(status = "Erreur"))
                }
            }
        }
    }

    // ===============================
    // PROGRESS
    // ===============================

    fun fetchJobProgress(printer: Printer) {
        viewModelScope.launch {
            try {

                if (printer.type.lowercase() != "octoprint") {
                    // 👉 Fluidd = pas de progress OctoPrint → on ignore
                    return@launch
                }

                if (printer.api_key.isNullOrBlank() || printer.api_key == "NULL") {
                    val current = printerStates[printer.id] ?: PrinterState()
                    printerStates = printerStates.toMutableMap().apply {
                        put(printer.id, current.copy(status = "Clé API manquante"))
                    }
                    return@launch
                }

                val response = OctoRetrofitInstance.api
                    .getJobStatus(printer.api_key!!)

                val completion = response.progress.completion ?: 0.0
                val timeLeft = response.progress.printTimeLeft ?: 0

                val current = printerStates[printer.id] ?: PrinterState()

                printerStates = printerStates.toMutableMap().apply {
                    put(
                        printer.id,
                        current.copy(
                            progress = completion.toFloat(),
                            timeRemaining = formatTime(timeLeft)
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("PRINT", "Erreur progress ${printer.nom}", e)
            }
        }
    }

    // ===============================
    // INFOS BDD
    // ===============================

    fun fetchPrinterDetails(token: String) {

        ApiClient.getPrinters(token) { printersArray ->

            if (
                printersArray != null &&
                printersArray.length() > 0
            ) {

                try {

                    val printer =
                        printersArray.getJSONObject(0)

                    printerModel =
                        printer.optString("modele")

                    printerLocation =
                        printer.optString("localisation")

                    printerMaterial =
                        printer.optString("materiau")

                    printerDescription =
                        printer.optString("description")

                } catch (e: Exception) {

                    Log.e(
                        "API",
                        "Erreur parsing printer",
                        e
                    )

                }

            }

        }

    }

    // ===============================
    // COMMANDES
    // ===============================
    fun pausePrint(printer: Printer) {
        viewModelScope.launch {
            try {
                OctoRetrofitInstance.api.sendJobCommand(
                    printer.api_key!!,
                    mapOf("command" to "pause", "action" to "toggle")
                )
            } catch (e: Exception) {
                val current = printerStates[printer.id] ?: PrinterState()
                printerStates = printerStates.toMutableMap().apply {
                    put(printer.id, current.copy(status = "Erreur pause"))
                }
            }
        }
    }

    fun cancelPrint(printer: Printer) {
        viewModelScope.launch {
            try {
                OctoRetrofitInstance.api.sendJobCommand(
                    printer.api_key!!,
                    mapOf("command" to "cancel")
                )
            } catch (e: Exception) {
                val current = printerStates[printer.id] ?: PrinterState()
                printerStates = printerStates.toMutableMap().apply {
                    put(printer.id, current.copy(status = "Erreur stop"))
                }
            }
        }
    }

    fun startPrint(printer: Printer, fileName: String) {
        viewModelScope.launch {
            try {
                OctoRetrofitInstance.api.startPrint(
                    printer.api_key!!,
                    fileName,
                    mapOf("command" to "select", "print" to true)
                )
            } catch (e: Exception) {
                val current = printerStates[printer.id] ?: PrinterState()
                printerStates = printerStates.toMutableMap().apply {
                    put(printer.id, current.copy(status = "Erreur démarrage"))
                }
            }
        }
    }

    // ===============================
// 🏷 RÉCUPÉRATION NOM IMPRIMANTE
// ===============================

    fun fetchPrinterName(apiKey: String) {

        viewModelScope.launch {

            try {

                val response =
                    OctoRetrofitInstance.api.getSettings(apiKey)

                val profiles =
                    response["printerProfiles"] as Map<*, *>

                val defaultProfile =
                    profiles["default"] as Map<*, *>

                printerName =
                    defaultProfile["name"] as? String
                        ?: "Imprimante 3D"

            } catch (e: Exception) {

                printerName = "Imprimante 3D"

            }

        }

    }

}
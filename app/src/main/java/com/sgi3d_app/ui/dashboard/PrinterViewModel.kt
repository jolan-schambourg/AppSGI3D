package com.sgi3d_app.ui.dashboard

import androidx.compose.runtime.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import android.content.Context

import kotlinx.coroutines.launch

import android.util.Log

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

    var nozzleTemp by mutableStateOf("--")
        private set

    var bedTemp by mutableStateOf("--")
        private set

    var status by mutableStateOf("Chargement...")
        private set

    var progress by mutableStateOf(0f)
        private set

    var printerName by mutableStateOf("Chargement...")
        private set

    var octoFiles by mutableStateOf<List<OctoFile>>(emptyList())
        private set

    var timeLeftSeconds by mutableStateOf(0)
        private set

    var timeRemainingText by mutableStateOf("Indisponible")
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
                            description = obj.optString("description")
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

                when (printer.type) {

                    "octoprint" -> {
                        val response = OctoRetrofitInstance.api
                            .getPrinterStatus(printer.api_key!!)

                        nozzleTemp = "${response.temperature.tool0.actual}°C"
                        bedTemp = "${response.temperature.bed.actual}°C"
                        status = response.state.text
                    }

                    "fluidd" -> {
                        val response = FluiddRetrofitInstance
                            .getApi(printer.ip)
                            .getPrinterInfo()

                        nozzleTemp = "${response.extruder.temperature}°C"
                        bedTemp = "${response.heater_bed.temperature}°C"
                        status = response.state
                    }
                }

            } catch (e: Exception) {
                status = "Erreur connexion"
            }
        }
    }

    // ===============================
    // PROGRESS
    // ===============================

    fun fetchJobProgress(apiKey: String) {

        viewModelScope.launch {

            try {

                val response =
                    OctoRetrofitInstance
                        .api
                        .getJobStatus(apiKey)

                val completion =
                    response.progress.completion ?: 0.0

// ⚠️ IMPORTANT : OctoPrint donne déjà 0 → 1
                progress =
                    completion.toFloat()

                val printTimeLeftApi =
                    response.progress.printTimeLeft ?: 0

                timeLeftSeconds =

                    if (printTimeLeftApi > 0) {

                        printTimeLeftApi

                    } else {

                        0 // on affichera "Indisponible"
                    }

                timeRemainingText =
                    formatTime(timeLeftSeconds)

            } catch (_: Exception) {

                progress = 0f
                timeLeftSeconds = 0
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

    fun pausePrint(apiKey: String) {

        viewModelScope.launch {

            try {

                OctoRetrofitInstance.api.sendJobCommand(
                    apiKey,
                    mapOf(
                        "command" to "pause",
                        "action" to "toggle"
                    )
                )

            } catch (e: Exception) {

                status = "Erreur pause"

            }

        }

    }

    fun cancelPrint(apiKey: String) {

        viewModelScope.launch {

            try {

                OctoRetrofitInstance.api.sendJobCommand(
                    apiKey,
                    mapOf(
                        "command" to "cancel"
                    )
                )

            } catch (e: Exception) {

                status = "Erreur stop"

            }

        }

    }

    fun startPrint(
        apiKey: String,
        fileName: String
    ) {

        viewModelScope.launch {

            try {

                OctoRetrofitInstance.api.startPrint(
                    apiKey,
                    fileName,
                    mapOf(
                        "command" to "select",
                        "print" to true
                    )
                )

            } catch (e: Exception) {

                status = "Erreur démarrage"

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
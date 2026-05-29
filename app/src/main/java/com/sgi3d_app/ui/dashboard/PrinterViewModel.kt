package com.sgi3d_app.ui.dashboard

import androidx.compose.runtime.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import android.content.Context

import kotlinx.coroutines.launch

import android.util.Log
import com.sgi3d_app.data.model.PrinterUI

import com.sgi3d_app.data.remote.OctoRetrofitInstance
import com.sgi3d_app.data.remote.OctoFile

import com.sgi3d_app.network.ApiClient

import com.sgi3d_app.ui.utils.cancelPrintNotification
import com.sgi3d_app.ui.utils.showFinishedNotification
import com.sgi3d_app.ui.utils.showPrintNotification

class PrinterViewModel : ViewModel() {

    // ===============================
    // OctoPrint
    // ===============================

    var printersData by mutableStateOf<Map<String, PrinterUI>>(emptyMap())
        private set

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

    var timeRemaining by mutableStateOf("-")
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

    fun startRealtimeMonitoring(apiKey: String, printerName: String, context: Context) {
        viewModelScope.launch {

            var lastStatus = ""
            var lastProgress = -1

            while (true) {
                try {
                    val response = OctoRetrofitInstance.api.getJobStatus(apiKey)

                    val seconds = response.progress.printTimeLeft
                    progress = response.progress.completion?.toFloat() ?: 0f

                    timeRemaining = if (seconds != null) {
                        val minutes = seconds / 60
                        val hours = minutes / 60
                        "${hours}h ${minutes % 60}min"
                    } else {
                        "-"
                    }

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

    fun fetchPrinterData(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = OctoRetrofitInstance.api.getPrinterStatus(apiKey)

                val existing = printersData[apiKey]

                val newData = PrinterUI(
                    id = existing?.id ?: 0,
                    nom = existing?.nom ?: "",
                    statut = response.state.text,
                    nozzleTemp = "${response.temperature.tool0.actual}°C",
                    bedTemp = "${response.temperature.bed.actual}°C",
                    progress = existing?.progress ?: 0f,
                    timeRemaining = existing?.timeRemaining ?: "-"
                )

                printersData = printersData.toMutableMap().apply {
                    put(apiKey, newData)
                }

            } catch (_: Exception) {}
        }
    }

    // ===============================
    // PROGRESS
    // ===============================

    fun fetchJobProgress(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = OctoRetrofitInstance.api.getJobStatus(apiKey)

                val existing = printersData[apiKey]

                val completion = response.progress.completion ?: 0.0
                val timeLeft = response.progress.printTimeLeft ?: 0

                val updated = PrinterUI(
                    id = existing?.id ?: 0,
                    nom = existing?.nom ?: "",
                    statut = existing?.statut ?: "Inconnu",
                    nozzleTemp = existing?.nozzleTemp ?: "--",
                    bedTemp = existing?.bedTemp ?: "--",
                    progress = completion.toFloat(),
                    timeRemaining = formatTime(timeLeft)
                )

                printersData = printersData.toMutableMap().apply {
                    put(apiKey, updated)
                }

            } catch (_: Exception) {}
        }
    }

    fun fetchPrinterDetails(token: String, printerId: Int) {
        ApiClient.getPrinters(token) { printersArray ->
            if (printersArray != null) {
                try {
                    for (i in 0 until printersArray.length()) {
                        val printer = printersArray.getJSONObject(i)

                        if (printer.getInt("id") == printerId) {

                            val updated = printersData[printer.getString("api_key")]

                            printersData = printersData.toMutableMap().apply {
                                put(
                                    printer.getString("api_key"),
                                    updated?.copy(
                                        nom = printer.optString("nom"),
                                        statut = printer.optString("statut")
                                    ) ?: PrinterUI(
                                        id = printerId,
                                        nom = printer.optString("nom"),
                                        statut = printer.optString("statut"),
                                        nozzleTemp = "--",
                                        bedTemp = "--",
                                        progress = 0f,
                                        timeRemaining = "-"
                                    )
                                )
                            }

                            break
                        }
                    }
                } catch (e: Exception) {
                    Log.e("API", "Erreur parsing printer", e)
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
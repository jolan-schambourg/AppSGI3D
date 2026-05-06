package com.sgi3d_app.ui.dashboard

import android.webkit.WebView
import android.webkit.WebViewClient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext


@Composable
fun PrinterCard(
    printerName: String,
    status: String,
    temperature: String,
    timeRemaining: String,
    progress: Float,
    printerIp: String,
    apiKey: String,
    onPause: () -> Unit,
    onCancel: () -> Unit,
    onMoreInfo: () -> Unit,
    onControlClick: (String, String) -> Unit,
    canControl: Boolean = true
) {



    var lastStatus by remember { mutableStateOf("") }
    var lastRealStatus by remember { mutableStateOf("") }
    var showCamera by remember { mutableStateOf(false) }

    var lastProgress by remember { mutableStateOf(-1) }

    val context = LocalContext.current

    // États
    val isPrinting =
        status.contains("Printing", true)

    val isPaused =
        status.contains("Paused", true)

    val showControls =
        isPrinting || isPaused

    // Sécurité progression
    val safeProgress =
        progress.coerceIn(0f, 1f)


    // Couleur statut
    val statusColor = when {

        isPrinting ->
            MaterialTheme.colorScheme.primary

        isPaused ->
            MaterialTheme.colorScheme.tertiary

        status.equals("Operational", true) ->
            MaterialTheme.colorScheme.secondary

        else ->
            MaterialTheme.colorScheme.error
    }



    // ===============================
    // UI
    // ===============================

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // Header

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    printerName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor
                ) {

                    Text(
                        text = status,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(temperature)

            Spacer(modifier = Modifier.height(12.dp))

            // ===============================
            // PROGRESSION
            // ===============================

            if (showControls && canControl) {

                LinearProgressIndicator(
                    progress = safeProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "${(safeProgress * 100).toInt()} % terminé"
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "Temps restant : $timeRemaining"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Button(
                        onClick = onPause,
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            if (isPaused)
                                "Reprendre"
                            else
                                "Pause"
                        )
                    }

                    Button(
                        onClick = onCancel,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    MaterialTheme.colorScheme.error
                            ),
                        modifier = Modifier.weight(1f)
                    ) {

                        Text("Stop")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


            }

            Spacer(modifier = Modifier.height(12.dp))

            // Caméra + Contrôle

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    onClick = {
                        showCamera = !showCamera
                    },
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        if (showCamera)
                            "Fermer caméra"
                        else
                            "Voir caméra"
                    )
                }

                if (canControl) {
                    OutlinedButton(
                        onClick = { onControlClick(printerIp, apiKey) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Contrôler")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onMoreInfo,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("En savoir plus")
            }

            // Caméra

            if (showCamera) {

                Spacer(modifier = Modifier.height(16.dp))

                AndroidView(

                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f),

                    factory = { context ->

                        WebView(context).apply {

                            webViewClient =
                                WebViewClient()

                            settings.javaScriptEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true

                            loadUrl(
                                "http://192.168.0.101:5000/webapi/entry.cgi?api=SYNO.SurveillanceStation.Stream.VideoStreaming&version=1&method=Stream&format=mjpeg&cameraId=3&StmKey=710a8877fd4e2a17364b49be2d0e82aa"
                            )
                        }
                    }
                )
            }
        }
    }
}
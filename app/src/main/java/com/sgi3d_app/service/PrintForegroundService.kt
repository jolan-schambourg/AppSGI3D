package com.sgi3d_app.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import com.sgi3d_app.data.remote.OctoRetrofitInstance
import com.sgi3d_app.ui.utils.buildStateNotification

class PrintForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val apiKey = intent?.getStringExtra("apiKey") ?: return START_NOT_STICKY
        val printerName = intent.getStringExtra("printerName") ?: "Imprimante 3D"

        // ✅ DÉMARRAGE FOREGROUND UNE SEULE FOIS
        startForeground(
            1,
            buildStateNotification(
                applicationContext,
                printerName,
                "PRINTING",
                "Initialisation...",
                0f
            )
        )

        serviceScope.launch {

            var lastStatus = ""
            var lastProgress = -1

            while (true) {
                try {
                    val response = OctoRetrofitInstance.api.getJobStatus(apiKey)

                    val status = response.state ?: ""

                    val completion = response.progress.completion ?: 0.0
                    val progressInt = completion.toInt()
                    val progressFloat = (completion / 100.0).toFloat()

                    val timeLeft = response.progress.printTimeLeft
                    val timeText =
                        if (timeLeft == null || timeLeft <= 0) "Calcul..."
                        else "${timeLeft / 60} min"

                    if (status != lastStatus || progressInt != lastProgress) {

                        when {

                            // 🖨️ IMPRESSION
                            status.equals("Printing", true) -> {
                                NotificationManagerCompat.from(applicationContext)
                                    .notify(
                                        1,
                                        buildStateNotification(
                                            applicationContext,
                                            printerName,
                                            "PRINTING",
                                            timeText,
                                            progressFloat
                                        )
                                    )
                            }

                            // ⏸️ PAUSE
                            status.equals("Paused", true) -> {
                                NotificationManagerCompat.from(applicationContext)
                                    .notify(
                                        1,
                                        buildStateNotification(
                                            applicationContext,
                                            printerName,
                                            "PAUSED",
                                            timeText,
                                            progressFloat
                                        )
                                    )
                            }

                            // ❌ ANNULATION
                            status.equals("Cancelling", true) -> {
                                NotificationManagerCompat.from(applicationContext)
                                    .notify(
                                        1,
                                        buildStateNotification(
                                            applicationContext,
                                            printerName,
                                            "CANCELLED",
                                            timeText,
                                            progressFloat
                                        )
                                    )
                            }

                            // ✅ FIN
                            status.equals("Operational", true) &&
                                    (lastStatus.equals("Printing", true)
                                            || lastStatus.equals("Paused", true)
                                            || lastStatus.equals("Cancelling", true)) -> {

                                val finalStatus =
                                    if (lastProgress >= 95) "DONE" else "CANCELLED"

                                NotificationManagerCompat.from(applicationContext)
                                    .notify(
                                        1,
                                        buildStateNotification(
                                            applicationContext,
                                            printerName,
                                            finalStatus,
                                            timeText,
                                            progressFloat
                                        )
                                    )

                                delay(3000)
                                stopSelf()
                                break
                            }
                        }

                        lastStatus = status
                        lastProgress = progressInt
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                delay(5000)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
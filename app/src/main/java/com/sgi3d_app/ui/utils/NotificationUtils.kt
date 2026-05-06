package com.sgi3d_app.ui.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun showPrintNotification(
    context: Context,
    printerName: String,
    timeRemaining: String,
    progress: Float
) {

    val channelId = "print_channel"

    val manager =
        context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

    val channel = NotificationChannel(
        channelId,
        "Impressions 3D",
        NotificationManager.IMPORTANCE_HIGH
    )

    manager.createNotificationChannel(channel)

    // ✅ Convertir progress (0→1) en %
    val progressPercent =
        (progress * 100).toInt()
            .coerceIn(0, 100)

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle("🖨️ Impression en cours")
        .setContentText("$printerName - Temps restant: $timeRemaining")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setProgress(100, progressPercent, false)
        .setOnlyAlertOnce(true)
        .setOngoing(true) // ✅ UNIQUEMENT ICI
        .build()

    NotificationManagerCompat
        .from(context)
        .notify(
            1001,
            notification
        )
}

fun cancelPrintNotification(context: Context) {

    NotificationManagerCompat
        .from(context)
        .cancel(1001)
}

fun showFinishedNotification(
    context: Context,
    title: String,
    message: String
) {
    val channelId = "foreground_channel"

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel = NotificationChannel(
        channelId,
        "Impressions 3D",
        NotificationManager.IMPORTANCE_HIGH
    )
    manager.createNotificationChannel(channel)

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setAutoCancel(true) // ✅ disparaît quand cliqué
        .build()

    NotificationManagerCompat.from(context).notify(1002, notification)
}

fun buildPrintNotification(
    context: Context,
    printerName: String,
    timeRemaining: String,
    progress: Float
): Notification {

    val channelId = "print_channel"

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(
        channelId,
        "Impressions 3D",
        NotificationManager.IMPORTANCE_HIGH
    )
    manager.createNotificationChannel(channel)

    val progressPercent = (progress * 100).toInt().coerceIn(0, 100)

    return NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle("🖨️ Impression en cours")
        .setContentText("$printerName - Temps restant: $timeRemaining")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setProgress(100, progressPercent, false)
        .setOnlyAlertOnce(true)
        .setOngoing(true)
        .build()
}

fun buildStateNotification(
    context: Context,
    printerName: String,
    status: String,
    timeRemaining: String,
    progress: Float
): Notification {

    val channelId = "print_channel"

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(
        channelId,
        "Impressions 3D",
        NotificationManager.IMPORTANCE_HIGH
    )
    manager.createNotificationChannel(channel)

    val progressPercent = (progress * 100).toInt().coerceIn(0, 100)

    val (title, text, ongoing) = when (status) {

        "PRINTING" -> Triple(
            "🖨️ Impression en cours",
            "$printerName - Temps restant: $timeRemaining",
            true
        )

        "PAUSED" -> Triple(
            "⏸️ Impression en pause",
            printerName,
            false
        )

        "CANCELLED" -> Triple(
            "❌ Impression annulée",
            printerName,
            false
        )

        "DONE" -> Triple(
            "✅ Impression terminée",
            printerName,
            false
        )

        else -> Triple(
            "Imprimante",
            printerName,
            false
        )
    }

    return NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setCategory(Notification.CATEGORY_SERVICE) // 🔥 IMPORTANT
        .setProgress(
            if (status == "PRINTING") 100 else 0,
            progressPercent,
            false
        )
        .setOnlyAlertOnce(true)
        .setOngoing(ongoing)
        .setAutoCancel(!ongoing)
        .build()
}

fun showSimpleNotification(
    context: Context,
    title: String,
    message: String,
    id: Int
) {
    val channelId = "general_channel"

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel = NotificationChannel(
        channelId,
        "Notifications générales",
        NotificationManager.IMPORTANCE_HIGH
    )
    manager.createNotificationChannel(channel)

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_notify_more)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(id, notification)
}
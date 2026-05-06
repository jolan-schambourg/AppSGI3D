package com.sgi3d_app.data.remote

data class SettingsResponse(
    val printerProfiles: PrinterProfiles
)

data class PrinterProfiles(
    val default: DefaultProfile
)

data class DefaultProfile(
    val name: String
)
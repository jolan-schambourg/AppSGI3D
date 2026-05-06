package com.sgi3d_app.data.remote

data class JobResponse(
    val progress: Progress,
    val state: String
)

data class Progress(
    val completion: Double?,
    val printTimeLeft: Int?,
    val printTime: Int?
)
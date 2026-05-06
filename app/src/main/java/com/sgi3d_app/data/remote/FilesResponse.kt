package com.sgi3d_app.data.remote

data class FilesResponse(
    val files: List<OctoFile>
)

data class OctoFile(
    val name: String,
    val type: String
)
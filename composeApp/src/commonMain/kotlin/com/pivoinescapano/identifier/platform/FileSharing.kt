package com.pivoinescapano.identifier.platform

expect class FileSharing {
    suspend fun shareFile(
        fileName: String,
        content: String,
        mimeType: String,
    ): Result<Unit>

    suspend fun saveFile(
        fileName: String,
        content: String,
        directory: String,
    ): Result<String>

    suspend fun pickFile(mimeType: String): Result<String>
}

expect fun provideFileSharing(): FileSharing

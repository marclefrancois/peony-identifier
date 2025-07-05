package com.pivoinescapano.identifier.data.storage

expect class FileSystemStorage {
    suspend fun writeFile(
        fileName: String,
        content: String,
    ): Result<Unit>

    suspend fun readFile(fileName: String): Result<String>

    suspend fun deleteFile(fileName: String): Result<Unit>

    suspend fun fileExists(fileName: String): Boolean

    suspend fun createDirectory(directoryName: String): Result<Unit>
}

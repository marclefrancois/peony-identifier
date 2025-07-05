package com.pivoinescapano.identifier.data.storage

import android.content.Context
import java.io.File
import java.io.IOException

actual class FileSystemStorage(private val context: Context) {
    private val appDirectory = File(context.filesDir, "PeonyIdentifier")

    init {
        if (!appDirectory.exists()) {
            appDirectory.mkdirs()
        }
    }

    actual suspend fun writeFile(
        fileName: String,
        content: String,
    ): Result<Unit> {
        return try {
            val file = File(appDirectory, fileName)
            file.writeText(content)
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    actual suspend fun readFile(fileName: String): Result<String> {
        return try {
            val file = File(appDirectory, fileName)
            if (file.exists()) {
                Result.success(file.readText())
            } else {
                Result.failure(IOException("File not found: $fileName"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    actual suspend fun deleteFile(fileName: String): Result<Unit> {
        return try {
            val file = File(appDirectory, fileName)
            if (file.exists()) {
                file.delete()
            }
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    actual suspend fun fileExists(fileName: String): Boolean {
        val file = File(appDirectory, fileName)
        return file.exists()
    }

    actual suspend fun createDirectory(directoryName: String): Result<Unit> {
        return try {
            val directory = File(appDirectory, directoryName)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}

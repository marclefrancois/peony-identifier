package com.pivoinescapano.identifier.data.storage

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile

actual class FileSystemStorage {
    @OptIn(ExperimentalForeignApi::class)
    private val documentsDirectory: String by lazy {
        val documentPaths =
            NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true,
            )
        val documentsPath = documentPaths.first() as NSString
        "$documentsPath/PeonyIdentifier"
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun ensureAppDirectoryExists() {
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(documentsDirectory)) {
            fileManager.createDirectoryAtPath(
                documentsDirectory,
                withIntermediateDirectories = true,
                attributes = null,
                error = null,
            )
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun writeFile(
        fileName: String,
        content: String,
    ): Result<Unit> {
        return try {
            ensureAppDirectoryExists()
            val filePath = "$documentsDirectory/$fileName"
            val nsString = NSString.create(string = content)
            val success =
                nsString.writeToFile(
                    filePath,
                    atomically = true,
                    encoding = NSUTF8StringEncoding,
                    error = null,
                )
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to write file: $fileName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun readFile(fileName: String): Result<String> {
        return try {
            val filePath = "$documentsDirectory/$fileName"
            val fileManager = NSFileManager.defaultManager

            if (fileManager.fileExistsAtPath(filePath)) {
                val content =
                    NSString.stringWithContentsOfFile(
                        filePath,
                        encoding = NSUTF8StringEncoding,
                        error = null,
                    )
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Failed to read file: $fileName"))
                }
            } else {
                Result.failure(Exception("File not found: $fileName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun deleteFile(fileName: String): Result<Unit> {
        return try {
            val filePath = "$documentsDirectory/$fileName"
            val fileManager = NSFileManager.defaultManager

            if (fileManager.fileExistsAtPath(filePath)) {
                fileManager.removeItemAtPath(filePath, error = null)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun fileExists(fileName: String): Boolean {
        val filePath = "$documentsDirectory/$fileName"
        return NSFileManager.defaultManager.fileExistsAtPath(filePath)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun createDirectory(directoryName: String): Result<Unit> {
        return try {
            val directoryPath = "$documentsDirectory/$directoryName"
            val fileManager = NSFileManager.defaultManager

            if (!fileManager.fileExistsAtPath(directoryPath)) {
                fileManager.createDirectoryAtPath(
                    directoryPath,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null,
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

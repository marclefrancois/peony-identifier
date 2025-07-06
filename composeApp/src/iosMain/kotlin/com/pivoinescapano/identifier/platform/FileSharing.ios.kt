package com.pivoinescapano.identifier.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIModalPresentationPageSheet

actual fun provideFileSharing(): FileSharing = FileSharing()

@OptIn(ExperimentalForeignApi::class)
actual class FileSharing {
    actual suspend fun shareFile(
        fileName: String,
        content: String,
        mimeType: String,
    ): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                // Use temporary directory for sharing files - iOS has fewer restrictions on sharing temp files
                val tempDirectory = NSTemporaryDirectory()
                val tempURL = platform.Foundation.NSURL.fileURLWithPath(tempDirectory)
                val fileURL = tempURL.URLByAppendingPathComponent(fileName)

                val nsString = NSString.create(string = content)
                val success =
                    nsString.writeToFile(
                        fileURL?.path ?: "",
                        atomically = true,
                        encoding = NSUTF8StringEncoding,
                        error = null,
                    )

                if (success && fileURL != null) {
                    val activityViewController =
                        UIActivityViewController(
                            activityItems = listOf(fileURL),
                            applicationActivities = null,
                        )

                    // Set presentation style for iPad compatibility
                    activityViewController.modalPresentationStyle = UIModalPresentationPageSheet

                    // Try multiple ways to get the root view controller for iOS 13+ compatibility
                    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController

                    if (rootViewController != null) {
                        rootViewController.presentViewController(
                            activityViewController,
                            animated = true,
                            completion = null,
                        )
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Could not find root view controller to present sharing dialog"))
                    }
                } else {
                    val errorMessage =
                        when {
                            !success -> "Failed to write file to ${fileURL?.path ?: "unknown path"}"
                            fileURL == null -> "Failed to create file URL"
                            else -> "Unknown error occurred"
                        }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    actual suspend fun saveFile(
        fileName: String,
        content: String,
        directory: String,
    ): Result<String> {
        return withContext(Dispatchers.Default) {
            try {
                val documentsDirectory =
                    NSFileManager.defaultManager.URLsForDirectory(
                        NSDocumentDirectory,
                        NSUserDomainMask,
                    ).first() as platform.Foundation.NSURL

                val peonyDir = documentsDirectory.URLByAppendingPathComponent("PeonyIdentifier")
                val exportDir = peonyDir?.URLByAppendingPathComponent(directory)

                // Create directories if they don't exist
                val fileManager = NSFileManager.defaultManager
                exportDir?.path?.let { path ->
                    fileManager.createDirectoryAtPath(
                        path,
                        withIntermediateDirectories = true,
                        attributes = null,
                        error = null,
                    )
                }

                val fileURL = exportDir?.URLByAppendingPathComponent(fileName)
                val nsString = NSString.create(string = content)
                val success =
                    nsString.writeToFile(
                        fileURL?.path ?: "",
                        atomically = true,
                        encoding = NSUTF8StringEncoding,
                        error = null,
                    )

                if (success) {
                    Result.success(fileURL?.path ?: "")
                } else {
                    Result.failure(Exception("Failed to save file"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    actual suspend fun pickFile(mimeType: String): Result<String> {
        return withContext(Dispatchers.Main) {
            try {
                // For now, return failure as file picking requires UIDocumentPickerViewController
                // This would need to be implemented with proper delegate handling
                Result.failure(Exception("File picking not implemented yet"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

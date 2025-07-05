package com.pivoinescapano.identifier.platform

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileWriter

actual fun provideFileSharing(): FileSharing = FileSharing()

actual class FileSharing : KoinComponent {
    private val context: Context by inject()

    actual suspend fun shareFile(
        fileName: String,
        content: String,
        mimeType: String,
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.externalCacheDir, fileName)
                FileWriter(file).use { writer ->
                    writer.write(content)
                }

                val uri =
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file,
                    )

                val shareIntent =
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = mimeType
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                val chooserIntent = Intent.createChooser(shareIntent, "Export Field Notes")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)

                Result.success(Unit)
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
        return withContext(Dispatchers.IO) {
            try {
                val documentsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PeonyIdentifier")
                val exportDir = File(documentsDir, directory)

                if (!exportDir.exists()) {
                    exportDir.mkdirs()
                }

                val file = File(exportDir, fileName)
                FileWriter(file).use { writer ->
                    writer.write(content)
                }

                Result.success(file.absolutePath)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    actual suspend fun pickFile(mimeType: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // For now, return failure as file picking requires activity result handling
                // This would need to be implemented with activity result contracts
                Result.failure(Exception("File picking not implemented yet"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

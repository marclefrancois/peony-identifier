package com.pivoinescapano.identifier.domain.service

import com.pivoinescapano.identifier.data.repository.FieldRepository
import com.pivoinescapano.identifier.domain.model.ExportFilter
import com.pivoinescapano.identifier.domain.model.ExportFormat
import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.repository.FieldNotesRepository
import com.pivoinescapano.identifier.platform.FileSharing
import com.pivoinescapano.identifier.platform.currentTimeMillis
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json

class ExportService(
    private val fieldNotesRepository: FieldNotesRepository,
    private val fieldRepository: FieldRepository,
    private val fileSharing: FileSharing,
) {
    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }

    suspend fun exportNotes(filter: ExportFilter): Result<String> {
        return try {
            val allNotes = fieldNotesRepository.getAllNotes().getOrThrow()
            val filteredNotes = applyFilter(allNotes, filter)

            val content =
                when (filter.format) {
                    ExportFormat.CSV -> generateCSV(filteredNotes)
                    ExportFormat.JSON -> generateJSON(filteredNotes)
                }

            val timestamp = getCurrentTimestamp()
            val extension = if (filter.format == ExportFormat.CSV) "csv" else "json"
            val fileName = "field-notes-export-$timestamp.$extension"

            // Save to exports directory
            val savedPath = fileSharing.saveFile(fileName, content, "exports").getOrThrow()

            Result.success(savedPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun shareNotes(filter: ExportFilter): Result<Unit> {
        return try {
            val allNotes = fieldNotesRepository.getAllNotes().getOrThrow()
            val filteredNotes = applyFilter(allNotes, filter)

            val content =
                when (filter.format) {
                    ExportFormat.CSV -> generateCSV(filteredNotes)
                    ExportFormat.JSON -> generateJSON(filteredNotes)
                }

            val timestamp = getCurrentTimestamp()
            val extension = if (filter.format == ExportFormat.CSV) "csv" else "json"
            val fileName = "field-notes-export-$timestamp.$extension"
            val mimeType = if (filter.format == ExportFormat.CSV) "text/csv" else "application/json"

            fileSharing.shareFile(fileName, content, mimeType)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBackup(): Result<String> {
        return try {
            val allNotes = fieldNotesRepository.getAllNotes().getOrThrow()
            val backup =
                BackupData(
                    notes = allNotes,
                    exportDate = currentTimeMillis(),
                    version = "1.0",
                )

            val content = json.encodeToString(backup)
            val timestamp = getCurrentTimestamp()
            val fileName = "field-notes-backup-$timestamp.json"

            val savedPath = fileSharing.saveFile(fileName, content, "exports").getOrThrow()
            Result.success(savedPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importFromBackup(replaceExisting: Boolean = false): Result<ImportResult> {
        return try {
            val content = fileSharing.pickFile("application/json").getOrThrow()
            val backup = json.decodeFromString<BackupData>(content)

            if (replaceExisting) {
                fieldNotesRepository.clearAllNotes().getOrThrow()
            }

            var importedCount = 0
            var skippedCount = 0

            backup.notes.forEach { note ->
                if (replaceExisting) {
                    fieldNotesRepository.createNote(note).getOrNull()?.let { importedCount++ }
                } else {
                    val existingNote = fieldNotesRepository.getNote(note.id).getOrNull()
                    if (existingNote == null) {
                        fieldNotesRepository.createNote(note).getOrNull()?.let { importedCount++ }
                    } else {
                        skippedCount++
                    }
                }
            }

            Result.success(ImportResult(importedCount, skippedCount, backup.notes.size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importFromJSON(
        jsonContent: String,
        replaceExisting: Boolean = false,
    ): Result<ImportResult> {
        return try {
            val notes = json.decodeFromString<List<FieldNote>>(jsonContent)

            if (replaceExisting) {
                fieldNotesRepository.clearAllNotes().getOrThrow()
            }

            var importedCount = 0
            var skippedCount = 0

            notes.forEach { note ->
                if (replaceExisting) {
                    fieldNotesRepository.createNote(note).getOrNull()?.let { importedCount++ }
                } else {
                    val existingNote = fieldNotesRepository.getNote(note.id).getOrNull()
                    if (existingNote == null) {
                        fieldNotesRepository.createNote(note).getOrNull()?.let { importedCount++ }
                    } else {
                        skippedCount++
                    }
                }
            }

            Result.success(ImportResult(importedCount, skippedCount, notes.size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun applyFilter(
        notes: List<FieldNote>,
        filter: ExportFilter,
    ): List<FieldNote> {
        var filtered = notes

        // Filter by fields
        if (filter.fields.isNotEmpty()) {
            filtered =
                filtered.filter { note ->
                    filter.fields.contains("${note.champ}-${note.parcelle}")
                }
        }

        // Filter by status
        if (filter.statuses.isNotEmpty()) {
            filtered =
                filtered.filter { note ->
                    filter.statuses.contains(note.status)
                }
        }

        // Filter by date range
        filter.dateRange?.let { dateRange ->
            filtered =
                filtered.filter { note ->
                    note.timestamp >= dateRange.startDate && note.timestamp <= dateRange.endDate
                }
        }

        // Filter empty notes
        if (!filter.includeEmptyNotes) {
            filtered =
                filtered.filter { note ->
                    note.notes.isNotBlank()
                }
        }

        return filtered
    }

    private suspend fun generateCSV(notes: List<FieldNote>): String {
        val csvHeader = "Field,Parcel,Row,Position,In our notes,Confirmed in the field,Notes,Status,Created,Modified"

        // Get all field entries for lookup
        val allFieldEntries = fieldRepository.getAllFieldEntries()
        val fieldEntryMap =
            allFieldEntries.associateBy { entry ->
                "${entry.champ ?: ""}-${entry.parcel ?: ""}-${entry.rang ?: ""}-${entry.trou ?: ""}"
            }

        val csvRows =
            notes.map { note ->
                val positionKey = "${note.champ}-${note.parcelle}-${note.rang}-${note.trou}"
                val fieldEntry = fieldEntryMap[positionKey]

                val inOurNotes = escapeCSV(fieldEntry?.variety ?: "")
                val confirmedInField = escapeCSV(note.variety ?: "")
                val notesContent = escapeCSV(note.notes)
                val createdDate = formatDate(note.timestamp)
                val modifiedDate = formatDate(note.lastModified)

                "${note.champ},${note.parcelle},${note.rang},${note.trou},$inOurNotes,$confirmedInField,$notesContent,${note.status},$createdDate,$modifiedDate"
            }

        return (listOf(csvHeader) + csvRows).joinToString("\n")
    }

    private fun generateJSON(notes: List<FieldNote>): String {
        return json.encodeToString(notes)
    }

    private fun escapeCSV(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun formatDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.date} ${localDateTime.time}"
    }

    private fun getCurrentTimestamp(): String {
        val instant = Clock.System.now()
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.date}-${localDateTime.hour}${localDateTime.minute}${localDateTime.second}"
    }

    @kotlinx.serialization.Serializable
    private data class BackupData(
        val notes: List<FieldNote>,
        val exportDate: Long,
        val version: String,
    )

    data class ImportResult(
        val importedCount: Int,
        val skippedCount: Int,
        val totalCount: Int,
    )
}

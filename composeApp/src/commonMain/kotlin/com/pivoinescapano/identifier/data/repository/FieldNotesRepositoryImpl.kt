package com.pivoinescapano.identifier.data.repository

import com.pivoinescapano.identifier.data.storage.FileSystemStorage
import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.domain.repository.FieldNotesRepository
import com.pivoinescapano.identifier.platform.currentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FieldNotesRepositoryImpl(
    private val fileSystemStorage: FileSystemStorage,
) : FieldNotesRepository {
    private val mutex = Mutex()
    private val notesFlow = MutableStateFlow<List<FieldNote>>(emptyList())
    private val fileName = "field-notes.json"
    private val backupFileName = "field-notes-backup.json"
    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = true
        }

    init {
        repositoryScope.launch {
            loadNotesFromFile()
        }
    }

    private suspend fun loadNotesFromFile() {
        mutex.withLock {
            try {
                val content = fileSystemStorage.readFile(fileName).getOrNull()
                if (content != null) {
                    val notes = json.decodeFromString<List<FieldNote>>(content)
                    notesFlow.value = notes
                } else {
                    notesFlow.value = emptyList()
                }
            } catch (e: Exception) {
                println("Error loading field notes: ${e.message}")
                notesFlow.value = emptyList()
            }
        }
    }

    private suspend fun saveNotesToFile(notes: List<FieldNote>) {
        try {
            val content = json.encodeToString(notes)
            fileSystemStorage.writeFile(fileName, content)
            fileSystemStorage.writeFile(backupFileName, content)
        } catch (e: Exception) {
            println("Error saving field notes: ${e.message}")
            throw e
        }
    }

    override suspend fun createNote(fieldNote: FieldNote): Result<FieldNote> {
        return try {
            mutex.withLock {
                val currentNotes = notesFlow.value.toMutableList()
                val existingIndex =
                    currentNotes.indexOfFirst {
                        it.champ == fieldNote.champ &&
                            it.parcelle == fieldNote.parcelle &&
                            it.rang == fieldNote.rang &&
                            it.trou == fieldNote.trou
                    }

                if (existingIndex >= 0) {
                    currentNotes[existingIndex] = fieldNote.copy(lastModified = currentTimeMillis())
                } else {
                    currentNotes.add(fieldNote)
                }

                saveNotesToFile(currentNotes)
                notesFlow.value = currentNotes
                Result.success(fieldNote)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(fieldNote: FieldNote): Result<FieldNote> {
        return try {
            mutex.withLock {
                val currentNotes = notesFlow.value.toMutableList()
                val index = currentNotes.indexOfFirst { it.id == fieldNote.id }

                if (index >= 0) {
                    val updatedNote = fieldNote.copy(lastModified = currentTimeMillis())
                    currentNotes[index] = updatedNote
                    saveNotesToFile(currentNotes)
                    notesFlow.value = currentNotes
                    Result.success(updatedNote)
                } else {
                    Result.failure(Exception("Note not found with id: ${fieldNote.id}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            mutex.withLock {
                val currentNotes = notesFlow.value.toMutableList()
                val noteToRemove = currentNotes.find { it.id == noteId }
                val removed =
                    if (noteToRemove != null) {
                        currentNotes.remove(noteToRemove)
                    } else {
                        false
                    }

                if (removed) {
                    saveNotesToFile(currentNotes)
                    notesFlow.value = currentNotes
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Note not found with id: $noteId"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNote(noteId: String): Result<FieldNote?> {
        return try {
            val note = notesFlow.value.find { it.id == noteId }
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllNotes(): Result<List<FieldNote>> {
        return try {
            Result.success(notesFlow.value)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNotesForPosition(
        champ: String,
        parcelle: String,
        rang: String,
        trou: String,
    ): Result<FieldNote?> {
        return try {
            val note =
                notesFlow.value.find {
                    it.champ == champ &&
                        it.parcelle == parcelle &&
                        it.rang == rang &&
                        it.trou == trou
                }
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNotesWithStatus(status: FieldNoteStatus): Result<List<FieldNote>> {
        return try {
            val filteredNotes = notesFlow.value.filter { it.status == status }
            Result.success(filteredNotes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchNotes(query: String): Result<List<FieldNote>> {
        return try {
            val searchResults =
                notesFlow.value.filter { note ->
                    note.notes.contains(query, ignoreCase = true) ||
                        note.variety?.contains(query, ignoreCase = true) == true ||
                        "${note.champ}-${note.parcelle}-${note.rang}-${note.trou}".contains(query, ignoreCase = true)
                }
            Result.success(searchResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearAllNotes(): Result<Unit> {
        return try {
            mutex.withLock {
                saveNotesToFile(emptyList())
                notesFlow.value = emptyList()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportNotes(): Result<String> {
        return try {
            val notes = notesFlow.value
            val csvHeader = "Field,Parcel,Row,Position,Variety,Notes,Status,Created,Modified"
            val csvRows =
                notes.map { note ->
                    val variety = note.variety?.replace(",", ";") ?: ""
                    val notes = note.notes.replace(",", ";").replace("\n", " ")
                    val createdDate = note.timestamp.toString()
                    val modifiedDate = note.lastModified.toString()

                    "${note.champ},${note.parcelle},${note.rang},${note.trou},$variety,$notes,${note.status},$createdDate,$modifiedDate"
                }

            val csvContent = listOf(csvHeader) + csvRows
            Result.success(csvContent.joinToString("\n"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeNotes(): Flow<List<FieldNote>> {
        return notesFlow
    }

    override fun observeNotesForPosition(
        champ: String,
        parcelle: String,
        rang: String,
        trou: String,
    ): Flow<FieldNote?> {
        return notesFlow.map { notes ->
            notes.find {
                it.champ == champ &&
                    it.parcelle == parcelle &&
                    it.rang == rang &&
                    it.trou == trou
            }
        }
    }
}

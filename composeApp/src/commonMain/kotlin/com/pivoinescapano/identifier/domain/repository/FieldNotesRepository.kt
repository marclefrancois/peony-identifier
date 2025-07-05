package com.pivoinescapano.identifier.domain.repository

import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import kotlinx.coroutines.flow.Flow

interface FieldNotesRepository {
    suspend fun createNote(fieldNote: FieldNote): Result<FieldNote>

    suspend fun updateNote(fieldNote: FieldNote): Result<FieldNote>

    suspend fun deleteNote(noteId: String): Result<Unit>

    suspend fun getNote(noteId: String): Result<FieldNote?>

    suspend fun getAllNotes(): Result<List<FieldNote>>

    suspend fun getNotesForPosition(
        champ: String,
        parcelle: String,
        rang: String,
        trou: String,
    ): Result<FieldNote?>

    suspend fun getNotesWithStatus(status: FieldNoteStatus): Result<List<FieldNote>>

    suspend fun searchNotes(query: String): Result<List<FieldNote>>

    suspend fun clearAllNotes(): Result<Unit>

    suspend fun exportNotes(): Result<String>

    fun observeNotes(): Flow<List<FieldNote>>

    fun observeNotesForPosition(
        champ: String,
        parcelle: String,
        rang: String,
        trou: String,
    ): Flow<FieldNote?>
}

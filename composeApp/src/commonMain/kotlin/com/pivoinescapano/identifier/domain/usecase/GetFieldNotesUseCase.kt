package com.pivoinescapano.identifier.domain.usecase

import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.domain.repository.FieldNotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GetFieldNotesUseCase(
    private val fieldNotesRepository: FieldNotesRepository,
) {
    suspend fun getAllNotes(): Result<List<FieldNote>> {
        return withContext(Dispatchers.IO) {
            try {
                fieldNotesRepository.getAllNotes()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getNotesForPosition(
        champ: String,
        parcelle: String,
        rang: String,
        trou: String,
    ): Result<FieldNote?> {
        return withContext(Dispatchers.IO) {
            try {
                fieldNotesRepository.getNotesForPosition(champ, parcelle, rang, trou)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getNotesWithStatus(status: FieldNoteStatus): Result<List<FieldNote>> {
        return withContext(Dispatchers.IO) {
            try {
                fieldNotesRepository.getNotesWithStatus(status)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun searchNotes(query: String): Result<List<FieldNote>> {
        return withContext(Dispatchers.IO) {
            try {
                fieldNotesRepository.searchNotes(query)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

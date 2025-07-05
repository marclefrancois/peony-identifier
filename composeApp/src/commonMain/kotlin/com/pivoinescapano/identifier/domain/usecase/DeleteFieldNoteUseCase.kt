package com.pivoinescapano.identifier.domain.usecase

import com.pivoinescapano.identifier.domain.repository.FieldNotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DeleteFieldNoteUseCase(
    private val fieldNotesRepository: FieldNotesRepository,
) {
    suspend operator fun invoke(noteId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                fieldNotesRepository.deleteNote(noteId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

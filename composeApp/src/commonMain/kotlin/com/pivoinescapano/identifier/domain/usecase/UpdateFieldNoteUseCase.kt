package com.pivoinescapano.identifier.domain.usecase

import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.repository.FieldNotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class UpdateFieldNoteUseCase(
    private val fieldNotesRepository: FieldNotesRepository,
) {
    suspend operator fun invoke(fieldNote: FieldNote): Result<FieldNote> {
        return withContext(Dispatchers.IO) {
            try {
                fieldNotesRepository.updateNote(fieldNote)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

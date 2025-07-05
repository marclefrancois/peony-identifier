package com.pivoinescapano.identifier.domain.usecase

import com.pivoinescapano.identifier.domain.repository.FieldNotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ExportFieldNotesUseCase(
    private val fieldNotesRepository: FieldNotesRepository,
) {
    suspend operator fun invoke(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                fieldNotesRepository.exportNotes()
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

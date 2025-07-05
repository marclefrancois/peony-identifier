package com.pivoinescapano.identifier.domain.usecase

import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.domain.repository.FieldNotesRepository
import com.pivoinescapano.identifier.platform.currentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CreateFieldNoteUseCase(
    private val fieldNotesRepository: FieldNotesRepository,
) {
    suspend operator fun invoke(
        champ: String,
        parcelle: String,
        rang: String,
        trou: String,
        variety: String?,
        notes: String,
        status: FieldNoteStatus = FieldNoteStatus.NORMAL,
    ): Result<FieldNote> {
        return withContext(Dispatchers.IO) {
            try {
                val fieldNote =
                    FieldNote(
                        id = generateNoteId(champ, parcelle, rang, trou),
                        champ = champ,
                        parcelle = parcelle,
                        rang = rang,
                        trou = trou,
                        variety = variety,
                        notes = notes,
                        status = status,
                        timestamp = currentTimeMillis(),
                        lastModified = currentTimeMillis(),
                    )

                fieldNotesRepository.createNote(fieldNote)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun generateNoteId(
        champ: String,
        parcelle: String,
        rang: String,
        trou: String,
    ): String {
        return "${champ}_${parcelle}_${rang}_${trou}_${currentTimeMillis()}"
    }
}

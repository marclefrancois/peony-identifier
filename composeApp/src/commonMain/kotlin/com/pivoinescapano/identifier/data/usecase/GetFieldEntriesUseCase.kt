package com.pivoinescapano.identifier.data.usecase

import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.repository.FieldRepository

class GetFieldEntriesUseCase(
    private val fieldRepository: FieldRepository,
) {
    suspend operator fun invoke(): List<FieldEntry> {
        // Get all distinct champs and then get entries for each
        val champs = fieldRepository.getDistinctChamps()
        val allEntries = mutableListOf<FieldEntry>()

        champs.forEach { champ ->
            allEntries.addAll(fieldRepository.getFieldEntries(champ))
        }

        return allEntries
    }
}

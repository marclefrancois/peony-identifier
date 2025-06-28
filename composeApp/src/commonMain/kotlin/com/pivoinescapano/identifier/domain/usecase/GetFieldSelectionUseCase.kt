package com.pivoinescapano.identifier.domain.usecase

import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.repository.FieldRepository

class GetFieldSelectionUseCase(
    private val fieldRepository: FieldRepository,
) {
    suspend fun getAvailableChamps(): List<String> {
        return fieldRepository.getDistinctChamps()
    }

    suspend fun getAvailableParcelles(champ: String): List<String> {
        return fieldRepository.getDistinctParcelles(champ)
    }

    suspend fun getAvailableRangs(
        champ: String,
        parcelle: String,
    ): List<String> {
        return fieldRepository.getDistinctRangs(champ, parcelle)
    }

    suspend fun getAvailableTrous(
        champ: String,
        parcelle: String,
        rang: String,
    ): List<String> {
        return fieldRepository.getDistinctTrous(champ, parcelle, rang)
    }

    suspend fun getFieldEntry(
        champ: String,
        parcelle: String,
        rang: String,
        trou: String,
    ): FieldEntry? {
        return fieldRepository.getFieldEntry(champ, parcelle, rang, trou)
    }

    suspend fun getRowEntries(
        champ: String,
        parcelle: String,
        rang: String,
    ): List<FieldEntry> {
        return fieldRepository.getRowEntries(champ, parcelle, rang)
    }
}

package com.pivoinescapano.identifier.data.repository.impl

import com.pivoinescapano.identifier.data.cache.DataCacheManager
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.repository.FieldRepository

class FieldRepositoryImpl(
    private val dataCacheManager: DataCacheManager
) : FieldRepository {
    
    private suspend fun loadFieldEntries(): List<FieldEntry> {
        return dataCacheManager.loadFieldEntries()
    }
    
    override suspend fun getFieldEntries(fieldNumber: String): List<FieldEntry> {
        return loadFieldEntries().filter { it.champ == fieldNumber }
    }
    
    override suspend fun getDistinctChamps(): List<String> {
        return loadFieldEntries().mapNotNull { it.champ }.distinct().sorted()
    }
    
    override suspend fun getDistinctParcelles(champ: String): List<String> {
        return loadFieldEntries()
            .filter { it.champ == champ }
            .mapNotNull { it.parcelle }
            .distinct()
            .sorted()
    }
    
    override suspend fun getDistinctRangs(champ: String, parcelle: String): List<String> {
        return loadFieldEntries()
            .filter { it.champ == champ && it.parcelle == parcelle }
            .mapNotNull { it.rang }
            .distinct()
            .sortedBy { it.toIntOrNull() ?: 0 }
    }
    
    override suspend fun getDistinctTrous(champ: String, parcelle: String, rang: String): List<String> {
        return loadFieldEntries()
            .filter { it.champ == champ && it.parcelle == parcelle && it.rang == rang }
            .mapNotNull { it.trou }
            .distinct()
            .sortedBy { it.toIntOrNull() ?: 0 }
    }
    
    override suspend fun getFieldEntry(champ: String, parcelle: String, rang: String, trou: String): FieldEntry? {
        return loadFieldEntries()
            .find { it.champ == champ && it.parcelle == parcelle && it.rang == rang && it.trou == trou }
    }
    
    override suspend fun getRowEntries(champ: String, parcelle: String, rang: String): List<FieldEntry> {
        return loadFieldEntries()
            .filter { it.champ == champ && it.parcelle == parcelle && it.rang == rang }
            .sortedBy { it.trou?.toIntOrNull() ?: 0 }
    }
}
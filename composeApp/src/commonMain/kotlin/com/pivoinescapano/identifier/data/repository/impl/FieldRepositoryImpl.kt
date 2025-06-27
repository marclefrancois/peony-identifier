package com.pivoinescapano.identifier.data.repository.impl

import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.repository.FieldRepository
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import peonyidentifier.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
class FieldRepositoryImpl : FieldRepository {
    
    private var cachedFieldEntries: List<FieldEntry>? = null
    
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    private suspend fun loadFieldEntries(): List<FieldEntry> {
        if (cachedFieldEntries == null) {
            try {
                val jsonString = Res.readBytes("files/Champ1PP.json").decodeToString()
                cachedFieldEntries = json.decodeFromString<List<FieldEntry>>(jsonString)
            } catch (e: Exception) {
                throw RuntimeException("Failed to load field data: ${e.message}", e)
            }
        }
        return cachedFieldEntries!!
    }
    
    override suspend fun getFieldEntries(fieldNumber: String): List<FieldEntry> {
        return loadFieldEntries().filter { it.champ == fieldNumber }
    }
    
    override suspend fun getDistinctChamps(): List<String> {
        return loadFieldEntries().map { it.champ }.distinct().sorted()
    }
    
    override suspend fun getDistinctParcelles(champ: String): List<String> {
        return loadFieldEntries()
            .filter { it.champ == champ }
            .map { it.parcelle }
            .distinct()
            .sorted()
    }
    
    override suspend fun getDistinctRangs(champ: String, parcelle: String): List<String> {
        return loadFieldEntries()
            .filter { it.champ == champ && it.parcelle == parcelle }
            .map { it.rang }
            .distinct()
            .sortedBy { it.toIntOrNull() ?: 0 }
    }
    
    override suspend fun getDistinctTrous(champ: String, parcelle: String, rang: String): List<String> {
        return loadFieldEntries()
            .filter { it.champ == champ && it.parcelle == parcelle && it.rang == rang }
            .map { it.trou }
            .distinct()
            .sortedBy { it.toIntOrNull() ?: 0 }
    }
    
    override suspend fun getFieldEntry(champ: String, parcelle: String, rang: String, trou: String): FieldEntry? {
        return loadFieldEntries()
            .find { it.champ == champ && it.parcelle == parcelle && it.rang == rang && it.trou == trou }
    }
}
package com.pivoinescapano.identifier.data.repository

import com.pivoinescapano.identifier.data.model.FieldEntry

interface FieldRepository {
    suspend fun getFieldEntries(fieldNumber: String): List<FieldEntry>
    suspend fun getDistinctChamps(): List<String>
    suspend fun getDistinctParcelles(champ: String): List<String>
    suspend fun getDistinctRangs(champ: String, parcelle: String): List<String>
    suspend fun getDistinctTrous(champ: String, parcelle: String, rang: String): List<String>
    suspend fun getFieldEntry(champ: String, parcelle: String, rang: String, trou: String): FieldEntry?
}
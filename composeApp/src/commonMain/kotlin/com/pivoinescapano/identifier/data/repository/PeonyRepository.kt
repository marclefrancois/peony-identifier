package com.pivoinescapano.identifier.data.repository

import com.pivoinescapano.identifier.data.model.PeonyInfo

interface PeonyRepository {
    suspend fun getAllPeonies(): List<PeonyInfo>

    suspend fun findPeonyByCultivar(cultivar: String): PeonyInfo?

    suspend fun findPeoniesByFuzzyMatch(
        varieteName: String,
        threshold: Double = 0.7,
    ): List<PeonyInfo>

    suspend fun getPeonyById(id: Int): PeonyInfo?
}

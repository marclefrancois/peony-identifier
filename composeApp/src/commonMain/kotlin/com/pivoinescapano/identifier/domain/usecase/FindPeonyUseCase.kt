package com.pivoinescapano.identifier.domain.usecase

import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.data.repository.PeonyRepository

class FindPeonyUseCase(
    private val peonyRepository: PeonyRepository,
) {
    suspend fun execute(varieteName: String): PeonyInfo? {
        return peonyRepository.findPeonyByCultivar(varieteName)
    }

    suspend fun findWithFuzzyMatching(
        varieteName: String,
        threshold: Double = 0.7,
    ): List<PeonyInfo> {
        return peonyRepository.findPeoniesByFuzzyMatch(varieteName, threshold)
    }
}

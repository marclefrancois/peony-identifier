package com.pivoinescapano.identifier.domain.usecase

import com.pivoinescapano.identifier.data.repository.FieldRepository
import com.pivoinescapano.identifier.domain.model.PeonyLocation
import kotlin.math.max
import kotlin.math.min

class SearchPeonyLocationsUseCase(
    private val fieldRepository: FieldRepository,
) {
    suspend fun execute(peonyName: String): List<PeonyLocation> {
        if (peonyName.isBlank()) return emptyList()

        val allFieldEntries = fieldRepository.getAllFieldEntries()
        val matches = mutableListOf<Pair<PeonyLocation, Double>>()

        for (entry in allFieldEntries) {
            val variete = entry.variete
            if (variete != null) {
                val similarity = calculateSimilarity(peonyName, variete)
                if (similarity >= 0.6) { // Use 0.6 threshold for fuzzy matching
                    val location =
                        PeonyLocation.fromFieldEntry(
                            champ = entry.champ ?: "",
                            parcelle = entry.parcelle ?: "",
                            rang = entry.rang ?: "",
                            trou = entry.trou ?: "",
                            variete = entry.variete,
                            taille = entry.taille,
                        )
                    matches.add(location to similarity)
                }
            }
        }

        return matches
            .sortedByDescending { it.second } // Sort by similarity score descending
            .map { it.first }
    }

    suspend fun getAllUniqueVarieties(): List<String> {
        val allFieldEntries = fieldRepository.getAllFieldEntries()
        return allFieldEntries
            .mapNotNull { it.variete }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    /**
     * Calculate string similarity using Levenshtein distance
     * Returns a value between 0.0 (completely different) and 1.0 (identical)
     * Reuses the same logic as PeonyRepositoryImpl for consistency
     */
    private fun calculateSimilarity(
        str1: String,
        str2: String,
    ): Double {
        val s1 = str1.lowercase().trim()
        val s2 = str2.lowercase().trim()

        if (s1 == s2) return 1.0
        if (s1.isEmpty() || s2.isEmpty()) return 0.0

        // Quick check for partial matches
        if (s1.contains(s2) || s2.contains(s1)) return 0.8

        val distance = levenshteinDistance(s1, s2)
        val maxLength = max(s1.length, s2.length)

        return 1.0 - (distance.toDouble() / maxLength)
    }

    /**
     * Calculate Levenshtein distance between two strings
     */
    private fun levenshteinDistance(
        str1: String,
        str2: String,
    ): Int {
        val dp = Array(str1.length + 1) { IntArray(str2.length + 1) }

        for (i in 0..str1.length) {
            dp[i][0] = i
        }

        for (j in 0..str2.length) {
            dp[0][j] = j
        }

        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                dp[i][j] =
                    if (str1[i - 1] == str2[j - 1]) {
                        dp[i - 1][j - 1]
                    } else {
                        min(
                            min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + 1,
                        )
                    }
            }
        }

        return dp[str1.length][str2.length]
    }
}

package com.pivoinescapano.identifier.data.repository.impl

import com.pivoinescapano.identifier.data.cache.DataCacheManager
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.data.repository.PeonyRepository
import kotlin.math.max
import kotlin.math.min

class PeonyRepositoryImpl(
    private val dataCacheManager: DataCacheManager
) : PeonyRepository {
    
    private suspend fun loadPeonies(): List<PeonyInfo> {
        return dataCacheManager.loadPeonies()
    }
    
    override suspend fun getAllPeonies(): List<PeonyInfo> {
        return loadPeonies()
    }
    
    override suspend fun findPeonyByCultivar(cultivar: String): PeonyInfo? {
        return loadPeonies().find { 
            it.cultivar.equals(cultivar, ignoreCase = true) 
        }
    }
    
    override suspend fun findPeoniesByFuzzyMatch(varieteName: String, threshold: Double): List<PeonyInfo> {
        val peonies = loadPeonies()
        val matches = mutableListOf<Pair<PeonyInfo, Double>>()
        
        for (peony in peonies) {
            val similarity = calculateSimilarity(varieteName, peony.cultivar)
            if (similarity >= threshold) {
                matches.add(peony to similarity)
            }
        }
        
        return matches
            .sortedByDescending { it.second }
            .take(10) // Limit to top 10 matches
            .map { it.first }
    }
    
    override suspend fun getPeonyById(id: Int): PeonyInfo? {
        return loadPeonies().find { it.id == id }
    }
    
    /**
     * Calculate string similarity using Levenshtein distance
     * Returns a value between 0.0 (completely different) and 1.0 (identical)
     */
    private fun calculateSimilarity(str1: String, str2: String): Double {
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
    private fun levenshteinDistance(str1: String, str2: String): Int {
        val dp = Array(str1.length + 1) { IntArray(str2.length + 1) }
        
        for (i in 0..str1.length) {
            dp[i][0] = i
        }
        
        for (j in 0..str2.length) {
            dp[0][j] = j
        }
        
        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                dp[i][j] = if (str1[i - 1] == str2[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    min(
                        min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + 1
                    )
                }
            }
        }
        
        return dp[str1.length][str2.length]
    }
}
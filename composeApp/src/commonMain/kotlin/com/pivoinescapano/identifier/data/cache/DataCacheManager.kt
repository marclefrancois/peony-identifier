package com.pivoinescapano.identifier.data.cache

import com.pivoinescapano.identifier.data.loader.JsonDataLoader
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.model.PeonyInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Thread-safe data cache manager with background loading optimization
 */
class DataCacheManager(
    private val jsonDataLoader: JsonDataLoader
) {
    
    // Thread-safe caching with mutexes
    private var cachedPeonies: List<PeonyInfo>? = null
    private var cachedFieldEntries: List<FieldEntry>? = null
    
    private val peonyMutex = Mutex()
    private val fieldMutex = Mutex()
    
    /**
     * Load peonies with background threading and thread-safe caching
     */
    suspend fun loadPeonies(): List<PeonyInfo> {
        return peonyMutex.withLock {
            cachedPeonies ?: run {
                val peonies = jsonDataLoader.loadAndParseJsonSerialization<List<PeonyInfo>>(
                    "files/peony-database.json"
                )
                cachedPeonies = peonies
                peonies
            }
        }
    }
    
    /**
     * Load field entries from all available field files with background threading and thread-safe caching
     */
    suspend fun loadFieldEntries(): List<FieldEntry> {
        return fieldMutex.withLock {
            cachedFieldEntries ?: run {
                val allFieldEntries = mutableListOf<FieldEntry>()
                
                // List of all available field files
                val fieldFiles = listOf(
                    "files/Champ1PP.json",
                    "files/Champ1GP.json", 
                    "files/Champ2PP.json"
                )
                
                // Load and combine all field files
                for (fieldFile in fieldFiles) {
                    try {
                        val entries = jsonDataLoader.loadAndParseJsonSerialization<List<FieldEntry>>(fieldFile)
                        allFieldEntries.addAll(entries)
                    } catch (e: Exception) {
                        // Log error but continue with other files
                        println("Warning: Could not load field file $fieldFile: ${e.message}")
                    }
                }
                
                cachedFieldEntries = allFieldEntries
                allFieldEntries
            }
        }
    }
    
    /**
     * Preload all data in the background for improved app startup performance
     */
    suspend fun preloadAllData() {
        // Load both datasets concurrently in background
        coroutineScope {
            launch(Dispatchers.IO) { loadPeonies() }
            launch(Dispatchers.IO) { loadFieldEntries() }
        }
    }
    
    /**
     * Clear cache to free memory if needed
     */
    suspend fun clearCache() {
        peonyMutex.withLock { cachedPeonies = null }
        fieldMutex.withLock { cachedFieldEntries = null }
    }
    
    /**
     * Check if data is already cached
     */
    fun isPeonyDataCached(): Boolean = cachedPeonies != null
    fun isFieldDataCached(): Boolean = cachedFieldEntries != null
}
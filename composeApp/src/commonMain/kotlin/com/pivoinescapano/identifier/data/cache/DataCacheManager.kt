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
     * Load field entries with background threading and thread-safe caching
     */
    suspend fun loadFieldEntries(): List<FieldEntry> {
        return fieldMutex.withLock {
            cachedFieldEntries ?: run {
                val fieldEntries = jsonDataLoader.loadAndParseJsonSerialization<List<FieldEntry>>(
                    "files/Champ1PP.json"
                )
                cachedFieldEntries = fieldEntries
                fieldEntries
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
package com.pivoinescapano.identifier.data.cache

import com.pivoinescapano.identifier.data.loader.JsonDataLoader
import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.data.remote.NetworkResult
import com.pivoinescapano.identifier.data.remote.RemoteDataSource
import com.pivoinescapano.identifier.data.storage.FileSystemStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataCacheManager(
    private val jsonDataLoader: JsonDataLoader,
    private val remoteDataSource: RemoteDataSource,
    private val fileSystemStorage: FileSystemStorage,
) {
    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            prettyPrint = true
        }

    private var cachedPeonies: List<PeonyInfo>? = null
    private var cachedFieldEntries: List<FieldEntry>? = null

    private val peonyMutex = Mutex()
    private val fieldMutex = Mutex()

    companion object {
        private const val FIELD_CACHE_FILE = "field-data-cache.json"
        private const val FIELD_METADATA_FILE = "field-data-metadata.json"
        private const val ENABLE_REMOTE_CACHE = false
    }

    /**
     * Load peonies with background threading and thread-safe caching
     */
    suspend fun loadPeonies(): List<PeonyInfo> {
        return peonyMutex.withLock {
            cachedPeonies ?: run {
                val peonies =
                    jsonDataLoader.loadAndParseJsonSerialization<List<PeonyInfo>>(
                        "files/peony-database.json",
                    )
                cachedPeonies = peonies
                peonies
            }
        }
    }

    suspend fun loadFieldEntries(): List<FieldEntry> {
        return fieldMutex.withLock {
            cachedFieldEntries ?: run {
                val entries = loadFieldEntriesWithRemote()
                cachedFieldEntries = entries
                entries
            }
        }
    }

    private suspend fun loadFieldEntriesWithRemote(): List<FieldEntry> {
        if (!ENABLE_REMOTE_CACHE) {
            println("Remote cache disabled, fetching from remote directly")
            return when (val remoteResult = remoteDataSource.fetchAllFieldData()) {
                is NetworkResult.Success -> {
                    println("Fetched field data from remote (${remoteResult.data.size} entries)")
                    remoteResult.data
                }

                is NetworkResult.NetworkUnavailable,
                is NetworkResult.Error,
                -> {
                    if (remoteResult is NetworkResult.Error) {
                        println("Remote fetch failed: ${remoteResult.message}")
                    }
                    println("Falling back to bundled JSON")
                    loadFieldEntriesFromBundledJson()
                }
            }
        }

        val cachedData = loadFieldEntriesFromCache()
        if (cachedData != null && !cachedData.metadata.isExpired()) {
            println("Using cached field data (not expired)")
            return cachedData.data
        }

        return when (val remoteResult = remoteDataSource.fetchAllFieldData()) {
            is NetworkResult.Success -> {
                println("Fetched field data from remote (${remoteResult.data.size} entries)")
                saveFieldEntriesToCache(remoteResult.data)
                remoteResult.data
            }

            is NetworkResult.NetworkUnavailable,
            is NetworkResult.Error,
            -> {
                if (remoteResult is NetworkResult.Error) {
                    println("Remote fetch failed: ${remoteResult.message}")
                }
                if (cachedData != null) {
                    println("Using expired cached data as fallback")
                    cachedData.data
                } else {
                    println("No cache available, falling back to bundled JSON")
                    loadFieldEntriesFromBundledJson()
                }
            }
        }
    }

    private suspend fun loadFieldEntriesFromCache(): CachedData<List<FieldEntry>>? {
        return try {
            val metadataResult = fileSystemStorage.readFile(FIELD_METADATA_FILE)
            val dataResult = fileSystemStorage.readFile(FIELD_CACHE_FILE)

            if (metadataResult.isSuccess && dataResult.isSuccess) {
                val metadata = json.decodeFromString<CacheMetadata>(metadataResult.getOrThrow())
                val data = json.decodeFromString<List<FieldEntry>>(dataResult.getOrThrow())
                CachedData(data, metadata)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Failed to load cached field data: ${e.message}")
            null
        }
    }

    private suspend fun saveFieldEntriesToCache(entries: List<FieldEntry>) {
        try {
            val metadata = CacheMetadata.now()
            val metadataJson = json.encodeToString(metadata)
            val dataJson = json.encodeToString(entries)

            fileSystemStorage.writeFile(FIELD_METADATA_FILE, metadataJson)
            fileSystemStorage.writeFile(FIELD_CACHE_FILE, dataJson)
            println("Saved ${entries.size} field entries to cache")
        } catch (e: Exception) {
            println("Failed to save field data to cache: ${e.message}")
        }
    }

    private suspend fun loadFieldEntriesFromBundledJson(): List<FieldEntry> {
        val allFieldEntries = mutableListOf<FieldEntry>()

        val fieldFiles =
            listOf(
                "files/Champ1PP.json",
                "files/Champ1GP.json",
                "files/Champ2PP.json",
            )

        for (fieldFile in fieldFiles) {
            try {
                val entries = jsonDataLoader.loadAndParseJsonSerialization<List<FieldEntry>>(fieldFile)
                allFieldEntries.addAll(entries)
            } catch (e: Exception) {
                println("Warning: Could not load field file $fieldFile: ${e.message}")
            }
        }

        return allFieldEntries
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

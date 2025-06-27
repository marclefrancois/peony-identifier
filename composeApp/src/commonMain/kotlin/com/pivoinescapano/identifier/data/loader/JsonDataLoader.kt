package com.pivoinescapano.identifier.data.loader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import peonyidentifier.composeapp.generated.resources.Res

/**
 * Optimized JSON data loader that performs file I/O and parsing on background threads
 */
@OptIn(ExperimentalResourceApi::class)
class JsonDataLoader {
    
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    /**
     * Load and parse JSON data on a background thread
     * @param filePath Path to the JSON file in resources
     * @return Parsed JSON string on IO dispatcher
     */
    suspend fun loadJsonString(filePath: String): String = withContext(Dispatchers.IO) {
        try {
            Res.readBytes(filePath).decodeToString()
        } catch (e: Exception) {
            throw RuntimeException("Failed to load JSON file: $filePath - ${e.message}", e)
        }
    }
    
    /**
     * Parse JSON string to typed object on a background thread
     * @param jsonString JSON content to parse
     * @param deserializer Function to deserialize JSON to target type
     * @return Parsed object on Default dispatcher
     */
    suspend fun <T> parseJson(
        jsonString: String,
        deserializer: (String) -> T
    ): T = withContext(Dispatchers.Default) {
        try {
            deserializer(jsonString)
        } catch (e: Exception) {
            throw RuntimeException("Failed to parse JSON data: ${e.message}", e)
        }
    }
    
    /**
     * Combined load and parse operation optimized for background execution
     * @param filePath Path to the JSON file in resources
     * @param deserializer Function to deserialize JSON to target type
     * @return Parsed object
     */
    suspend fun <T> loadAndParseJson(
        filePath: String,
        deserializer: (String) -> T
    ): T {
        val jsonString = loadJsonString(filePath)
        return parseJson(jsonString, deserializer)
    }
    
    /**
     * Load and parse JSON using kotlinx.serialization with background threading
     * @param filePath Path to the JSON file in resources
     * @return Parsed object
     */
    suspend inline fun <reified T> loadAndParseJsonSerialization(filePath: String): T {
        return loadAndParseJson(filePath) { jsonString ->
            json.decodeFromString<T>(jsonString)
        }
    }
}
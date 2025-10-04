package com.pivoinescapano.identifier.data.config

import com.pivoinescapano.identifier.data.loader.JsonDataLoader

class FieldConfigLoader(
    private val jsonDataLoader: JsonDataLoader,
) {
    private var cachedConfigs: List<FieldConfig>? = null

    suspend fun loadConfigs(): List<FieldConfig> {
        return cachedConfigs ?: run {
            val configs =
                jsonDataLoader.loadAndParseJsonSerialization<List<FieldConfig>>(
                    "files/field-config.json",
                )
            cachedConfigs = configs
            configs
        }
    }

    fun clearCache() {
        cachedConfigs = null
    }
}

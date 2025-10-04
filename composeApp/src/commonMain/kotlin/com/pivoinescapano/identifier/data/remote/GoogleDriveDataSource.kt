package com.pivoinescapano.identifier.data.remote

import com.pivoinescapano.identifier.data.config.FieldConfigLoader
import com.pivoinescapano.identifier.data.model.FieldEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class GoogleDriveDataSource(
    private val googleDriveService: GoogleDriveService,
    private val fieldConfigLoader: FieldConfigLoader,
) : RemoteDataSource {
    override suspend fun fetchAllFieldData(): NetworkResult<List<FieldEntry>> {
        return coroutineScope {
            try {
                val configs = fieldConfigLoader.loadConfigs()

                val deferredResults =
                    configs.map { config ->
                        async {
                            googleDriveService.fetchFieldData(config)
                        }
                    }

                val results = deferredResults.awaitAll()

                val allEntries = mutableListOf<FieldEntry>()
                val errors = mutableListOf<String>()

                results.forEach { result ->
                    when (result) {
                        is NetworkResult.Success -> allEntries.addAll(result.data)
                        is NetworkResult.Error -> errors.add(result.message)
                        is NetworkResult.NetworkUnavailable -> {
                            return@coroutineScope NetworkResult.NetworkUnavailable
                        }
                    }
                }

                if (allEntries.isEmpty() && errors.isNotEmpty()) {
                    NetworkResult.Error("Failed to fetch any field data: ${errors.joinToString("; ")}")
                } else {
                    NetworkResult.Success(allEntries)
                }
            } catch (e: Exception) {
                NetworkResult.Error("Failed to load field configurations: ${e.message}", e)
            }
        }
    }
}

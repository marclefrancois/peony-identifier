package com.pivoinescapano.identifier.data.remote

import com.pivoinescapano.identifier.data.config.FieldConfig
import com.pivoinescapano.identifier.data.model.FieldEntry
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GoogleDriveServiceImpl(
    private val httpClient: HttpClient,
    private val csvParser: CsvParser,
) : GoogleDriveService {
    override suspend fun fetchSpreadsheetCsv(
        spreadsheetId: String,
        gid: String,
    ): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://docs.google.com/spreadsheets/d/$spreadsheetId/export?format=csv&gid=$gid"
                val response = httpClient.get(url)

                if (response.status.isSuccess()) {
                    val csvContent = response.bodyAsText()
                    NetworkResult.Success(csvContent)
                } else {
                    NetworkResult.Error(
                        "HTTP ${response.status.value}: Failed to fetch spreadsheet",
                    )
                }
            } catch (e: Exception) {
                NetworkResult.Error(
                    "Network error: ${e.message}",
                    e,
                )
            }
        }
    }

    override suspend fun fetchFieldData(config: FieldConfig): NetworkResult<List<FieldEntry>> {
        return withContext(Dispatchers.Default) {
            when (val csvResult = fetchSpreadsheetCsv(config.spreadsheetId, config.sheetGid)) {
                is NetworkResult.Success -> {
                    try {
                        val entries =
                            csvParser.parseFieldEntries(
                                csvContent = csvResult.data,
                                columnMapping = config.columnMapping,
                                headerRowIndex = config.headerRowIndex,
                                fieldId = config.fieldId,
                                parcelId = config.parcelId,
                            )
                        NetworkResult.Success(entries)
                    } catch (e: Exception) {
                        NetworkResult.Error(
                            "CSV parsing error: ${e.message}",
                            e,
                        )
                    }
                }

                is NetworkResult.Error -> csvResult
                is NetworkResult.NetworkUnavailable -> csvResult
            }
        }
    }
}

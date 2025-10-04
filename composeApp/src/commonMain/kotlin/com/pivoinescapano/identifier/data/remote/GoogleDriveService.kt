package com.pivoinescapano.identifier.data.remote

import com.pivoinescapano.identifier.data.config.FieldConfig
import com.pivoinescapano.identifier.data.model.FieldEntry

interface GoogleDriveService {
    suspend fun fetchSpreadsheetCsv(
        spreadsheetId: String,
        gid: String,
    ): NetworkResult<String>

    suspend fun fetchFieldData(config: FieldConfig): NetworkResult<List<FieldEntry>>
}

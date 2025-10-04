package com.pivoinescapano.identifier.data.config

import kotlinx.serialization.Serializable

@Serializable
data class FieldConfig(
    val fieldId: String,
    val parcelId: String,
    val spreadsheetId: String,
    val sheetGid: String,
    val columnMapping: Map<String, String>,
    val headerRowIndex: Int = 0,
)

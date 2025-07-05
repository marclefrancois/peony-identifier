package com.pivoinescapano.identifier.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExportFilter(
    // Empty means all fields
    val fields: Set<String> = emptySet(),
    // Empty means all statuses
    val statuses: Set<FieldNoteStatus> = emptySet(),
    val dateRange: DateRange? = null,
    val includeEmptyNotes: Boolean = true,
    val format: ExportFormat = ExportFormat.CSV,
)

@Serializable
data class DateRange(
    val startDate: Long,
    val endDate: Long,
)

@Serializable
enum class ExportFormat {
    CSV,
    JSON,
}

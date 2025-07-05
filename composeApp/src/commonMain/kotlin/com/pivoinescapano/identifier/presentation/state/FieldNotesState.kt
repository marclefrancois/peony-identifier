package com.pivoinescapano.identifier.presentation.state

import com.pivoinescapano.identifier.domain.model.ExportFilter
import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.domain.service.ExportService

data class FieldNotesState(
    val notes: List<FieldNote> = emptyList(),
    val filteredNotes: List<FieldNote> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedStatus: FieldNoteStatus? = null,
    val sortOrder: SortOrder = SortOrder.POSITION,
    val isExportDialogVisible: Boolean = false,
    val exportFilter: ExportFilter = ExportFilter(),
    val availableFields: Set<String> = emptySet(),
    val isExporting: Boolean = false,
    val exportSuccess: String? = null,
    val isImportDialogVisible: Boolean = false,
    val isImporting: Boolean = false,
    val importResult: ExportService.ImportResult? = null,
    val isClearConfirmDialogVisible: Boolean = false,
)

enum class SortOrder {
    POSITION,
    DATE_CREATED,
    DATE_MODIFIED,
    STATUS,
}

package com.pivoinescapano.identifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pivoinescapano.identifier.domain.model.ExportFilter
import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.domain.repository.FieldNotesRepository
import com.pivoinescapano.identifier.domain.service.ExportService
import com.pivoinescapano.identifier.presentation.state.FieldNotesState
import com.pivoinescapano.identifier.presentation.state.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FieldNotesViewModel(
    private val fieldNotesRepository: FieldNotesRepository,
    private val exportService: ExportService,
) : ViewModel() {
    private val _state = MutableStateFlow(FieldNotesState())
    val state: StateFlow<FieldNotesState> = _state.asStateFlow()

    init {
        observeNotes()
        updateAvailableFields()
    }

    private fun observeNotes() {
        fieldNotesRepository.observeNotes()
            .combine(_state) { notes, currentState ->
                val filteredAndSorted =
                    filterAndSortNotes(
                        notes = notes,
                        searchQuery = currentState.searchQuery,
                        selectedStatus = currentState.selectedStatus,
                        sortOrder = currentState.sortOrder,
                    )

                currentState.copy(
                    notes = notes,
                    filteredNotes = filteredAndSorted,
                    isLoading = false,
                ).also {
                    updateAvailableFields()
                }
            }
            .onEach { newState ->
                _state.value = newState
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        val currentState = _state.value
        val filteredAndSorted =
            filterAndSortNotes(
                notes = currentState.notes,
                searchQuery = query,
                selectedStatus = currentState.selectedStatus,
                sortOrder = currentState.sortOrder,
            )

        _state.value =
            currentState.copy(
                searchQuery = query,
                filteredNotes = filteredAndSorted,
            )
    }

    fun updateStatusFilter(status: FieldNoteStatus?) {
        val currentState = _state.value
        val filteredAndSorted =
            filterAndSortNotes(
                notes = currentState.notes,
                searchQuery = currentState.searchQuery,
                selectedStatus = status,
                sortOrder = currentState.sortOrder,
            )

        _state.value =
            currentState.copy(
                selectedStatus = status,
                filteredNotes = filteredAndSorted,
            )
    }

    fun updateSortOrder(sortOrder: SortOrder) {
        val currentState = _state.value
        val filteredAndSorted =
            filterAndSortNotes(
                notes = currentState.notes,
                searchQuery = currentState.searchQuery,
                selectedStatus = currentState.selectedStatus,
                sortOrder = sortOrder,
            )

        _state.value =
            currentState.copy(
                sortOrder = sortOrder,
                filteredNotes = filteredAndSorted,
            )
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            fieldNotesRepository.deleteNote(noteId)
                .onSuccess {
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = null,
                        )
                }
                .onFailure { exception ->
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = "Failed to delete note: ${exception.message}",
                        )
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun showExportDialog() {
        _state.value = _state.value.copy(isExportDialogVisible = true)
    }

    fun hideExportDialog() {
        _state.value =
            _state.value.copy(
                isExportDialogVisible = false,
                exportFilter = ExportFilter(),
            )
    }

    fun updateExportFilter(filter: ExportFilter) {
        _state.value = _state.value.copy(exportFilter = filter)
    }

    fun exportNotesWithFilter() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true, exportSuccess = null)

            exportService.exportNotes(_state.value.exportFilter)
                .onSuccess { exportPath ->
                    _state.value =
                        _state.value.copy(
                            isExporting = false,
                            exportSuccess = "Notes exported successfully to: $exportPath",
                            isExportDialogVisible = false,
                            exportFilter = ExportFilter(),
                        )
                }
                .onFailure { exception ->
                    _state.value =
                        _state.value.copy(
                            isExporting = false,
                            error = "Failed to export notes: ${exception.message}",
                        )
                }
        }
    }

    fun shareNotesWithFilter() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true, exportSuccess = null)

            exportService.shareNotes(_state.value.exportFilter)
                .onSuccess {
                    _state.value =
                        _state.value.copy(
                            isExporting = false,
                            exportSuccess = "Notes shared successfully",
                            isExportDialogVisible = false,
                            exportFilter = ExportFilter(),
                        )
                }
                .onFailure { exception ->
                    _state.value =
                        _state.value.copy(
                            isExporting = false,
                            error = "Failed to share notes: ${exception.message}",
                        )
                }
        }
    }

    fun clearExportSuccess() {
        _state.value = _state.value.copy(exportSuccess = null)
    }

    fun showImportDialog() {
        _state.value = _state.value.copy(isImportDialogVisible = true)
    }

    fun hideImportDialog() {
        _state.value =
            _state.value.copy(
                isImportDialogVisible = false,
                importResult = null,
            )
    }

    fun importFromBackup(replaceExisting: Boolean = false) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isImporting = true, importResult = null)

            exportService.importFromBackup(replaceExisting)
                .onSuccess { result ->
                    _state.value =
                        _state.value.copy(
                            isImporting = false,
                            importResult = result,
                            isImportDialogVisible = false,
                        )
                }
                .onFailure { exception ->
                    _state.value =
                        _state.value.copy(
                            isImporting = false,
                            error = "Failed to import backup: ${exception.message}",
                        )
                }
        }
    }

    fun createBackup() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true, exportSuccess = null)

            exportService.createBackup()
                .onSuccess { exportPath ->
                    _state.value =
                        _state.value.copy(
                            isExporting = false,
                            exportSuccess = "Backup created successfully: $exportPath",
                        )
                }
                .onFailure { exception ->
                    _state.value =
                        _state.value.copy(
                            isExporting = false,
                            error = "Failed to create backup: ${exception.message}",
                        )
                }
        }
    }

    fun clearImportResult() {
        _state.value = _state.value.copy(importResult = null)
    }

    fun showClearConfirmDialog() {
        _state.value = _state.value.copy(isClearConfirmDialogVisible = true)
    }

    fun hideClearConfirmDialog() {
        _state.value = _state.value.copy(isClearConfirmDialogVisible = false)
    }

    fun clearAllNotesWithExport() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true, isClearConfirmDialogVisible = false)

            // First create a backup
            exportService.createBackup()
                .onSuccess { exportPath ->
                    // Then clear all notes
                    clearAllNotesInternal()
                    _state.value =
                        _state.value.copy(
                            isExporting = false,
                            exportSuccess = "Backup created before clearing: $exportPath",
                        )
                }
                .onFailure { exception ->
                    _state.value =
                        _state.value.copy(
                            isExporting = false,
                            error = "Failed to create backup before clearing: ${exception.message}",
                        )
                }
        }
    }

    fun clearAllNotesWithoutExport() {
        _state.value = _state.value.copy(isClearConfirmDialogVisible = false)
        clearAllNotesInternal()
    }

    private fun clearAllNotesInternal() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            fieldNotesRepository.clearAllNotes()
                .onSuccess {
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = null,
                        )
                }
                .onFailure { exception ->
                    _state.value =
                        _state.value.copy(
                            isLoading = false,
                            error = "Failed to clear notes: ${exception.message}",
                        )
                }
        }
    }

    private fun updateAvailableFields() {
        val currentNotes = _state.value.notes
        val fields = currentNotes.map { "${it.champ}-${it.parcelle}" }.toSet()
        _state.value = _state.value.copy(availableFields = fields)
    }

    private fun filterAndSortNotes(
        notes: List<FieldNote>,
        searchQuery: String,
        selectedStatus: FieldNoteStatus?,
        sortOrder: SortOrder,
    ): List<FieldNote> {
        var filtered = notes

        if (searchQuery.isNotBlank()) {
            filtered =
                filtered.filter { note ->
                    note.notes.contains(searchQuery, ignoreCase = true) ||
                        note.variety?.contains(searchQuery, ignoreCase = true) == true ||
                        "${note.champ}-${note.parcelle}-${note.rang}-${note.trou}".contains(searchQuery, ignoreCase = true)
                }
        }

        if (selectedStatus != null) {
            filtered = filtered.filter { it.status == selectedStatus }
        }

        return when (sortOrder) {
            SortOrder.POSITION ->
                filtered.sortedWith(
                    compareBy<FieldNote> { it.champ.toIntOrNull() ?: 0 }
                        .thenBy { it.parcelle }
                        .thenBy { it.rang.toIntOrNull() ?: 0 }
                        .thenBy { it.trou.toIntOrNull() ?: 0 },
                )
            SortOrder.DATE_CREATED -> filtered.sortedByDescending { it.timestamp }
            SortOrder.DATE_MODIFIED -> filtered.sortedByDescending { it.lastModified }
            SortOrder.STATUS -> filtered.sortedBy { it.status }
        }
    }
}

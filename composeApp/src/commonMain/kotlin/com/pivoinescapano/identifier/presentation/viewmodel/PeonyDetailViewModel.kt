package com.pivoinescapano.identifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.domain.model.FieldNote
import com.pivoinescapano.identifier.domain.model.FieldNoteStatus
import com.pivoinescapano.identifier.domain.usecase.CreateFieldNoteUseCase
import com.pivoinescapano.identifier.domain.usecase.FindPeonyUseCase
import com.pivoinescapano.identifier.domain.usecase.GetFieldNotesUseCase
import com.pivoinescapano.identifier.domain.usecase.GetFieldSelectionUseCase
import com.pivoinescapano.identifier.domain.usecase.UpdateFieldNoteUseCase
import com.pivoinescapano.identifier.presentation.state.PeonyDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PeonyDetailViewModel(
    private val champ: String,
    private val parcelle: String,
    private val rang: String,
    private val trou: String,
    private val getFieldSelectionUseCase: GetFieldSelectionUseCase,
    private val findPeonyUseCase: FindPeonyUseCase,
    private val createFieldNoteUseCase: CreateFieldNoteUseCase,
    private val updateFieldNoteUseCase: UpdateFieldNoteUseCase,
    private val getFieldNotesUseCase: GetFieldNotesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PeonyDetailState())
    val uiState: StateFlow<PeonyDetailState> = _uiState.asStateFlow()

    init {
        loadDetailData()
    }

    private fun loadDetailData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val fieldEntry = getFieldSelectionUseCase.getFieldEntry(champ, parcelle, rang, trou)

                if (fieldEntry != null) {
                    _uiState.value = _uiState.value.copy(fieldEntry = fieldEntry)

                    val fieldNoteResult = getFieldNotesUseCase.getNotesForPosition(champ, parcelle, rang, trou)
                    val fieldNote = fieldNoteResult.getOrNull()

                    val exactPeony =
                        if (fieldEntry.variety != null) {
                            findPeonyUseCase.execute(fieldEntry.variety)
                        } else {
                            null
                        }

                    val confirmedPeony =
                        if (fieldNote?.variety != null && fieldNote.variety != fieldEntry.variety) {
                            findPeonyUseCase.execute(fieldNote.variety)
                        } else {
                            null
                        }

                    val fuzzyMatches =
                        if (fieldEntry.variety != null && exactPeony == null) {
                            val matches = findPeonyUseCase.findWithFuzzyMatching(fieldEntry.variety, 0.6)

                            // If there's a confirmed peony, include it in the fuzzy matches
                            if (confirmedPeony != null && !matches.contains(confirmedPeony)) {
                                listOf(confirmedPeony) + matches
                            } else {
                                matches
                            }
                        } else if (exactPeony != null && confirmedPeony != null && fieldEntry.variety != null) {
                            // When there's both exact and confirmed peony, show fuzzy matches including the confirmed one
                            val matches = findPeonyUseCase.findWithFuzzyMatching(fieldEntry.variety, 0.6)
                            if (!matches.contains(confirmedPeony)) {
                                listOf(confirmedPeony) + matches
                            } else {
                                matches
                            }
                        } else {
                            emptyList()
                        }

                    _uiState.value =
                        _uiState.value.copy(
                            peony = exactPeony, // Only show exact match as the main peony
                            fuzzyMatches = fuzzyMatches,
                            fieldNote = fieldNote,
                            isPeonyConfirmed = confirmedPeony != null,
                            isLoading = false,
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "No field entry found for the selected position",
                        )
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load field entry: ${e.message}",
                    )
            }
        }
    }

    fun onFuzzyMatchSelected(peony: PeonyInfo) {
        saveSelectedVarietyToFieldNote(peony.cultivar)
        // The state will be updated when loadDetailData() is called after saving the field note
    }

    private fun saveSelectedVarietyToFieldNote(variety: String) {
        viewModelScope.launch {
            try {
                val currentNote = _uiState.value.fieldNote
                if (currentNote != null) {
                    val updatedNote = currentNote.copy(variety = variety)
                    val result = updateFieldNoteUseCase(updatedNote)
                    result.onSuccess { updated ->
                        updateStateWithConfirmedVariety(updated, variety)
                    }.onFailure { error ->
                        println("Failed to update field note variety: ${error.message}")
                    }
                } else {
                    val result =
                        createFieldNoteUseCase(
                            champ = champ,
                            parcelle = parcelle,
                            rang = rang,
                            trou = trou,
                            variety = variety,
                            notes = "",
                            status = FieldNoteStatus.NORMAL,
                        )
                    result.onSuccess { created ->
                        updateStateWithConfirmedVariety(created, variety)
                    }.onFailure { error ->
                        println("Failed to create field note with variety: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                println("Error saving variety to field note: ${e.message}")
            }
        }
    }

    private fun updateStateWithConfirmedVariety(
        updatedFieldNote: FieldNote,
        variety: String,
    ) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                // Find the confirmed peony
                val confirmedPeony = findPeonyUseCase.execute(variety)

                // Update fuzzy matches to include the confirmed peony if not already present
                val currentFuzzyMatches = currentState.fuzzyMatches
                val updatedFuzzyMatches =
                    if (confirmedPeony != null && !currentFuzzyMatches.contains(confirmedPeony)) {
                        listOf(confirmedPeony) + currentFuzzyMatches
                    } else {
                        currentFuzzyMatches
                    }

                _uiState.value =
                    currentState.copy(
                        fieldNote = updatedFieldNote,
                        fuzzyMatches = updatedFuzzyMatches,
                        isPeonyConfirmed = true,
                    )
            } catch (e: Exception) {
                println("Error updating state with confirmed variety: ${e.message}")
                // Fallback to full reload if state update fails
                loadDetailData()
            }
        }
    }

    fun updateFieldNote(notes: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isNoteSaving = true)

                val currentNote = _uiState.value.fieldNote
                if (currentNote != null) {
                    val updatedNote = currentNote.copy(notes = notes)
                    val result = updateFieldNoteUseCase(updatedNote)
                    result.onSuccess { updated ->
                        _uiState.value =
                            _uiState.value.copy(
                                fieldNote = updated,
                                isNoteSaving = false,
                            )
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(isNoteSaving = false)
                        println("Failed to update field note: ${error.message}")
                    }
                } else {
                    val result =
                        createFieldNoteUseCase(
                            champ = champ,
                            parcelle = parcelle,
                            rang = rang,
                            trou = trou,
                            notes = notes,
                        )
                    result.onSuccess { created ->
                        _uiState.value =
                            _uiState.value.copy(
                                fieldNote = created,
                                isNoteSaving = false,
                            )
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(isNoteSaving = false)
                        println("Failed to create field note: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isNoteSaving = false)
                println("Error updating field note: ${e.message}")
            }
        }
    }

    fun updateFieldNoteStatus(status: FieldNoteStatus) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isNoteSaving = true)

                val currentNote = _uiState.value.fieldNote
                if (currentNote != null) {
                    val updatedNote = currentNote.copy(status = status)
                    val result = updateFieldNoteUseCase(updatedNote)
                    result.onSuccess { updated ->
                        _uiState.value =
                            _uiState.value.copy(
                                fieldNote = updated,
                                isNoteSaving = false,
                            )
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(isNoteSaving = false)
                        println("Failed to update field note status: ${error.message}")
                    }
                } else {
                    val result =
                        createFieldNoteUseCase(
                            champ = champ,
                            parcelle = parcelle,
                            rang = rang,
                            trou = trou,
                            status = status,
                        )
                    result.onSuccess { created ->
                        _uiState.value =
                            _uiState.value.copy(
                                fieldNote = created,
                                isNoteSaving = false,
                            )
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(isNoteSaving = false)
                        println("Failed to create field note with status: ${error.message}")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isNoteSaving = false)
                println("Error updating field note status: ${e.message}")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

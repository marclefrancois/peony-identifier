package com.pivoinescapano.identifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pivoinescapano.identifier.data.usecase.GetFieldEntriesUseCase
import com.pivoinescapano.identifier.presentation.state.FieldSelectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FieldSelectionViewModel(
    private val getFieldEntriesUseCase: GetFieldEntriesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FieldSelectionState())
    val uiState: StateFlow<FieldSelectionState> = _uiState.asStateFlow()

    init {
        loadAvailableFields()
    }

    private fun loadAvailableFields() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                val fieldEntries = getFieldEntriesUseCase()
                val champs = fieldEntries.mapNotNull { it.champ }.distinct().sorted()

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        availableChamps = champs,
                    )

                // Auto-select first field if no saved state and fields are available
                if (champs.isNotEmpty() && _uiState.value.selectedChamp == null) {
                    onChampSelected(champs.first())
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load field data: ${e.message}",
                    )
            }
        }
    }

    fun onChampSelected(champ: String) {
        viewModelScope.launch {
            try {
                val fieldEntries = getFieldEntriesUseCase()
                val parcelles =
                    fieldEntries
                        .filter { it.champ == champ }
                        .mapNotNull { it.parcelle }
                        .distinct()
                        .sorted()

                _uiState.value =
                    _uiState.value.copy(
                        selectedChamp = champ,
                        availableParcelles = parcelles,
                        selectedParcelle = null,
                        canContinue = false,
                    )

                // Auto-select first parcel if available and no parcel is currently selected
                if (parcelles.isNotEmpty() && _uiState.value.selectedParcelle == null) {
                    onParcelleSelected(parcelles.first())
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        errorMessage = "Failed to load parcels: ${e.message}",
                    )
            }
        }
    }

    fun onParcelleSelected(parcelle: String) {
        _uiState.value =
            _uiState.value.copy(
                selectedParcelle = parcelle,
                canContinue = true,
            )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // v1.3 State persistence: Restore previous field and parcel selections
    fun restoreSelections(
        champ: String,
        parcelle: String,
    ) {
        viewModelScope.launch {
            try {
                // First restore the field selection and load parcelles
                val fieldEntries = getFieldEntriesUseCase()
                val parcelles =
                    fieldEntries
                        .filter { it.champ == champ }
                        .mapNotNull { it.parcelle }
                        .distinct()
                        .sorted()

                // Restore both field and parcel selections
                _uiState.value =
                    _uiState.value.copy(
                        selectedChamp = champ,
                        availableParcelles = parcelles,
                        selectedParcelle = parcelle,
                        canContinue = true,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        errorMessage = "Failed to restore selections: ${e.message}",
                    )
            }
        }
    }
}

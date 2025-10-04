package com.pivoinescapano.identifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pivoinescapano.identifier.data.cache.DataCacheManager
import com.pivoinescapano.identifier.domain.usecase.GetFieldNotesUseCase
import com.pivoinescapano.identifier.domain.usecase.GetFieldSelectionUseCase
import com.pivoinescapano.identifier.presentation.state.PeonyIdentifierState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PeonyIdentifierViewModel(
    private val selectedChamp: String,
    private val selectedParcelle: String,
    private val getFieldSelectionUseCase: GetFieldSelectionUseCase,
    private val dataCacheManager: DataCacheManager,
    private val getFieldNotesUseCase: GetFieldNotesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PeonyIdentifierState())
    val uiState: StateFlow<PeonyIdentifierState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        preloadDataInBackground()
    }

    private fun preloadDataInBackground() {
        viewModelScope.launch {
            try {
                dataCacheManager.preloadAllData()
            } catch (e: Exception) {
                println("Background data preloading failed: ${e.message}")
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = true,
                        error = null,
                        selectedChamp = selectedChamp,
                        selectedParcelle = selectedParcelle,
                    )

                val parcelles = getFieldSelectionUseCase.getAvailableParcelles(selectedChamp)
                val rangs = getFieldSelectionUseCase.getAvailableRangs(selectedChamp, selectedParcelle)

                _uiState.value =
                    _uiState.value.copy(
                        availableParcelles = parcelles,
                        availableRangs = rangs,
                        isLoading = false,
                    )

                if (rangs.isNotEmpty()) {
                    onRangSelected(rangs.first())
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load field data: ${e.message}",
                    )
            }
        }
    }

    fun onRangSelected(rang: String) {
        viewModelScope.launch {
            try {
                _uiState.value =
                    _uiState.value.copy(
                        selectedRang = rang,
                        selectedTrou = null,
                        availableTrous = emptyList(),
                        currentRowEntries = emptyList(),
                    )

                val trous = getFieldSelectionUseCase.getAvailableTrous(selectedChamp, selectedParcelle, rang)
                val rowEntries = getFieldSelectionUseCase.getRowEntries(selectedChamp, selectedParcelle, rang)

                val rowFieldNotesResult = getFieldNotesUseCase.getNotesForRow(selectedChamp, selectedParcelle, rang)
                val rowFieldNotes = rowFieldNotesResult.getOrElse { emptyList() }

                _uiState.value =
                    _uiState.value.copy(
                        availableTrous = trous,
                        currentRowEntries = rowEntries,
                        rowFieldNotes = rowFieldNotes,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = "Failed to load positions: ${e.message}",
                        currentRowEntries = emptyList(),
                    )
            }
        }
    }

    fun onTrouSelected(trou: String) {
        _uiState.value = _uiState.value.copy(selectedTrou = trou)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun reset() {
        _uiState.value =
            PeonyIdentifierState(
                selectedChamp = selectedChamp,
                selectedParcelle = selectedParcelle,
            )
        loadInitialData()
    }

    fun goToNextRow() {
        val currentRang = _uiState.value.selectedRang ?: return
        val availableRangs = _uiState.value.availableRangs
        val currentIndex = availableRangs.indexOf(currentRang)

        if (currentIndex >= 0 && currentIndex < availableRangs.size - 1) {
            val nextRang = availableRangs[currentIndex + 1]
            onRangSelected(nextRang)
        }
    }

    fun goToPreviousRow() {
        val currentRang = _uiState.value.selectedRang ?: return
        val availableRangs = _uiState.value.availableRangs
        val currentIndex = availableRangs.indexOf(currentRang)

        if (currentIndex > 0) {
            val previousRang = availableRangs[currentIndex - 1]
            onRangSelected(previousRang)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                dataCacheManager.clearCache()
                val currentRang = _uiState.value.selectedRang
                loadInitialData()
                if (currentRang != null) {
                    onRangSelected(currentRang)
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = "Failed to refresh data: ${e.message}",
                    )
            }
        }
    }
}

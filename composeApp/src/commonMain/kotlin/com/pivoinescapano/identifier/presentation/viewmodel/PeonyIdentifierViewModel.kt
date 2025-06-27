package com.pivoinescapano.identifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pivoinescapano.identifier.data.cache.DataCacheManager
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.domain.usecase.FindPeonyUseCase
import com.pivoinescapano.identifier.domain.usecase.GetFieldSelectionUseCase
import com.pivoinescapano.identifier.presentation.state.PeonyIdentifierState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PeonyIdentifierViewModel(
    private val getFieldSelectionUseCase: GetFieldSelectionUseCase,
    private val findPeonyUseCase: FindPeonyUseCase,
    private val dataCacheManager: DataCacheManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PeonyIdentifierState())
    val uiState: StateFlow<PeonyIdentifierState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        preloadDataInBackground()
    }
    
    /**
     * Preload all JSON data in background for improved performance
     */
    private fun preloadDataInBackground() {
        viewModelScope.launch {
            try {
                dataCacheManager.preloadAllData()
            } catch (e: Exception) {
                // Log error but don't show to user as this is background optimization
                println("Background data preloading failed: ${e.message}")
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val champs = getFieldSelectionUseCase.getAvailableChamps()
                _uiState.value = _uiState.value.copy(
                    availableChamps = champs,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load field data: ${e.message}"
                )
            }
        }
    }

    fun onChampSelected(champ: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedChamp = champ,
                    selectedParcelle = null,
                    selectedRang = null,
                    selectedTrou = null,
                    availableParcelles = emptyList(),
                    availableRangs = emptyList(),
                    availableTrous = emptyList(),
                    currentFieldEntry = null,
                    currentPeony = null,
                    showPeonyDetails = false
                )

                val parcelles = getFieldSelectionUseCase.getAvailableParcelles(champ)
                _uiState.value = _uiState.value.copy(availableParcelles = parcelles)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to load parcelles: ${e.message}")
            }
        }
    }

    fun onParcelleSelected(parcelle: String) {
        val currentChamp = _uiState.value.selectedChamp ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedParcelle = parcelle,
                    selectedRang = null,
                    selectedTrou = null,
                    availableRangs = emptyList(),
                    availableTrous = emptyList(),
                    currentFieldEntry = null,
                    currentPeony = null,
                    showPeonyDetails = false
                )

                val rangs = getFieldSelectionUseCase.getAvailableRangs(currentChamp, parcelle)
                _uiState.value = _uiState.value.copy(availableRangs = rangs)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to load rangs: ${e.message}")
            }
        }
    }

    fun onRangSelected(rang: String) {
        val currentChamp = _uiState.value.selectedChamp ?: return
        val currentParcelle = _uiState.value.selectedParcelle ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedRang = rang,
                    selectedTrou = null,
                    availableTrous = emptyList(),
                    currentFieldEntry = null,
                    currentRowEntries = emptyList(),
                    currentPeony = null,
                    showPeonyDetails = false
                )

                val trous = getFieldSelectionUseCase.getAvailableTrous(currentChamp, currentParcelle, rang)
                val rowEntries = getFieldSelectionUseCase.getRowEntries(currentChamp, currentParcelle, rang)
                
                _uiState.value = _uiState.value.copy(
                    availableTrous = trous,
                    currentRowEntries = rowEntries
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load positions: ${e.message}",
                    currentRowEntries = emptyList()
                )
            }
        }
    }

    fun onTrouSelected(trou: String) {
        val currentChamp = _uiState.value.selectedChamp ?: return
        val currentParcelle = _uiState.value.selectedParcelle ?: return
        val currentRang = _uiState.value.selectedRang ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(selectedTrou = trou, isLoading = true)

                val fieldEntry = getFieldSelectionUseCase.getFieldEntry(currentChamp, currentParcelle, currentRang, trou)
                
                if (fieldEntry != null) {
                    _uiState.value = _uiState.value.copy(currentFieldEntry = fieldEntry)
                    
                    // Try to find matching peony
                    val varieteName = fieldEntry.variete
                    val peony = if (varieteName != null) {
                        findPeonyUseCase.execute(varieteName)
                    } else null
                    
                    val fuzzyMatches = if (peony == null && varieteName != null) {
                        findPeonyUseCase.findWithFuzzyMatching(varieteName, 0.6)
                    } else {
                        emptyList()
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        currentPeony = peony,
                        fuzzyMatches = fuzzyMatches,
                        showPeonyDetails = true, // Always show details when we have a field entry
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No field entry found for the selected position"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load field entry: ${e.message}"
                )
            }
        }
    }

    fun onFuzzyMatchSelected(peony: PeonyInfo) {
        _uiState.value = _uiState.value.copy(
            currentPeony = peony,
            fuzzyMatches = emptyList(),
            showPeonyDetails = true
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun reset() {
        _uiState.value = PeonyIdentifierState()
        loadInitialData()
    }
}
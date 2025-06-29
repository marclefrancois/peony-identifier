package com.pivoinescapano.identifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pivoinescapano.identifier.domain.usecase.SearchPeonyLocationsUseCase
import com.pivoinescapano.identifier.presentation.state.PeonySearchState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class PeonySearchViewModel(
    private val searchPeonyLocationsUseCase: SearchPeonyLocationsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PeonySearchState())
    val uiState: StateFlow<PeonySearchState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")
    private var allVarieties: List<String> = emptyList()

    init {
        loadAllVarieties()
        setupDebouncedSearch()
    }

    private fun loadAllVarieties() {
        viewModelScope.launch {
            try {
                allVarieties = searchPeonyLocationsUseCase.getAllUniqueVarieties()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load varieties: ${e.message}"
                )
            }
        }
    }

    private fun setupDebouncedSearch() {
        searchQueryFlow
            .debounce(300) // Wait 300ms after user stops typing
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotBlank()) {
                    performSearch(query)
                } else {
                    _uiState.value = _uiState.value.copy(
                        searchResults = emptyList(),
                        hasSearched = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            suggestions = if (query.isNotBlank()) {
                generateSuggestions(query)
            } else {
                emptyList()
            }
        )
        searchQueryFlow.value = query
    }

    fun onSuggestionSelected(suggestion: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = suggestion,
            suggestions = emptyList()
        )
        searchQueryFlow.value = suggestion
    }

    private fun generateSuggestions(query: String): List<String> {
        return allVarieties
            .filter { it.contains(query, ignoreCase = true) }
            .take(5)
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val results = searchPeonyLocationsUseCase.execute(query)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    searchResults = results,
                    hasSearched = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Search failed: ${e.message}",
                    hasSearched = true
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSearch() {
        _uiState.value = PeonySearchState()
        searchQueryFlow.value = ""
    }
}
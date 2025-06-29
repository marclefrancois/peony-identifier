package com.pivoinescapano.identifier.presentation.state

import com.pivoinescapano.identifier.domain.model.PeonyLocation

data class PeonySearchState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<PeonyLocation> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val hasSearched: Boolean = false,
    val errorMessage: String? = null,
)

package com.pivoinescapano.identifier.presentation.state

data class FieldSelectionState(
    val isLoading: Boolean = true,
    val availableChamps: List<String> = emptyList(),
    val availableParcelles: List<String> = emptyList(),
    val selectedChamp: String? = null,
    val selectedParcelle: String? = null,
    val canContinue: Boolean = false,
    val errorMessage: String? = null
)
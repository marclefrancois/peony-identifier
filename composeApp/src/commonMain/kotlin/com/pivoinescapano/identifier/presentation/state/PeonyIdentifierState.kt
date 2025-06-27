package com.pivoinescapano.identifier.presentation.state

import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.model.PeonyInfo

data class PeonyIdentifierState(
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Available selections for dropdowns
    val availableChamps: List<String> = emptyList(),
    val availableParcelles: List<String> = emptyList(),
    val availableRangs: List<String> = emptyList(),
    val availableTrous: List<String> = emptyList(),
    
    // Current selections
    val selectedChamp: String? = null,
    val selectedParcelle: String? = null,
    val selectedRang: String? = null,
    val selectedTrou: String? = null,
    
    // Field entry and peony details
    val currentFieldEntry: FieldEntry? = null,
    val currentPeony: PeonyInfo? = null,
    val fuzzyMatches: List<PeonyInfo> = emptyList(),
    
    // UI flags
    val showPeonyDetails: Boolean = false
)
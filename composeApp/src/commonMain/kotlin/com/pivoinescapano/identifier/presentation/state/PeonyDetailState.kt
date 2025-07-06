package com.pivoinescapano.identifier.presentation.state

import com.pivoinescapano.identifier.data.model.FieldEntry
import com.pivoinescapano.identifier.data.model.PeonyInfo
import com.pivoinescapano.identifier.domain.model.FieldNote

data class PeonyDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val fieldEntry: FieldEntry? = null,
    val peony: PeonyInfo? = null,
    val fuzzyMatches: List<PeonyInfo> = emptyList(),
    val isPeonyConfirmed: Boolean = false,
    val fieldNote: FieldNote? = null,
    val isNoteSaving: Boolean = false,
)

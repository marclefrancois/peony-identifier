package com.pivoinescapano.identifier.domain.model

import com.pivoinescapano.identifier.platform.currentTimeMillis
import kotlinx.serialization.Serializable

@Serializable
data class FieldNote(
    val id: String,
    val champ: String,
    val parcelle: String,
    val rang: String,
    val trou: String,
    val variety: String? = null,
    val notes: String = "",
    val status: FieldNoteStatus = FieldNoteStatus.NORMAL,
    val timestamp: Long = currentTimeMillis(),
    val lastModified: Long = currentTimeMillis(),
)

@Serializable
enum class FieldNoteStatus {
    NORMAL,
    DEAD,
    BLOCKED,
}

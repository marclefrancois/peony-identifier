package com.pivoinescapano.identifier.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FieldEntry(
    val champ: String,
    val parcelle: String,
    val rang: String?,
    val trou: String?,
    val variete: String?,
    val annee_plantation: String? = null,
    val taille: String? = null,
    val etiquette: String? = null,
    val vente: String? = null
)
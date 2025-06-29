package com.pivoinescapano.identifier.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the Peony Identifier app.
 * These serializable classes define the navigation destinations and their parameters.
 */

@Serializable
data class FieldSelectionRoute(
    val initialChamp: String? = null,
    val initialParcelle: String? = null,
)

@Serializable
data class PeonyIdentifierRoute(
    val champ: String,
    val parcelle: String,
    val selectedRang: String? = null,
    val selectedTrou: String? = null,
)

@Serializable
data class PeonyDetailRoute(
    val champ: String,
    val parcelle: String,
    val rang: String,
    val trou: String,
    val fromSearchTerm: String? = null,
)

@Serializable
object PeonySearchRoute

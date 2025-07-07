package com.pivoinescapano.identifier.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FieldEntry(
    val champ: String?,
    @SerialName("parcelle")
    val parcel: String?,
    val rang: String?,
    val trou: String?,
    @SerialName("variete")
    val variety: String?,
    @SerialName("annee_plantation")
    val yearPlanted: String? = null,
    @SerialName("taille")
    val size: String? = null,
    val etiquette: String? = null,
    @SerialName("vente")
    val forSale: String? = null,
)

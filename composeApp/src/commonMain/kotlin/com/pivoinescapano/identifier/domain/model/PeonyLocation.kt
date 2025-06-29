package com.pivoinescapano.identifier.domain.model

data class PeonyLocation(
    val champ: String,
    val parcelle: String,
    val rang: String,
    val trou: String,
    val variete: String?,
    val taille: String?,
    val fullLocationName: String,
) {
    companion object {
        fun fromFieldEntry(
            champ: String,
            parcelle: String,
            rang: String,
            trou: String,
            variete: String?,
            taille: String?,
        ): PeonyLocation {
            return PeonyLocation(
                champ = champ,
                parcelle = parcelle,
                rang = rang,
                trou = trou,
                variete = variete,
                taille = taille,
                fullLocationName = "Field $champ, Parcel $parcelle, Row $rang, Position $trou",
            )
        }
    }
}
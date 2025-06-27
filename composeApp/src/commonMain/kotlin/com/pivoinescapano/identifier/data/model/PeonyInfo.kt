package com.pivoinescapano.identifier.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PeonyInfo(
    val id: Int,
    val cultivar: String,
    val originator: String,
    val date: String,
    val group: String,
    val reference: String? = null,
    val country: String? = null,
    val description: String,
    val image: String,
    val url: String
)
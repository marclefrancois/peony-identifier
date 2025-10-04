package com.pivoinescapano.identifier.data.cache

import kotlinx.serialization.Serializable

@Serializable
data class CachedData<T>(
    val data: T,
    val metadata: CacheMetadata,
)

package com.pivoinescapano.identifier.data.cache

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Serializable
data class CacheMetadata(
    val lastUpdated: Long,
    val version: Int = 1,
) {
    fun isExpired(expirationDuration: Duration = 24.hours): Boolean {
        val now = Clock.System.now()
        val lastUpdatedInstant = Instant.fromEpochMilliseconds(lastUpdated)
        return (now - lastUpdatedInstant) > expirationDuration
    }

    companion object {
        fun now() = CacheMetadata(lastUpdated = Clock.System.now().toEpochMilliseconds())
    }
}

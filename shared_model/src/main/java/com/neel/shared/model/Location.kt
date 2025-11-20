package com.neel.shared.model

/**
 * A location reading with time.
 */
data class Location(
    val location: Coordinate,
    val timestampMillis: Long,
)
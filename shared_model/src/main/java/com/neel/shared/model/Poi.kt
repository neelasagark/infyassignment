package com.neel.shared.model

/**
 * A point of interest, which is a map location with extra details, like a shop or restaurant.
 */
data class Poi(
    val id: String,
    val name: String,
    val category: PoiCategory,
    val coordinate: Coordinate,
    val address: String? = null,
    val distanceInMeters: Double? = null,
    val ratingScore: Double? = null,
    val reviewCount: Int? = null,
)

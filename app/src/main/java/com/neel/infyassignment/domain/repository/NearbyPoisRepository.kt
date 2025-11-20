package com.neel.infyassignment.domain.repository

import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository for nearby POIs.
 *
 * It exposes:
 * - Current POIs (based on algorithm suggests now).
 * - POI history (that was stored in the app run).
 * - Optional filtering by category for both current and history.
 */
interface NearbyPoisRepository {
    /**
     * Flow of current POIs.
     *
     * If [category] is not null, only POIs with that category are included.
     * If [category] is null, all current POIs are returned without filtering.
     */
    fun observeCurrentPoisByCategory(
        category: PoiCategory? = null,
    ): Flow<List<Poi>>

    /**
     * Flow of historical POIs.
     *
     * If [category] is not null, only POIs with that category are included.
     * If [category] is null, the full history is returned without filtering.
     */
    fun observeHistoricalPoisByCategory(
        category: PoiCategory? = null,
    ): Flow<List<Poi>>

    /**
     * Clears the stored POI history.
     * Does not have to affect the current POIs in memory.
     */
    fun clearHistory()

}

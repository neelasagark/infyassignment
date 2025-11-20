package com.neel.infyassignment.data.poi

import com.neel.infyassignment.domain.repository.NearbyPoisRepository
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *  Repository that forwards POI calls to [PoisManager].
 *
 * - Current POIs are exposed as Flow.
 * - Historical POIs are loaded on demand and wrapped into Flow.
 */
class PoisRepository(
    private val poiManager: PoisManager,
) : NearbyPoisRepository {

    override fun observeCurrentPoisByCategory(
        category: PoiCategory?,
    ): Flow<List<Poi>> {
        return if (category == null) {
            // All current POIs
            poiManager.currentPoisFlow
        } else {
            // Filtered by category
            poiManager.observeCurrentPoisByCategory(category)
        }
    }

    override fun observeHistoricalPoisByCategory(
        category: PoiCategory?,
    ): Flow<List<Poi>> {
        return flow {
            val result: List<Poi> = if (category == null) {
                // All historical POIs
                poiManager.loadHistoricalPois()
            } else {
                // Historical POIs for a specific category
                poiManager.loadHistoricalPoisByCategory(category)
            }
            emit(result)
        }
    }

    override fun clearHistory() {
        poiManager.clearHistory()
    }
}

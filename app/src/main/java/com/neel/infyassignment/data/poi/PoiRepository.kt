package com.neel.infyassignment.data.poi

import com.neel.infyassignment.domain.repository.NearbyPoisRepository
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import kotlinx.coroutines.flow.Flow

class PoiRepository(
    private val poiManager: PoiManager,
) : NearbyPoisRepository {

    override fun observeCurrentPoisByCategory(
        category: PoiCategory?,
    ): Flow<List<Poi>> {
        return if (category == null) {
            poiManager.currentPoisFlow
        } else {
            poiManager.observeCurrentPoisByCategory(category)
        }
    }

    override fun observeHistoricalPoisByCategory(
        category: PoiCategory?,
    ): Flow<List<Poi>> {
        return if (category == null) {
            poiManager.historicalPoisFlow
        } else {
            poiManager.observeHistoricalPoisByCategory(category)
        }
    }

    override fun clearHistory() {
        poiManager.clearHistory()
    }
}

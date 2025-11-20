package com.neel.infyassignment.domain.usecase

import com.neel.infyassignment.domain.repository.NearbyPoisRepository
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import kotlinx.coroutines.flow.Flow

/**
 * Use case that exposes the current nearby POIs as a Flow.
 *
 * If [category] is not null, only POIs with that category are returned.
 * If [category] is null, all current POIs are returned without filtering.
 */
class GetCurrentPoisUseCase(
    private val nearbyPoisRepository: NearbyPoisRepository,
) {

    operator fun invoke(category: PoiCategory? = null): Flow<List<Poi>> {
        return nearbyPoisRepository.observeCurrentPoisByCategory(category)
    }
}

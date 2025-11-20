package com.neel.infyassignment.domain.usecase

import com.neel.infyassignment.domain.repository.NearbyPoisRepository

/**
 * Use case for clearing stored POI history.
 */
class ClearHistoricalPoisUseCase(
    private val nearbyPoisRepository: NearbyPoisRepository,
) {
    operator fun invoke() {
        nearbyPoisRepository.clearHistory()
    }
}

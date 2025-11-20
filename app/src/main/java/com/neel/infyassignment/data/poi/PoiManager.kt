package com.neel.infyassignment.data.poi

import com.neel.infyassignment.data.storage.HistoricalPoisStorage
import com.neel.nearbyplaces.api.NearbyPoisApi
import com.neel.platform.api.PlatformApi
import com.neel.shared.model.Location
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PoiManager(
    private val platformApi: PlatformApi,
    private val nearbyPoisApi: NearbyPoisApi,
    private val coroutineScope: CoroutineScope,
    private val historicalPoisStorage: HistoricalPoisStorage,
) {

    private val rawLocationHistory: MutableList<Location> = mutableListOf()
    private val historicalPoiList: MutableList<Poi> = mutableListOf()

    private val currentPois: MutableStateFlow<List<Poi>> =
        MutableStateFlow(emptyList())

    private val historicalPoisState: MutableStateFlow<List<Poi>> =
        MutableStateFlow(emptyList())

    private val currentCategoryState: MutableStateFlow<PoiCategory> =
        MutableStateFlow(PoiCategory.UNKNOWN)

    val currentPoisFlow: StateFlow<List<Poi>> = currentPois.asStateFlow()
    val historicalPoisFlow: StateFlow<List<Poi>> = historicalPoisState.asStateFlow()

    init {
        // Load history from storage.
        coroutineScope.launch {
            val storedHistory: List<Poi> = historicalPoisStorage.readPois()
            if (storedHistory.isNotEmpty()) {
                historicalPoiList.addAll(storedHistory)
                historicalPoisState.value = storedHistory
            }
        }

        // Listen to location updates.
        coroutineScope.launch {
            platformApi.observeCurrentLocation().collect { newLocation ->
                rawLocationHistory.add(newLocation)
                recomputePoisSuggestions()
            }
        }

        // Listen to category changes.
        coroutineScope.launch {
            platformApi.observeCurrentNearbyPoiCategory().collect { newCategory ->
                currentCategoryState.value = newCategory
                recomputePoisSuggestions()
            }
        }

        // Listen to vehicle info if needed later.
        coroutineScope.launch {
            platformApi.observeVehicleInfo().collect { vehicle ->
                // Placeholder: could adjust algorithm based on vehicle type later.
            }
        }
    }

    /**
     * Rebuilds current suggestions based on:
     * - full location history
     * - current category
     */
    private fun recomputePoisSuggestions() {
        val locationHistorySnapshot: List<Location> = rawLocationHistory.toList()
        val currentCategory: PoiCategory = currentCategoryState.value

        val suggestedPois: List<Poi> =
            nearbyPoisApi.getCurrentNearbyPois(locationHistorySnapshot, currentCategory)

        updateCurrentPois(suggestedPois)
        addPoisToHistory(suggestedPois)
    }

    private fun updateCurrentPois(suggestedPois: List<Poi>) {
        currentPois.value = suggestedPois
    }

    private fun addPoisToHistory(newPoiList: List<Poi>) {
        if (newPoiList.isEmpty()) {
            return
        }

        val existingIds: MutableSet<String> =
            historicalPoiList.mapTo(mutableSetOf()) { poi -> poi.id }

        for (poi in newPoiList) {
            if (existingIds.add(poi.id)) {
                historicalPoiList.add(poi)
            }
        }

        val historySnapshot: List<Poi> = historicalPoiList.toList()
        historicalPoisState.value = historySnapshot

        coroutineScope.launch {
            historicalPoisStorage.savePois(historySnapshot)
        }
    }

    fun observeCurrentPoisByCategory(category: PoiCategory): Flow<List<Poi>> {
        return currentPoisFlow.map { currentList ->
            currentList.filter { poi -> poi.category == category }
        }
    }

    fun observeHistoricalPoisByCategory(category: PoiCategory): Flow<List<Poi>> {
        return historicalPoisFlow.map { fullHistory ->
            fullHistory.filter { poi -> poi.category == category }
        }
    }

    fun clearHistory() {
        historicalPoiList.clear()
        historicalPoisState.value = emptyList()

        coroutineScope.launch {
            historicalPoisStorage.clear()
        }
    }
}


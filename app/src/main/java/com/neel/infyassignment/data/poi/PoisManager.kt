package com.neel.infyassignment.data.poi

import com.neel.infyassignment.data.storage.HistoricalPoisStorage
import com.neel.nearbypois.api.NearbyPoisApi
import com.neel.platform.api.PlatformApi
import com.neel.shared.model.Location
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

/**
 * Builds current POI suggestions from platform data and nearby POI algorithm.
 */
@Suppress("MagicNumber")
class PoisManager(
    private val platformApi: PlatformApi,
    private val nearbyPoisApi: NearbyPoisApi,
    private val historicalPoisStorage: HistoricalPoisStorage,
    private val coroutineScope: CoroutineScope,
) {
    private val rawLocation: MutableList<Location> = mutableListOf()

    private val currentPoisState: MutableStateFlow<List<Poi>> =
        MutableStateFlow(emptyList())

    /**
     * Current POI suggestions while the app is running.
     */
    val currentPoisFlow: StateFlow<List<Poi>> = currentPoisState.asStateFlow()

    /**
     * Last category selected by the user.
     */
    private var currentCategory: PoiCategory = PoiCategory.UNKNOWN

    init {
        coroutineScope.launch {
            platformApi.observeCurrentLocation()
                .sample(5_000L) // recompute at most once every 5 seconds,
                // or other logic need to be implemented for lowering the frequency
                .collect { newLocation ->
                    rawLocation.add(newLocation)
                    recomputePoisSuggestions()
                }
        }

    }

    private fun recomputePoisSuggestions() {
        if (rawLocation.isEmpty()) {
            currentPoisState.value = emptyList()
            return
        }

        val locationHistorySnapshot: List<Location> = rawLocation.toList()
        val category: PoiCategory = currentCategory

        val suggestedPois: List<Poi> =
            nearbyPoisApi.getCurrentNearbyPois(locationHistorySnapshot, category)

        currentPoisState.value = suggestedPois
        savePoisToHistory(suggestedPois)
    }

    private fun savePoisToHistory(newPoiList: List<Poi>) {
        if (newPoiList.isEmpty()) {
            return
        }

        // for now, no filtering or merging with current list, just save to db.
        coroutineScope.launch {
            historicalPoisStorage.savePois(newPoiList)
        }
    }

    /**
     * Current POIs filtered by [category].
     */
    fun observeCurrentPoisByCategory(category: PoiCategory): StateFlow<List<Poi>> {
        val filteredState = MutableStateFlow<List<Poi>>(emptyList())

        coroutineScope.launch {
            currentPoisFlow
                .map { currentList -> currentList.filter { poi -> poi.category == category } }
                .collect { filteredList -> filteredState.value = filteredList }
        }

        return filteredState
    }

    /**
     * Reads all stored POIs from history on demand.
     */
    suspend fun loadHistoricalPois(): List<Poi> {
        return historicalPoisStorage.readPois()
    }

    /**
     * Reads stored POIs for a given [category] on demand.
     */
    suspend fun loadHistoricalPoisByCategory(category: PoiCategory): List<Poi> {
        val allPois: List<Poi> = historicalPoisStorage.readPois()
        return allPois.filter { poi -> poi.category == category }
    }

    /**
     * Clears stored POI history.
     */
    fun clearHistory() {
        coroutineScope.launch {
            historicalPoisStorage.clear()
        }
    }
}

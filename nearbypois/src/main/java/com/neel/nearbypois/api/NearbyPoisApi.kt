package com.neel.nearbypois.api

import com.neel.shared.model.Location
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory

/**
 * API interface for finding nearby pois for the user.
 *
 * This API can be used inside the app to apply filtering logic on:
 * the vehicle's recent locations
 * the current POI category (for example restaurant or charging station)
 *
 * The implementation can live in the app module or in a separate
 * nearby-location module.
 */
interface NearbyPoisApi {

    /**
     * Builds the list of POIs that should be suggested to the user.
     *
     * @param rawLocationList full list of raw vehicle locations.
     * @param currentCategory currently selected POI category
     *
     * @return POIs that match the category and the movement of the vehicle.
     */
    // for now i have not put this as suspend function, but if the algorithm depends on
    // heavy computation and RESTApi calls then this function need to be made suspend
    fun getCurrentNearbyPois(
        rawLocationList: List<Location>,
        currentCategory: PoiCategory,
    ): List<Poi>
}

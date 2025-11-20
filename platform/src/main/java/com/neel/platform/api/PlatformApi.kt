package com.neel.platform.api

import com.neel.shared.model.Location
import com.neel.shared.model.PoiCategory
import com.neel.shared.model.VehicleInfo
import kotlinx.coroutines.flow.Flow

/**
 * Platform API used by the app.
 *
 * This interface hides how we read data from the car platform
 * (AAOS, Android services, VHAL, etc.).
 *
 * The app and domain code only talk to this API and do not know
 * about CarPropertyManager, LocationManager, or other low-level details.
 * The implementation (for example `VehiclePlatform`) can read from
 * those sources and expose clean models as Flows.
 */
interface PlatformApi {

    /**
     * Flow of vehicle information.
     */
    fun observeVehicleInfo(): Flow<VehicleInfo>

    /**
     * Flow of raw vehicle locations.
     */
    fun observeCurrentLocation(): Flow<Location>

    /**
     * Flow of current POI category for nearby search.
     */
    fun observeCurrentNearbyPoiCategory(): Flow<PoiCategory>
}
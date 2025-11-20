package com.neel.nearbypois

import com.neel.nearbypois.api.NearbyPoisApi
import com.neel.platform.api.PlatformApi
import com.neel.shared.model.EngineType
import com.neel.shared.model.Location
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import com.neel.shared.model.VehicleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Its the implementation of [NearbyPoisApi] for a specific car model.
 *
 * This class is to be used by the "algorithm team".
 * They can implement the filtering logic.
 *  Use the full history of vehicle locations to understand where the car has been in the near time.
 *  Call their own REST API client (for example, Foursquare or any other data provider)
 *   to fetch POIs around the current position.
 * Apply their own filtering and scoring logic per [PoiCategory]and vehicle information[like for EV, ICE, Engine etc].
 *
 * The idea is that this module can evolve independently:
 *
 * It can bring its own DI setup (Hilt/Koin/manual) if needed.
 * It can follow its own clean architecture inside this module.
 * The rest of the app only depends on the [NearbyPoisApi] interface,
 *   not on how the algorithm is implemented.
 */

class NearbyPois(
    private val platformApi: PlatformApi,
    private val coroutineScope: CoroutineScope,
) : NearbyPoisApi {

    private var currentVehicleType: VehicleType = VehicleType.UNKNOWN
    private var currentEngineType: EngineType = EngineType.UNKNOWN

    init {
        // Listen to VehicleInfo once and keep updating the latest type/engine.
        coroutineScope.launch {
            platformApi.observeVehicleInfo().collect { vehicleInfo ->
                currentVehicleType = vehicleInfo.vehicleType ?: VehicleType.UNKNOWN
                currentEngineType = vehicleInfo.engineType ?: EngineType.UNKNOWN

            }
        }
    }

    override fun getCurrentNearbyPois(
        rawLocationList: List<Location>,
        currentCategory: PoiCategory,
    ): List<Poi> {

        if (rawLocationList.isEmpty()) {
            return emptyList()
        }

        return when (currentCategory) {
            PoiCategory.RESTAURANT -> filterForRestaurants(rawLocationList)
            PoiCategory.CHARGING_STATION -> filterForChargingStations(rawLocationList)
            PoiCategory.GAS_STATION -> filterForGasStations(rawLocationList)
            PoiCategory.SUPERMARKET -> filterForSupermarkets(rawLocationList)
            PoiCategory.PARKING -> filterForParking(rawLocationList)
            PoiCategory.ALL -> emptyList()
            PoiCategory.UNKNOWN -> emptyList()
        }
    }

    private fun filterForRestaurants(
        locationList: List<Location>,
    ): List<Poi> {
        return emptyList()
    }

    private fun filterForChargingStations(
        locationList: List<Location>,
    ): List<Poi> {
        return emptyList()
    }

    private fun filterForGasStations(
        locationList: List<Location>,
    ): List<Poi> {
        return emptyList()
    }

    private fun filterForSupermarkets(
        locationList: List<Location>,
    ): List<Poi> {
        return emptyList()

    }

    private fun filterForParking(
        locationList: List<Location>,
    ): List<Poi> {
        return emptyList()
    }

}

package com.neel.platform

import android.Manifest
import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.neel.platform.api.PlatformApi
import com.neel.shared.model.Coordinate
import com.neel.shared.model.EngineType
import com.neel.shared.model.Location as VehicleLocation
import com.neel.shared.model.PoiCategory
import com.neel.shared.model.VehicleInfo
import com.neel.shared.model.VehicleType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * PlatformApi implementation used by the app.
 *
 * It reads data directly from:
 * AAOS CarPropertyManager for vehicle info.
 * Android LocationManager for the current location.
 *
 * I kept it simple and only use the built-in platform sources.
 */
class VehiclePlatform(
    private val context: Context,
) : PlatformApi {

    /**
     * Lazy creation of [CarPropertyManager] instance.
     * Returns null if car services are not available on the device.
     */
    private val carPropertyManager: CarPropertyManager? by lazy {
        createCarPropertyManager()
    }

    /**
     * A [Flow] of [VehicleInfo].
     *
     * It reads basic info from [CarPropertyManager] once and emits it.
     * If the model name cannot be read, it falls back to "Unknown model".
     */
    override fun observeVehicleInfo(): Flow<VehicleInfo> {
        return flow {
            val propertyManager: CarPropertyManager? = carPropertyManager

            val identificationId: String? = propertyManager?.let { manager ->
                readStringProperty(
                    manager = manager,
                    propertyId = VehiclePropertyIds.INFO_VIN,
                )
            }

            val modelName: String = propertyManager?.let { manager ->
                readStringProperty(
                    manager = manager,
                    propertyId = VehiclePropertyIds.INFO_MODEL,
                )
            } ?: "Unknown model"

            val vehicleType: VehicleType = propertyManager?.let { manager ->
                val rawType: Int? = readIntProperty(
                    manager = manager,
                    propertyId = 324345345, // have to map to right Vehicle Type with Hal layer.
                )

                // this function can be moved to separate mapper
                when (rawType) {
                    1 -> VehicleType.CAR
                    2 -> VehicleType.TRUCK
                    3 -> VehicleType.MOTORBIKE
                    4 -> VehicleType.VAN
                    5 -> VehicleType.BUS
                    else -> VehicleType.UNKNOWN
                }
            } ?: VehicleType.CAR

            val engineType: EngineType = propertyManager?.let { manager ->
                val rawFuelType: Int? = readIntProperty(
                    manager = manager,
                    propertyId = VehiclePropertyIds.INFO_FUEL_TYPE,
                )

                // this function can be moved to separate mapper
                when (rawFuelType) {
                    1 -> EngineType.PETROL
                    2 -> EngineType.DIESEL
                    3 -> EngineType.ELECTRIC
                    4 -> EngineType.HYBRID
                    5 -> EngineType.PLUG_IN_HYBRID
                    else -> EngineType.UNKNOWN
                }
            } ?: EngineType.ELECTRIC

            emit(
                VehicleInfo(
                    identificationNumber = identificationId,
                    model = modelName,
                    vehicleType = vehicleType,
                    engineType = engineType,
                ),
            )
        }.flowOn(Dispatchers.IO)
    }

    /**
     * A [Flow] of current vehicle locations.
     *
     * It reads the last known location once from [LocationManager]
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION])
    override fun observeCurrentLocation(): Flow<VehicleLocation> {
        return flow {
            val systemLocationManager: LocationManager? =
                context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

            if (systemLocationManager == null) {
                return@flow
            }

            val hasFineLocationPermission: Boolean =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED

            if (!hasFineLocationPermission) {
                return@flow
            }

            val lastAvailableLocation: Location? =
                systemLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: systemLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

            if (lastAvailableLocation == null) {
                return@flow
            }

            val vehicleLocation = VehicleLocation(
                location = Coordinate(
                    latitude = lastAvailableLocation.latitude,
                    longitude = lastAvailableLocation.longitude,
                ),
                timestampMillis = lastAvailableLocation.time,
            )

            emit(vehicleLocation)
        }.flowOn(Dispatchers.IO)
    }

    private fun createCarPropertyManager(): CarPropertyManager? {
        return try {
            val carInstance: Car = Car.createCar(context)
            carInstance.getCarManager(Car.PROPERTY_SERVICE) as? CarPropertyManager
        } catch (throwable: Throwable) {
            null
        }
    }

    /**
     * Helper to read a String car property.
     */
    private fun readStringProperty(
        manager: CarPropertyManager,
        propertyId: Int,
        areaId: Int = 0,
    ): String? {
        return try {
            val propertyValue = manager.getProperty<String>(
                propertyId,
                areaId,
            )
            propertyValue?.value as? String
        } catch (throwable: Throwable) {
            null
        }
    }

    /**
     * Helper to read an Int car property.
     */
    private fun readIntProperty(
        manager: CarPropertyManager,
        propertyId: Int,
        areaId: Int = 0,
    ): Int? {
        return try {
            val propertyValue = manager.getProperty<Int>(
                propertyId,
                areaId,
            )
            propertyValue?.value as? Int
        } catch (throwable: Throwable) {
            null
        }
    }

}

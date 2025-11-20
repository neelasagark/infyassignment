package com.neel.shared.model

/**
 * Basic details about the vehicle.
 */
data class VehicleInfo(
    val identificationNumber: String?,
    val vehicleType: VehicleType?,
    val engineType: EngineType?,
    val model: String,
)

/**
 * Type of vehicle.
 */
enum class VehicleType {
    CAR,
    TRUCK,
    MOTORBIKE,
    VAN,
    BUS,
    UNKNOWN,
}

/**
 * Type of engine.
 */
enum class EngineType {
    PETROL,
    DIESEL,
    HYBRID,
    PLUG_IN_HYBRID,
    ELECTRIC,
    UNKNOWN,
}

package com.neel.shared.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Test for shared models.
 */
class SharedModelTest {

    @Test
    fun coordinateKeepsValues() {
        val point = Coordinate(
            latitude = 51.0,
            longitude = 5.0,
        )

        assertEquals(51.0, point.latitude, 0.0)
        assertEquals(5.0, point.longitude, 0.0)
    }

    @Test
    fun locationKeepsCoordinateAndTime() {
        val point = Coordinate(10.0, 20.0)
        val time = 1_000L

        val location = Location(
            location = point,
            timestampMillis = time,
        )

        assertEquals(point, location.location)
        assertEquals(time, location.timestampMillis)
    }

    @Test
    fun poiUsesDefaultsWhenNotGiven() {
        val poi = Poi(
            id = "id-1",
            name = "Test Place",
            category = PoiCategory.RESTAURANT,
            coordinate = Coordinate(1.0, 2.0),
        )

        assertNull(poi.address)
        assertNull(poi.distanceInMeters)
        assertNull(poi.ratingScore)
        assertNull(poi.reviewCount)
    }

    @Test
    fun vehicleInfoKeepsValues() {
        val info = VehicleInfo(
            identificationNumber = "TEST VIN-1",
            vehicleType = VehicleType.CAR,
            engineType = EngineType.PETROL,
            model = "Test Model",
        )

        assertEquals("TEST VIN-1", info.identificationNumber)
        assertEquals(VehicleType.CAR, info.vehicleType)
        assertEquals(EngineType.PETROL, info.engineType)
        assertEquals("Test Model", info.model)
    }
}

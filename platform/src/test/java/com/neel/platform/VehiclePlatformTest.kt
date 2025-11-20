package com.neel.platform

import android.car.Car
import android.content.Context
import com.neel.shared.model.EngineType
import com.neel.shared.model.PoiCategory
import com.neel.shared.model.VehicleType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic

@OptIn(ExperimentalCoroutinesApi::class)
class VehiclePlatformTest {

    @Test
    fun observeVehicleInfoUsesFallbackWhenCarServiceIsMissing() = runTest {
        // GIVEN
        val context: Context = mockk(relaxed = true)

        mockkStatic(Car::class)
        every { Car.createCar(context) } throws RuntimeException("no car service")

        val platform = VehiclePlatform(context)

        // wWHEN
        val info = platform.observeVehicleInfo().first()

        // THEN
        assertNull(info.identificationNumber)
        assertEquals("Unknown model", info.model)
        assertEquals(VehicleType.CAR, info.vehicleType)
        assertEquals(EngineType.ELECTRIC, info.engineType)
    }

    @Test
    fun observeCurrentNearbyPoiCategoryAlwaysEmitsUnknownForNow() = runTest {
        val context: Context = mockk(relaxed = true)
        val platform = VehiclePlatform(context)

        val category = platform.observeCurrentNearbyPoiCategory().first()

        assertEquals(PoiCategory.UNKNOWN, category)
    }
}

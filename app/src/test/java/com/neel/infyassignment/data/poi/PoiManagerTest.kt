package com.neel.infyassignment.data.poi

import com.neel.infyassignment.data.storage.HistoricalPoisStorage
import com.neel.nearbypois.api.NearbyPoisApi
import com.neel.platform.api.PlatformApi
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PoiManagerTest {

    @Test
    fun `loadHistoricalPois returns all stored pois`() = runTest {
        val platformApi: PlatformApi = mockk {
            every { observeCurrentLocation() } returns emptyFlow()
        }
        val nearbyPoisApi: NearbyPoisApi = mockk(relaxed = true)
        val historicalPoisStorage: HistoricalPoisStorage = mockk()

        val storedPois: List<Poi> = listOf(
            mockk(),
            mockk(),
        )

        coEvery { historicalPoisStorage.readPois() } returns storedPois

        val poiManager = PoisManager(
            platformApi = platformApi,
            nearbyPoisApi = nearbyPoisApi,
            historicalPoisStorage = historicalPoisStorage,
            coroutineScope = TestScope(this.testScheduler),
        )

        val result: List<Poi> = poiManager.loadHistoricalPois()

        assertEquals(storedPois, result)
        coVerify(exactly = 1) { historicalPoisStorage.readPois() }
    }

    @Test
    fun `loadHistoricalPoisByCategory filters by category`() = runTest {
        val platformApi: PlatformApi = mockk {
            every { observeCurrentLocation() } returns emptyFlow()
        }
        val nearbyPoisApi: NearbyPoisApi = mockk(relaxed = true)
        val historicalPoisStorage: HistoricalPoisStorage = mockk()

        val restaurantPoi: Poi = mockk {
            every { category } returns PoiCategory.RESTAURANT
        }
        val chargingPoi: Poi = mockk {
            every { category } returns PoiCategory.CHARGING_STATION
        }

        val storedPois: List<Poi> = listOf(
            restaurantPoi,
            chargingPoi,
        )

        coEvery { historicalPoisStorage.readPois() } returns storedPois

        val poiManager = PoisManager(
            platformApi = platformApi,
            nearbyPoisApi = nearbyPoisApi,
            historicalPoisStorage = historicalPoisStorage,
            coroutineScope = TestScope(this.testScheduler),
        )

        val result: List<Poi> =
            poiManager.loadHistoricalPoisByCategory(PoiCategory.CHARGING_STATION)

        assertEquals(listOf(chargingPoi), result)
        coVerify(exactly = 1) { historicalPoisStorage.readPois() }
    }

    @Test
    fun `clearHistory calls storage clear`() = runTest {
        val platformApi: PlatformApi = mockk {
            every { observeCurrentLocation() } returns emptyFlow()
        }
        val nearbyPoisApi: NearbyPoisApi = mockk(relaxed = true)
        val historicalPoisStorage: HistoricalPoisStorage = mockk(relaxed = true)

        val poiManager = PoisManager(
            platformApi = platformApi,
            nearbyPoisApi = nearbyPoisApi,
            historicalPoisStorage = historicalPoisStorage,
            coroutineScope = TestScope(this.testScheduler),
        )

        poiManager.clearHistory()

        coVerify(exactly = 1) { historicalPoisStorage.clear() }
    }
}

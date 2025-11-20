package com.neel.infyassignment.data.poi

import com.neel.shared.model.Poi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PoiRepositoryTest {

    private lateinit var poiManager: PoisManager
    private lateinit var poiRepository: PoisRepository

    @Before
    fun setUp() {
        poiManager = mockk(relaxed = true)
        poiRepository = PoisRepository(poiManager)
    }

    @Test
    fun `observeCurrentPoisByCategory null returns all current pois`() = runTest {
        val poiOne: Poi = mockk()
        val poiTwo: Poi = mockk()
        val currentList: List<Poi> = listOf(poiOne, poiTwo)

        val currentStateFlow: MutableStateFlow<List<Poi>> =
            MutableStateFlow(currentList)

        every {
            poiManager.currentPoisFlow
        } returns currentStateFlow as StateFlow<List<Poi>>

        val result: List<Poi> =
            poiRepository
                .observeCurrentPoisByCategory(category = null)
                .first()

        assertEquals(currentList, result)
    }

    @Test
    fun `observeHistoricalPoisByCategory null loads all history once`() = runTest {
        val historyPoiOne: Poi = mockk()
        val historyPoiTwo: Poi = mockk()
        val historyList: List<Poi> = listOf(historyPoiOne, historyPoiTwo)

        coEvery {
            poiManager.loadHistoricalPois()
        } returns historyList

        val result: List<Poi> =
            poiRepository
                .observeHistoricalPoisByCategory(category = null)
                .first()

        assertEquals(historyList, result)
        coVerify(exactly = 1) {
            poiManager.loadHistoricalPois()
        }
    }

    @Test
    fun `clearHistory forwards call to poiManager`() {
        poiRepository.clearHistory()

        verify(exactly = 1) {
            poiManager.clearHistory()
        }
    }
}

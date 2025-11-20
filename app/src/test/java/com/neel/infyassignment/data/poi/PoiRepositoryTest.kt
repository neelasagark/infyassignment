package com.neel.infyassignment.data.poi

import com.neel.infyassignment.domain.repository.NearbyPoisRepository
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PoiRepositoryTest {

    private lateinit var poiManager: PoiManager
    private lateinit var repository: NearbyPoisRepository

    @Before
    fun setUp() {
        poiManager = mockk(relaxed = true)
        repository = PoiRepository(poiManager)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `clearHistory delegates to poiManager`() {
        repository.clearHistory()

        verify(exactly = 1) {
            poiManager.clearHistory()
        }
    }
}

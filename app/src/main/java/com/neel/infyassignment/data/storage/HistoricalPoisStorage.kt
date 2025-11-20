package com.neel.infyassignment.data.storage

import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory

interface HistoricalPoisStorage {

    suspend fun savePois(poiList: List<Poi>)

    suspend fun readPois(poiCategory: PoiCategory? = null): List<Poi>

    suspend fun clear(): Boolean
}

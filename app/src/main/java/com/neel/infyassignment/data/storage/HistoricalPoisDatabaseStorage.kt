package com.neel.infyassignment.data.storage

import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Stub storage that can be later used for a real database for POI history.
 *
 * For now i implemented only the skeleton of the class.
 */
class HistoricalPoisDatabaseStorage(
    // In a real scenario would inject a library or database or dataObject here.
    // private val dataObject: DataObject,
    private val ioDispatcher: CoroutineDispatcher,
) : HistoricalPoisStorage {

    override suspend fun savePois(poiList: List<Poi>) {
        withContext(ioDispatcher) {
            throw NotImplementedError("savePois is not implemented yet")
        }
    }

    override suspend fun readPois(poiCategory: PoiCategory?): List<Poi> {
        withContext(ioDispatcher) {
            throw NotImplementedError("readPois is not implemented yet")
        }
    }

    override suspend fun clear(): Boolean {
        withContext(ioDispatcher) {
            throw NotImplementedError("clear is not implemented yet")
        }
    }
}

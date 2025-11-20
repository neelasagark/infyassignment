package com.neel.infyassignment.di

import android.content.Context
import com.neel.infyassignment.data.poi.PoiManager
import com.neel.infyassignment.data.poi.PoiRepository
import com.neel.infyassignment.data.storage.HistoricalPoisStorage
import com.neel.infyassignment.domain.repository.NearbyPoisRepository
import com.neel.infyassignment.domain.usecase.ClearHistoricalPoisUseCase
import com.neel.infyassignment.domain.usecase.GetCurrentPoisUseCase
import com.neel.infyassignment.domain.usecase.GetHistoricalPoisUseCase
import com.neel.nearbyplaces.NearbyPois
import com.neel.nearbyplaces.api.NearbyPoisApi
import com.neel.platform.VehiclePlatform
import com.neel.platform.api.PlatformApi
import com.neel.shared.model.Poi
import com.neel.shared.model.PoiCategory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }



    @Singleton
    @Provides
    fun providePlatformApi(
        @ApplicationContext appContext: Context,
    ): PlatformApi {
        return VehiclePlatform(
            context = appContext,
        )
    }

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideNearbyPoisApi(
        platformApi: PlatformApi,
        applicationScope: CoroutineScope,
    ): NearbyPoisApi {
        return NearbyPois(
            platformApi = platformApi,
            coroutineScope = applicationScope,
        )
    }

    @Singleton
    @Provides
    fun provideHistoricalPoisStorage(
        ioDispatcher: CoroutineDispatcher,
    ): HistoricalPoisStorage {
        // For now its a stub. can be replaced with
        // this with a real DatabasePoiHistoryStorage.
        return object : HistoricalPoisStorage {

            override suspend fun savePois(poiList: List<Poi>) {
                return Unit
            }

            override suspend fun readPois(poiCategory: PoiCategory?): List<Poi> {
                return emptyList()
            }

            override suspend fun clear(): Boolean {
                return false
            }
        }
    }

    @Singleton
    @Provides
    fun providePoiManager(
        platformApi: PlatformApi,
        nearbyLocationApi: NearbyPoisApi,
        historicalPoisStorage: HistoricalPoisStorage,
        applicationScope: CoroutineScope,
    ): PoiManager {
        return PoiManager(
            platformApi = platformApi,
            nearbyPoisApi = nearbyLocationApi,
            coroutineScope = applicationScope,
            historicalPoisStorage = historicalPoisStorage,
        )
    }

    @Singleton
    @Provides
    fun provideNearbyPoisRepository(
        poiManager: PoiManager,
    ): NearbyPoisRepository {
        return PoiRepository(
            poiManager = poiManager,
        )
    }

    @Singleton
    @Provides
    fun provideGetCurrentPoisUseCase(
        nearbyPoisRepository: NearbyPoisRepository,
    ): GetCurrentPoisUseCase {
        return GetCurrentPoisUseCase(nearbyPoisRepository)
    }

    @Singleton
    @Provides
    fun provideGetHistoricalPoisUseCase(
        nearbyPoisRepository: NearbyPoisRepository,
    ): GetHistoricalPoisUseCase {
        return GetHistoricalPoisUseCase(nearbyPoisRepository)
    }

    @Singleton
    @Provides
    fun provideClearHistoricalPoisUseCase(
        nearbyPoisRepository: NearbyPoisRepository,
    ): ClearHistoricalPoisUseCase {
        return ClearHistoricalPoisUseCase(nearbyPoisRepository)
    }
}

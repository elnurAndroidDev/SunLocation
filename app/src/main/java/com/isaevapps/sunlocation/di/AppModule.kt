package com.isaevapps.sunlocation.di

import android.content.Context
import com.isaevapps.data.algorithm.SunCalculator
import com.isaevapps.data.repository.ResourceTimeZoneRepository
import com.isaevapps.data.repository.SunRepositoryImpl
import com.isaevapps.domain.repository.SunRepository
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.usecase.CalculateSunPositionUseCase
import com.isaevapps.domain.usecase.ExtractCoordinatesUseCase
import com.isaevapps.domain.utils.CoordinatesParser
import com.isaevapps.domain.utils.DefaultCoordinatesParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCoordinatesParser(): CoordinatesParser {
        return DefaultCoordinatesParser()
    }

    @Provides
    @Singleton
    fun provideExtractCoordinatesUseCase(coordinatesParser: CoordinatesParser): ExtractCoordinatesUseCase {
        return ExtractCoordinatesUseCase(coordinatesParser)
    }

    @Provides
    fun provideSunCalculator(): SunCalculator = SunCalculator

    @Provides
    @Singleton
    fun provideSunRepository(sunCalculator: SunCalculator): SunRepository {
        return SunRepositoryImpl(sunCalculator)
    }

    @Provides
    @Singleton
    fun provideTimeZonesRepository(@ApplicationContext context: Context): TimeZoneRepository {
        return ResourceTimeZoneRepository()
    }

    @Provides
    @Singleton
    fun provideCalculateSunPositionUseCase(
        repository: SunRepository,
        dispatchers: AppDispatchers
    ): CalculateSunPositionUseCase {
        return CalculateSunPositionUseCase(repository, dispatchers.default)
    }


}
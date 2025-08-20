package com.isaevapps.sunlocation.di

import com.isaevapps.data.repository.CompassRepositoryImpl
import com.isaevapps.data.repository.LocationRepositoryImpl
import com.isaevapps.data.repository.ResourceTimeZoneRepository
import com.isaevapps.data.repository.SunRepositoryImpl
import com.isaevapps.data.repository.WeatherRepositoryImpl
import com.isaevapps.domain.repository.CompassRepository
import com.isaevapps.domain.repository.LocationRepository
import com.isaevapps.domain.repository.SunRepository
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindSunRepository(
        impl: SunRepositoryImpl
    ): SunRepository

    @Binds
    @Singleton
    abstract fun bindTimeZoneRepository(
        impl: ResourceTimeZoneRepository
    ): TimeZoneRepository

    @Binds
    @Singleton
    abstract fun bindCompassRepository(
        impl: CompassRepositoryImpl
    ): CompassRepository
}
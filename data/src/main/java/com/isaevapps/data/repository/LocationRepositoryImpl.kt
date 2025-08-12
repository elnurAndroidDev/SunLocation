package com.isaevapps.data.repository

import com.isaevapps.data.location.LocationDataSource
import com.isaevapps.domain.model.Location
import com.isaevapps.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource
): LocationRepository {
    override fun getCurrentLocation(): Flow<Location> {
        return locationDataSource.flow()
    }
}
package com.isaevapps.data.repository

import com.isaevapps.data.location.LocationDataSource
import com.isaevapps.domain.model.Location
import com.isaevapps.domain.repository.LocationRepository
import com.isaevapps.domain.result.LocationError
import com.isaevapps.domain.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource
): LocationRepository {
    override fun getCurrentLocation(): Flow<Result<Location, LocationError>> {
        return locationDataSource.flow()
    }
}
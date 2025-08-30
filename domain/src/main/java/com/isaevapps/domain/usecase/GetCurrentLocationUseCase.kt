package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.Location
import com.isaevapps.domain.repository.LocationRepository
import com.isaevapps.domain.result.LocationError
import com.isaevapps.domain.result.Result
import kotlinx.coroutines.flow.Flow

class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<Result<Location, LocationError>> =
        locationRepository.getCurrentLocation()
}
package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.Location
import com.isaevapps.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<Location> = locationRepository.getCurrentLocation()
}
package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.CompassData
import com.isaevapps.domain.repository.CompassRepository
import kotlinx.coroutines.flow.Flow

class ObserveCompassUseCase(
    private val repository: CompassRepository
) {
    operator fun invoke(): Flow<CompassData> = repository.observeAzimuth()
}
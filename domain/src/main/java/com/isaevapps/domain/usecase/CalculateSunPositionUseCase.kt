package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.SunPosition
import com.isaevapps.domain.repository.SunRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class CalculateSunPositionUseCase(
    private val repository: SunRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        dateTime: LocalDateTime,
        utcOffset: Double
    ): SunPosition =
        withContext(dispatcher) { repository.getSunPosition(latitude, longitude, dateTime, utcOffset) }
}
package com.isaevapps.data.repository

import com.isaevapps.data.algorithm.SunCalculator
import com.isaevapps.domain.model.SunPosition
import com.isaevapps.domain.repository.SunRepository
import java.time.LocalDateTime

class SunRepositoryImpl(
    private val sunCalculator: SunCalculator
): SunRepository {
    override suspend fun getSunPosition(
        latitude: Double,
        longitude: Double,
        date: LocalDateTime,
        gmtOffset: Double
    ): SunPosition {
        return sunCalculator.calculateSunPosition(latitude, longitude, date, gmtOffset)
    }
}
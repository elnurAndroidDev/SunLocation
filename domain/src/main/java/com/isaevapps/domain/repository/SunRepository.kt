package com.isaevapps.domain.repository

import com.isaevapps.domain.model.SunPosition
import java.time.LocalDateTime

interface SunRepository {
    suspend fun getSunPosition(latitude: Double, longitude: Double, dateTime: LocalDateTime, utcOffset: Double): SunPosition
}
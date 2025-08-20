package com.isaevapps.domain.repository

import com.isaevapps.domain.model.CompassData
import kotlinx.coroutines.flow.Flow

interface CompassRepository {
    fun observeAzimuth(): Flow<CompassData>
}
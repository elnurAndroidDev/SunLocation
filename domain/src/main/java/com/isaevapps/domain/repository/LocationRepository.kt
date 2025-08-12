package com.isaevapps.domain.repository

import com.isaevapps.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentLocation(): Flow<Location>
}
package com.isaevapps.domain.repository

import com.isaevapps.domain.model.Location
import com.isaevapps.domain.result.LocationError
import com.isaevapps.domain.result.Result
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentLocation(): Flow<Result<Location, LocationError>>
}
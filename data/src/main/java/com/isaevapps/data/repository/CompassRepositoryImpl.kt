package com.isaevapps.data.repository

import com.isaevapps.data.sensor.SensorDataSource
import com.isaevapps.data.utils.CompassMath
import com.isaevapps.domain.model.CompassData
import com.isaevapps.domain.repository.CompassRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CompassRepositoryImpl @Inject constructor(
    private val sensorDataSource: SensorDataSource
) : CompassRepository {

    override fun observeAzimuth(): Flow<CompassData> {
        return sensorDataSource.observeRotationVector()
            .map { values -> CompassMath.calculateAzimuth(values) }
            .distinctUntilChanged()
    }

}
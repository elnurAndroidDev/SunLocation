package com.isaevapps.data.repository

import android.hardware.SensorManager
import com.isaevapps.data.sensor.SensorDataSource
import com.isaevapps.domain.model.CompassData
import com.isaevapps.domain.repository.CompassRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.roundToInt

class CompassRepositoryImpl @Inject constructor(
    private val sensorDataSource: SensorDataSource
) : CompassRepository {

    override fun observeAzimuth(): Flow<CompassData> {
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        return sensorDataSource.observeRotationVector()
            .map { values ->
                SensorManager.getRotationMatrixFromVector(rotationMatrix, values)
                SensorManager.getOrientation(rotationMatrix, orientation)
                toDegree(orientation[0])
            }
            .map { azimuth -> CompassData(smooth(azimuth)) }
            .distinctUntilChanged()
    }

    private fun toDegree(orientation: Float): Float {
        var azimuth = Math.toDegrees(orientation.toDouble()).toFloat()
        if (azimuth < 0) azimuth += 360f
        return azimuth
    }

    private var last: Float? = null
    private fun smooth(value: Float, alpha: Float = 0.12f): Int {
        val prev = last
        val out = if (prev == null) value else {
            val delta = angleDelta(prev, value)
            prev + alpha * delta
        }
        last = out
        return out.roundToInt()
    }

    private fun angleDelta(a: Float, b: Float): Float {
        var d = (b - a + 540f) % 360f - 180f
        if (d < -180f) d += 360f
        return d
    }

}
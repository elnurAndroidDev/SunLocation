package com.isaevapps.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.isaevapps.domain.model.CompassData
import com.isaevapps.domain.repository.CompassRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.roundToInt

class CompassRepositoryImpl @Inject constructor(
    @param:ApplicationContext
    private val context: Context
) : CompassRepository {

    override fun observeAzimuth(): Flow<CompassData> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (rotationSensor == null) {
            close(IllegalStateException("Rotation Vector sensor not available"))
            return@callbackFlow
        }

        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientation)
                var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                if (azimuth < 0) azimuth += 360f
                trySend(azimuth)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.registerListener(
            listener,
            rotationSensor,
            SensorManager.SENSOR_DELAY_GAME
        )

        awaitClose { sensorManager.unregisterListener(listener) }
    }
        .map { CompassData(smooth(it).roundToInt()) }
        .distinctUntilChanged()

    private var last: Float? = null
    private fun smooth(value: Float, alpha: Float = 0.12f): Float {
        val prev = last
        val out = if (prev == null) value else {
            val delta = angleDelta(prev, value)
            prev + alpha * delta
        }
        last = out
        return out
    }

    private fun angleDelta(a: Float, b: Float): Float {
        var d = (b - a + 540f) % 360f - 180f
        if (d < -180f) d += 360f
        return d
    }

}
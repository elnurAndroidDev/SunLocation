package com.isaevapps.data.utils

import android.hardware.SensorManager
import com.isaevapps.domain.model.CompassData
import kotlin.math.roundToInt

object CompassMath {

    fun calculateAzimuth(values: FloatArray): CompassData {
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, values)
        SensorManager.getOrientation(rotationMatrix, orientation)

        var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
        if (azimuth < 0) azimuth += 360f
        return CompassData(smooth(azimuth))
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
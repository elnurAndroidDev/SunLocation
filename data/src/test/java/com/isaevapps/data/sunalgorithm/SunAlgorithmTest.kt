package com.isaevapps.data.sunalgorithm

import com.isaevapps.data.algorithm.SunCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class SunAlgorithmTest {

    private val sunAlgorithm = SunCalculator

    @Test
    fun `test 1`() {
        val lat = 41.476104
        val lon = 69.575205
        val utcOffset = 5.0
        val dateTime = LocalDateTime.of(2025, 8, 28, 10, 8)
        val sunPosition = sunAlgorithm.calculateSunPosition(lat, lon, dateTime, utcOffset)
        assertEquals(127.27, sunPosition.azimuth, 0.1)
        assertEquals(46.51, sunPosition.altitude, 0.1)
    }

    @Test
    fun `test 2 new time`() {
        val lat = 41.476104
        val lon = 69.575205
        val utcOffset = 5.0
        val dateTime = LocalDateTime.of(2025, 8, 28, 15, 15)
        val sunPosition = sunAlgorithm.calculateSunPosition(lat, lon, dateTime, utcOffset)
        assertEquals(242.4, sunPosition.azimuth, 0.1)
        assertEquals(40.67, sunPosition.altitude, 0.1)
    }

    @Test
    fun `test 3 new date`() {
        val lat = 41.476104
        val lon = 69.575205
        val utcOffset = 5.0
        val dateTime = LocalDateTime.of(2025, 8, 30, 10, 14)
        val sunPosition = sunAlgorithm.calculateSunPosition(lat, lon, dateTime, utcOffset)
        assertEquals(129.79, sunPosition.azimuth, 0.1)
        assertEquals(46.91, sunPosition.altitude, 0.1)
    }

    @Test
    fun `test 4 new latitude`() {
        val lat = 41.560088
        val lon = 69.575205
        val utcOffset = 5.0
        val dateTime = LocalDateTime.of(2025, 8, 30, 10, 14)
        val sunPosition = sunAlgorithm.calculateSunPosition(lat, lon, dateTime, utcOffset)
        assertEquals(129.86, sunPosition.azimuth, 0.1)
        assertEquals(46.85, sunPosition.altitude, 0.1)
    }

    @Test
    fun `test 5 new longitude`() {
        val lat = 41.560088
        val lon = 84.575205
        val utcOffset = 6.0
        val dateTime = LocalDateTime.of(2025, 8, 30, 10, 14)
        val sunPosition = sunAlgorithm.calculateSunPosition(lat, lon, dateTime, utcOffset)
        assertEquals(129.86, sunPosition.azimuth, 0.1)
        assertEquals(46.85, sunPosition.altitude, 0.1)
    }
}
package com.isaevapps.domain

import com.isaevapps.domain.utils.DefaultCoordinatesParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoordinateParserTest {
    private val delta = 1e-6 // точность сравнения

    private fun parseCoordinate(input: String, isLatitude: Boolean): Result<Double> {
        val parser = DefaultCoordinatesParser()
        return parser.parseCoordinate(input, isLatitude)
    }

    @Test
    fun `parse decimal latitude`() {
        val result = parseCoordinate("55.7558", isLatitude = true)
        assertTrue(result.isSuccess)
        assertEquals(55.7558, result.getOrNull()!!, delta)
    }

    @Test
    fun `parse decimal longitude`() {
        val result = parseCoordinate("37.6173", isLatitude = false)
        assertTrue(result.isSuccess)
        assertEquals(37.6173, result.getOrNull()!!, delta)
    }

    @Test
    fun `parse DMS latitude north`() {
        val result = parseCoordinate("41°28'34.3\"N", isLatitude = true)
        assertTrue(result.isSuccess)
        assertEquals(41.4761944, result.getOrNull()!!, delta)
    }

    @Test
    fun `parse DMS latitude south`() {
        val result = parseCoordinate("55°45'0\"S", isLatitude = true)
        assertTrue(result.isSuccess)
        assertEquals(-55.75, result.getOrNull()!!, delta)
    }

    @Test
    fun `parse DMS longitude east`() {
        val result = parseCoordinate("37°37'0\"E", isLatitude = false)
        assertTrue(result.isSuccess)
        assertEquals(37.616667, result.getOrNull()!!, delta)
    }

    @Test
    fun `parse DMS longitude west`() {
        val result = parseCoordinate("37°37'0\"W", isLatitude = false)
        assertTrue(result.isSuccess)
        assertEquals(-37.616667, result.getOrNull()!!, delta)
    }

    @Test
    fun `invalid coordinate format`() {
        val result = parseCoordinate("invalid", isLatitude = true)
        assertTrue(result.isFailure)
    }
}
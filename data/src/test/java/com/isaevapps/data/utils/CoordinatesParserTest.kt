package com.isaevapps.data.utils

import com.isaevapps.domain.model.Coordinates
import com.isaevapps.domain.result.CoordinatesError
import com.isaevapps.domain.result.Result
import com.isaevapps.domain.utils.CoordinatesParser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CoordinatesParserTest {

    private lateinit var parser: CoordinatesParser

    @Before
    fun setUp() {
        parser = DefaultCoordinatesParser()
    }

    @Test
    fun `valid coordinates string`() {
        val validCoordinatesString = "12.345,67.890"
        val expectedCoordinates = Coordinates(12.345, 67.890)
        val successResult: Result<Coordinates, CoordinatesError> =
            Result.Success(expectedCoordinates)
        val actualResult = parser.parse(validCoordinatesString)
        assertEquals(successResult, actualResult)
    }

    @Test
    fun `valid coordinates with space between`() {
        val validCoordinatesString = "41.476104, 69.575205"
        val expectedCoordinates = Coordinates(41.476104, 69.575205)
        val successResult: Result<Coordinates, CoordinatesError> =
            Result.Success(expectedCoordinates)
        val actualResult = parser.parse(validCoordinatesString)
        assertEquals(successResult, actualResult)
    }

    @Test
    fun `invalid latitude string`() {
        val invalidCoordinatesString = "12.345n,67.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `invalid longitude string`() {
        val invalidCoordinatesString = "12.345,67.890s"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `empty string`() {
        val emptyCoordinatesString = ""
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.TWO_COORDINATES)
        val actualResult = parser.parse(emptyCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `out of max range latitude`() {
        val invalidCoordinatesString = "90.345,67.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `out of min range latitude`() {
        val invalidCoordinatesString = "-90.345,67.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `out of range max longitude`() {
        val invalidCoordinatesString = "12.345,180.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `out of range min longitude`() {
        val invalidCoordinatesString = "12.345,-180.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `too few coordinate parts`() {
        val invalidCoordinatesString = "12.345"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.TWO_COORDINATES)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `too many coordinate parts`() {
        val invalidCoordinatesString = "12.34,56.78,90.12"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.TWO_COORDINATES)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `leading trailing whitespace`() {
        val coordinatesStringWithWhitespace = "  12.34, 56.78  "
        val expectedCoordinates = Coordinates(12.34, 56.78)
        val successResult: Result<Coordinates, CoordinatesError> =
            Result.Success(expectedCoordinates)
        val actualResult = parser.parse(coordinatesStringWithWhitespace)
        assertEquals(successResult, actualResult)
    }

    @Test
    fun `NSEW valid coordinates`() {
        val validCoordinatesString = "40°26'46\"N, 79°58'56\"W"
        val expectedCoordinates = Coordinates(40.446111, -79.982222)
        val actualResult = parser.parse(validCoordinatesString)
        val lat = (actualResult as Result.Success).data.latitude
        val lon = actualResult.data.longitude
        assertEquals(lat, expectedCoordinates.latitude, 0.0001)
        assertEquals(lon, expectedCoordinates.longitude, 0.0001)
    }

    @Test
    fun `NSEW invalid latitude`() {
        val invalidCoordinatesString = "40°26'46\"X, 79°58'56\"W"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `NSEW invalid longitude`() {
        val invalidCoordinatesString = "40°26'46\"N, 79°58'56\"Y"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `NSEW out of max range latitude`() {
        val invalidCoordinatesString = "90°00'01\"N, 79°58'56\"W"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `NSEW out of min range latitude`() {
        val invalidCoordinatesString = "90°00'01\"S, 79°58'56\"W" // Equivalent to -90.00027...
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `NSEW out of max range longitude`() {
        val invalidCoordinatesString = "40°26'46\"N, 180°00'01\"E"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `NSEW  out of min range longitude`() {
        val invalidCoordinatesString = "40°26'46\"N, 180°00'01\"W" // Equivalent to -180.00027...
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        val actualResult = parser.parse(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
    }

    @Test
    fun `NSEW leading trailing whitespace`() {
        val coordinatesStringWithWhitespace = "  40°26'46\"N, 79°58'56\"W  "
        val expectedCoordinates = Coordinates(40.446111, -79.982222)
        val actualResult: Result<Coordinates, CoordinatesError> =
            parser.parse(coordinatesStringWithWhitespace)
        val lat = (actualResult as Result.Success).data.latitude
        val lon = actualResult.data.longitude
        assertEquals(lat, expectedCoordinates.latitude, 0.0001)
        assertEquals(lon, expectedCoordinates.longitude, 0.0001)
    }
}
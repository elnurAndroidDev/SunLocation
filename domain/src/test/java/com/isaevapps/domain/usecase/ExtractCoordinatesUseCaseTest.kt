package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.Coordinates
import com.isaevapps.domain.result.CoordinatesError
import com.isaevapps.domain.utils.CoordinatesParser
import com.isayevapps.domain.result.Result
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.Assert.assertEquals

class ExtractCoordinatesUseCaseTest {

    private val mockCoordinatesParser: CoordinatesParser = mockk()
    private val useCase = ExtractCoordinatesUseCase(mockCoordinatesParser)

    @Test
    fun `valid coordinates string`() {
        val validCoordinatesString = "12.345,67.890"
        val expectedCoordinates = Coordinates(12.345, 67.890)
        val successResult: Result<Coordinates, CoordinatesError> =
            Result.Success(expectedCoordinates)
        every { mockCoordinatesParser.parse(validCoordinatesString) } returns successResult
        val actualResult = useCase.invoke(validCoordinatesString)
        assertEquals(successResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(validCoordinatesString) }
    }

    @Test
    fun `invalid latitude string`() {
        val invalidCoordinatesString = "12.345n,67.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `invalid longitude string`() {
        val invalidCoordinatesString = "12.345,67.890s"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `empty string`() {
        val emptyCoordinatesString = ""
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.TWO_COORDINATES)
        every { mockCoordinatesParser.parse(emptyCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(emptyCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(emptyCoordinatesString) }
    }

    @Test
    fun `out of max range latitude`() {
        val invalidCoordinatesString = "90.345,67.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `out of min range latitude`() {
        val invalidCoordinatesString = "-90.345,67.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `out of range max longitude`() {
        val invalidCoordinatesString = "12.345,180.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `out of range min longitude`() {
        val invalidCoordinatesString = "12.345,-180.890"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `too few coordinate parts`() {
        val invalidCoordinatesString = "12.345"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.TWO_COORDINATES)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }
    @Test
    fun `too many coordinate parts`() {
        val invalidCoordinatesString = "12.34,56.78,90.12"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.TWO_COORDINATES)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `leading trailing whitespace`() {
        val coordinatesStringWithWhitespace = "  12.34, 56.78  "
        val expectedCoordinates = Coordinates(12.34, 56.78)
        val successResult: Result<Coordinates, CoordinatesError> =
            Result.Success(expectedCoordinates)
        every { mockCoordinatesParser.parse(coordinatesStringWithWhitespace) } returns successResult
        val actualResult = useCase.invoke(coordinatesStringWithWhitespace)
        assertEquals(successResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(coordinatesStringWithWhitespace) }
    }

    @Test
    fun `NSEW valid coordinates`() {
        val validCoordinatesString = "40°26'46\"N, 79°58'56\"W"
        val expectedCoordinates = Coordinates(40.446111, -79.982222) // Example, actual conversion depends on parser logic
        val successResult: Result<Coordinates, CoordinatesError> =
            Result.Success(expectedCoordinates)
        every { mockCoordinatesParser.parse(validCoordinatesString) } returns successResult
        val actualResult = useCase.invoke(validCoordinatesString)
        assertEquals(successResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(validCoordinatesString) }
    }

    @Test
    fun `NSEW invalid latitude`() {
        val invalidCoordinatesString = "40°26'46\"X, 79°58'56\"W"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `NSEW invalid longitude`() {
        val invalidCoordinatesString = "40°26'46\"N, 79°58'56\"Y"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `NSEW out of max range latitude`() {
        val invalidCoordinatesString = "90°00'01\"N, 79°58'56\"W"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `NSEW out of min range latitude`() {
        val invalidCoordinatesString = "90°00'01\"S, 79°58'56\"W" // Equivalent to -90.00027...
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LATITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `NSEW out of max range longitude`() {
        val invalidCoordinatesString = "40°26'46\"N, 180°00'01\"E"
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `NSEW  out of min range longitude`() {
        val invalidCoordinatesString = "40°26'46\"N, 180°00'01\"W" // Equivalent to -180.00027...
        val errorResult: Result<Coordinates, CoordinatesError> =
            Result.Error(CoordinatesError.INVALID_LONGITUDE)
        every { mockCoordinatesParser.parse(invalidCoordinatesString) } returns errorResult
        val actualResult = useCase.invoke(invalidCoordinatesString)
        assertEquals(errorResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(invalidCoordinatesString) }
    }

    @Test
    fun `NSEW leading trailing whitespace`() {
        val coordinatesStringWithWhitespace = "  40°26'46\"N, 79°58'56\"W  "
        val expectedCoordinates = Coordinates(40.446111, -79.982222) // Example
        val successResult: Result<Coordinates, CoordinatesError> =
            Result.Success(expectedCoordinates)
        every { mockCoordinatesParser.parse(coordinatesStringWithWhitespace) } returns successResult
        val actualResult = useCase.invoke(coordinatesStringWithWhitespace)
        assertEquals(successResult, actualResult)
        verify(exactly = 1) { mockCoordinatesParser.parse(coordinatesStringWithWhitespace) }
    }
}
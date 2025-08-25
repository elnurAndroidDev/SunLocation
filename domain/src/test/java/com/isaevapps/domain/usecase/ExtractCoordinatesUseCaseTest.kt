package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.Coordinates
import com.isaevapps.domain.result.CoordinatesError
import com.isaevapps.domain.result.Result
import com.isaevapps.domain.utils.CoordinatesParser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExtractCoordinatesUseCaseTest {
    private lateinit var parser: CoordinatesParser
    private lateinit var useCase: ExtractCoordinatesUseCase

    @Before
    fun setUp() {
        parser = mockk()
        useCase = ExtractCoordinatesUseCase(parser)
    }

    @Test
    fun `invoke returns success when parser succeeds`() {
        val coordinates = Coordinates(55.75, 37.62)
        every { parser.parse("55.75, 37.62") } returns Result.Success(coordinates)

        val result = useCase("55.75, 37.62")

        assertTrue(result is Result.Success)
        assertEquals(coordinates, (result as Result.Success).data)
        verify(exactly = 1) { parser.parse("55.75, 37.62") }
    }

    @Test
    fun `invoke returns error when parser fails`() {
        every { parser.parse("invalid") } returns Result.Error(CoordinatesError.TWO_COORDINATES)

        val result = useCase("invalid")

        assertTrue(result is Result.Error)
        assertEquals(CoordinatesError.TWO_COORDINATES, (result as Result.Error).error)
        verify(exactly = 1) { parser.parse("invalid") }
    }
}
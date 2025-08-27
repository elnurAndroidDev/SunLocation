package com.isaevapps.data.repository

import com.isaevapps.data.algorithm.SunCalculator
import com.isaevapps.domain.model.SunPosition
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class SunRepositoryImplTest {

    private lateinit var sunCalculator: SunCalculator
    private lateinit var repository: SunRepositoryImpl

    @Before
    fun setUp() {
        sunCalculator = mockk()
        repository = SunRepositoryImpl(sunCalculator)
    }

    @Test
    fun `getSunPosition delegates to SunCalculator and returns result`() = runTest {
        // given
        val latitude = 55.75
        val longitude = 37.62
        val dateTime = LocalDateTime.of(2023, 6, 1, 12, 0)
        val utcOffset = 3.0

        val expected = SunPosition(
            azimuth = 180.0,
            altitude = 45.0
        )

        coEvery {
            sunCalculator.calculateSunPosition(latitude, longitude, dateTime, utcOffset)
        } returns expected

        // when
        val result = repository.getSunPosition(latitude, longitude, dateTime, utcOffset)

        // then
        assertEquals(expected, result)

        coVerify(exactly = 1) {
            sunCalculator.calculateSunPosition(latitude, longitude, dateTime, utcOffset)
        }
    }
}
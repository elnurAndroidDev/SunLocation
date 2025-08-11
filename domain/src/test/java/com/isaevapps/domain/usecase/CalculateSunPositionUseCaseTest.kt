package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.SunPosition
import com.isaevapps.domain.repository.SunRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class CalculateSunPositionUseCaseTest {

    @Test
    fun `returns same result as repository`() = runTest {
        val expected = SunPosition(
            altitude = 45.0,
            azimuth = 180.0
        )

        val fakeRepo = object : SunRepository {
            override suspend fun getSunPosition(
                latitude: Double,
                longitude: Double,
                dateTime: LocalDateTime,
                utcOffset: Double
            ) = expected
        }

        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase = CalculateSunPositionUseCase(fakeRepo, dispatcher)

        val actual = useCase(
            latitude = 41.2995,
            longitude = 69.2401,
            dateTime = LocalDateTime.of(2025, 8, 11, 12, 0, 0),
            utcOffset = 5.0
        )

        assertEquals(expected, actual)
    }
}
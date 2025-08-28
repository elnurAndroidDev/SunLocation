package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.SunPosition
import com.isaevapps.domain.repository.SunRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CalculateSunPositionUseCaseTest {

    private lateinit var repository: SunRepository
    private lateinit var useCase: CalculateSunPositionUseCase

    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)

    @Before
    fun setUp() {
        repository = mockk()
        useCase = CalculateSunPositionUseCase(repository, testDispatcher)
    }

    @Test
    fun `invoke returns sun position from repository`() = runTest(scheduler) {
        // given
        val latitude = 55.75
        val longitude = 37.62
        val dateTime = LocalDateTime.of(2023, 8, 25, 12, 0)
        val utcOffset = 3.0
        val expected = SunPosition(10.0, 20.0)

        coEvery {
            repository.getSunPosition(
                latitude,
                longitude,
                dateTime,
                utcOffset
            )
        } returns expected

        // when
        val result = useCase(latitude, longitude, dateTime, utcOffset)

        // then
        assertEquals(expected, result)
        coVerify(exactly = 1) {
            repository.getSunPosition(latitude, longitude, dateTime, utcOffset)
        }
    }
}

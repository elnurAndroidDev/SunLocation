package com.isaevapps.data.repository

import app.cash.turbine.test
import com.isaevapps.data.sensor.SensorDataSource
import com.isaevapps.data.utils.CompassMath
import com.isaevapps.domain.model.CompassData
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CompassRepositoryImplTest {

    private lateinit var sensorDataSource: SensorDataSource
    private lateinit var repository: CompassRepositoryImpl

    @Before
    fun setUp() {
        sensorDataSource = mockk()
        repository = CompassRepositoryImpl(sensorDataSource)
    }

    @Test
    fun `observeAzimuth emits CompassData`() = runTest {
        // given
        val expected = CompassData(90)
        mockkObject(CompassMath)
        every { CompassMath.calculateAzimuth(any()) } returns expected
        every { sensorDataSource.observeRotationVector() } returns flowOf(floatArrayOf(1f, 2f, 3f))

        // when
        val flow = repository.observeAzimuth()

        // then
        flow.test {
            val item = awaitItem()
            assertEquals(expected.azimuth, item.azimuth) // проверяем, что угол корректный
            cancelAndConsumeRemainingEvents()
        }
    }
}
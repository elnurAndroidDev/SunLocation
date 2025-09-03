package com.isaevapps.presentation.viewmodels

import app.cash.turbine.test
import com.isaevapps.domain.model.Coordinates
import com.isaevapps.domain.model.SunPosition
import com.isaevapps.domain.model.TimeZone
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.result.CoordinatesError
import com.isaevapps.domain.result.Result
import com.isaevapps.domain.usecase.CalculateSunPositionUseCase
import com.isaevapps.domain.usecase.ExtractCoordinatesUseCase
import com.isaevapps.presentation.MainDispatcherRule
import com.isaevapps.presentation.screens.calculator.CalculateViewModel
import com.isaevapps.presentation.utils.toStringFormatted
import com.isaevapps.presentation.utils.toUiText
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class CalculateViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var extractCoordinatesUseCase: ExtractCoordinatesUseCase
    private lateinit var calculateSunPositionUseCase: CalculateSunPositionUseCase
    private lateinit var timeZoneRepository: TimeZoneRepository
    private lateinit var viewModel: CalculateViewModel
    private lateinit var mockTimeZones: List<TimeZone>

    @Before
    fun setup() {
        extractCoordinatesUseCase = mockk()
        calculateSunPositionUseCase = mockk()
        timeZoneRepository = mockk()

        mockTimeZones = listOf(
            TimeZone("UTC+05:00 Ashgabat, Tashkent"),
            TimeZone("UTC+00:00 London, Lisbon")
        )
        every { timeZoneRepository.timeZones } returns mockTimeZones
        every { timeZoneRepository.getSystemUtc() } returns TimeZone("UTC+05:00 Ashgabat, Tashkent")

        viewModel = CalculateViewModel(
            extractCoordinatesUseCase,
            calculateSunPositionUseCase,
            timeZoneRepository
        )
    }

    @Test
    fun `initial uiState is correct`() = runTest {
        viewModel.uiState.test {
            skipItems(1) //skip empty state
            val state = awaitItem()
            assertEquals("", state.coordinates)
            assertNull(state.invalidCoordinates)
            assertEquals(LocalDate.now().toStringFormatted(), state.date)
            assertEquals(LocalTime.now().toStringFormatted(), state.time)
            assertEquals(false, state.showDatePicker)
            assertEquals(false, state.showTimePicker)
            assertEquals(mockTimeZones.map { it.name }, state.timeZones)
            assertEquals("UTC+05:00 Ashgabat, Tashkent", state.timeZone)
            assertEquals("-", state.azimuth)
            assertEquals("-", state.altitude)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `valid coordinates input`() = runTest {
        val input = "40.7128, -74.0060"
        val coordinates = Coordinates(40.7128, -74.0060)
        coEvery { extractCoordinatesUseCase(input) } returns Result.Success(coordinates)

        viewModel.onCoordinateChange(input)

        viewModel.uiState.test {
            skipItems(1) //skip empty state

            val state = awaitItem()
            assertEquals(input, state.coordinates)
            assertNull(state.invalidCoordinates)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invalid coordinates input`() = runTest {
        val input = "invalid"
        val error = CoordinatesError.TWO_COORDINATES
        coEvery { extractCoordinatesUseCase(input) } returns Result.Error(error)

        advanceUntilIdle()
        viewModel.onCoordinateChange(input)

        viewModel.uiState.test {
            skipItems(1) //skip empty state

            val state = awaitItem()
            assertEquals(input, state.coordinates)

            val errorState = awaitItem()
            assertEquals(error.toUiText(), errorState.invalidCoordinates)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `calculateSunPosition updates sunPosition on success`() = runTest {
        val input = "40.7128, -74.0060"
        val coordinates = Coordinates(40.7128, -74.0060)
        coEvery { extractCoordinatesUseCase(input) } returns Result.Success(coordinates)

        val date = LocalDate.of(2025, 1, 1)
        val time = LocalTime.of(0, 0)

        val sunPosition = SunPosition(180.0, 45.0)
        coEvery {
            calculateSunPositionUseCase(
                coordinates.latitude,
                coordinates.longitude,
                date.atTime(time),
                5.0
            )
        } returns sunPosition

        viewModel.uiState.test {
            skipItems(1) //skip empty state

            viewModel.onDateChange(date)
            val dateChange = awaitItem()
            assertEquals(date.toStringFormatted(), dateChange.date)

            viewModel.onTimeChange(time)
            val timeChange = awaitItem()
            assertEquals(time.toStringFormatted(), timeChange.time)

            viewModel.onCoordinateChange(input)
            val coordinatesChange = awaitItem()
            assertEquals(input, coordinatesChange.coordinates)
            assertNull(coordinatesChange.invalidCoordinates)

            advanceUntilIdle()
            viewModel.calculateSunPosition()
            val sunPositionState = awaitItem()
            assertEquals("180.0", sunPositionState.azimuth)
            assertEquals("45.0", sunPositionState.altitude)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `calculateSunPosition does nothing without coordinates`() = runTest {

        viewModel.uiState.test {
            skipItems(1) //skip empty state

            val init = awaitItem()
            assertEquals("", init.coordinates)
            assertNull(init.invalidCoordinates)
            assertEquals("-", init.azimuth)
            assertEquals("-", init.altitude)

            viewModel.calculateSunPosition()
            advanceUntilIdle()
            expectNoEvents()
        }
    }

    @Test
    fun `onDateChange updates date`() = runTest {
        val newDate = LocalDate.now().plusDays(2)

        viewModel.uiState.test {
            skipItems(1) //skip empty state

            val oldDateState = awaitItem()
            assertEquals(LocalDate.now().toStringFormatted(), oldDateState.date)

            viewModel.onDateChange(newDate)
            val newDateState = awaitItem()
            assertEquals(newDate.toStringFormatted(), newDateState.date)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onTimeChange updates time`() = runTest {
        val newTime = LocalTime.now().plusMinutes(10)

        viewModel.uiState.test {
            skipItems(1) //skip empty state

            val oldTimeState = awaitItem()
            assertEquals(LocalTime.now().toStringFormatted(), oldTimeState.time)

            viewModel.onTimeChange(newTime)
            val newTimeState = awaitItem()
            assertEquals(newTime.toStringFormatted(), newTimeState.time)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onTimeZoneChange updates timeZone`() = runTest {
        val systemUtc = TimeZone("UTC+05:00 Ashgabat, Tashkent")
        val newTimeZoneName = "UTC+00:00 London, Lisbon"

        viewModel.uiState.test {
            skipItems(1) //skip empty state

            val init = awaitItem()
            assertEquals(systemUtc.name, init.timeZone)

            viewModel.onTimeZoneChange(newTimeZoneName)
            val newTimeZoneState = awaitItem()
            assertEquals(newTimeZoneName, newTimeZoneState.timeZone)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onTimeZoneChange falls back to systemUtc on invalid name`() = runTest {
        val systemUtc = TimeZone("UTC+05:00 Ashgabat, Tashkent")
        val newTimeZoneName = "UTC+00:00 London, Lisbon"
        val invalidTimeZoneName = "InvalidTimeZone"

        viewModel.uiState.test {
            skipItems(1) //skip empty state

            val init = awaitItem()
            assertEquals(systemUtc.name, init.timeZone)

            viewModel.onTimeZoneChange(newTimeZoneName)
            val newTimeZoneState = awaitItem()
            assertEquals(newTimeZoneName, newTimeZoneState.timeZone)

            viewModel.onTimeZoneChange(invalidTimeZoneName)
            val invalidTimeZoneState = awaitItem()
            assertEquals(systemUtc.name, invalidTimeZoneState.timeZone)

            expectNoEvents()
        }
    }

    @Test
    fun `showDatePicker and hideDatePicker toggle showDatePicker`() = runTest {
        viewModel.uiState.test {
            skipItems(2) //skip empty and init state

            viewModel.showDatePicker()
            assertEquals(true, awaitItem().showDatePicker)

            viewModel.hideDatePicker()
            assertEquals(false, awaitItem().showDatePicker)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `showTimePicker and hideTimePicker toggle showTimePicker`() = runTest {
        viewModel.uiState.test {
            skipItems(2) //skip empty and init state

            viewModel.showTimePicker()
            assertEquals(true, awaitItem().showTimePicker)

            viewModel.hideTimePicker()
            assertEquals(false, awaitItem().showTimePicker)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
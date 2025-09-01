package com.isaevapps.presentation.viewmodels

import app.cash.turbine.test
import com.isaevapps.domain.model.CompassData
import com.isaevapps.domain.model.Location
import com.isaevapps.domain.model.SunPosition
import com.isaevapps.domain.model.TimeZone
import com.isaevapps.domain.model.Weather
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.result.Result
import com.isaevapps.domain.usecase.CalculateSunPositionUseCase
import com.isaevapps.domain.usecase.GetCurrentLocationUseCase
import com.isaevapps.domain.usecase.GetCurrentWeatherUseCase
import com.isaevapps.domain.usecase.ObserveCompassUseCase
import com.isaevapps.domain.utils.NetworkMonitor
import com.isaevapps.presentation.MainDispatcherRule
import com.isaevapps.presentation.screens.main.HomeUiState
import com.isaevapps.presentation.screens.main.HomeViewModel
import com.isaevapps.presentation.utils.toUiData
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase = mockk()
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase = mockk()
    private val calculateSunPositionUseCase: CalculateSunPositionUseCase = mockk()
    private val observeCompassUseCase: ObserveCompassUseCase = mockk()
    private val networkMonitor: NetworkMonitor = mockk()
    private val timeZoneRepository: TimeZoneRepository = mockk()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        every { timeZoneRepository.getSystemUtc() } returns TimeZone("UTC+05:00")
    }

    @Test
    fun `initial state is default`() = runTest {
        every { getCurrentLocationUseCase() } returns flowOf()
        every { observeCompassUseCase() } returns flowOf()
        every { networkMonitor.observe() } returns flowOf(false)

        viewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            getCurrentLocationUseCase,
            calculateSunPositionUseCase,
            observeCompassUseCase,
            networkMonitor,
            timeZoneRepository
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(HomeUiState(), state)
    }

    @Test
    fun `weather success`() = runTest {
        every { getCurrentLocationUseCase() } returns flowOf(
            Result.Success(Location(lat = 10.0, lon = 20.0))
        )
        every { observeCompassUseCase() } returns flowOf(
            CompassData(azimuth = 10)
        )
        every { networkMonitor.observe() } returns flowOf(true)
        coEvery { calculateSunPositionUseCase(any(), any(), any(), any()) } returns SunPosition(
            0.0,
            0.0
        )

        val successWeather = Weather(
            city = "Tashkent",
            temp = 25,
            condition = "Sunny",
            country = "USA",
            localtime = LocalDateTime.of(2025, 1, 1, 0, 0),
            lastUpdated = LocalDateTime.of(2025, 1, 1, 0, 0)
        )
        val successWeatherUi = successWeather.toUiData()
        coEvery { getCurrentWeatherUseCase(lat = 10.0, lon = 20.0) } returns Result.Success(
            successWeather
        )

        viewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            getCurrentLocationUseCase,
            calculateSunPositionUseCase,
            observeCompassUseCase,
            networkMonitor,
            timeZoneRepository
        )

        viewModel.uiState.test {
            val initial = awaitItem()
            val second = awaitItem() //weather loading
            val third = awaitItem() //weather loaded
            println(third)

            assertEquals(true, second.weather.isLoading)
            assertEquals(null, second.weather.error)

            assertEquals(successWeatherUi.city, third.weather.weatherUiData.city)
            assertEquals(successWeatherUi.temp, third.weather.weatherUiData.temp)
            assertEquals(successWeatherUi.condition, third.weather.weatherUiData.condition)
            assertEquals(false, third.weather.isLoading)
            assertEquals(null, third.weather.error)

            val item = awaitItem()
            println(item)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
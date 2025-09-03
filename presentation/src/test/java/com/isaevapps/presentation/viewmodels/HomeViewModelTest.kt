package com.isaevapps.presentation.viewmodels

import app.cash.turbine.test
import com.isaevapps.domain.model.CompassData
import com.isaevapps.domain.model.Location
import com.isaevapps.domain.model.SunPosition
import com.isaevapps.domain.model.TimeZone
import com.isaevapps.domain.model.Weather
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.result.LocationError
import com.isaevapps.domain.result.NetworkError
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
import com.isaevapps.presentation.utils.toUiText
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

        val initialState = viewModel.uiState.value

        assertEquals(HomeUiState(), initialState)
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

        coEvery {
            calculateSunPositionUseCase(any(), any(), any(), any())
        } returns SunPosition(0.0, 0.0)

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
            skipItems(1)

            val loading = awaitItem()
            assertEquals(true, loading.weather.isLoading)
            assertEquals(null, loading.weather.error)

            val success = awaitItem()
            assertEquals(successWeatherUi.city, success.weather.weatherUiData.city)
            assertEquals(successWeatherUi.temp, success.weather.weatherUiData.temp)
            assertEquals(successWeatherUi.condition, success.weather.weatherUiData.condition)
            assertEquals(false, success.weather.isLoading)
            assertEquals(null, success.weather.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `weather no internet error`() = runTest {

        every { getCurrentLocationUseCase() } returns flowOf(
            Result.Success(Location(lat = 10.0, lon = 20.0))
        )
        every { observeCompassUseCase() } returns flowOf(
            CompassData(azimuth = 10)
        )
        every { networkMonitor.observe() } returns flowOf(false)
        coEvery {
            calculateSunPositionUseCase(any(), any(), any(), any())
        } returns SunPosition(0.0, 0.0)

        val expectedError = NetworkError.NO_INTERNET
        val errorUiText = expectedError.toUiText()
        coEvery { getCurrentWeatherUseCase(lat = 10.0, lon = 20.0) } returns Result.Error(
            expectedError
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
            skipItems(1) //skip initial

            val loading = awaitItem()
            assertEquals(true, loading.weather.isLoading)
            assertEquals(null, loading.weather.error)

            val error = awaitItem()
            assertEquals(false, error.weather.isLoading)
            assertEquals(errorUiText, error.weather.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `success weather after internet on`() = runTest {
        every { getCurrentLocationUseCase() } returns flowOf(
            Result.Success(Location(lat = 10.0, lon = 20.0))
        )
        every { observeCompassUseCase() } returns flowOf(
            CompassData(azimuth = 10)
        )
        coEvery {
            calculateSunPositionUseCase(any(), any(), any(), any())
        } returns SunPosition(0.0, 0.0)

        //Internet: off -> on
        val networkFlow = MutableSharedFlow<Boolean>()
        every { networkMonitor.observe() } returns networkFlow

        val expectedError = NetworkError.NO_INTERNET
        val errorUiText = expectedError.toUiText()

        val expectedWeather = Weather(
            city = "Tashkent",
            temp = 25,
            condition = "Sunny",
            country = "USA",
            localtime = LocalDateTime.of(2025, 1, 1, 0, 0),
            lastUpdated = LocalDateTime.of(2025, 1, 1, 0, 0)
        )
        val expectedWeatherUiData = expectedWeather.toUiData()

        coEvery { getCurrentWeatherUseCase(lat = 10.0, lon = 20.0) } returnsMany listOf(
            Result.Error(expectedError),
            Result.Success(expectedWeather)
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
            skipItems(1) //skip initial

            networkFlow.emit(false)

            val loading = awaitItem()
            assertEquals(true, loading.weather.isLoading)
            assertEquals(null, loading.weather.error)

            val error = awaitItem()
            assertEquals(false, error.weather.isLoading)
            assertEquals(errorUiText, error.weather.error)

            networkFlow.emit(true)

            val loading2 = awaitItem()
            assertEquals(true, loading2.weather.isLoading)
            assertEquals(null, loading2.weather.error)

            val success = awaitItem()
            assertEquals(expectedWeatherUiData.city, success.weather.weatherUiData.city)
            assertEquals(expectedWeatherUiData.temp, success.weather.weatherUiData.temp)
            assertEquals(expectedWeatherUiData.condition, success.weather.weatherUiData.condition)
            assertEquals(false, success.weather.isLoading)
            assertEquals(null, success.weather.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `weather when location off then on`() = runTest {
        val locationFlow = MutableSharedFlow<Result<Location, LocationError>>(
            replay = 1,
            extraBufferCapacity = 1
        )
        every { getCurrentLocationUseCase() } returns locationFlow
        every { observeCompassUseCase() } returns flowOf(CompassData(azimuth = 0))
        coEvery {
            calculateSunPositionUseCase(any(), any(), any(), any())
        } returns SunPosition(0.0, 0.0)
        every { networkMonitor.observe() } returns flowOf(true)

        val expectedWeather = Weather(
            city = "Tashkent",
            temp = 25,
            condition = "Sunny",
            country = "USA",
            localtime = LocalDateTime.of(2025, 1, 1, 0, 0),
            lastUpdated = LocalDateTime.of(2025, 1, 1, 0, 0)
        )
        val expectedWeatherUiData = expectedWeather.toUiData()

        coEvery {
            getCurrentWeatherUseCase(lat = 10.0, lon = 20.0)
        } returns Result.Success(expectedWeather)


        viewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            getCurrentLocationUseCase,
            calculateSunPositionUseCase,
            observeCompassUseCase,
            networkMonitor,
            timeZoneRepository
        )

        viewModel.uiState.test {
            skipItems(1) //skip initial

            locationFlow.emit(Result.Error(LocationError.NOT_AVAILABLE))
            val loading = awaitItem()
            assertEquals(true, loading.weather.isLoading)
            assertEquals(null, loading.weather.error)

            advanceTimeBy(3_001)
            val error = awaitItem()
            assertEquals(false, error.weather.isLoading)
            assertEquals("-", error.weather.weatherUiData.city)
            assertEquals("-°C", error.weather.weatherUiData.temp)
            assertEquals("-", error.weather.weatherUiData.condition)
            assertEquals(LocationError.NOT_AVAILABLE.toUiText(), error.weather.error)

            locationFlow.emit(Result.Success(Location(lat = 10.0, lon = 20.0)))
            skipItems(1) // emit from sunFlow
            val loading2 = awaitItem()
            assertEquals(true, loading2.weather.isLoading)
            assertEquals(null, loading2.weather.error)

            val success = awaitItem()
            assertEquals(expectedWeatherUiData.city, success.weather.weatherUiData.city)
            assertEquals(expectedWeatherUiData.temp, success.weather.weatherUiData.temp)
            assertEquals(expectedWeatherUiData.condition, success.weather.weatherUiData.condition)
            assertEquals(false, success.weather.isLoading)
            assertEquals(null, success.weather.error)
        }
    }

    @Test
    fun `location success`() = runTest {
        every { getCurrentLocationUseCase() } returns flowOf(
            Result.Success(Location(lat = 10.0, lon = 20.0))
        )
        every { observeCompassUseCase() } returns flowOf(
            CompassData(azimuth = 10)
        )
        coEvery {
            calculateSunPositionUseCase(any(), any(), any(), any())
        } returns SunPosition(100.0, 40.0)

        val mockWeather = Weather(
            city = "Tashkent",
            temp = 25,
            condition = "Sunny",
            country = "USA",
            localtime = LocalDateTime.of(2025, 1, 1, 0, 0),
            lastUpdated = LocalDateTime.of(2025, 1, 1, 0, 0)
        )
        coEvery {
            getCurrentWeatherUseCase(lat = 10.0, lon = 20.0)
        } returns Result.Success(mockWeather)

        every { networkMonitor.observe() } returns flowOf(true)

        val viewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            getCurrentLocationUseCase,
            calculateSunPositionUseCase,
            observeCompassUseCase,
            networkMonitor,
            timeZoneRepository
        )

        viewModel.uiState.test {
            skipItems(1) //skip initial

            val state = awaitItem()
            val sun = state.sun

            assertEquals("10.0, 20.0", sun.coordinates)
            assertEquals("100.0°", sun.azimuth)
            assertEquals("40.0°", sun.altitude)
            assertEquals(null, sun.error)
        }
    }

    @Test
    fun `location initial error`() = runTest {
        every { getCurrentLocationUseCase() } returns flowOf(
            Result.Error(LocationError.NOT_AVAILABLE)
        )
        every { observeCompassUseCase() } returns flowOf(
            CompassData(azimuth = 10)
        )
        coEvery {
            calculateSunPositionUseCase(any(), any(), any(), any())
        } returns SunPosition(100.0, 40.0)

        val mockWeather = Weather(
            city = "Tashkent",
            temp = 25,
            condition = "Sunny",
            country = "USA",
            localtime = LocalDateTime.of(2025, 1, 1, 0, 0),
            lastUpdated = LocalDateTime.of(2025, 1, 1, 0, 0)
        )
        coEvery {
            getCurrentWeatherUseCase(lat = 10.0, lon = 20.0)
        } returns Result.Success(mockWeather)

        every { networkMonitor.observe() } returns flowOf(true)

        val viewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            getCurrentLocationUseCase,
            calculateSunPositionUseCase,
            observeCompassUseCase,
            networkMonitor,
            timeZoneRepository
        )

        viewModel.uiState.test {
            skipItems(1) //skip initial

            val locationError = awaitItem()
            val sun = locationError.sun

            assertEquals("-", sun.coordinates)
            assertEquals("-°", sun.azimuth)
            assertEquals("-°", sun.altitude)
            assertEquals(LocationError.NOT_AVAILABLE.toUiText(), sun.error)
        }
    }

    @Test
    fun `location success after initial error`() = runTest {
        val locationFlow =
            MutableSharedFlow<Result<Location, LocationError>>(replay = 1, extraBufferCapacity = 1)

        every { getCurrentLocationUseCase() } returns locationFlow
        every { observeCompassUseCase() } returns flowOf(
            CompassData(azimuth = 10)
        )
        coEvery {
            calculateSunPositionUseCase(any(), any(), any(), any())
        } returns SunPosition(100.0, 40.0)

        val mockWeather = Weather(
            city = "Tashkent",
            temp = 25,
            condition = "Sunny",
            country = "USA",
            localtime = LocalDateTime.of(2025, 1, 1, 0, 0),
            lastUpdated = LocalDateTime.of(2025, 1, 1, 0, 0)
        )
        coEvery {
            getCurrentWeatherUseCase(lat = 10.0, lon = 20.0)
        } returns Result.Success(mockWeather)

        every { networkMonitor.observe() } returns flowOf(true)

        val viewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            getCurrentLocationUseCase,
            calculateSunPositionUseCase,
            observeCompassUseCase,
            networkMonitor,
            timeZoneRepository
        )

        viewModel.uiState.test {
            skipItems(1) //skip initial

            locationFlow.emit(Result.Error(LocationError.NOT_AVAILABLE))
            val locationError = awaitItem()
            val sun = locationError.sun

            assertEquals("-", sun.coordinates)
            assertEquals("-°", sun.azimuth)
            assertEquals("-°", sun.altitude)
            assertEquals(LocationError.NOT_AVAILABLE.toUiText(), sun.error)

            locationFlow.emit(Result.Success(Location(lat = 10.0, lon = 20.0)))
            val locationSuccess = awaitItem()
            val sunSuccess = locationSuccess.sun
            assertEquals("10.0, 20.0", sunSuccess.coordinates)
            assertEquals("100.0°", sunSuccess.azimuth)
            assertEquals("40.0°", sunSuccess.altitude)
            assertEquals(null, sunSuccess.error)
        }
    }

    @Test
    fun `location error after success`() = runTest {
        val locationFlow =
            MutableSharedFlow<Result<Location, LocationError>>(replay = 1, extraBufferCapacity = 1)

        every { getCurrentLocationUseCase() } returns locationFlow
        every { observeCompassUseCase() } returns flowOf(
            CompassData(azimuth = 10)
        )
        coEvery {
            calculateSunPositionUseCase(any(), any(), any(), any())
        } returns SunPosition(100.0, 40.0)

        val mockWeather = Weather(
            city = "Tashkent",
            temp = 25,
            condition = "Sunny",
            country = "USA",
            localtime = LocalDateTime.of(2025, 1, 1, 0, 0),
            lastUpdated = LocalDateTime.of(2025, 1, 1, 0, 0)
        )
        coEvery {
            getCurrentWeatherUseCase(lat = 10.0, lon = 20.0)
        } returns Result.Success(mockWeather)

        every { networkMonitor.observe() } returns flowOf(true)

        val viewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            getCurrentLocationUseCase,
            calculateSunPositionUseCase,
            observeCompassUseCase,
            networkMonitor,
            timeZoneRepository
        )

        viewModel.uiState.test {
            skipItems(1) //skip initial

            locationFlow.emit(Result.Success(Location(lat = 10.0, lon = 20.0)))
            val locationSuccess = awaitItem()
            val sunSuccess = locationSuccess.sun
            assertEquals("10.0, 20.0", sunSuccess.coordinates)
            assertEquals("100.0°", sunSuccess.azimuth)
            assertEquals("40.0°", sunSuccess.altitude)
            assertEquals(null, sunSuccess.error)

            skipItems(1) //skip success weather
            locationFlow.emit(Result.Error(LocationError.NOT_AVAILABLE))
            val locationError = awaitItem()
            println(locationError)
            val sun = locationError.sun

            assertEquals("10.0, 20.0", sun.coordinates)
            assertEquals("100.0°", sun.azimuth)
            assertEquals("40.0°", sun.altitude)
            assertEquals(LocationError.NOT_AVAILABLE.toUiText(), sun.error)
        }
    }

    @Test
    fun compass() = runTest {
        every { getCurrentLocationUseCase() } returns flowOf(
            Result.Success(Location(lat = 10.0, lon = 20.0))
        )
        every { observeCompassUseCase() } returns flowOf(
            CompassData(azimuth = 10)
        )
        every { networkMonitor.observe() } returns flowOf(false)
        coEvery {
            calculateSunPositionUseCase(any(), any(), any(), any())
        } returns SunPosition(0.0, 0.0)

        val mockWeather = Weather(
            city = "Tashkent",
            temp = 25,
            condition = "Sunny",
            country = "USA",
            localtime = LocalDateTime.of(2025, 1, 1, 0, 0),
            lastUpdated = LocalDateTime.of(2025, 1, 1, 0, 0)
        )
        coEvery {
            getCurrentWeatherUseCase(lat = 10.0, lon = 20.0)
        } returns Result.Success(mockWeather)

        viewModel = HomeViewModel(
            getCurrentWeatherUseCase,
            getCurrentLocationUseCase,
            calculateSunPositionUseCase,
            observeCompassUseCase,
            networkMonitor,
            timeZoneRepository
        )

        viewModel.uiState.test {
            val init = awaitItem()
            var compass = init.compass
            assertEquals(0, compass.azimuth)
            assertEquals(0, compass.rotation)

            val success = awaitItem()
            compass = success.compass
            assertEquals(10, compass.azimuth)
            assertEquals(-10, compass.rotation)
        }
    }
}
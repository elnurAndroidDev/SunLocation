package com.isaevapps.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaevapps.domain.model.Location
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.result.LocationError
import com.isaevapps.domain.result.Result
import com.isaevapps.domain.usecase.CalculateSunPositionUseCase
import com.isaevapps.domain.usecase.GetCurrentLocationUseCase
import com.isaevapps.domain.usecase.GetCurrentWeatherUseCase
import com.isaevapps.domain.usecase.ObserveCompassUseCase
import com.isaevapps.domain.utils.NetworkMonitor
import com.isaevapps.presentation.utils.tickerFlow
import com.isaevapps.presentation.utils.toUiData
import com.isaevapps.presentation.utils.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val calculateSunPositionUseCase: CalculateSunPositionUseCase,
    private val observeCompassUseCase: ObserveCompassUseCase,
    networkMonitor: NetworkMonitor,
    timeZoneRepository: TimeZoneRepository
) : ViewModel() {
    private val systemUTC = timeZoneRepository.getSystemUtc().getUTCDouble()

    private val networkFlow = networkMonitor.observe()

    private val lastLocation: StateFlow<Result<Location, LocationError>?> =
        getCurrentLocationUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    private val compassFlow: Flow<CompassUiState> = observeCompassUseCase()
        .map { compass ->
            CompassUiState(
                azimuth = (compass.azimuth + 360) % 360,
                rotation = -compass.azimuth
            )
        }

    @OptIn(FlowPreview::class)
    private val sunFlow: Flow<SunUiState> = lastLocation
        .filterNotNull()
        .map { resultLocation ->
            if (resultLocation is Result.Success) {
                val loc = resultLocation.data
                val sun = calculateSunPositionUseCase(
                    latitude = loc.lat,
                    longitude = loc.lon,
                    dateTime = LocalDateTime.now(),
                    utcOffset = systemUTC
                )
                uiState.value.sun.copy(
                    coordinates = "${loc.lat}, ${loc.lon}",
                    azimuth = "${sun.azimuth}°",
                    altitude = "${sun.altitude}°",
                    error = null
                )
            } else
                uiState.value.sun.copy(
                    error = (resultLocation as Result.Error).error.toUiText()
                )
        }

    private val weatherRefreshTriggers: Flow<Unit> =
        merge(
            flowOf(Unit),
            networkFlow
                .filter { it }
                .drop(1)
                .map { },
            tickerFlow(30.minutes)
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val weatherFlow: Flow<WeatherUiState> =
        weatherRefreshTriggers.flatMapLatest {
            flow {
                emit(
                    uiState.value.weather.copy(isLoading = true, error = null)
                )

                val locationResult = lastLocation.filter { it is Result.Success }.first()
                val loc = (locationResult as Result.Success).data
                when (val result = getCurrentWeatherUseCase(loc.lat, loc.lon)) {
                    is Result.Success -> emit(
                        WeatherUiState(
                            weatherUiData = result.data.toUiData(),
                            isLoading = false,
                            error = null
                        )
                    )

                    is Result.Error -> emit(
                        uiState.value.weather.copy(
                            isLoading = false,
                            error = result.error.toUiText()
                        )
                    )
                }
            }
        }


    val uiState: StateFlow<HomeUiState> =
        combine(
            weatherFlow,
            sunFlow,
            compassFlow
        ) { weather, sun, compass ->
            HomeUiState(
                weather = weather,
                sun = sun,
                compass = compass
            )
        }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeUiState()
            )
}
package com.isaevapps.presentation.screens.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaevapps.domain.model.Coordinates
import com.isaevapps.domain.model.SunPosition
import com.isaevapps.domain.model.TimeZone
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.result.CoordinatesError
import com.isaevapps.domain.result.Result
import com.isaevapps.domain.result.Result.Success
import com.isaevapps.domain.usecase.CalculateSunPositionUseCase
import com.isaevapps.domain.usecase.ExtractCoordinatesUseCase
import com.isaevapps.presentation.utils.toStringFormatted
import com.isaevapps.presentation.utils.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class CalculateViewModelState(
    val timeZone: TimeZone,
    val sunPosition: SunPosition? = null,
    val coordinates: Coordinates? = null,
    val coordinatesInput: String = "",
    val invalidCoordinates: CoordinatesError? = null,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class CalculateViewModel @Inject constructor(
    private val extractCoordinatesUseCase: ExtractCoordinatesUseCase,
    private val calculateSunPositionUseCase: CalculateSunPositionUseCase,
    timeZoneRepository: TimeZoneRepository
) : ViewModel() {

    private val timeZones = timeZoneRepository.timeZones
    private val systemUtc = timeZoneRepository.getSystemUtc()

    private val coordinatesInputFlow = MutableStateFlow("")
    private val vmState = MutableStateFlow(CalculateViewModelState(timeZone = systemUtc))
    val uiState = vmState.map { state ->
        CalculateUiState(
            coordinates = state.coordinatesInput,
            invalidCoordinates = state.invalidCoordinates?.toUiText(),
            date = state.date.toStringFormatted(),
            time = state.time.toStringFormatted(),
            showDatePicker = state.showDatePicker,
            showTimePicker = state.showTimePicker,
            timeZones = timeZones.map { it.name },
            timeZone = state.timeZone.name,
            azimuth = state.sunPosition?.azimuth?.toString() ?: "-",
            altitude = state.sunPosition?.altitude?.toString() ?: "-"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalculateUiState()
    )

    init {
        viewModelScope.launch {
            coordinatesInputFlow
                .drop(1)
                .debounce(300)
                .distinctUntilChanged()
                .collect { input ->
                    val result = extractCoordinatesUseCase(input)
                    vmState.update { state ->
                        state.copy(
                            coordinates = (result as? Success)?.data,
                            invalidCoordinates = (result as? Result.Error)?.error,
                        )
                    }
                }
        }
    }

    fun calculateSunPosition() = viewModelScope.launch {
        val coordinates = vmState.value.coordinates ?: return@launch
        val date = vmState.value.date
        val time = vmState.value.time
        val dateTime = date.atTime(time)
        val utcOffset = vmState.value.timeZone.getUTCDouble()

        val sunPosition = calculateSunPositionUseCase(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
            dateTime = dateTime,
            utcOffset = utcOffset
        )
        vmState.update { state ->
            state.copy(sunPosition = sunPosition)
        }
    }

    fun onCoordinateChange(coordinates: String) {
        vmState.update { it ->
            it.copy(coordinatesInput = coordinates)
        }
        coordinatesInputFlow.value = coordinates
    }

    fun onDateChange(date: LocalDate) {
        vmState.update { state ->
            state.copy(date = date)
        }
    }

    fun onTimeChange(time: LocalTime) {
        vmState.update { state ->
            state.copy(time = time)
        }
    }

    fun onTimeZoneChange(timeZoneName: String) {
        val timeZone = timeZones.find { it.name == timeZoneName } ?: systemUtc
        vmState.update { state ->
            state.copy(timeZone = timeZone)
        }
    }

    fun showDatePicker() {
        vmState.update { state ->
            state.copy(showDatePicker = true)
        }
    }

    fun showTimePicker() {
        vmState.update { state ->
            state.copy(showTimePicker = true)
        }
    }

    fun hideDatePicker() {
        vmState.update { state ->
            state.copy(showDatePicker = false)
        }
    }

    fun hideTimePicker() {
        vmState.update { state ->
            state.copy(showTimePicker = false)
        }
    }
}
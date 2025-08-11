package com.isaevapps.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isaevapps.presentation.screens.components.AppDatePickerDialog
import com.isaevapps.presentation.screens.components.AppTextField
import com.isaevapps.presentation.screens.components.AppTimePickerDialog
import com.isaevapps.presentation.screens.components.DropDownMenu
import com.isaevapps.presentation.ui.theme.SunLocationTheme
import com.isaevapps.presentation.utils.toLocalDateOrNull
import com.isaevapps.presentation.utils.toLocalTimeOrNull
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Log.d("HomeScreen", "date: ${uiState.date}")

    HomeScreenContent(
        state = uiState,
        onCoordinatesChange = viewModel::onCoordinateChange,
        onTimeZoneChange = viewModel::onTimeZoneChange,
        onDateClick = viewModel::showDatePicker,
        onTimeClick = viewModel::showTimePicker,
        onCalculateClick = viewModel::calculateSunPosition,
        modifier = modifier
    )

    if (uiState.showDatePicker) {
        AppDatePickerDialog(
            initialDate = uiState.date.toLocalDateOrNull() ?: LocalDate.now(),
            onDateChange = viewModel::onDateChange,
            hideDialog = viewModel::hideDatePicker
        )
    }

    if (uiState.showTimePicker) {
        AppTimePickerDialog(
            initialTime = uiState.time.toLocalTimeOrNull() ?: LocalTime.now(),
            onTimeChange = viewModel::onTimeChange,
            hideDialog = viewModel::hideTimePicker
        )
    }
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onCoordinatesChange: (String) -> Unit = {},
    onTimeZoneChange: (String) -> Unit = {},
    onDateClick: () -> Unit = {},
    onTimeClick: () -> Unit = {},
    onCalculateClick: () -> Unit = {}
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppTextField(
                value = state.coordinates,
                onValueChange = onCoordinatesChange,
                placeholderText = "Enter coordinates",
                label = "Coordinates",
                isError = state.invalidCoordinates != null,
                modifier = Modifier.fillMaxWidth()
            )
            Log.d("HomeScreenContent", "systemUTC: ${state.timeZone}")
            DropDownMenu(
                options = state.timeZones,
                selectedOption = state.timeZone,
                onSelectionChange = onTimeZoneChange,
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                AppTextField(
                    value = state.date,
                    onClick = {
                        Log.d("HomeScreenContent", "clicked date")
                        onDateClick()
                    },
                    readOnly = true,
                    label = "Date",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(Modifier.width(16.dp))

                AppTextField(
                    value = state.time,
                    onClick = onTimeClick,
                    readOnly = true,
                    label = "Time",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Azimuth: ${state.azimuth}")
                Spacer(Modifier.height(8.dp))
                Text(text = "Altitude: ${state.altitude}")
            }

            Button(
                onClick = onCalculateClick
            ) {
                Text("Calculate")
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeScreenContentPreview() {
    SunLocationTheme {
        HomeScreenContent(
            state = HomeUiState(
                timeZones = listOf("UTC+01:00", "UTC+02:00", "UTC+03:00"),
                timeZone = "UTC+02:00",
                date = "01.01.2023",
                time = "12:00",
                coordinates = "55°45′0″N 37°37′0″E"
            ), modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}
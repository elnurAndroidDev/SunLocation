package com.isaevapps.presentation.screens.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isaevapps.presentation.screens.components.AppDatePickerDialog
import com.isaevapps.presentation.screens.components.AppTextField
import com.isaevapps.presentation.screens.components.AppTimePickerDialog
import com.isaevapps.presentation.screens.components.DropDownMenu
import com.isaevapps.presentation.screens.components.GlassCard
import com.isaevapps.presentation.screens.components.MetricPill
import com.isaevapps.presentation.screens.components.SunBadge
import com.isaevapps.presentation.ui.theme.ButtonGradient
import com.isaevapps.presentation.ui.theme.SunLocationTheme
import com.isaevapps.presentation.utils.toLocalDateOrNull
import com.isaevapps.presentation.utils.toLocalTimeOrNull
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculateScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<CalculateViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CalculateScreenContent(
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
fun CalculateScreenContent(
    state: CalculateUiState,
    modifier: Modifier = Modifier,
    onCoordinatesChange: (String) -> Unit = {},
    onTimeZoneChange: (String) -> Unit = {},
    onDateClick: () -> Unit = {},
    onTimeClick: () -> Unit = {},
    onCalculateClick: () -> Unit = {}
) {
    Box(
        modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Sun Calculator",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )

            GlassCard {
                Column {
                    Text(
                        "Input",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(Modifier.height(12.dp))

                    AppTextField(
                        value = state.coordinates,
                        onValueChange = onCoordinatesChange,
                        label = "Coordinates",
                        isError = state.invalidCoordinates != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    DropDownMenu(
                        options = state.timeZones,
                        selectedOption = state.timeZone,
                        onSelectionChange = onTimeZoneChange,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        AppTextField(
                            value = state.date,
                            onClick = onDateClick,
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

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onCalculateClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ButtonGradient, RoundedCornerShape(16.dp)),
                    ) {
                        Text("Calculate", color = Color.White)
                    }
                }
            }

            // Карточка — результаты и траектория солнца
            GlassCard(padding = 16.dp) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Sun position",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        SunBadge()
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricPill(
                            title = "Azimuth",
                            value = state.azimuth,
                            icon = Icons.Outlined.Info
                        )
                        MetricPill(
                            title = "Altitude",
                            value = state.altitude,
                            icon = Icons.Outlined.Info
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = false, showBackground = false)
@Composable
private fun CalculateScreenContentPreview() {
    SunLocationTheme {
        CalculateScreenContent(
            state = CalculateUiState(
                timeZones = listOf("UTC+01:00", "UTC+02:00", "UTC+03:00"),
                timeZone = "UTC+02:00",
                date = "01.01.2023",
                time = "12:00",
                coordinates = "55°45′0″N 37°37′0″E"
            )
        )
    }
}
package com.isaevapps.presentation.screens.calculator

import android.content.res.Configuration
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isaevapps.presentation.R
import com.isaevapps.presentation.screens.common.GlassCard
import com.isaevapps.presentation.screens.components.AppDatePickerDialog
import com.isaevapps.presentation.screens.components.AppTextField
import com.isaevapps.presentation.screens.components.AppTimePickerDialog
import com.isaevapps.presentation.screens.components.DropDownMenu
import com.isaevapps.presentation.screens.components.MetricPill
import com.isaevapps.presentation.screens.components.SunBadge
import com.isaevapps.presentation.ui.theme.SunLocationTheme
import com.isaevapps.presentation.ui.theme.appColors
import com.isaevapps.presentation.ui.theme.appTypography
import com.isaevapps.presentation.ui.theme.gradients
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
                stringResource(R.string.sun_calculator),
                style = MaterialTheme.appTypography.headline,
                color = MaterialTheme.appColors.onBackground
            )

            GlassCard(padding = 16.dp) {
                Column {
                    Text(
                        stringResource(R.string.input),
                        style = MaterialTheme.appTypography.title,
                        color = MaterialTheme.appColors.onBackground,
                    )
                    Spacer(Modifier.height(12.dp))

                    AppTextField(
                        value = state.coordinates,
                        onValueChange = onCoordinatesChange,
                        label = stringResource(R.string.coordinates),
                        errorText = state.invalidCoordinates?.asString(LocalContext.current),
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
                            label = stringResource(R.string.date),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        AppTextField(
                            value = state.time,
                            onClick = onTimeClick,
                            readOnly = true,
                            label = stringResource(R.string.time),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onCalculateClick,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                        ),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.gradients.accent,
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.calculate),
                                style = MaterialTheme.appTypography.body,
                                color = Color.White
                            )
                        }
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
                            stringResource(R.string.sun_position),
                            style = MaterialTheme.appTypography.title,
                            color = MaterialTheme.appColors.onBackground,
                        )
                        SunBadge()
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricPill(
                            title = stringResource(R.string.azimuth),
                            value = state.azimuth
                        )
                        MetricPill(
                            title = stringResource(R.string.altitude),
                            value = state.altitude
                        )
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun CalculateScreenContentPreview() {
    SunLocationTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.gradients.background),
            color = Color.Transparent
        ) {
            CalculateScreenContent(
                state = CalculateUiState(
                    timeZones = listOf("UTC+01:00", "UTC+02:00", "UTC+03:00"),
                    timeZone = "UTC+02:00",
                    date = "01.01.2023",
                    time = "12:00",
                    coordinates = "41.476104, 69.575205",
                    azimuth = "123°",
                    altitude = "45°",
                    invalidCoordinates = null
                )
            )
        }
    }
}
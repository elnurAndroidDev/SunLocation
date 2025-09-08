@file:Suppress("UnusedImport")

package com.isaevapps.presentation.screens.main

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.isaevapps.presentation.R
import com.isaevapps.presentation.screens.main.cards.CompassCard
import com.isaevapps.presentation.screens.main.cards.LocationCard
import com.isaevapps.presentation.screens.main.cards.WeatherCard
import com.isaevapps.presentation.screens.main.models.WeatherUiData
import com.isaevapps.presentation.ui.theme.SunLocationTheme
import com.isaevapps.presentation.ui.theme.appColors
import com.isaevapps.presentation.ui.theme.appTypography
import com.isaevapps.presentation.ui.theme.gradients

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<HomeViewModel>()

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val compassSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCompassSheet by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.appColors.background,
                onClick = { showCompassSheet = true }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_compass),
                    tint = MaterialTheme.appColors.primary,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
    ) {
        HomeScreenContent(
            state = state,
            modifier = modifier
        )
        if (showCompassSheet) {
            val compassUiState by viewModel.compassFlow.collectAsStateWithLifecycle(CompassUiState())
            ModalBottomSheet(
                onDismissRequest = { showCompassSheet = false },
                containerColor = MaterialTheme.appColors.background,
                sheetState = compassSheetState,
                dragHandle = {
                    BottomSheetDefaults.DragHandle(
                        color = MaterialTheme.appColors.primary
                    )
                }
            ) {
                CompassCard(state = compassUiState, Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    state: HomeUiState, modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.sun_position),
                style = MaterialTheme.appTypography.headline,
                color = MaterialTheme.appColors.onBackground,
                modifier = Modifier.alpha(0.95f)
            )
            WeatherCard(state.weather)
            LocationCard(state.sun)
        }
    }
}

@Preview(name = "Light")
@Composable
private fun PreviewHomeLight() {

    val weatherData = WeatherUiData(
        city = "Tashkent",
        temp = "25°C",
        condition = "Sunny"
    )
    val weatherState = WeatherUiState(weatherUiData = weatherData, isLoading = false)

    val sunState = SunUiState(
        coordinates = "41.476104, 69.575205",
        azimuth = "123°",
        altitude = "45°",
        error = null
    )

    val compassState = CompassUiState(azimuth = 123, rotation = -123)

    val state = HomeUiState(
        weather = weatherState,
        sun = sunState
    )

    SunLocationTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.gradients.background),
            color = Color.Transparent
        ) {
            HomeScreenContent(state)
        }
    }
}

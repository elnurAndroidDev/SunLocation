@file:Suppress("UnusedImport")

package com.isaevapps.presentation.screens.main

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.isaevapps.presentation.ui.theme.SunLocationTheme
import com.isaevapps.presentation.ui.theme.appColors
import com.isaevapps.presentation.ui.theme.appTypography

@OptIn(ExperimentalPermissionsApi::class)
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
    HomeScreenContent(
        state = state,
        modifier = modifier
    )
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
            CompassCard(state.compass)
        }
    }
}

@Preview(name = "Sun UI — Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = false)
@Composable
private fun PreviewSunUiLight() {
    SunLocationTheme {
    }
}

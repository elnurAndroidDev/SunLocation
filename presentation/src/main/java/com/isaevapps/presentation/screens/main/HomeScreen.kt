@file:Suppress("UnusedImport")

package com.isaevapps.presentation.screens.main

import android.Manifest
import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.isaevapps.presentation.R
import com.isaevapps.presentation.screens.components.GlassCard
import com.isaevapps.presentation.screens.components.MetricPill
import com.isaevapps.presentation.screens.components.shimmer
import com.isaevapps.presentation.ui.theme.SunLocationTheme
import com.isaevapps.presentation.ui.theme.appColors

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
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.appColors.onBackground
                ),
                modifier = Modifier.alpha(0.95f)
            )
            WeatherCard(
                state.weather, modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
            LocationCard(
                state.sun,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            CompassCard(state.compass, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun WeatherCard(state: WeatherUiState, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, padding = 0.dp) {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        modifier = Modifier.size(18.dp),
                        contentDescription = null,
                        tint = MaterialTheme.appColors.onBackground.copy(alpha = 0.9f)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = state.city,
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.appColors.onBackground),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .widthIn(min = 80.dp)
                            .shimmer(visible = state.isLoading)
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.temp,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.appColors.onBackground
                        ),
                        modifier = Modifier
                            .widthIn(min = 30.dp)
                            .shimmer(visible = state.isLoading),
                        textAlign = TextAlign.Center
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        state.condition,
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.appColors.onBackground),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .widthIn(min = 80.dp)
                            .shimmer(visible = state.isLoading)
                    )
                }
            }
            if (state.error != null) {
                Text(
                    text = state.error.asString(LocalContext.current),
                    fontSize = 12.sp,
                    color = MaterialTheme.appColors.onBackground,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun LocationCard(state: SunUiState, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, padding = 0.dp) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.Center)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        modifier = Modifier.size(18.dp),
                        contentDescription = null,
                        tint = MaterialTheme.appColors.onBackground.copy(alpha = 0.9f)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = state.coordinates,
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.appColors.onBackground),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .widthIn(min = 120.dp)
                            .shimmer(visible = state.coordinates.isBlank())
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricPill(
                        title = stringResource(R.string.azimuth),
                        value = state.azimuth,
                        shimmerVisible = state.azimuth.isBlank()
                    )
                    MetricPill(
                        title = stringResource(R.string.altitude),
                        value = state.altitude,
                        shimmerVisible = state.altitude.isBlank()
                    )
                }
            }
            if (state.error != null) {
                Text(
                    text = state.error.asString(LocalContext.current),
                    fontSize = 12.sp,
                    color = MaterialTheme.appColors.onBackground,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun CompassCard(state: CompassUiState, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "${state.azimuth}°",
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.appColors.onBackground),
            )
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
                Image(
                    painter = painterResource(R.drawable.compass),
                    contentDescription = null,
                    colorFilter = if (isSystemInDarkTheme()) ColorFilter.tint(Color.White) else null
                )
                val rotation by animateFloatAsState(
                    targetValue = state.rotation.toFloat(),
                    animationSpec = tween(),
                    label = ""
                )
                Image(
                    painter = painterResource(R.drawable.arrow),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = rotation
                    }
                )
            }
        }
    }
}

@Preview(name = "Sun UI — Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = false)
@Composable
private fun PreviewSunUiLight() {
    SunLocationTheme {
    }
}

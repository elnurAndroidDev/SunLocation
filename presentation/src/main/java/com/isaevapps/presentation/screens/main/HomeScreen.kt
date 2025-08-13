@file:Suppress("UnusedImport")

package com.isaevapps.presentation.screens.main

import android.Manifest
import com.isaevapps.presentation.R
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.isaevapps.presentation.SunLocationApp
import com.isaevapps.presentation.screens.components.GlassCard
import com.isaevapps.presentation.screens.components.MetricPill
import com.isaevapps.presentation.screens.components.SunBadge
import com.isaevapps.presentation.ui.theme.BackgroundGradient
import com.isaevapps.presentation.ui.theme.SunLocationTheme
import kotlin.math.cos
import kotlin.math.sin

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

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.collectLocation()
        } else {
            Log.d("HomeScreen", "Permissions not granted")
        }
    }
    val state by viewModel.uiState.collectAsState()
    HomeScreenContent(state = state, modifier = modifier)
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    modifier: Modifier = Modifier
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
                text = "Sun Location",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),
                modifier = Modifier.alpha(0.95f)
            )

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = state.city,
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        state.temp,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    state.condition,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.alpha(0.95f),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White.copy(alpha = 0.08f),
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }

            GlassCard {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            modifier = Modifier.size(18.dp),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = state.coords,
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricPill(
                            title = "Azimuth",
                            value = state.azimuth,
                            icon = null
                        )
                        MetricPill(
                            title = "Altitude",
                            value = state.altitude,
                            icon = null,
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Sun UI — Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = false)
@Composable
private fun PreviewSunUiLight() {
    SunLocationTheme {
        HomeScreenContent(
            state = HomeUiState(
                city = "Rome",
                temp = "33°",
                condition = "Clear",
                coords = "41.2995, 69.2401",
                azimuth = "123°",
                altitude = "66°"
            )
        )
    }
}

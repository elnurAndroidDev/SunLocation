package com.isaevapps.presentation.screens.main.cards

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.isaevapps.domain.result.LocationError
import com.isaevapps.presentation.R
import com.isaevapps.presentation.screens.common.GlassCard
import com.isaevapps.presentation.screens.components.MetricPill
import com.isaevapps.presentation.screens.components.shimmer
import com.isaevapps.presentation.screens.main.SunUiState
import com.isaevapps.presentation.ui.theme.SunLocationTheme
import com.isaevapps.presentation.ui.theme.appColors
import com.isaevapps.presentation.ui.theme.appTypography
import com.isaevapps.presentation.utils.toUiText

@Composable
fun LocationCard(state: SunUiState) {
    val shimmerVisible = state.error == null && state.coordinates == "-"
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.Center)
            ) {
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
                        style = MaterialTheme.appTypography.body,
                        color = MaterialTheme.appColors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .widthIn(min = 120.dp)
                            .shimmer(visible = shimmerVisible)
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
                        shimmerVisible = shimmerVisible
                    )
                    MetricPill(
                        title = stringResource(R.string.altitude),
                        value = state.altitude,
                        shimmerVisible = shimmerVisible
                    )
                }
            }
            if (state.error != null) {
                Text(
                    text = state.error.asString(LocalContext.current),
                    style = MaterialTheme.appTypography.bodySmall,
                    color = MaterialTheme.appColors.onBackground,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(name = "Loading")
@Composable
private fun LoadingLocationCardPreview() {
    val state = SunUiState()
    SunLocationTheme {
        LocationCard(state)
    }
}

@Preview(name = "Success")
@Composable
private fun SuccessLocationCardPreview() {
    val state = SunUiState(
        coordinates = "41.476104, 69.575205",
        azimuth = "123°",
        altitude = "45°",
        error = null
    )
    SunLocationTheme {
        LocationCard(state)
    }
}

@Preview(name = "FirstError")
@Composable
private fun FirstErrorLocationCardPreview() {
    val state = SunUiState(
        error = LocationError.NOT_AVAILABLE.toUiText()
    )
    SunLocationTheme {
        LocationCard(state)
    }
}

@Preview(name = "NextError")
@Composable
private fun NextErrorLocationCardPreview() {
    val state = SunUiState(
        coordinates = "41.476104, 69.575205",
        azimuth = "123°",
        altitude = "45°",
        error = LocationError.NOT_AVAILABLE.toUiText()
    )
    SunLocationTheme {
        LocationCard(state)
    }
}
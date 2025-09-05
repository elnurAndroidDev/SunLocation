package com.isaevapps.presentation.screens.main.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.isaevapps.presentation.R
import com.isaevapps.presentation.screens.common.GlassCard
import com.isaevapps.presentation.screens.main.CompassUiState
import com.isaevapps.presentation.ui.theme.SunLocationTheme
import com.isaevapps.presentation.ui.theme.appColors
import com.isaevapps.presentation.ui.theme.appTypography

@Composable
fun CompassCard(state: CompassUiState, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, padding = 20.dp) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "${state.azimuth}°",
                style = MaterialTheme.appTypography.bodyLarge,
                color = MaterialTheme.appColors.onBackground
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


@Preview
@Composable
private fun CompassPreview() {
    val state = CompassUiState(azimuth = 123, rotation = -123)
    SunLocationTheme {
        CompassCard(state)
    }
}
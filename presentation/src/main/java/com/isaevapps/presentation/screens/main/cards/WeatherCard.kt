package com.isaevapps.presentation.screens.main.cards

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.isaevapps.presentation.screens.common.GlassCard
import com.isaevapps.presentation.screens.components.shimmer
import com.isaevapps.presentation.screens.main.WeatherUiState
import com.isaevapps.presentation.ui.theme.appColors
import com.isaevapps.presentation.ui.theme.appTypography

@Composable
fun WeatherCard(state: WeatherUiState) {
    val weather = state.weatherUiData
    val error = state.error
    val isLoading = state.isLoading
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
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
                        text = weather.city,
                        style = MaterialTheme.appTypography.body,
                        color = MaterialTheme.appColors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .widthIn(min = 80.dp)
                            .shimmer(visible = isLoading)
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = weather.temp,
                        style = MaterialTheme.appTypography.bodyLarge,
                        color = MaterialTheme.appColors.onBackground,
                        modifier = Modifier
                            .widthIn(min = 30.dp)
                            .shimmer(visible = isLoading),
                        textAlign = TextAlign.Center
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        weather.condition,
                        style = MaterialTheme.appTypography.body,
                        color = MaterialTheme.appColors.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .widthIn(min = 80.dp)
                            .shimmer(visible = isLoading)
                    )
                }
            }
            if (error != null) {
                Text(
                    text = error.asString(LocalContext.current),
                    style = MaterialTheme.appTypography.bodySmall,
                    color = MaterialTheme.appColors.onBackground,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

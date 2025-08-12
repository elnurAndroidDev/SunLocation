package com.isaevapps.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.isaevapps.presentation.screens.calculator.CalculateScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Chart

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Any = Home,
    modifier: Modifier = Modifier
) {
    NavHost(navController, startDestination, modifier) {
        composable<Home> {
            CalculateScreen()
        }

        composable<Chart> {

        }
    }

}
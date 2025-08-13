package com.isaevapps.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.isaevapps.presentation.screens.calculator.CalculateScreen
import com.isaevapps.presentation.screens.main.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Calculator

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Any = Home,
    modifier: Modifier = Modifier
) {
    NavHost(navController, startDestination, modifier) {
        composable<Home> {
            HomeScreen()
        }

        composable<Calculator> {
            CalculateScreen()
        }
    }

}
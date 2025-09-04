package com.isaevapps.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.isaevapps.presentation.navigation.BottomBar
import com.isaevapps.presentation.navigation.MainNavHost

@Composable
fun SunLocationApp() {
    val navController = rememberNavController()
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->
        Surface(
            color = Color.Transparent
        ) {
            MainNavHost(navController, modifier = Modifier.padding(innerPadding))
        }
    }
}
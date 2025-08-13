package com.isaevapps.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.isaevapps.presentation.navigation.MainNavHost
import com.isaevapps.presentation.ui.theme.BackgroundGradient

@Composable
fun SunLocationApp() {
    val navController = rememberNavController()
    Scaffold(
//        topBar = {
//            AnimeQuoteTopAppBar(
//                scrollBehavior = scrollBehavior,
//                label = BottomNavItem.items()[selectedItemIndex].label
//            )
//        },
//        bottomBar = {
//            BottomBar(navController)
//        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize().background(BackgroundGradient),
            color = Color.Transparent
        ) {
            MainNavHost(navController, modifier = Modifier.padding(innerPadding))
        }
    }
}
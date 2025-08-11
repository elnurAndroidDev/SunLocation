package com.isaevapps.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.isaevapps.presentation.navigation.MainNavHost

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
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainNavHost(navController, modifier = Modifier.padding(innerPadding))
        }
    }
}
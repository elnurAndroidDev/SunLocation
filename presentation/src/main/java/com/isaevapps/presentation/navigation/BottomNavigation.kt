package com.isaevapps.presentation.navigation

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.isaevapps.presentation.R
import com.isaevapps.presentation.ui.theme.BackgroundGradient
import com.isaevapps.presentation.ui.theme.BottomBarGradient

data class BottomNavItem<T : Any>(
    val route: T,
    @DrawableRes
    val selectedIcon: Int,
    @DrawableRes
    val unselectedIcon: Int,
    @StringRes val label: Int
) {
    companion object {
        fun items() = listOf(
            BottomNavItem(Home, R.drawable.home_filled, R.drawable.home, R.string.home),
            BottomNavItem(Calculator, R.drawable.calculator_filled, R.drawable.calculator, R.string.calculator)
        )
    }
}

val Any.routeName: String
    get() = this::class.qualifiedName.toString()

@SuppressLint("RestrictedApi")
@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier.background(brush = BottomBarGradient),
    ) {
        BottomNavItem.items().forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.route.routeName
            } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
                    Icon(painter = painterResource(icon), contentDescription = null)
                },
                label = { Text(text = stringResource(item.label)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
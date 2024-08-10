package com.tloske.overtimetracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.tloske.overtimetracker.composables.AdBanner
import com.tloske.overtimetracker.screens.HolidayTracker
import com.tloske.overtimetracker.screens.OvertimeTracker
import com.tloske.overtimetracker.screens.Screen

class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
        )
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Overtime.route
                ) {
                    composable(Screen.Overtime.route) {
                        OvertimeTracker { BottomNavBar(navController = navController) }
                    }
                    composable(Screen.Holiday.route) {
                        HolidayTracker { BottomNavBar(navController = navController) }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val navItems = listOf(Screen.Overtime, Screen.Holiday)
    Column {
        AdBanner()
        NavigationBar(
            windowInsets = WindowInsets.navigationBars,
            containerColor = MaterialTheme.colorScheme.primary,
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            navItems.forEach { screen ->
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.inversePrimary,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true

                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.icon),
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(id = screen.resourceId)) },
                    alwaysShowLabel = true,
                )
            }
        }
    }
}
package com.example.mymoodtracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    DIARY("diary", "Diary", Icons.Default.Edit, "Diary"),
    MOOD("mood", "Mood", Icons.Default.Favorite, "Mood"),
    EMERGENCY("emergency", "Emergency", Icons.Default.Warning, "Emergency"),
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    db: AppDatabase,
    modifier: Modifier,
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.DIARY-> DiaryPageFeature(db)
                    Destination.MOOD -> FirstPage(db)
                    Destination.EMERGENCY -> EmergencyPage()
                }
            }
        }
    }
}

@Composable
fun NavigationBarFeature(db: AppDatabase, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.MOOD
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                // 1. Change the background color of the Bar
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                Destination.entries.forEachIndexed { index, destination ->
                    val isSelected = selectedDestination == index
                    
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) },
                        // 2. Change the colors of the Icons and Text
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(
            navController,
            startDestination,
            db,
            Modifier.padding(contentPadding),
        )
    }
}

package com.example.allinone.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*

@Composable
fun DashboardScreen(
    navController: NavController
) {
    val bottomNavController = rememberNavController()
    val expenseViewModel: ExpenseViewModel = viewModel()


    // Theme Colors
    val themePrimary = MaterialTheme.colorScheme.primary
    val themeBackground = MaterialTheme.colorScheme.background
    val themeSurface = MaterialTheme.colorScheme.surface

    Scaffold(
        containerColor = themeBackground, // Ensures the whole screen background is dark

        floatingActionButton = {
            FloatingActionButton(
                onClick = { expenseViewModel.setExpenseToEdit(null)
                    navController.navigate("add_expense") },
                containerColor = themePrimary,
                contentColor = Color.Black,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },

        bottomBar = {
            NavigationBar(
                containerColor = themeSurface, // Slightly lighter than background per your theme
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val items = listOf(
                    Triple("home_tab", "Home", Icons.Default.Home),
                    Triple("analytics_tab", "Analytics", Icons.Default.PieChart),
                    Triple("budget_tab", "Budget", Icons.Default.AccountBalanceWallet),
                    Triple("settings_tab", "Settings", Icons.Default.Settings)
                )

                items.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            if (currentRoute != route) {
                                bottomNavController.navigate(route) {
                                    // Pop up to the start destination to avoid building a large stack
                                    popUpTo(bottomNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = themePrimary,
                            indicatorColor = themePrimary, // The "pill" behind the icon
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }

    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home_tab",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home_tab") {
                HomeScreen(navController)
            }
            composable("analytics_tab") {
                // Assuming these exist or will be created
                AnalyticsScreen()
            }
            composable("budget_tab") {
                BudgetScreen()
            }
            composable("settings_tab") {
                SettingsScreen(navController)
            }
        }
    }
}
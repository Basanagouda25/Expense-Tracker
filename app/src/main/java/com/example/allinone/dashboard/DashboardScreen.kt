package com.example.allinone.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.*

@Composable
fun DashboardScreen(
    navController: NavController
) {

    val bottomNavController = rememberNavController()

    Scaffold(

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_expense")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },

        bottomBar = {
            NavigationBar {

                val currentRoute =
                    bottomNavController.currentBackStackEntryAsState()
                        .value?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == "home_tab",
                    onClick = {
                        bottomNavController.navigate("home_tab")
                    },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = currentRoute == "analytics_tab",
                    onClick = {
                        bottomNavController.navigate("analytics_tab")
                    },
                    icon = { Icon(Icons.Default.PieChart, null) },
                    label = { Text("Analytics") }
                )

                NavigationBarItem(
                    selected = currentRoute == "budget_tab",
                    onClick = {
                        bottomNavController.navigate("budget_tab")
                    },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, null) },
                    label = { Text("Budget") }
                )

                NavigationBarItem(
                    selected = currentRoute == "settings_tab",
                    onClick = {
                        bottomNavController.navigate("settings_tab")
                    },
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Settings") }
                )
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
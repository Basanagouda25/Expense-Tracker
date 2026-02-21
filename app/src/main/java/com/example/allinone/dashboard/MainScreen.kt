package com.example.allinone.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(){
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ){ padding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding)
        ) {

            composable("dashboard") { HomeScreen(navController) }
            composable("analytics") { AnalyticsScreen() }
            composable("add") { AddExpenseScreen(navController) }
            composable("budget") { BudgetScreen() }
            composable("settings") { SettingsScreen(navController) }
        }
    }
}
package com.example.allinone.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.allinone.auth.LoginScreen
import com.example.allinone.navigation.Screen
import com.example.allinone.auth.Register
import com.example.allinone.dashboard.AddExpenseScreen
import com.example.allinone.dashboard.DashboardScreen
import com.example.allinone.dashboard.ExpenseViewModel
import com.example.allinone.dashboard.HomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(){
    //navigation controller
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }
    //nav host
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route)
        {
            LoginScreen(navController)
        }
        composable(Screen.Register.route)
        {
            Register(navController)
        }
        composable(Screen.Home.route) {
            DashboardScreen(navController)
        }
        composable(Screen.AddExpense.route) { backStackEntry ->

            AddExpenseScreen(navController)
        }
    }
}
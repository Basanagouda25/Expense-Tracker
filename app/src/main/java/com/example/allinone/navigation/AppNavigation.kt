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
import com.example.allinone.dashboard.ExpenseViewModel
import com.example.allinone.dashboard.HomeScreen

@Composable
fun AppNavigation(){
    //navigation controller
    val navController = rememberNavController()

    //nav host
    NavHost(
        navController = navController,
        //app starts from login page
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route)
        {
            LoginScreen(navController)
        }
        composable(Screen.Register.route)
        {
            Register(navController)
        }
        composable(Screen.Home.route) { backStackEntry ->

            val viewModel: ExpenseViewModel = viewModel(backStackEntry)

            HomeScreen(navController, viewModel)
        }
        composable(Screen.AddExpense.route) { backStackEntry ->

            AddExpenseScreen(navController)
        }
    }
}
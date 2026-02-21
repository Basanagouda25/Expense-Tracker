package com.example.allinone.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.allinone.navigation.Screen
import com.example.allinone.auth.Register
import com.example.allinone.auth.login
import com.example.allinone.dashboard.home

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
            login(navController)
        }
        composable(Screen.Register.route)
        {
            Register(navController)
        }
        composable("home"){
            home()
        }
    }
}
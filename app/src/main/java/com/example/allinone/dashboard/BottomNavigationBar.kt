package com.example.allinone.dashboard

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(
    navController: NavController
){
    val items = listOf(
        "dashboard",
        "analytics",
        "add",
        "budget",
        "settings"
    )
    NavigationBar(
        containerColor = Color.Black
    ) {
        items.forEach { route ->
            NavigationBarItem(
                selected = false,
                onClick = {
                    navController.navigate(route)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null
                    )
                }
            )
        }
    }
}
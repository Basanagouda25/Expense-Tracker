package com.example.allinone.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.allinone.data.model.Expense
import com.example.allinone.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = viewModel()
) {

    val expenses by expenseViewModel.expenses.collectAsState()

    LaunchedEffect(Unit) {
        expenseViewModel.loadExpenses()
    }

    val totalAmount = expenses.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Expense Tracker",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total Spent: ₹ $totalAmount",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Recent Transactions",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(expenses) { expense ->
                ExpenseItem(expense = expense)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                navController.navigate(Screen.AddExpense.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Expense")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text(
            text = expense.category,
            fontSize = 16.sp
        )

        Text(
            text = expense.note,
            fontSize = 12.sp
        )

        Text(
            text = "₹ ${expense.amount}",
            fontSize = 16.sp
        )
    }
}
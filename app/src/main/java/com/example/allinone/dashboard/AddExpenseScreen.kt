package com.example.allinone.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AddExpenseScreen(
    navController: NavController
) {

    val viewModel: ExpenseViewModel = viewModel()

    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val message by viewModel.message.collectAsState()

    LaunchedEffect(message) {
        if (message == "SUCCESS") {
            navController.popBackStack()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Add Your Expense", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val amountDouble = amount.toDoubleOrNull()
                if (amountDouble != null) {
                    viewModel.addExpense(amountDouble, category, note)
                }
            }
        ) {
            Text("Save Expense")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (message.isNotEmpty() && message != "SUCCESS") {
            Text(message)
        }
    }
}
package com.example.allinone.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BudgetScreen(
    viewModel: ExpenseViewModel = viewModel()
) {

    val expenses by viewModel.expenses.collectAsState()
    val budget by viewModel.budget.collectAsState()

    var inputBudget by remember { mutableStateOf("") }

    val totalSpent = expenses.sumOf { it.amount }

    val progress =
        if (budget > 0) (totalSpent / budget).toFloat()
        else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Budget", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputBudget,
            onValueChange = { inputBudget = it },
            label = { Text("Set Monthly Budget") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            inputBudget.toDoubleOrNull()?.let {
                viewModel.setBudget(it)
            }
        }) {
            Text("Save Budget")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Spent: ₹ $totalSpent")
        Text("Budget: ₹ $budget")

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
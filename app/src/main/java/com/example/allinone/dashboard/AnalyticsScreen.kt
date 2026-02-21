package com.example.allinone.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AnalyticsScreen() {

    val viewModel: ExpenseViewModel = viewModel()

    val monthlyTotals by viewModel.monthlyTotals.collectAsState()
    val weeklyTotals by viewModel.weeklyTotals.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()

    var selectedType by remember { mutableStateOf(AnalyticsType.MONTH) }

    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Toggle Buttons
        Row {
            Button(
                onClick = { selectedType = AnalyticsType.MONTH }
            ) {
                Text("Month")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = { selectedType = AnalyticsType.WEEK }
            ) {
                Text("Week")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Graph on top
        if (selectedType == AnalyticsType.MONTH) {
            MonthlyLineChart(monthlyTotals)
        } else {
            MonthlyLineChart(weeklyTotals)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Category Breakdown")

        Spacer(modifier = Modifier.height(10.dp))

        categoryTotals.forEach { (category, total) ->
            Text("$category : ₹$total")
        }
    }
}
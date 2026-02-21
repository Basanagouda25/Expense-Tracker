package com.example.allinone.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel = viewModel()
) {

    val categoryData by viewModel.categoryTotals.collectAsState()
    val total = categoryData.values.sum()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Analytics", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Total Spent: ₹ $total", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        categoryData.forEach { (category, amount) ->

            val percentage =
                if (total != 0.0) (amount / total) * 100 else 0.0

            Text("$category : ₹ $amount (${percentage.toInt()}%)")

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
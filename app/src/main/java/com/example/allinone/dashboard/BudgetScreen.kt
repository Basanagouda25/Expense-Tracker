package com.example.allinone.dashboard

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BudgetScreen(
    viewModel: ExpenseViewModel = viewModel(LocalActivity.current as ComponentActivity)
) {
    val expenses by viewModel.expenses.collectAsState()
    val budget by viewModel.budget.collectAsState()

    // FIX 2: Make sure the expenses are actually loaded from the database!
    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
    }

    var inputBudget by remember { mutableStateOf(if (budget > 0) budget.toString() else "") }

    val themePrimary = MaterialTheme.colorScheme.primary
    val themeSurface = MaterialTheme.colorScheme.surface
    val themeBackground = MaterialTheme.colorScheme.background
    val errorColor = Color(0xFFF44336)

    val totalSpent = expenses.filter { it.type == "Expense" }.sumOf { it.amount }

    val remaining = budget - totalSpent
    val isOverBudget = remaining < 0
    val progress = if (budget > 0) (totalSpent / budget).toFloat() else 0f
    val progressColor = if (isOverBudget) errorColor else themePrimary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeBackground)
            .padding(20.dp)
    ) {
        Text(
            text = "Monthly Budget",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text("Total Spent", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "₹ $totalSpent",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = progressColor,
                    trackColor = Color.DarkGray,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Budget Limit", color = Color.Gray, fontSize = 12.sp)
                        Text("₹ $budget", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        if (isOverBudget) {
                            Text("Over Budget By", color = errorColor, fontSize = 12.sp)
                            Text("₹ ${remaining * -1}", color = errorColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Remaining", color = Color.Gray, fontSize = 12.sp)
                            Text("₹ $remaining", color = themePrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Update Budget",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                OutlinedTextField(
                    value = inputBudget,
                    onValueChange = { inputBudget = it },
                    label = { Text("Set Monthly Budget (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themePrimary,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedLabelColor = themePrimary,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        inputBudget.toDoubleOrNull()?.let {
                            viewModel.setBudget(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themePrimary,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Save Budget", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
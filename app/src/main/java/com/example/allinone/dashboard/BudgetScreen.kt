package com.example.allinone.dashboard

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BudgetScreen(
    viewModel: ExpenseViewModel = viewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val budget by viewModel.budget.collectAsState()

    // Pre-fill the input with the current budget if it exists
    var inputBudget by remember { mutableStateOf(if (budget > 0) budget.toString() else "") }

    val themePrimary = MaterialTheme.colorScheme.primary
    val themeSurface = MaterialTheme.colorScheme.surface
    val themeBackground = MaterialTheme.colorScheme.background
    val errorColor = Color(0xFFF44336) // Red for over-budget

    // BUG FIX: Only calculate transactions that are actual "Expenses"
    val totalSpent = expenses.filter { it.type == "Expense" }.sumOf { it.amount }

    // Dynamic calculations
    val remaining = budget - totalSpent
    val isOverBudget = remaining < 0
    val progress = if (budget > 0) (totalSpent / budget).toFloat() else 0f

    // Change progress bar color based on status
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

        // Budget Summary Card
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("Total Spent", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            text = "₹ $totalSpent",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "of ₹ $budget",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Modern, thick progress bar with rounded edges
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f), // Keeps it from breaking UI if > 100%
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = progressColor,
                    trackColor = Color.DarkGray,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dynamic Remaining / Over-budget Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (isOverBudget) {
                        Text("Over Budget By", color = errorColor, fontSize = 14.sp)
                        Text("₹ ${remaining * -1}", color = errorColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Remaining", color = Color.Gray, fontSize = 14.sp)
                        Text("₹ $remaining", color = themePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Set Budget Section
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
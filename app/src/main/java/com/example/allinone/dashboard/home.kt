package com.example.allinone.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.allinone.data.model.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class) // Required for SwipeToDismissBox
@Composable
fun HomeScreen(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = viewModel()
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val themeSurface = MaterialTheme.colorScheme.surface
    val themeBackground = MaterialTheme.colorScheme.background

    LaunchedEffect(Unit) {
        expenseViewModel.loadExpenses()
    }

    // Dynamic Calculations based on the "type" field
    val totalIncome = expenses.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpense = expenses.filter { it.type == "Expense" }.sumOf { it.amount }
    val totalBalance = totalIncome - totalExpense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeBackground)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Total Balance",
            color = Color.Gray,
            fontSize = 16.sp
        )
        Text(
            text = "₹ $totalBalance",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Balance Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceInfoItem("Income", "₹ $totalIncome", Icons.Default.ArrowUpward, Color(0xFF4CAF50))
                BalanceInfoItem("Expenses", "₹ $totalExpense", Icons.Default.ArrowDownward, Color(0xFFF44336))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recent Transactions",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = expenses,
                key = { it.id } // Important for accurate swipe-to-delete animations
            ) { expense ->

                // --- Swipe to Delete Logic ---
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            expenseViewModel.deleteExpense(expense.id)
                            true
                        } else {
                            false
                        }
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                else -> Color.Transparent
                            }, label = "bgColorAnimation"
                        )
                        val scale by animateFloatAsState(
                            if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 0.8f,
                            label = "iconScaleAnimation"
                        )

                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color, RoundedCornerShape(16.dp))
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.scale(scale)
                            )
                        }
                    },
                    content = {
                        // --- The Clickable Expense Item ---
                        ExpenseItem(
                            expense = expense,
                            themeSurface = themeSurface,
                            onClick = {
                                // 1. Tell ViewModel which item we want to edit
                                expenseViewModel.setExpenseToEdit(expense)
                                // 2. Navigate to AddExpenseScreen
                                navController.navigate("add_expense")
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun BalanceInfoItem(label: String, amount: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, color = Color.Gray, fontSize = 12.sp)
            Text(text = amount, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    themeSurface: Color,
    onClick: () -> Unit // <-- Click listener injected
) {
    val isIncome = expense.type == "Income"
    val amountColor = if (isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
    val amountPrefix = if (isIncome) "+" else "-"

    // FIX: Safely format the Date? object. Fallback to current date if it's null.
    val formattedDate = remember(expense.timestamp) {
        val safeDate = expense.timestamp ?: Date()
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(safeDate)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Trigger navigation on click
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = themeSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = expense.category, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                if (expense.note.isNotEmpty()) {
                    Text(text = expense.note, color = Color.Gray, fontSize = 13.sp)
                }
                // Show formatted Date directly under the note!
                Text(text = formattedDate, color = Color.DarkGray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Text(text = "$amountPrefix ₹${expense.amount}", color = amountColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
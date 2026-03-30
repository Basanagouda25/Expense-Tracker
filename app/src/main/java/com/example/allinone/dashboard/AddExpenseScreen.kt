package com.example.allinone.dashboard

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavController) {

    val activity = LocalActivity.current as ComponentActivity
    val viewModel: ExpenseViewModel = viewModel(activity)

    val expenseToEdit by viewModel.expenseToEdit.collectAsState()
    val isEditing = expenseToEdit != null

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("Expense") }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val message by viewModel.message.collectAsState()

    LaunchedEffect(expenseToEdit) {
        expenseToEdit?.let {
            amount = it.amount.toString().removeSuffix(".0")
            note = it.note
            transactionType = it.type
            selectedDateMillis = it.timestamp?.time ?: System.currentTimeMillis()
        }
    }

    LaunchedEffect(message) {
        if (message == "SUCCESS") {
            viewModel.clearMessage()
            viewModel.setExpenseToEdit(null)
            navController.popBackStack()
        }
    }

    val themePrimary = MaterialTheme.colorScheme.primary
    val themeSurface = MaterialTheme.colorScheme.surface

    val dateString = remember(selectedDateMillis) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(Date(selectedDateMillis))
    }

    // ---------------- DATE PICKER ----------------
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDateMillis = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ---------------- UI ----------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                viewModel.setExpenseToEdit(null)
                navController.popBackStack()
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Text(
                text = if (isEditing) "Edit Transaction" else "Add Transaction",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- TYPE SWITCH ----------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(themeSurface, RoundedCornerShape(25.dp))
                .padding(4.dp)
        ) {
            ToggleButton("Expense", transactionType == "Expense", themePrimary, Modifier.weight(1f)) {
                transactionType = "Expense"
            }
            ToggleButton("Income", transactionType == "Income", themePrimary, Modifier.weight(1f)) {
                transactionType = "Income"
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(themeSurface, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Note (AI will use this)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (e.g. Uber ride, Pizza)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date
            OutlinedTextField(
                value = dateString,
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ---------------- SAVE BUTTON ----------------
        Button(
            onClick = {
                val amountDouble = amount.toDoubleOrNull()

                if (amountDouble != null && note.isNotBlank()) {
                    val dateToSave = Date(selectedDateMillis)

                    if (isEditing) {
                        // Editing keeps manual category
                        viewModel.updateExpense(
                            expenseToEdit!!.id,
                            amountDouble,
                            expenseToEdit!!.category,
                            note,
                            transactionType,
                            dateToSave
                        )
                    } else {
                        // 🔥 AI call here
                        viewModel.addExpenseWithAI(
                            amount = amountDouble,
                            note = note,
                            type = transactionType,
                            timestamp = dateToSave
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isEditing) "Update Transaction" else "Save",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ---------------- TOGGLE ----------------
@Composable
fun ToggleButton(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(25.dp))
            .background(if (isSelected) selectedColor else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
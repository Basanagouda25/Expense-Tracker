package com.example.allinone.dashboard

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
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavController) {
    val viewModel: ExpenseViewModel = viewModel()

    // Check if we are editing or adding
    val expenseToEdit by viewModel.expenseToEdit.collectAsState()
    val isEditing = expenseToEdit != null

    // Pre-fill states if editing
    var amount by remember { mutableStateOf(expenseToEdit?.amount?.toString()?.removeSuffix(".0") ?: "") }
    var category by remember { mutableStateOf(expenseToEdit?.category ?: "") }
    var note by remember { mutableStateOf(expenseToEdit?.note ?: "") }
    var transactionType by remember { mutableStateOf(expenseToEdit?.type ?: "Expense") }

    // FIX: Safely extract the time in milliseconds from the Date object, fallback to current time
    var selectedDateMillis by remember {
        mutableStateOf(expenseToEdit?.timestamp?.time ?: System.currentTimeMillis())
    }

    var showDatePicker by remember { mutableStateOf(false) }

    val message by viewModel.message.collectAsState()
    val themePrimary = MaterialTheme.colorScheme.primary
    val themeSurface = MaterialTheme.colorScheme.surface

    LaunchedEffect(message) {
        if (message == "SUCCESS") {
            viewModel.clearMessage()
            viewModel.setExpenseToEdit(null) // Reset on success
            navController.popBackStack()
        }
    }

    // Date Formatter for display
    val dateString = remember(selectedDateMillis) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDateMillis))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDateMillis = it }
                    showDatePicker = false
                }) { Text("OK", color = themePrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = themePrimary) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // Top Bar Area
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                viewModel.setExpenseToEdit(null) // Clear if user backs out
                navController.popBackStack()
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = if (isEditing) "Edit Transaction" else "Add Transaction",
                fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle Switch
        Row(modifier = Modifier.fillMaxWidth().height(50.dp).background(themeSurface, RoundedCornerShape(25.dp)).padding(4.dp)) {
            ToggleButton("Expense", transactionType == "Expense", themePrimary, Modifier.weight(1f)) { transactionType = "Expense" }
            ToggleButton("Income", transactionType == "Income", themePrimary, Modifier.weight(1f)) { transactionType = "Income" }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Form Card
        Column(modifier = Modifier.fillMaxWidth().background(themeSurface, RoundedCornerShape(24.dp)).padding(24.dp)) {

            // Amount
            OutlinedTextField(
                value = amount, onValueChange = { amount = it }, label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themePrimary, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Category
            OutlinedTextField(
                value = category, onValueChange = { category = it }, label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themePrimary, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Note
            OutlinedTextField(
                value = note, onValueChange = { note = it }, label = { Text("Note (Optional)") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themePrimary, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Date Picker Field
            OutlinedTextField(
                value = dateString, onValueChange = { }, readOnly = true, enabled = false,
                label = { Text("Date") },
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Pick Date", tint = themePrimary) },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }, // Click opens picker
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White, disabledBorderColor = Color.Gray.copy(alpha = 0.5f), disabledLabelColor = Color.Gray, disabledTrailingIconColor = themePrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val amountDouble = amount.toDoubleOrNull()
                if (amountDouble != null && category.isNotBlank()) {
                    // FIX: Convert the Long (milliseconds) back into a Date object
                    val dateToSave = Date(selectedDateMillis)

                    if (isEditing) {
                        viewModel.updateExpense(expenseToEdit!!.id, amountDouble, category, note, transactionType, dateToSave)
                    } else {
                        viewModel.addExpense(amountDouble, category, note, transactionType, dateToSave)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themePrimary, contentColor = Color.Black)
        ) {
            Text(if (isEditing) "Update Transaction" else "Save Transaction", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ToggleButton(text: String, isSelected: Boolean, selectedColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.fillMaxHeight().clip(RoundedCornerShape(25.dp))
            .background(if (isSelected) selectedColor else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = if (isSelected) Color.Black else Color.Gray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 16.sp)
    }
}
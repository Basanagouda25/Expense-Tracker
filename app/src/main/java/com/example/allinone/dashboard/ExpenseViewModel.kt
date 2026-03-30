package com.example.allinone.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.allinone.api.RetrofitInstance // 🔥 Use the centralized Retrofit instance
import com.example.allinone.data.model.Expense
import com.example.allinone.data.repository.BudgetDataStore
import com.example.allinone.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository()
    private val budgetStore = BudgetDataStore(application)

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun clearMessage() {
        _message.value = ""
    }

    // ---------------- ADD EXPENSE (Standard) ----------------
    fun addExpense(
        amount: Double,
        category: String,
        note: String,
        type: String,
        timestamp: Date? = null
    ) {
        viewModelScope.launch {
            val expense = Expense(
                amount = amount,
                category = category,
                note = note,
                type = type,
                timestamp = timestamp
            )

            val result = repository.addExpense(expense)

            result.onSuccess {
                Log.d("ExpenseDebug", "Transaction Added Success")
                _message.value = "SUCCESS"
            }

            result.onFailure {
                _message.value = it.message ?: "Error in adding"
            }
        }
    }

    // ---------------- 🔥 AI POWERED ADD EXPENSE ----------------
    fun addExpenseWithAI(
        amount: Double,
        note: String,
        type: String,
        timestamp: Date? = null
    ) {
        viewModelScope.launch {
            try {
                // 1. Call AI API to predict category based on note
                Log.d("API_DEBUG", "Sending note to AI: $note")
                val response = RetrofitInstance.api.predictCategory(note)
                val predictedCategory = response.category
                Log.d("API_DEBUG", "AI Prediction Received: $predictedCategory")

                val expense = Expense(
                    amount = amount,
                    category = predictedCategory, // Automatic AI category!
                    note = note,
                    type = type,
                    timestamp = timestamp
                )

                val result = repository.addExpense(expense)

                result.onSuccess {
                    Log.d("ExpenseDebug", "AI Transaction Added Success")
                    _message.value = "SUCCESS"
                }

                result.onFailure {
                    _message.value = it.message ?: "Error in adding to database"
                }

            } catch (e: Exception) {
                // 2. ERROR HANDLING: If AI fails (URL wrong, server down, etc.)
                Log.e("API_ERROR", "AI Prediction Failed: ${e.message}")
                
                // Show real error to user (optional, can be changed back to offline msg)
                _message.value = "AI Offline: ${e.message ?: "Check URL"}"

                // 3. FALLBACK: Save with "Others" category if AI is unavailable
                val expense = Expense(
                    amount = amount,
                    category = "Others", 
                    note = note,
                    type = type,
                    timestamp = timestamp
                )

                repository.addExpense(expense)
                // Note: We don't set _message to SUCCESS yet, so the screen might show the error message.
            }
        }
    }

    // ---------------- Expense List ----------------
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    fun loadExpenses() {
        viewModelScope.launch {
            repository.getExpenses().collect {
                _expenses.value = it
            }
        }
    }

    // ---------------- Analytics ----------------
    val categoryTotals: StateFlow<Map<String, Double>>
        get() = expenses
            .map { list ->
                list.filter { it.type == "Expense" }
                    .groupBy { it.category }
                    .mapValues { entry ->
                        entry.value.sumOf { it.amount }
                    }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyMap()
            )

    // ---------------- Budget ----------------
    val budget: StateFlow<Double> = budgetStore.budgetFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    fun setBudget(amount: Double) {
        viewModelScope.launch {
            budgetStore.saveBudget(amount)
        }
    }

    // ---------------- Timeline ----------------
    val monthlyTotals: StateFlow<Map<String, Double>>
        get() = expenses
            .map { list ->
                val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                list.filter { it.type == "Expense" }
                    .groupBy { expense ->
                        val date = expense.timestamp ?: Date()
                        sdf.format(date)
                    }.mapValues { entry ->
                        entry.value.sumOf { it.amount }
                    }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyMap()
            )

    val weeklyTotals: StateFlow<Map<String, Double>>
        get() = expenses.map { list ->
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            list.filter { it.type == "Expense" }
                .groupBy { expense ->
                    val date = expense.timestamp ?: Date()
                    sdf.format(date)
                }.mapValues { entry ->
                    entry.value.sumOf { it.amount }
                }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )

    // ---------------- Delete ----------------
    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            repository.deleteExpense(expenseId)
        }
    }

    // ---------------- Edit ----------------
    private val _expenseToEdit = MutableStateFlow<Expense?>(null)
    val expenseToEdit: StateFlow<Expense?> = _expenseToEdit

    fun setExpenseToEdit(expense: Expense?) {
        _expenseToEdit.value = expense
    }

    fun updateExpense(
        id: String,
        amount: Double,
        category: String,
        note: String,
        type: String,
        timestamp: Date?
    ) {
        viewModelScope.launch {
            val expense = Expense(id, amount, category, note, type, timestamp)
            val result = repository.updateExpense(expense)
            result.onSuccess {
                _message.value = "SUCCESS"
                _expenseToEdit.value = null
            }
            result.onFailure {
                _message.value = it.message ?: "Error updating"
            }
        }
    }
}
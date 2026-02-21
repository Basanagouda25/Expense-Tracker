package com.example.allinone.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allinone.data.model.Expense
import com.example.allinone.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExpenseViewModel : ViewModel() {

    private val repository = ExpenseRepository()

    // ---------------- Message State ----------------
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    // ---------------- Add Expense ----------------
    fun addExpense(
        amount: Double,
        category: String,
        note: String,
    ) {
        viewModelScope.launch {

            val expense = Expense(
                amount = amount,
                category = category,
                note = note
            )

            val result = repository.addExpense(expense)

            result.onSuccess {
                Log.d("ExpenseDebug", "Expense Added Success")
                _message.value = "SUCCESS"
            }

            result.onFailure {
                _message.value = it.message ?: "Error in adding"
            }
        }
    }

    fun clearMessage() {
        _message.value = ""
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
                list.groupBy { it.category }
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
    private val _budget = MutableStateFlow(0.0)
    val budget: StateFlow<Double> = _budget

    fun setBudget(amount: Double) {
        _budget.value = amount
    }

    val monthlyTotals: StateFlow<Map<String, Double>>
        get() = expenses
            .map { list ->
                list.groupBy { expense ->
                    val month = java.text.SimpleDateFormat(
                        "MMM yyyy",
                        java.util.Locale.getDefault()
                    ).format(java.util.Date(expense.timestamp))
                    month
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

            val sdf = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())

            list.groupBy { expense ->
                sdf.format(java.util.Date(expense.timestamp))
            }.mapValues { entry ->
                entry.value.sumOf { it.amount }
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )
}
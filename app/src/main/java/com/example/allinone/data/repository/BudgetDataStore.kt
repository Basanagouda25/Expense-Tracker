package com.example.allinone.data.repository

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// This creates the actual file on the device named "settings.preferences_pb"
val Context.dataStore by preferencesDataStore(name = "settings")

class BudgetDataStore(private val context: Context) {

    companion object {
        // The "Key" we use to find the budget inside the DataStore
        val BUDGET_KEY = doublePreferencesKey("monthly_budget")
    }

    // A flow that continuously reads the saved budget (defaults to 0.0 if empty)
    val budgetFlow: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[BUDGET_KEY] ?: 0.0
        }

    // A function to save the new budget to the device
    suspend fun saveBudget(amount: Double) {
        context.dataStore.edit { preferences ->
            preferences[BUDGET_KEY] = amount
        }
    }
}
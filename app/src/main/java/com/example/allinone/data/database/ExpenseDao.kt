package com.example.allinone.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.allinone.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("Select * from expenses ORDER BY timestamp DESC")
    fun getAllExpenses() : Flow<List<Expense>>
}
package com.example.allinone.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.allinone.data.model.Expense

@Database(
    entities = [
        Expense::class
        ],
    version = 1)

abstract class AppDatabase : RoomDatabase(){
    abstract fun expenseDao() : ExpenseDao
}
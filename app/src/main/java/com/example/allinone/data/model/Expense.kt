package com.example.allinone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey
    val id : String = "",
    val amount: Double = 0.0,
    val category : String = "",
    val note : String = "",
    val timestamp: Long = System.currentTimeMillis()
)
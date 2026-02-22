package com.example.allinone.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.allinone.data.model.Expense
import java.util.Date


@Suppress("unused")
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
@Database(
    entities = [
        Expense::class
        ],
    version = 1)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase(){
    abstract fun expenseDao() : ExpenseDao
}
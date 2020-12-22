package com.michaeldang.countingcalories.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.michaeldang.countingcalories.database.typeconverters.DateTypeConverter
import com.michaeldang.countingcalories.database.typeconverters.FoodPeriodTypeConverter

@Database(entities = [CaloriesEntriesEntity::class], version = 2, exportSchema = false)
@TypeConverters(DateTypeConverter::class, FoodPeriodTypeConverter::class)
abstract class CaloriesDatabase : RoomDatabase() {
    abstract fun caloriesDao() : CaloriesDao
}
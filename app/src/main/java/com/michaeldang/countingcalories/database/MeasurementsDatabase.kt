package com.michaeldang.countingcalories.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.michaeldang.countingcalories.database.typeconverters.DateTypeConverter

@Database(entities = [MeasurementsEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class MeasurementsDatabase : RoomDatabase() {
    abstract fun caloriesDao() : CaloriesDao
}
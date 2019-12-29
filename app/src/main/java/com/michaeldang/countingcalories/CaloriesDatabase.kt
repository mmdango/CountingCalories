package com.michaeldang.countingcalories

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CaloriesEntriesEntity::class], version = 1, exportSchema = false)
abstract class CaloriesDatabase : RoomDatabase() {


    abstract fun caloriesDao() : CaloriesDao
}
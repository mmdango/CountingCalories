package com.michaeldang.countingcalories.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.michaeldang.countingcalories.roundNearestDay
import com.michaeldang.countingcalories.roundNextDay
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
abstract class CaloriesDao {
    @Query("SELECT * FROM CaloriesEntriesEntity")
    abstract fun getAllEntries() : List<CaloriesEntriesEntity>

    @Query("SELECT * FROM CaloriesEntriesEntity WHERE date = :date AND food_period = 'Breakfast'")
    abstract fun getBreakfastEntriesForDate(date: LocalDate): List<CaloriesEntriesEntity>

    @Query("SELECT * FROM CaloriesEntriesEntity WHERE date = :date AND food_period = 'Lunch'")
    abstract fun getLunchEntriesForDate(date: LocalDate): List<CaloriesEntriesEntity>

    @Query("SELECT * FROM CaloriesEntriesEntity WHERE date = :date AND food_period = 'Dinner'")
    abstract fun getDinnerEntriesForDate(date: LocalDate): List<CaloriesEntriesEntity>

    @Insert
    abstract fun insertEntry(entry: CaloriesEntriesEntity)
}
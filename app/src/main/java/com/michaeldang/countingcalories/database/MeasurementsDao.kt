package com.michaeldang.countingcalories.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate

@Dao
abstract class MeasurementsDao {
    @Query("SELECT * FROM MeasurementsEntity")
    abstract fun getAllEntries() : List<MeasurementsEntity>

    @Query("SELECT * FROM MeasurementsEntity WHERE date = :date AND body_part = 'Thigh'")
    abstract fun getThighMeasurementsForDate(date: LocalDate): List<MeasurementsEntity>

    @Query("SELECT * FROM MeasurementsEntity WHERE date = :date AND body_part = 'Waist'")
    abstract fun getWaistMeasurementsForDate(date: LocalDate): List<MeasurementsEntity>

    @Query("SELECT * FROM MeasurementsEntity WHERE date = :date AND body_part = 'Bicep'")
    abstract fun getBicepMeasurementsForDate(date: LocalDate): List<MeasurementsEntity>

    @Insert
    abstract fun insertEntry(entry: MeasurementsEntity)
}
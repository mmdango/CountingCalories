package com.michaeldang.countingcalories.database

import android.text.Html
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesViewModel.FoodPeriod
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
open class CaloriesEntriesEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "label") val label: String?,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "food_period") val foodPeriod: FoodPeriod
)

//@Entity
//open class CaloriesEntriesEntity(
//    @PrimaryKey(autoGenerate = true) val uid: Int,
//    @ColumnInfo(name = "label") val label: String?,
//    @ColumnInfo(name = "calories") val calories: Int,
//    @ColumnInfo(name = "timestamp") val timestamp: LocalDateTime
//)


class CaloriesEntriesEntityDummy(date: LocalDate, foodPeriod: FoodPeriod) : CaloriesEntriesEntity(
    -1,
    date,
    "",
    0,
    foodPeriod
)
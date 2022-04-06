package com.michaeldang.countingcalories.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesViewModel
import java.time.LocalDate

@Entity
open class MeasurementsEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "body_part") val bodyPart: String?,
    @ColumnInfo(name = "size") val size: Int,
)

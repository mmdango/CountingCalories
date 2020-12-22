package com.michaeldang.countingcalories.database.typeconverters

import androidx.room.TypeConverter
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateTypeConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): LocalDate? {
        return value?.let {
            return formatter.parse(value, LocalDate::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: LocalDate?): String? {
        return date?.format(formatter)
    }
}

object FoodPeriodTypeConverter {
    @TypeConverter
    @JvmStatic
    fun toFoodPeriod(value: String?): CaloriesEntriesViewModel.FoodPeriod? {
        return CaloriesEntriesViewModel.FoodPeriod.values().firstOrNull { it.name == value }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(foodPeriod: CaloriesEntriesViewModel.FoodPeriod?): String? {
        return foodPeriod?.name
    }
}
package com.michaeldang.countingcalories.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesViewModel.FoodPeriod
import java.time.LocalDate

@Entity
data class CaloriesEntriesEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "label") val label: String?,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "food_period") val foodPeriod: FoodPeriod
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        LocalDate.parse(parcel.readString()),
        parcel.readString(),
        parcel.readInt(),
        FoodPeriod.valueOf(parcel.readString()!!)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(uid)
        parcel.writeString(date.toString())
        parcel.writeString(label)
        parcel.writeInt(calories)
        parcel.writeString(foodPeriod.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CaloriesEntriesEntity> {
        override fun createFromParcel(parcel: Parcel): CaloriesEntriesEntity {
            return CaloriesEntriesEntity(parcel)
        }

        override fun newArray(size: Int): Array<CaloriesEntriesEntity?> {
            return arrayOfNulls(size)
        }
    }
}
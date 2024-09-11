package com.michaeldang.countingcalories.feat.entries

import android.app.Application
import android.text.Html
import android.text.Spanned
import androidx.lifecycle.*
import androidx.room.Room
import com.michaeldang.countingcalories.database.CaloriesDao
import com.michaeldang.countingcalories.database.CaloriesDatabase
import com.michaeldang.countingcalories.database.CaloriesEntriesEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime

class CaloriesEntriesViewModel(application: Application) : AndroidViewModel(application) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val mutableTotalCalories: MutableLiveData<Int> = MutableLiveData(0)
    private val breakfastCaloriesMutableLiveData: MutableLiveData<List<CaloriesEntriesEntity>> = MutableLiveData()
    private val lunchCaloriesMutableLiveData: MutableLiveData<List<CaloriesEntriesEntity>> = MutableLiveData()
    private val dinnerCaloriesMutableLiveData: MutableLiveData<List<CaloriesEntriesEntity>> = MutableLiveData()
    private val mutableDate: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())

    private val totalCaloriesKey = "total_calories"

    private val caloriesDao: CaloriesDao
    private val sharedPrefs = application.getSharedPreferences("entries_preferences", 0)


    init {
        mutableDate.value = LocalDate.now()
        mutableTotalCalories.value = sharedPrefs.getInt(totalCaloriesKey, 2000)

        caloriesDao = Room.databaseBuilder(
            application.applicationContext,
            CaloriesDatabase::class.java, "database-1"
        )
            .build()
            .caloriesDao()
    }

    fun dateLiveData(): LiveData<LocalDate> = mutableDate

    fun totalCaloriesLiveData(): LiveData<Int> = mutableTotalCalories

    fun getTotalCaloriesFractionText(): Spanned {
        val breakfastEntries = breakfastCaloriesMutableLiveData.value ?: emptyList()
        val lunchEntries = lunchCaloriesMutableLiveData.value ?: emptyList()
        val dinnerEntries = dinnerCaloriesMutableLiveData.value ?: emptyList()
        val entries = breakfastEntries + lunchEntries + dinnerEntries
        val totalCalories = mutableTotalCalories.value ?: 0
        val totalConsumedCalories = entries.foldRight(0, {entity, acc -> entity.calories + acc})
        val percentConsumed = totalConsumedCalories / totalCalories.toDouble()
        val consumedCaloriesTextColor = if (percentConsumed < 0.5) {
            "<font color='green'>"
        } else if (percentConsumed >= 0.5 && percentConsumed < 1.0) {
            "<font color='orange'>"
        } else if (percentConsumed == 1.0) {
            "<font color='blue'>"
        } else {
            "<font color='red'>"
        }

        val remainingCalories = totalCalories - totalConsumedCalories
        return Html.fromHtml(
            "$consumedCaloriesTextColor$totalConsumedCalories</font> / <font color='black'>$totalCalories calories<br>$remainingCalories remaining</font>",
            Html.FROM_HTML_MODE_COMPACT
        )
    }

    fun storeEntry(label: String?, value: Int, foodPeriod: FoodPeriod) = coroutineScope.launch {
        val date = dateLiveData().value ?: LocalDate.now()
        caloriesDao.insertEntry(
            CaloriesEntriesEntity(
                0,
                date,
                label,
                value,
                foodPeriod
            )
        )
        fetchEntries(date)
    }

    fun removeEntry(entry: CaloriesEntriesEntity) = coroutineScope.launch{
        caloriesDao.removeEntry(entry)
        val date = dateLiveData().value ?: LocalDate.now()
        fetchEntries(date)
    }

    fun fetchEntries(date: LocalDate) = coroutineScope.launch {
        val breakfastEntries = caloriesDao.getBreakfastEntriesForDate(date)
        val lunchEntries = caloriesDao.getLunchEntriesForDate(date)
        val dinnerEntries = caloriesDao.getDinnerEntriesForDate(date)
        withContext(Dispatchers.Main) {
            breakfastCaloriesMutableLiveData.value = breakfastEntries
            lunchCaloriesMutableLiveData.value = lunchEntries
            dinnerCaloriesMutableLiveData.value = dinnerEntries
        }
    }

    fun breakfastCaloriesLiveData(): LiveData<List<CaloriesEntriesEntity>> {
        return breakfastCaloriesMutableLiveData
    }

    fun lunchCaloriesLiveData(): LiveData<List<CaloriesEntriesEntity>> {
        return lunchCaloriesMutableLiveData
    }

    fun dinnerCaloriesLiveData(): LiveData<List<CaloriesEntriesEntity>> {
        return dinnerCaloriesMutableLiveData
    }

    fun setTotalCalories(totalCalories: Int) {
        mutableTotalCalories.value = totalCalories
        sharedPrefs.edit().putInt(totalCaloriesKey, totalCalories).apply()
    }

    fun prevDate() {
        mutableDate.value = mutableDate.value?.minusDays(1)
    }

    fun nextDate() {
        mutableDate.value = mutableDate.value?.plusDays(1)
    }

    enum class FoodPeriod(val timeStart: LocalTime, val timeEnd: LocalTime) {
        Breakfast(LocalTime.MIDNIGHT, LocalTime.of(10,30)),
        Lunch(LocalTime.of(10,31), LocalTime.of(15,0)),
        Dinner(LocalTime.of(15, 1), LocalTime.MAX);
    }
}
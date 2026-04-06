package com.michaeldang.countingcalories.feat.entries

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.michaeldang.countingcalories.database.CaloriesDao
import com.michaeldang.countingcalories.database.CaloriesEntriesEntity
import com.michaeldang.countingcalories.feat.entries.AllMealEntries.Companion.NO_ENTRIES
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesViewModel.FoodPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CaloriesEntriesComposeViewModel @Inject constructor(
    val caloriesDao: CaloriesDao,
    val sharedPrefs: SharedPreferences
) : ViewModel() {

    val TOTAL_CALORIES_SHARED_PREFS_KEY = "TOTAL_CALORIES"
    private val _allMealEntries = MutableStateFlow(NO_ENTRIES)
    val allMealEntries = _allMealEntries.asStateFlow()

    private val _totalCalories = MutableStateFlow(2000)
    val totalCalories = _totalCalories.asStateFlow()

    private val _currentCalories = MutableStateFlow(0)
    val currentCalories = _currentCalories.asStateFlow()

    val selectedDate = MutableStateFlow(LocalDate.now())


    init {
        viewModelScope.launch {
            selectedDate.collect {
                fetchEntries(it)
            }
        }
        _totalCalories.value = sharedPrefs.getInt(TOTAL_CALORIES_SHARED_PREFS_KEY, 2000)
    }

    private fun fetchEntries(date: LocalDate) = viewModelScope.launch {
        val breakfastEntries = caloriesDao.getBreakfastEntriesForDate(date)
        val lunchEntries = caloriesDao.getLunchEntriesForDate(date)
        val dinnerEntries = caloriesDao.getDinnerEntriesForDate(date)
        withContext(Dispatchers.Main) {
            _allMealEntries.value = AllMealEntries(
                breakfastEntries.toMealEntries(FoodPeriod.Breakfast),
                lunchEntries.toMealEntries(FoodPeriod.Lunch),
                dinnerEntries.toMealEntries(FoodPeriod.Dinner),
            )
            _currentCalories.value = (breakfastEntries + lunchEntries + dinnerEntries).sumOf { it.calories }
        }
    }

    fun onPrevDateClicked() {
        selectedDate.value = selectedDate.value.minusDays(1)
    }

    fun onNextDateClicked() {
        selectedDate.value = selectedDate.value.plusDays(1)
    }

    fun updateTotalCalories(newTotal: Int) {
        sharedPrefs.edit(commit = true) {
            putInt(TOTAL_CALORIES_SHARED_PREFS_KEY, newTotal)
        }
        _totalCalories.value = newTotal
    }

    suspend fun addEntry(
        calories: Int,
        label: String?,
        foodPeriod: FoodPeriod
    ): Boolean {
        if (calories <= 0 || foodPeriod == FoodPeriod.None) {
            return false
        }
        val entry = CaloriesEntriesEntity(
            uid = 0, // 0 will cause Room to autogenerate UUID
            selectedDate.value,
            label,
            calories,
            foodPeriod
        )
        try {
            caloriesDao.insertEntry(entry)
        } catch (e : Exception) {
            println("Failed to save entry: $e")
            return false
        }
        fetchEntries(selectedDate.value)
        return true
    }

}
fun List<CaloriesEntriesEntity>.toMealEntries(foodPeriod: FoodPeriod): MealEntries {
    return MealEntries(foodPeriod, this.map { it.calories })
}
data class AllMealEntries(
    val breakfastEntries: MealEntries,
    val lunchEntries: MealEntries,
    val dinnerEntries: MealEntries
) {
    companion object {
        val NO_ENTRIES = AllMealEntries(
            breakfastEntries = MealEntries(FoodPeriod.Breakfast, emptyList()),
            lunchEntries = MealEntries(FoodPeriod.Lunch, emptyList()),
            dinnerEntries = MealEntries(FoodPeriod.Dinner, emptyList())
        )
    }
}

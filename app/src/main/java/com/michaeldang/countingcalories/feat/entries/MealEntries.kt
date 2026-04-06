package com.michaeldang.countingcalories.feat.entries

data class MealEntries(
    val foodPeriod: CaloriesEntriesViewModel.FoodPeriod,
    val calorieEntries: List<Int>
)

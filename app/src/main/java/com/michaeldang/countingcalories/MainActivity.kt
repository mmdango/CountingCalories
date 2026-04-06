package com.michaeldang.countingcalories

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesComposeViewModel
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Serializable
    object Entries: NavKey

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val backStack = rememberNavBackStack(Entries)
            Scaffold { innerPadding ->
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.remove(backStack.last()) },
                    entryProvider = { key ->
                        when(key) {
                            is Entries -> {
                                NavEntry(key) {
                                    val composeViewModel: CaloriesEntriesComposeViewModel = hiltViewModel()
                                    val totalCalories = composeViewModel.totalCalories.collectAsStateWithLifecycle()
                                    val currentCalories = composeViewModel.currentCalories.collectAsStateWithLifecycle()
                                    val allMealEntries = composeViewModel.allMealEntries.collectAsStateWithLifecycle()
                                    val date = composeViewModel.selectedDate.collectAsStateWithLifecycle()

                                    CaloriesEntriesScreen(
                                        modifier = Modifier.padding(innerPadding),
                                        onEntryAdded = { amount, label, foodPeriod -> composeViewModel.addEntry(amount, label, foodPeriod) },
                                        currentCalories = currentCalories.value,
                                        totalCalories = totalCalories.value,
                                        breakfastEntries = allMealEntries.value.breakfastEntries,
                                        lunchEntries = allMealEntries.value.lunchEntries,
                                        dinnerEntries = allMealEntries.value.dinnerEntries,
                                        date = date.value,
                                        onNextDateClicked = { composeViewModel.onNextDateClicked() },
                                        onPrevDateClicked = { composeViewModel.onPrevDateClicked() },
                                        updateTotalCalories = { composeViewModel.updateTotalCalories(it) }
                                    )
                                }
                            }
                            else -> {
                                NavEntry(key) {
                                    Text("idk")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

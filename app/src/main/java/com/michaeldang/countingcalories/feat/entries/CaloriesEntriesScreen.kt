@file:OptIn(ExperimentalMaterial3Api::class)

package com.michaeldang.countingcalories.feat.entries

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CaloriesEntriesScreen(
    modifier: Modifier = Modifier,
    onEntryAdded: suspend (Int, String?, CaloriesEntriesViewModel.FoodPeriod) -> Boolean,
    date: LocalDate,
    onPrevDateClicked: () -> Unit,
    onNextDateClicked: () -> Unit,
    currentTotal: Int,
    maxTotal: Int,
    updateMaxCalories: (Int) -> Unit,
    breakfastEntries: MealEntries,
    lunchEntries: MealEntries,
    dinnerEntries: MealEntries
) {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(CaloriesEntriesViewModel.FoodPeriod.None) }

    val onAddClicked: suspend (Int, String?) -> Boolean = { amount, label ->
        onEntryAdded(amount, label, selectedOption)
    }

    var showEditMaxCaloriesSheet by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        // Note: date and callbacks need to be provided here to compile correctly
        DateNavigationRow(date = date, onPrevClick = onPrevDateClicked, onNextClick = onNextDateClicked)
        CaloriesTotal(Modifier.defaultMinSize(minHeight = 150.dp).fillMaxWidth(), currentTotal, maxTotal, { showEditMaxCaloriesSheet = true })
        EntryInputRow(onEntryAdded = onAddClicked)

        PeriodSelectorRowAndEntries(
            selectedOption = selectedOption,
            onOptionSelected = onOptionSelected,
            breakfastEntries = breakfastEntries,
            lunchEntries = lunchEntries,
            dinnerEntries = dinnerEntries
        )
    }
    if (showEditMaxCaloriesSheet) {
        EditMaxCalories(
            modifier,
            onDismissed = { showEditMaxCaloriesSheet = false },
            onSavedClick = { updateMaxCalories(it) }
        )
    }
}

@Composable
fun DateNavigationRow(modifier: Modifier = Modifier, date: LocalDate, onPrevClick: () -> Unit, onNextClick: () -> Unit) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EE, MMM d") }
    val formattedDate = remember(date) {
        dateFormatter.format(date)
    }
    Row(modifier = modifier) {
        Button(
            onClick = onPrevClick
        ) {
            Text("Prev")
        }
        Text(
            text = formattedDate,
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = onNextClick
        ) {
            Text("Next")
        }
    }
}

@Composable
fun CaloriesTotal(modifier: Modifier = Modifier, currentTotal: Int, maxTotal: Int, onLongClick: () -> Unit) {
    val percentConsumed = currentTotal.toDouble() / maxTotal
    val remaining = maxTotal - currentTotal
    Box(
        modifier = modifier.combinedClickable(
            interactionSource = null,
            indication = ripple(),
            onClick = { },
            onLongClick = onLongClick
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$currentTotal / $maxTotal",
                color = when (percentConsumed) {
                    in 0.0..0.5 -> Color.Green
                    in 0.5..1.0 -> Color.Yellow
                    else -> Color.Red
                }
            )
            Text(
                "$remaining remaining"
            )
        }
    }
}

@Composable
fun EntryInputRow(modifier: Modifier = Modifier, onEntryAdded: suspend (Int, String?) -> Boolean) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val amountTextState = rememberTextFieldState()
        val labelTextState = rememberTextFieldState()
        val scope = rememberCoroutineScope()
        var isAdding by remember { mutableStateOf(false) }
        TextField(state = amountTextState, label = { Text("Amount") }, modifier = Modifier.weight(1f))
        TextField(state = labelTextState, label = { Text("Label") }, modifier = Modifier.weight(1f))
        Button(
            enabled = !isAdding,
            onClick = {
                scope.launch {
                    isAdding = true
                    try {
                        val succeeded = onEntryAdded(
                            amountTextState.text.toString().toIntOrNull() ?: 0,
                            labelTextState.text.toString()
                        )
                        if (succeeded) {
                            amountTextState.clearText()
                            labelTextState.clearText()
                        }
                    } finally {
                        isAdding = false
                    }
                }
            }
        ) {
            Text("Add")
        }
    }
}

@Composable
fun PeriodSelectorRowAndEntries(
    modifier: Modifier = Modifier,
    selectedOption: CaloriesEntriesViewModel.FoodPeriod,
    onOptionSelected: (CaloriesEntriesViewModel.FoodPeriod) -> Unit,
    breakfastEntries: MealEntries,
    lunchEntries: MealEntries,
    dinnerEntries: MealEntries
) {
    val radioOptions = listOf(breakfastEntries, lunchEntries, dinnerEntries)
    Row(modifier = modifier) {
        radioOptions.forEach { mealEntries ->
            Column(modifier = Modifier.weight(1f)) {
                PeriodButton(selected = mealEntries.foodPeriod == selectedOption, foodPeriod = mealEntries.foodPeriod, onSelected = onOptionSelected)
                EntriesColumn(mealEntries = mealEntries)
            }
        }
    }
}

@Composable
fun PeriodButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    foodPeriod: CaloriesEntriesViewModel.FoodPeriod,
    onSelected: (CaloriesEntriesViewModel.FoodPeriod) -> Unit
) {
    val buttonColor = if (selected) 0xFFAAAAAA else 0xFF444444
    Button(
        modifier = modifier.padding(16.dp).background(color = Color(buttonColor), shape = RoundedCornerShape(16.dp)),
        onClick = { onSelected(foodPeriod) },
    ) {
        Text(
            text = foodPeriod.name,
        )
    }
}

@Composable
fun EntriesColumn(modifier: Modifier = Modifier, mealEntries: MealEntries) {
    LazyColumn(modifier = modifier) {
        items(mealEntries.calorieEntries.size) { idx ->
            Text(mealEntries.calorieEntries[idx].toString())
        }
    }
}

@Composable
fun EditMaxCalories(modifier: Modifier = Modifier, onDismissed: () -> Unit, onSavedClick: (Int) -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismissed,
    ) {
        val (newMaxCalories, onTextUpdated) = remember { mutableStateOf("") }
        Column(
            modifier = Modifier.defaultMinSize(64.dp)
        ) {
            TextField(newMaxCalories, onTextUpdated)
            Row {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onSavedClick(newMaxCalories.toInt()) }
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onDismissed
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}

data class CaloriesEntriesUIState(
    val selectedFoodPeriod: CaloriesEntriesViewModel.FoodPeriod = CaloriesEntriesViewModel.FoodPeriod.None,
    val caloriesAmount: Int? = null,
    val label: String? = null
)
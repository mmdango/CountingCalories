@file:OptIn(ExperimentalMaterial3Api::class)

package com.michaeldang.countingcalories.feat.entries

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    currentCalories: Int,
    totalCalories: Int,
    updateTotalCalories: (Int) -> Unit,
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
        DateNavigationRow(
            modifier = Modifier.padding(8.dp),
            date = date,
            onPrevClick = onPrevDateClicked,
            onNextClick = onNextDateClicked
        )
        CaloriesTotal(
            Modifier
                .defaultMinSize(minHeight = 150.dp)
                .fillMaxWidth(),
            currentCalories,
            totalCalories,
            { showEditMaxCaloriesSheet = true }
        )
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
            onSavedClick = {
                updateTotalCalories(it)
                showEditMaxCaloriesSheet = false
            }
        )
    }
}

@Composable
fun DateNavigationRow(modifier: Modifier = Modifier, date: LocalDate, onPrevClick: () -> Unit, onNextClick: () -> Unit) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EE, MMM d") }
    val formattedDate = remember(date) {
        dateFormatter.format(date)
    }
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPrevClick
        ) {
            Text("Prev")
        }
        Text(
            text = formattedDate,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
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
fun CaloriesTotal(modifier: Modifier = Modifier, currentCalories: Int, maxCalories: Int, onLongClick: () -> Unit) {
    val percentConsumed = currentCalories.toDouble() / maxCalories
    val remaining = maxCalories - currentCalories
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
                text = "$currentCalories / $maxCalories",
                fontSize = 24.sp,
            )
            Text(
                text = "$remaining remaining",
                fontSize = 24.sp,
                color = when (percentConsumed) {
                    in 0.0..0.5 -> Color.Green
                    in 0.5..1.0 -> Color.Yellow
                    else -> Color.Red
                }
            )
        }
    }
}

@Composable
fun EntryInputRow(modifier: Modifier = Modifier, onEntryAdded: suspend (Int, String?) -> Boolean) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .border(
                        BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
    val buttonColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        onClick = { onSelected(foodPeriod) },
    ) {
        Text(
            text = foodPeriod.name,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun EntriesColumn(modifier: Modifier = Modifier, mealEntries: MealEntries) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(mealEntries.calorieEntries.size) { idx ->
            val isEven = idx % 2 == 0
            val backgroundColor = if (isEven) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.background
            Text(
                text = mealEntries.calorieEntries[idx].toString(),
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth().background(color = backgroundColor)
            )
            // TODO How to add a label nicely? Columns are a bit too narrow.
        }
    }
}

@Composable
fun EditMaxCalories(modifier: Modifier = Modifier, onDismissed: () -> Unit, onSavedClick: (Int) -> Unit) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissed,
    ) {
        val (newMaxCalories, onTextUpdated) = remember { mutableStateOf("") }
        Column(
            modifier = Modifier.defaultMinSize(64.dp)
        ) {
            TextField(
                newMaxCalories,
                onTextUpdated,
                label = { Text("New max calories...") }
            )
            Row {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onDismissed
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onSavedClick(newMaxCalories.toInt()) }
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
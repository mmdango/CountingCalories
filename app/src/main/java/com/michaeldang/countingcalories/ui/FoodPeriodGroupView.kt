package com.michaeldang.countingcalories.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CheckedTextView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.michaeldang.countingcalories.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@ExperimentalCoroutinesApi
class FoodPeriodGroupView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val selectedView = MutableStateFlow<CheckedTextView?>(null)
    val selectedViewFlow: StateFlow<CheckedTextView?> = selectedView
    init {
        val view = View.inflate(context, R.layout.food_period_group_layout, this)
        val breakfastTextView = view.findViewById<CheckedTextView>(R.id.breakfast_selector)
        setOnClickListener {

        }
    }
}
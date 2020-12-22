package com.michaeldang.countingcalories.feat.entries

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.michaeldang.countingcalories.R
import kotlinx.android.synthetic.main.entries_fragment.*
import java.time.format.DateTimeFormatter

class CaloriesEntriesFragment : Fragment() {

    lateinit var breakfastAdapter: CaloriesEntriesAdapter
    lateinit var lunchAdapter: CaloriesEntriesAdapter
    lateinit var dinnerAdapter: CaloriesEntriesAdapter

    private val viewModel: CaloriesEntriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.entries_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        caloriesTotalView.isClickable = true
        caloriesTotalView.setOnLongClickListener {
            childFragmentManager.beginTransaction().add(TotalCaloriesDialogFragment(), "total_calories").commit()
            true
        }

        viewModel.dateLiveData().observe(viewLifecycleOwner, Observer { date ->
            currentDateView.text = date.format(DateTimeFormatter.ofPattern("EE, MMM d"))
            prevDateButton.text = date.minusDays(1).format(DateTimeFormatter.ofPattern("EE"))
            nextDateButton.text = date.plusDays(1).format(DateTimeFormatter.ofPattern("EE"))
            viewModel.fetchEntries(date)
        })

        viewModel.breakfastCaloriesLiveData().observe(viewLifecycleOwner, Observer { entries ->
            breakfastAdapter.setEntries(entries)
            caloriesTotalView.text = viewModel.getTotalCaloriesFractionText()
        })

        viewModel.lunchCaloriesLiveData().observe(viewLifecycleOwner, Observer { entries ->
            lunchAdapter.setEntries(entries)
            caloriesTotalView.text = viewModel.getTotalCaloriesFractionText()
        })

        viewModel.dinnerCaloriesLiveData().observe(viewLifecycleOwner, Observer { entries ->
            dinnerAdapter.setEntries(entries)
            caloriesTotalView.text = viewModel.getTotalCaloriesFractionText()
        })

        viewModel.totalCaloriesLiveData().observe(viewLifecycleOwner, Observer { _ ->
            caloriesTotalView.text = viewModel.getTotalCaloriesFractionText()
        })

        prevDateButton.setOnClickListener { loadPrevDate() }
        nextDateButton.setOnClickListener { loadNextDate() }

        confirmButton.setOnClickListener {
            storeEntry()
        }
        calorieLabelView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                storeEntry()
                true
            } else {
                false
            }
        }

        breakfastAdapter = CaloriesEntriesAdapter()
        breakfastCaloriesView.adapter = breakfastAdapter
        breakfastCaloriesView.layoutManager = LinearLayoutManager(context)

        lunchAdapter = CaloriesEntriesAdapter()
        lunchCaloriesView.adapter = lunchAdapter
        lunchCaloriesView.layoutManager = LinearLayoutManager(context)
        dinnerAdapter = CaloriesEntriesAdapter()
        dinnerCaloriesView.adapter = dinnerAdapter
        dinnerCaloriesView.layoutManager = LinearLayoutManager(context)
    }

    private fun storeEntry() {
        if (!canAddEntry()) {
            Snackbar.make(view ?: return, "Must select a meal period and enter calorie amount.", Snackbar.LENGTH_SHORT).show()
            return
        }
        val label = calorieLabelView.text.toString()
        val calories = Integer.valueOf(calorieEntryView.text.toString())
        val foodPeriod = CaloriesEntriesViewModel.FoodPeriod.valueOf(checkedFoodPeriod())
        viewModel.storeEntry(label, calories, foodPeriod)
        calorieEntryView.text = null
        calorieLabelView.text = null
        val imm: InputMethodManager = context
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

    }

    private fun canAddEntry(): Boolean {
        return foodPeriodRadioGroup.checkedRadioButtonId != -1 && calorieEntryView.text.isNotBlank()
    }

    private fun checkedFoodPeriod(): String {
        return foodPeriodRadioGroup
            .findViewById<RadioButton>(foodPeriodRadioGroup.checkedRadioButtonId)
            .text
            .toString()
    }

    private fun loadPrevDate() {
        viewModel.prevDate()
    }

    private fun loadNextDate() {
        viewModel.nextDate()

    }
}
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
import com.michaeldang.countingcalories.database.CaloriesEntriesEntity
import com.michaeldang.countingcalories.databinding.EntriesFragmentBinding
import java.time.format.DateTimeFormatter

class CaloriesEntriesFragment : Fragment(), ConfirmDeleteEntry {

    lateinit var breakfastAdapter: CaloriesEntriesAdapter
    lateinit var lunchAdapter: CaloriesEntriesAdapter
    lateinit var dinnerAdapter: CaloriesEntriesAdapter

    private val viewModel: CaloriesEntriesViewModel by activityViewModels()
    private var _binding: EntriesFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EntriesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.caloriesTotalView.isClickable = true
        binding.caloriesTotalView.setOnLongClickListener {
            childFragmentManager.beginTransaction().add(TotalCaloriesDialogFragment(), "total_calories").commit()
            true
        }

        viewModel.dateLiveData().observe(viewLifecycleOwner, Observer { date ->
            binding.currentDateView.text = date.format(DateTimeFormatter.ofPattern("EE, MMM d"))
            binding.prevDateButton.text = date.minusDays(1).format(DateTimeFormatter.ofPattern("EE"))
            binding.nextDateButton.text = date.plusDays(1).format(DateTimeFormatter.ofPattern("EE"))
            viewModel.fetchEntries(date)
        })

        viewModel.breakfastCaloriesLiveData().observe(viewLifecycleOwner, Observer { entries ->
            breakfastAdapter.setEntries(entries)
            binding.caloriesTotalView.text = viewModel.getTotalCaloriesFractionText()
        })

        viewModel.lunchCaloriesLiveData().observe(viewLifecycleOwner, Observer { entries ->
            lunchAdapter.setEntries(entries)
            binding.caloriesTotalView.text = viewModel.getTotalCaloriesFractionText()
        })

        viewModel.dinnerCaloriesLiveData().observe(viewLifecycleOwner, Observer { entries ->
            dinnerAdapter.setEntries(entries)
            binding.caloriesTotalView.text = viewModel.getTotalCaloriesFractionText()
        })

        viewModel.totalCaloriesLiveData().observe(viewLifecycleOwner, Observer { _ ->
            binding.caloriesTotalView.text = viewModel.getTotalCaloriesFractionText()
        })

        binding.prevDateButton.setOnClickListener { loadPrevDate() }
        binding.nextDateButton.setOnClickListener { loadNextDate() }

        binding.confirmButton.setOnClickListener {
            storeEntry()
        }
        binding.calorieLabelView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                storeEntry()
                true
            } else {
                false
            }
        }

        breakfastAdapter = CaloriesEntriesAdapter(this)
        binding.breakfastCaloriesView.adapter = breakfastAdapter
        binding.breakfastCaloriesView.layoutManager = LinearLayoutManager(context)

        lunchAdapter = CaloriesEntriesAdapter(this)
        binding.lunchCaloriesView.adapter = lunchAdapter
        binding.lunchCaloriesView.layoutManager = LinearLayoutManager(context)
        dinnerAdapter = CaloriesEntriesAdapter(this)
        binding.dinnerCaloriesView.adapter = dinnerAdapter
        binding.dinnerCaloriesView.layoutManager = LinearLayoutManager(context)
    }

    private fun storeEntry() {
        if (!canAddEntry()) {
            Snackbar.make(view ?: return, "Must select a meal period and enter calorie amount.", Snackbar.LENGTH_SHORT).show()
            return
        }
        val label = binding.calorieLabelView.text.toString()
        val calories = Integer.valueOf(binding.calorieEntryView.text.toString())
        val foodPeriod = CaloriesEntriesViewModel.FoodPeriod.valueOf(checkedFoodPeriod())
        viewModel.storeEntry(label, calories, foodPeriod)
        binding.calorieEntryView.text = null
        binding.calorieLabelView.text = null
        val imm: InputMethodManager = context
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

    }

    private fun canAddEntry(): Boolean {
        return binding.foodPeriodRadioGroup.checkedRadioButtonId != -1 && binding.calorieEntryView.text.isNotBlank()
    }

    private fun checkedFoodPeriod(): String {
        return binding.foodPeriodRadioGroup
            .findViewById<RadioButton>(binding.foodPeriodRadioGroup.checkedRadioButtonId)
            .text
            .toString()
    }

    private fun loadPrevDate() {
        viewModel.prevDate()
    }

    private fun loadNextDate() {
        viewModel.nextDate()

    }

    override fun invoke(entry: CaloriesEntriesEntity) {
        val fragment = ConfirmDeleteDialogFragment()
        fragment.arguments = Bundle().apply {
            putParcelable(ENTRY_ARG_KEY, entry)
        }
        childFragmentManager.beginTransaction().add(fragment, "confirm_delete").commit()
    }
}
package com.michaeldang.countingcalories.feat.entries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.michaeldang.countingcalories.R
import kotlinx.android.synthetic.main.total_calories_dialog_fragment.*

class TotalCaloriesDialogFragment : DialogFragment() {

    private val viewModel: CaloriesEntriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.total_calories_dialog_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialogConfirmButton.setOnClickListener {
            viewModel.setTotalCalories(Integer.valueOf(totalCaloriesEditText.text.toString()))
            dismiss()
        }
        dialogCancelButton.setOnClickListener { dismiss() }
    }
}
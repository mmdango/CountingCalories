package com.michaeldang.countingcalories.feat.entries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.michaeldang.countingcalories.databinding.TotalCaloriesDialogFragmentBinding

class TotalCaloriesDialogFragment : DialogFragment() {

    private val viewModel: CaloriesEntriesViewModel by activityViewModels()
    private var _binding: TotalCaloriesDialogFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TotalCaloriesDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.dialogConfirmButton.setOnClickListener {
            viewModel.setTotalCalories(Integer.valueOf(binding.totalCaloriesEditText.text.toString()))
            dismiss()
        }
        binding.dialogCancelButton.setOnClickListener { dismiss() }
    }
}
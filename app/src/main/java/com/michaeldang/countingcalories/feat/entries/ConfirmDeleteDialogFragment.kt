package com.michaeldang.countingcalories.feat.entries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.michaeldang.countingcalories.database.CaloriesEntriesEntity
import com.michaeldang.countingcalories.databinding.ConfirmDeleteDialogFragmentBinding

const val ENTRY_ARG_KEY = "entry_arg"
class ConfirmDeleteDialogFragment: DialogFragment() {

    private val viewModel: CaloriesEntriesViewModel by activityViewModels()
    private var _binding: ConfirmDeleteDialogFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var entryToDelete: CaloriesEntriesEntity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ConfirmDeleteDialogFragmentBinding.inflate(inflater, container, false)
        entryToDelete = arguments?.getParcelable(ENTRY_ARG_KEY) ?: error("Entry to delete required")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.dialogConfirmButton.setOnClickListener {
            viewModel.removeEntry(entryToDelete)
            dismiss()
        }
        binding.dialogCancelButton.setOnClickListener { dismiss() }
    }
}
package com.michaeldang.countingcalories.feat.entries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.michaeldang.countingcalories.R
import com.michaeldang.countingcalories.database.CaloriesEntriesEntity
import com.michaeldang.countingcalories.database.CaloriesEntriesEntityDummy
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesViewModel.FoodPeriod.Companion.FOOD_PERIODS
import java.time.LocalDate

class CaloriesEntriesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val entries: MutableList<CaloriesEntriesEntity> = mutableListOf()

    fun setEntries(entriesToAdd: List<CaloriesEntriesEntity>) {
        entries.clear()
        entries.addAll(entriesToAdd)
        notifyDataSetChanged()
    }

    fun addEntries(entriesToAdd: List<CaloriesEntriesEntity>) {
        entries.addAll(entriesToAdd)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Entry) {
            CaloriesEntriesViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.entries_view_holder,
                    parent,
                    false
                )
            )
        } else {
            CaloriesEntriesLabelViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.entries_label_view_holder,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (entries[position] is CaloriesEntriesEntityDummy) FoodPeriodLabel else Entry
    }

    val Entry = 0
    val FoodPeriodLabel = 1

    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CaloriesEntriesViewHolder) {
            val label = entries[position].label ?: "Quick add"
            holder.caloriesAmountView.text = label + ": " + entries[position].calories.toString()
        } else if (holder is CaloriesEntriesLabelViewHolder) {
            holder.labelView.text = entries[position].foodPeriod.name
        }
    }
}

class CaloriesEntriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val caloriesAmountView: TextView = view.findViewById(R.id.calories_amount)
}

class CaloriesEntriesLabelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val labelView: TextView = view.findViewById(R.id.label)
}
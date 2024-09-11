package com.michaeldang.countingcalories.feat.entries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.michaeldang.countingcalories.R
import com.michaeldang.countingcalories.database.CaloriesEntriesEntity

class CaloriesEntriesAdapter(val confirmDeleteEntry: ConfirmDeleteEntry) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val entries: MutableList<CaloriesEntriesEntity> = mutableListOf()

    fun setEntries(entriesToAdd: List<CaloriesEntriesEntity>) {
        entries.clear()
        entries.addAll(entriesToAdd)
        notifyDataSetChanged()
    }

    fun removeEntry(entry: CaloriesEntriesEntity) {
        entries.remove(entry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == RowType.Entry.id) {
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
        return RowType.Entry.id
    }

    enum class RowType(val id: Int) {
        Entry(0),
        FoodPeriodLabel(1)
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CaloriesEntriesViewHolder) {
            val label = entries[position].label.takeIf { it?.isEmpty() == false } ?: "Item"
            holder.caloriesAmountView.text = holder.caloriesAmountView.context.getString(R.string.label_and_amount, label, entries[position].calories)
            holder.caloriesAmountView.setOnLongClickListener { view ->
                confirmDeleteEntry(entries[position])
                true
            }
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

fun interface ConfirmDeleteEntry {
    operator fun invoke(entry: CaloriesEntriesEntity)
}
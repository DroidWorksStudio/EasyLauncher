package com.github.droidworksstudio.launcher.ui.numberpicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.R

class NumberPickerAdapter(
    private val numbers: List<Int>,
    private val onNumberSelected: (Int) -> Unit
) : RecyclerView.Adapter<NumberPickerAdapter.NumberViewHolder>() {

    inner class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberText: TextView = itemView.findViewById(R.id.number_text)

        init {
            itemView.setOnClickListener {
                onNumberSelected(numbers[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_number, parent, false)
        return NumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.numberText.text = numbers[position].toString()
    }

    override fun getItemCount(): Int = numbers.size
}

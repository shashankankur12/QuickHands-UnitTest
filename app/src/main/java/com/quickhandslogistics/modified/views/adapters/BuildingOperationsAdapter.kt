package com.quickhandslogistics.modified.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.item_building_operation.view.*

class BuildingOperationsAdapter(private val allowUpdate: Boolean) :
    Adapter<BuildingOperationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_building_operation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewHeader: TextView = view.textViewHeader
        var editTextValue: EditText = view.editTextValue

        fun bind() {
            editTextValue.isEnabled = allowUpdate
        }
    }
}
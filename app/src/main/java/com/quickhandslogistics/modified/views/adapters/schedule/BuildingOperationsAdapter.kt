package com.quickhandslogistics.modified.views.adapters.schedule

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.item_building_operation.view.*
import java.util.*

class BuildingOperationsAdapter(
    private val allowUpdate: Boolean,
    private val parameters: ArrayList<String>
) :
    Adapter<BuildingOperationsAdapter.ViewHolder>() {

    private var data = HashMap<String, String>()

    init {
        parameters.sort()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_building_operation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parameters.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    fun updateData(data: HashMap<String, String>) {
        this.data.clear()
        this.data.putAll(data)
        notifyDataSetChanged()
    }

    fun getUpdatedData(): HashMap<String, String> {
        return data
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), TextWatcher {
        var textViewHeader: TextView = view.textViewHeader
        var editTextValue: EditText = view.editTextValue

        fun bind() {
            editTextValue.isEnabled = allowUpdate

            val header = parameters[adapterPosition]
            textViewHeader.text = header.capitalize()
            if (data.containsKey(header)) {
                editTextValue.setText(data[header])
            }
            editTextValue.addTextChangedListener(this)
        }

        override fun afterTextChanged(text: Editable?) {
            val header = parameters[adapterPosition]
            if (!data.containsKey(header)) {
                data[header] = ""
            }
            data[header] = text.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}
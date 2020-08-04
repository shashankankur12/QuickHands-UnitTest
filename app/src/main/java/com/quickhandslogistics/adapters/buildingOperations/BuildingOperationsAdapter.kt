package com.quickhandslogistics.adapters.buildingOperations

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

class BuildingOperationsAdapter(private val parameters: ArrayList<String>) : Adapter<BuildingOperationsAdapter.ViewHolder>() {

    private var data = HashMap<String, String>()
    var isTextChanged :Boolean= false

    init {
        parameters.sortWith(Comparator { value1: String, value2: String ->
            value1.toLowerCase().compareTo(value2.toLowerCase())
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_building_operation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parameters.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), TextWatcher {
        private val textViewHeader: TextView = view.textViewHeader
        private val editTextValue: EditText = view.editTextValue

        fun bind() {
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
            isTextChanged=true
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    fun updateData(data: HashMap<String, String>) {
        this.data.clear()
        this.data.putAll(data)
        notifyDataSetChanged()
    }

    fun getUpdatedData(): HashMap<String, String> {
        return data
    }
}
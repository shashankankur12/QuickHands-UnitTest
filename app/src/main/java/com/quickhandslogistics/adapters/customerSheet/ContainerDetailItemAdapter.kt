package com.quickhandslogistics.adapters.customerSheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.item_container_detail_item.view.*
import java.util.*

class ContainerDetailItemAdapter(buildingOps: HashMap<String, String>?, parameters: ArrayList<String>?) :
    RecyclerView.Adapter<ContainerDetailItemAdapter.ViewHolder>() {

    private val parameters: ArrayList<String> = ArrayList()
    private val buildingOps: HashMap<String, String> = HashMap()

    init {
        parameters?.let {
            parameters.sortWith(Comparator { value1: String, value2: String ->
                value1.toLowerCase().compareTo(value2.toLowerCase())
            })
            this.parameters.addAll(parameters)
        }

        buildingOps?.let {
            this.buildingOps.putAll(buildingOps)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_container_detail_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (parameters.size > 3) 3 else parameters.size
    }

    private fun getItem(position: Int): Pair<String, String?> {
        val header = parameters[position]
        val value = buildingOps[header]
        return Pair(header, value)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewHeader: TextView = view.textViewHeader
        private val textViewValue: TextView = view.textViewValue

        fun bind(pair: Pair<String, String?>) {
            textViewHeader.text = pair.first.capitalize()
            textViewValue.text = if (!pair.second.isNullOrEmpty()) pair.second else "---"
        }
    }
}
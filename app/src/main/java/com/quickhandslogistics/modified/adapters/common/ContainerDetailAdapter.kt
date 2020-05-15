package com.quickhandslogistics.modified.adapters.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.item_container_detail.view.*
import java.util.*
import kotlin.collections.ArrayList

class ContainerDetailAdapter : RecyclerView.Adapter<ContainerDetailAdapter.ViewHolder>() {

    private val parameters: ArrayList<String> = ArrayList()
    private val buildingOps: HashMap<String, String> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_container_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parameters.size
    }

    private fun getItem(position: Int): Pair<String, String?> {
        val header = parameters[position]
        val value = buildingOps[header]
        return Pair(header, value)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewHeader: TextView = view.textViewHeader
        private val textViewValue: TextView = view.textViewValue

        fun bind(pair: Pair<String, String?>) {
            textViewHeader.text = pair.first.capitalize()
            textViewValue.text = if (!pair.second.isNullOrEmpty()) pair.second else "NA"
        }
    }

    fun updateData(buildingOps: HashMap<String, String>?, parameters: ArrayList<String>?) {
        this.parameters.clear()
        this.buildingOps.clear()

        parameters?.let {
            parameters.sortWith(Comparator { value1: String, value2: String ->
                value1.toLowerCase().compareTo(value2.toLowerCase())
            })
            this.parameters.addAll(parameters)
        }

        buildingOps?.let {
            this.buildingOps.putAll(buildingOps)
        }
        notifyDataSetChanged()
    }
}
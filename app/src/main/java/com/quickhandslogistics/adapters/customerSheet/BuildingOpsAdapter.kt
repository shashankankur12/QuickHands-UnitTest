package com.quickhandslogistics.adapters.customerSheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.AppConstant
import kotlinx.android.synthetic.main.item_customer_sheet_table.view.*
import java.util.ArrayList
import java.util.HashMap

class BuildingOpsAdapter(buildingOps: HashMap<String, String>?, parameters: ArrayList<String>?) :
    RecyclerView.Adapter<BuildingOpsAdapter.ViewHolder>() {

    private val parameters: ArrayList<String> = ArrayList()
    private val buildingOps: HashMap<String, String> = HashMap()

    init {
        parameters?.let {
//            parameters.sortWith(Comparator { value1: String, value2: String ->
//                value1.toLowerCase().compareTo(value2.toLowerCase())
//            })
            this.parameters.addAll(parameters)
        }

        buildingOps?.let {
            this.buildingOps.putAll(buildingOps)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_sheet_table, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  parameters.size
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
        private val textViewValue: TextView = view.textViewItem

        fun bind(pair: Pair<String, String?>) {
            textViewValue.text = if (!pair.second.isNullOrEmpty()) pair.second else AppConstant.NOTES_NOT_AVAILABLE
        }
    }
}
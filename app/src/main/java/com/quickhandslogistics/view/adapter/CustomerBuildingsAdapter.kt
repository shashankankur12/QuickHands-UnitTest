package com.quickhandslogistics.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.LumperJobHistoryActivity

class CustomerBuildingsAdapter(val context: Context) : RecyclerView.Adapter<CustomerBuildingsAdapter.BuildingsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingsHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_buildings, parent, false)
        return BuildingsHolder(view)
    }

    override fun onBindViewHolder(holder: BuildingsHolder, position: Int) {
        if (position == 0) {
            holder.buildingName.text = "Building 1"
            holder.buildingAddress.text = "T-800, Sohna Road, Gurgaon"
        } else if (position == 1) {
            holder.buildingName.text = "Building 2"
            holder.buildingAddress.text = "A-400, MG Road, Gurgaon"
        } else if (position == 2) {
            holder.buildingName.text = "Building 3"
            holder.buildingAddress.text = "A-100, Jaipur-Gurgaon Highway, Gurgaon"
        }

        holder.constraintRoots.setOnClickListener {
            context.startActivity(Intent(context, LumperJobHistoryActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    inner class BuildingsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var buildingName = itemView.findViewById<TextView>(R.id.text_building_name)
        var buildingAddress = itemView.findViewById<TextView>(R.id.text_address)
        var constraintRoots = itemView.findViewById<ConstraintLayout>(R.id.constraint_root)
    }
}

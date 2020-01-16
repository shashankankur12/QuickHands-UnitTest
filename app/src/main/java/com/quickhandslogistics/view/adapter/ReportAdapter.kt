package com.quickhandslogistics.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.DashboardActivity
import kotlinx.android.synthetic.main.item_lumper_layout.view.constraint_root
import kotlinx.android.synthetic.main.report_item_layout.view.*
import android.widget.Toast

class ReportAdapter (val items: ArrayList<String>, val context: Context) : RecyclerView.Adapter<reportHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): reportHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.report_item_layout, parent, false)
        return reportHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: reportHolder, position: Int) {
        holder?.reportText?.text = items[position]

        holder.constraintRoot.setOnClickListener {

            if(position == 1){
            val intent = Intent(context, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("drawer_tab", 2)
                context.startActivity(intent)
            } else Toast.makeText(context, "Jobs Activity", Toast.LENGTH_SHORT).show()
        }
    }
}

class reportHolder(view: View) : RecyclerView.ViewHolder(view) {
    var reportText = view.report_text
    var constraintRoot = view.constraint_root
}


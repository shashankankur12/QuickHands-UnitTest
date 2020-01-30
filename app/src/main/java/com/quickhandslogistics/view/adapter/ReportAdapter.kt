package com.quickhandslogistics.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.CustomerActivity
import com.quickhandslogistics.view.activities.LumperListActivity
import kotlinx.android.synthetic.main.item_lumper_layout.view.constraint_root
import kotlinx.android.synthetic.main.report_item_layout.view.*

class ReportAdapter (val items: ArrayList<String>,val itemimages: ArrayList<Int>, val context: Context) : RecyclerView.Adapter<reportHolder>() {

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
        holder?.reportImage?.setImageResource(itemimages[position])

        holder.constraintRoot.setOnClickListener {

            if (position == 1) {

                val intent = Intent(context, LumperListActivity::class.java)
                intent.putExtra(
                    context.getString(R.string.string_lumper),
                    context.getString(R.string.string_lumper)
                )
                context.startActivity(intent)
            } else if (position == 0) {
                val intent = Intent(context, CustomerActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}

class reportHolder(view: View) : RecyclerView.ViewHolder(view) {
    var reportText = view.report_text
    var constraintRoot = view.constraint_root
    var reportImage =view.lumper_history_image
}


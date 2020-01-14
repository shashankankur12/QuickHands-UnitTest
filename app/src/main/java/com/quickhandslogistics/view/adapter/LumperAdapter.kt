package com.quickhandslogistics.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.LumperDetailsActivity
import kotlinx.android.synthetic.main.item_lumper_layout.view.*

class LumperAdapter(val items: ArrayList<String>, val context: Context) : Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_lumper_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.lumperText?.text = items.get(position)
        holder.constraintRoot.setOnClickListener {
            context.startActivity(Intent(context, LumperDetailsActivity::class.java))
        }
    }
}


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var lumperText = view.text_lumper
    var constraintRoot = view.constraint_root
}
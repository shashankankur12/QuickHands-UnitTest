package com.quickhandslogistics.utils.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.item_lumper_layout.view.*

class LumperAdapter (val items : ArrayList<String>, val context: Context) : Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.item_lumper_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return 12
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

}


class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
 val lumperText = view.text_lumper
}
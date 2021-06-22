package com.quickhandslogistics.adapters.customerSheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.item_perameter_view.view.*
import java.util.*

class ParameterAdapter(var mNoteList: ArrayList<String>) :
    RecyclerView.Adapter<ParameterAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_perameter_view, viewGroup, false)
        return CustomViewHolder(itemView)
    }

    private fun getItem(position: Int): String {
        return mNoteList[position]
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return mNoteList.size
    }

    inner class CustomViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mContentText: TextView = itemView.textViewItem

        fun bind(s: String) {
            mContentText.text = s
        }
    }

}
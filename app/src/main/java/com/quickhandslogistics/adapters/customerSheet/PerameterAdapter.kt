package com.quickhandslogistics.adapters.customerSheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import java.util.ArrayList

class PerameterAdapter(var mNoteList: ArrayList<String>) :
    RecyclerView.Adapter<PerameterAdapter.CustomeViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomeViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_perameter_view, viewGroup, false)
        return CustomeViewHolder(itemView)
    }

    private fun getItem(position: Int): String {
        return mNoteList[position]
    }

    override fun onBindViewHolder(viewHolder: CustomeViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return mNoteList.size
    }

    inner class CustomeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mContentText: TextView = itemView.findViewById(R.id.textViewItem)

        fun bind(s: String) {
            mContentText.text=s
        }
    }

}
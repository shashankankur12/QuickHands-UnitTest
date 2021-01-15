package com.quickhandslogistics.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.ListContentAdapter.MyViewHolder
import java.util.*

class ListContentAdapter(var mNoteList: ArrayList<String>) :
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_contect_list_view, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, i: Int) {
        viewHolder.mContentText.text = mNoteList.get(i)
    }

    override fun getItemCount(): Int {
        return mNoteList.size
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mContentText: TextView = itemView.findViewById(R.id.content_item_text)

    }

}
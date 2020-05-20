package com.quickhandslogistics.modified.adapters.scheduleTime

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.scheduleTime.RequestLumpersContract
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import kotlinx.android.synthetic.main.item_request_lumpers.view.*

class RequestLumpersAdapter(private val onAdapterClick: RequestLumpersContract.View.OnAdapterItemClickListener) :
    Adapter<RequestLumpersAdapter.ViewHolder>() {

    private val scheduleTimeList: ArrayList<ScheduleTimeDetail> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_request_lumpers, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return scheduleTimeList.size
    }

    private fun getItem(position: Int): ScheduleTimeDetail {
        return scheduleTimeList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewStatus: TextView = view.textViewStatus
        private val textViewRequestedLumpersCount: TextView = view.textViewRequestedLumpersCount
        private val textViewNote: TextView = view.textViewNote
        private val textViewUpdateRequest: TextView = view.textViewUpdateRequest

        fun bind() {
            textViewStatus.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {

                    }

                }
            }
        }
    }
}
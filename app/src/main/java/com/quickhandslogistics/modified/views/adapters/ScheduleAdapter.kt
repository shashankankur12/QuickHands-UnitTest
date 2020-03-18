package com.quickhandslogistics.modified.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import kotlinx.android.synthetic.main.layout_item_schedule.view.*
import java.util.*

class ScheduleAdapter(var adapterItemClickListener: ScheduleContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var scheduledData: ArrayList<ScheduleData> = ArrayList()
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ScheduleViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.layout_item_schedule, viewGroup, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(scheduleViewHolder: ScheduleViewHolder, position: Int) {
        val item = getItem(position)
        scheduleViewHolder.bind(item)
    }

    override fun getItemCount(): Int {
        return scheduledData.size
    }

    fun getItem(position: Int): ScheduleData {
        return scheduledData[position]
    }

    fun updateList(scheduledData: ArrayList<ScheduleData>) {
        this.scheduledData.clear()
        this.scheduledData = scheduledData
        notifyDataSetChanged()
    }

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var textViewBuildingName: TextView = itemView.textViewBuildingName
        var textViewCreatedDate: TextView = itemView.textViewCreatedDate
        var textViewScheduleType: TextView = itemView.textViewScheduleType
        var textViewWorkItemsCount: TextView = itemView.textViewWorkItemsCount


        fun bind(item: ScheduleData) {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        adapterItemClickListener.onItemClick()
                    }
                }
            }
        }
    }
}
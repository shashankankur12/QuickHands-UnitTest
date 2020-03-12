package com.quickhandslogistics.modified.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleData
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
                .inflate(R.layout.layout_item_events, viewGroup, false)
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

        var textTitle: TextView = itemView.findViewById(R.id.text_title)
        var textCustomerName: TextView = itemView.findViewById(R.id.text_customer_name)
        var textSubService: TextView = itemView.findViewById(R.id.text_service_subservice)
        var textTime: TextView = itemView.findViewById(R.id.text_time)

        fun bind(item: ScheduleData) {
            textTitle.text = item.title
            textCustomerName.text = item.name
            textSubService.text = item.subService
            textTime.text = item.time

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
package com.quickhandslogistics.modified.views.adapters.schedule

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import kotlinx.android.synthetic.main.layout_unscheduled_work_item.view.*

class UnScheduledWorkItemAdapter(
    private val resources: Resources, private val workItemTypeDisplayName: String
) : RecyclerView.Adapter<UnScheduledWorkItemAdapter.WorkItemViewHolder>() {

    private val workItemsList: ArrayList<WorkItemDetail> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_unscheduled_work_item, parent, false)
        return WorkItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    fun getItem(position: Int): WorkItemDetail {
        return workItemsList[position]
    }

    override fun onBindViewHolder(holder: WorkItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateData(workItemsList: List<WorkItemDetail>) {
        this.workItemsList.clear()
        this.workItemsList.addAll(workItemsList)
        notifyDataSetChanged()
    }

    inner class WorkItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textViewStartTime: TextView = view.textViewStartTime
        private val textViewDropItems: TextView = view.textViewDropItems

        fun bind(workItemDetail: WorkItemDetail) {
            textViewStartTime.text = String.format(
                resources.getString(R.string.start_time_container),
                workItemDetail.startTime
            )

            when (workItemTypeDisplayName) {
                resources.getString(R.string.string_drops) -> {
                    textViewDropItems.text = String.format(
                        resources.getString(R.string.no_of_drops),
                        workItemDetail.numberOfDrops
                    )
                }
                resources.getString(R.string.string_live_loads) -> {
                    textViewDropItems.text = String.format(
                        resources.getString(R.string.live_load_sequence),
                        workItemDetail.sequence
                    )
                }
                else -> {
                    textViewDropItems.text = String.format(
                        resources.getString(R.string.outbound_sequence),
                        workItemDetail.sequence
                    )
                }
            }
        }
    }
}
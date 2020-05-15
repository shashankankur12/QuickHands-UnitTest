package com.quickhandslogistics.modified.adapters.schedule

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.common.LumperImagesAdapter
import com.quickhandslogistics.modified.contracts.common.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.controls.OverlapDecoration
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import kotlinx.android.synthetic.main.item_schedule.view.*

class ScheduleAdapter(private val resources: Resources, var adapterItemClickListener: ScheduleContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private var workItemsList: ArrayList<ScheduleDetail> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_schedule, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    fun getItem(position: Int): ScheduleDetail {
        return workItemsList[position]
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, LumperImagesContract.OnItemClickListener {

        private val textViewBuildingName: TextView = itemView.textViewBuildingName
        private val textViewScheduleType: TextView = itemView.textViewScheduleType
        private val textViewWorkItemsCount: TextView = itemView.textViewWorkItemsCount
        private val recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind(scheduleDetail: ScheduleDetail) {
            if (!scheduleDetail.buildingName.isNullOrEmpty())
                textViewBuildingName.text = scheduleDetail.buildingName?.capitalize()
            textViewScheduleType.text = scheduleDetail.scheduleTypeNames
            textViewWorkItemsCount.text = String.format(resources.getString(R.string.work_items_count), scheduleDetail.totalNumberOfWorkItems)

            recyclerViewLumpersImagesList.apply {
                adapter = LumperImagesAdapter(scheduleDetail.allAssignedLumpers, this@ViewHolder)
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onScheduleItemClick(getItem(adapterPosition))
                }
            }
        }

        override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
            adapterItemClickListener.onLumperImagesClick(lumpersList)
        }
    }

    fun updateList(scheduledData: ArrayList<ScheduleDetail>, currentPageIndex: Int) {
        if (currentPageIndex == 1) {
            this.workItemsList.clear()
        }
        this.workItemsList.addAll(scheduledData)
        notifyDataSetChanged()
    }
}
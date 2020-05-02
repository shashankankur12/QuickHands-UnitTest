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

class ScheduleAdapter(
    private val resources: Resources,
    var adapterItemClickListener: ScheduleContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private var workItemsList: ArrayList<ScheduleDetail> = ArrayList()

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ScheduleViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_schedule, viewGroup, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(scheduleViewHolder: ScheduleViewHolder, position: Int) {
        val item = getItem(position)
        scheduleViewHolder.bind(item)
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    fun getItem(position: Int): ScheduleDetail {
        return workItemsList[position]
    }

    fun updateList(scheduledData: ArrayList<ScheduleDetail>) {
        this.workItemsList.clear()
        this.workItemsList = scheduledData
        notifyDataSetChanged()
    }

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, LumperImagesContract.OnItemClickListener {

        var textViewBuildingName: TextView = itemView.textViewBuildingName
        var textViewScheduleType: TextView = itemView.textViewScheduleType
        var textViewWorkItemsCount: TextView = itemView.textViewWorkItemsCount
        var recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList

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
            textViewWorkItemsCount.text = String.format(
                resources.getString(R.string.work_items_count),
                scheduleDetail.totalNumberOfWorkItems
            )

            recyclerViewLumpersImagesList.apply {
                adapter =
                    LumperImagesAdapter(
                        scheduleDetail.allAssignedLumpers,
                        this@ScheduleViewHolder
                    )
            }

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onScheduleItemClick(
                        getItem(
                            adapterPosition
                        )
                    )
                }
            }
        }

        override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
            adapterItemClickListener.onLumperImagesClick(lumpersList)
        }
    }
}
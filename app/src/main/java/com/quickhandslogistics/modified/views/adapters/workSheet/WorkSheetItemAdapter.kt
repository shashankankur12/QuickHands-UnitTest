package com.quickhandslogistics.modified.views.adapters.workSheet

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import kotlinx.android.synthetic.main.layout_item_work_sheet.view.*

class WorkSheetItemAdapter(
    private val workItemType: String, private val resources: Resources,
    var adapterItemClickListener: ScheduleContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<WorkSheetItemAdapter.ScheduleViewHolder>() {

    private var workItemsList: ArrayList<ScheduleDetail> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.layout_item_work_sheet, viewGroup, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(scheduleViewHolder: ScheduleViewHolder, position: Int) {
        //  val item = getItem(position)
        // scheduleViewHolder.bind(item)
        when (workItemType) {
            resources.getString(R.string.ongoing) -> {
                when (position) {
                    0 -> {
                        scheduleViewHolder.textViewStatus.text = "In Progress"
                        scheduleViewHolder.textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
                        scheduleViewHolder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_in_progress)
                    }
                    1 -> {
                        scheduleViewHolder.textViewStatus.text = "On Hold"
                        scheduleViewHolder.textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                        scheduleViewHolder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_on_hold)
                    }
                    else -> {
                        scheduleViewHolder.textViewStatus.text = "Scheduled"
                        scheduleViewHolder.textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                        scheduleViewHolder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
                    }
                }
            }
            resources.getString(R.string.cancelled) -> {
                scheduleViewHolder.textViewStatus.text = "Cancelled"
                scheduleViewHolder.textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                scheduleViewHolder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_cancelled)
            }
            resources.getString(R.string.completed) -> {
                scheduleViewHolder.textViewStatus.text = "Completed"
                scheduleViewHolder.textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                scheduleViewHolder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_completed)
            }
        }
    }

    override fun getItemCount(): Int {
        return 5
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
        var textViewStatus: TextView = itemView.textViewStatus
        var relativeLayoutSide: RelativeLayout = itemView.relativeLayoutSide
        var recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind(scheduleDetail: ScheduleDetail) {
            textViewBuildingName.text = scheduleDetail.buildingName
            textViewScheduleType.text = scheduleDetail.scheduleTypeNames
            textViewWorkItemsCount.text = String.format(
                resources.getString(R.string.work_items_count),
                scheduleDetail.totalNumberOfWorkItems
            )

            recyclerViewLumpersImagesList.apply {
                adapter =
                    LumperImagesAdapter(scheduleDetail.allAssignedLumpers, this@ScheduleViewHolder)
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
package com.quickhandslogistics.modified.adapters.workSheet

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.common.LumperImagesContract
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.adapters.common.LumperImagesAdapter
import com.quickhandslogistics.modified.controls.OverlapDecoration
import kotlinx.android.synthetic.main.item_work_sheet.view.*

class WorkSheetItemAdapter(
    private val workItemType: String, private val resources: Resources,
    var adapterItemClickListener: WorkSheetItemContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<WorkSheetItemAdapter.ViewHolder>() {

    private var workItemsList: ArrayList<ScheduleDetail> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_work_sheet, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //  val item = getItem(position)
        // scheduleViewHolder.bind(item)
        when (workItemType) {
            resources.getString(R.string.ongoing) -> {
                when (position) {
                    0 -> {
                        holder.textViewStatus.text = resources.getString(R.string.in_progress)
                        holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
                        holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_in_progress)
                    }
                    1 -> {
                        holder.textViewStatus.text = resources.getString(R.string.on_hold)
                        holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                        holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_on_hold)
                    }
                    else -> {
                        holder.textViewStatus.text = resources.getString(R.string.scheduled)
                        holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                        holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
                    }
                }
            }
            resources.getString(R.string.cancelled) -> {
                holder.textViewStatus.text = resources.getString(R.string.cancelled)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_cancelled)
            }
            resources.getString(R.string.completed) -> {
                holder.textViewStatus.text = resources.getString(R.string.completed)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_completed)
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
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

            itemView.setOnClickListener(this)
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
                    LumperImagesAdapter(
                        scheduleDetail.allAssignedLumpers,
                        this@ViewHolder
                    )
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> adapterItemClickListener.onItemClick()
                }
            }
        }

        override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
            //adapterItemClickListener.onLumperImagesClick(lumpersList)
        }
    }
}
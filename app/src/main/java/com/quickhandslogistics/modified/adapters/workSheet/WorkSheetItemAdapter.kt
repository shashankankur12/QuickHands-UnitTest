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
import com.quickhandslogistics.modified.adapters.common.LumperImagesAdapter
import com.quickhandslogistics.modified.contracts.common.LumperImagesContract
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemContract
import com.quickhandslogistics.modified.controls.OverlapDecoration
import com.quickhandslogistics.modified.controls.ScheduleUtils
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import kotlinx.android.synthetic.main.item_work_sheet.view.*

class WorkSheetItemAdapter(
    private val workItemType: String, private val resources: Resources,
    var adapterItemClickListener: WorkSheetItemContract.View.OnAdapterItemClickListener
) :
    RecyclerView.Adapter<WorkSheetItemAdapter.ViewHolder>() {

    private var workItemsList: ArrayList<WorkItemDetail> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_work_sheet, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return workItemsList.size
    }

    fun getItem(position: Int): WorkItemDetail {
        return workItemsList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workItemDetail = getItem(position)
        holder.bind(workItemDetail)

        when (workItemDetail.status) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED -> {
                holder.textViewStatus.text = resources.getString(R.string.scheduled)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
            }
            AppConstant.WORK_ITEM_STATUS_ON_HOLD -> {
                holder.textViewStatus.text = resources.getString(R.string.on_hold)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_on_hold)
            }
            AppConstant.WORK_ITEM_STATUS_CANCELLED -> {
                holder.textViewStatus.text = resources.getString(R.string.cancelled)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_cancelled)
            }
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> {
                holder.textViewStatus.text = resources.getString(R.string.in_progress)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_in_progress)
            }
            else -> {
                holder.textViewStatus.text = resources.getString(R.string.completed)
                holder.textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                holder.relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_completed)
            }
        }
    }

    fun updateList(workItemsList: ArrayList<WorkItemDetail>) {
        this.workItemsList.clear()
        this.workItemsList = workItemsList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, LumperImagesContract.OnItemClickListener {

        var textViewStartTime: TextView = itemView.textViewStartTime
        var textViewWorkItemType: TextView = itemView.textViewWorkItemType
        var textViewNoOfDrops: TextView = itemView.textViewNoOfDrops
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

        fun bind(workItemDetail: WorkItemDetail) {
            textViewStartTime.text = String.format(
                resources.getString(R.string.start_time_container),
                DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime)
            )

            val workItemTypeDisplayName =
                ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.workItemType, resources)
            textViewWorkItemType.text = workItemTypeDisplayName

            when (workItemTypeDisplayName) {
                resources.getString(R.string.string_drops) -> {
                    textViewNoOfDrops.text = String.format(
                        resources.getString(R.string.no_of_drops),
                        workItemDetail.numberOfDrops
                    )
                }
                resources.getString(R.string.string_live_loads) -> {
                    textViewNoOfDrops.text = String.format(
                        resources.getString(R.string.live_load_sequence),
                        workItemDetail.sequence
                    )
                }
                else -> {
                    textViewNoOfDrops.text = String.format(
                        resources.getString(R.string.outbound_sequence),
                        workItemDetail.sequence
                    )
                }
            }

            workItemDetail.assignedLumpersList?.let { imagesList ->
                recyclerViewLumpersImagesList.adapter = LumperImagesAdapter(
                    imagesList, this@ViewHolder
                )
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val workItemDetail = getItem(adapterPosition)
                        val workItemTypeDisplayName =
                            ScheduleUtils.getWorkItemTypeDisplayName(
                                workItemDetail.workItemType,
                                resources
                            )
                        adapterItemClickListener.onItemClick(
                            workItemDetail.id!!,
                            workItemTypeDisplayName
                        )
                    }
                }
            }
        }

        override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
            adapterItemClickListener.onLumperImagesClick(lumpersList)
        }
    }
}
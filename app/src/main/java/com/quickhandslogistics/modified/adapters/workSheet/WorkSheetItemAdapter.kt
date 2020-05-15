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
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import kotlinx.android.synthetic.main.item_work_sheet.view.*

class WorkSheetItemAdapter(private val resources: Resources, var adapterItemClickListener: WorkSheetItemContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<WorkSheetItemAdapter.ViewHolder>() {

    private var workItemsList: ArrayList<WorkItemDetail> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_work_sheet, parent, false)
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
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, LumperImagesContract.OnItemClickListener {

        private val textViewStartTime: TextView = itemView.textViewStartTime
        private val textViewWorkItemType: TextView = itemView.textViewWorkItemType
        private val textViewNoOfDrops: TextView = itemView.textViewNoOfDrops
        private val textViewStatus: TextView = itemView.textViewStatus
        private val relativeLayoutSide: RelativeLayout = itemView.relativeLayoutSide
        private val recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }

            itemView.setOnClickListener(this)
        }

        fun bind(workItemDetail: WorkItemDetail) {
            textViewStartTime.text = String.format(resources.getString(R.string.start_time_container), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))

            val workItemTypeDisplayName = ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.workItemType, resources)
            textViewWorkItemType.text = workItemTypeDisplayName

            when (workItemTypeDisplayName) {
                resources.getString(R.string.string_drops) -> textViewNoOfDrops.text = String.format(resources.getString(R.string.no_of_drops), workItemDetail.numberOfDrops)
                resources.getString(R.string.string_live_loads) -> textViewNoOfDrops.text = String.format(resources.getString(R.string.live_load_sequence), workItemDetail.sequence)
                else -> textViewNoOfDrops.text = String.format(resources.getString(R.string.outbound_sequence), workItemDetail.sequence)
            }

            workItemDetail.assignedLumpersList?.let { imagesList ->
                recyclerViewLumpersImagesList.adapter = LumperImagesAdapter(imagesList, this@ViewHolder)
            }

            ScheduleUtils.changeStatusUIByValue(resources, workItemDetail.status, textViewStatus, relativeLayoutSide)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val workItemDetail = getItem(adapterPosition)
                        val workItemTypeDisplayName = ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.workItemType, resources)
                        adapterItemClickListener.onItemClick(workItemDetail.id!!, workItemTypeDisplayName)
                    }
                }
            }
        }

        override fun onLumperImageItemClick(lumpersList: ArrayList<EmployeeData>) {
            adapterItemClickListener.onLumperImagesClick(lumpersList)
        }
    }

    fun updateList(workItemsList: ArrayList<WorkItemDetail>) {
        this.workItemsList.clear()
        this.workItemsList = workItemsList
        notifyDataSetChanged()
    }
}
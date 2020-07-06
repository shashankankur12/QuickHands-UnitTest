package com.quickhandslogistics.adapters.workSheet

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.common.LumperImagesAdapter
import com.quickhandslogistics.contracts.common.LumperImagesContract
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemContract
import com.quickhandslogistics.controls.OverlapDecoration
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.SharedPref
import kotlinx.android.synthetic.main.item_work_sheet.view.*

class WorkSheetItemAdapter(private val resources: Resources, private val sharedPref: SharedPref, var adapterItemClickListener: WorkSheetItemContract.View.OnAdapterItemClickListener) :
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
        private val textViewNoOfDrops: TextView = itemView.textViewNoOfDrops
        private val textViewDoor: TextView = itemView.textViewDoor
        private val textViewContainer: TextView = itemView.textViewContainer
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
            textViewStartTime.text = String.format(resources.getString(R.string.start_time_s), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))

            val workItemTypeDisplayName = ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.workItemType, resources)
            when (workItemTypeDisplayName) {
                resources.getString(R.string.drops) -> textViewNoOfDrops.text = String.format(resources.getString(R.string.no_of_drops_s), workItemDetail.sequence)
                resources.getString(R.string.live_loads) -> textViewNoOfDrops.text = String.format(resources.getString(R.string.live_load_s), workItemDetail.sequence)
                else -> textViewNoOfDrops.text = String.format(resources.getString(R.string.out_bound_s), workItemDetail.sequence)
            }

            var doorValue: String? = null
            var containerNumberValue: String? = null
            if (!workItemDetail.buildingOps.isNullOrEmpty()) {
                doorValue = workItemDetail.buildingOps!!["Door"]
                containerNumberValue = workItemDetail.buildingOps!!["Container Number"]
            }

            textViewDoor.text = String.format(resources.getString(R.string.door_s), if (!doorValue.isNullOrEmpty()) doorValue else "---")
            textViewContainer.text = String.format(resources.getString(R.string.container_no_s), if (!containerNumberValue.isNullOrEmpty()) containerNumberValue else "---")

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
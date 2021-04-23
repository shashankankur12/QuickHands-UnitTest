package com.quickhandslogistics.adapters.schedule

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
import com.quickhandslogistics.contracts.schedule.ScheduleWorkItemContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.utils.UIUtils
import kotlinx.android.synthetic.main.item_work_sheet.view.*

class ScheduleWorkSheetItemAdapter(private val resources: Resources, private val sharedPref: SharedPref, var adapterItemClickListener: ScheduleWorkItemContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<ScheduleWorkSheetItemAdapter.ViewHolder>() {

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
        private val textViewWorkSheetNote: TextView = itemView.textViewWorkSheetNote
        private val textViewIsScheduleLead: TextView = itemView.textViewIsScheduleLead
        private val relativeLayoutSide: RelativeLayout = itemView.relativeLayoutSide
        private val recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//                addItemDecoration(OverlapDecoration())
            }

            itemView.setOnClickListener(this)
        }

        fun bind(workItemDetail: WorkItemDetail) {
            textViewStartTime.text =
                (workItemDetail.startTime)?.let { UIUtils.getSpannableText(resources.getString(R.string.start_time_bold), DateUtils.convertMillisecondsToUTCTimeString(it)!!)}

            when (ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.type, resources)) {
                resources.getString(R.string.drops) -> textViewNoOfDrops.text =  UIUtils.getSpannableText(resources.getString(R.string.no_of_drops_bold_has), workItemDetail.label.toString())
                resources.getString(R.string.live_loads) -> textViewNoOfDrops.text = UIUtils.getSpannableText(resources.getString(R.string.live_load_bold_has), workItemDetail.label.toString())
                else -> textViewNoOfDrops.text = UIUtils.getSpannableText(resources.getString(R.string.out_bound_bold_has), workItemDetail.label.toString())
            }

            var doorValue: String? = null
            var containerNumberValue: String? = null
            if (!workItemDetail.buildingOps.isNullOrEmpty()) {
                doorValue = workItemDetail.buildingOps!!["Door"]
                containerNumberValue = workItemDetail.buildingOps!!["Container Number"]
            }

            textViewDoor.text =  UIUtils.getSpannableText(resources.getString(R.string.door_bold), if (!doorValue.isNullOrEmpty()) doorValue else "---")
            textViewContainer.text = UIUtils.getSpannableText(
                resources.getString(R.string.container_no_bold),
                if (!containerNumberValue.isNullOrEmpty()) containerNumberValue else "---"
            )
            if (workItemDetail.schedule != null) {
                textViewWorkSheetNote.visibility = View.VISIBLE
                textViewWorkSheetNote.text =
                    ScheduleUtils.scheduleTypeNote(workItemDetail.schedule, resources)
            } else textViewWorkSheetNote.visibility = View.GONE
            textViewWorkSheetNote.text=ScheduleUtils.scheduleTypeNote(workItemDetail.schedule, resources)
            textViewWorkSheetNote.isEnabled=(!workItemDetail.schedule?.scheduleNote.isNullOrEmpty() && !getItem(adapterPosition).schedule?.scheduleNote!!.equals("NA"))

            workItemDetail.assignedLumpersList?.let { imagesList ->
                recyclerViewLumpersImagesList.adapter = LumperImagesAdapter(imagesList, sharedPref,this@ViewHolder)
            }
            textViewIsScheduleLead.visibility = if (workItemDetail.isScheduledByLead!!) View.VISIBLE else View.GONE

            ScheduleUtils.changeStatusUIByValue(resources, workItemDetail.status, textViewStatus, relativeLayoutSide)
            textViewWorkSheetNote.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        val workItemDetail = getItem(adapterPosition)
                        val workItemTypeDisplayName = ScheduleUtils.getWorkItemTypeDisplayName(workItemDetail.type, resources)
                        adapterItemClickListener.onItemClick(workItemDetail.id!!, workItemTypeDisplayName)
                    }
                    textViewWorkSheetNote.id->{
                        if (!getItem(adapterPosition).schedule?.scheduleNote.isNullOrEmpty() && !getItem(adapterPosition).schedule?.scheduleNote!!.equals("NA"))
                        adapterItemClickListener.onNoteClick(getItem(adapterPosition))
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
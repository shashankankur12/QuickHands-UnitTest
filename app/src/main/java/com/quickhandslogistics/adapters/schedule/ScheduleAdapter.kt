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
import com.quickhandslogistics.contracts.schedule.ScheduleContract
import com.quickhandslogistics.controls.OverlapDecoration
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.VIEW_DETAILS
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.sharedPref
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.UIUtils
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
        private val textViewStatus: TextView = itemView.textViewStatus
        private val textViewScheduleType: TextView = itemView.textViewScheduleType
        private val textViewWorkItemsCount: TextView = itemView.textViewWorkItemsCount
        private val textViewScheduleTypeLiveLoad: TextView = itemView.textViewScheduleTypeLiveLoad
        private val textViewScheduleTypeDrops: TextView = itemView.textViewScheduleTypeDrops
        private val textViewWorkItemsLeadName: TextView = itemView.textViewWorkItemsLeadName
        private val textViewScheduleTypeStartTime: TextView = itemView.textViewScheduleTypeStartTime
        private val textViewScheduleTypeLiveLoadStartTime: TextView = itemView.textViewScheduleTypeLiveLoadStartTime
        private val textViewScheduleTypeDropsStartTime: TextView = itemView.textViewScheduleTypeDropsStartTime
        private val recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList
        private val relativeLayoutSide: RelativeLayout = itemView.relativeLayoutSide

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind(scheduleDetail: ScheduleDetail) {
            val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

            textViewBuildingName.text = UIUtils.getSpannableText(resources.getString(R.string.department_full),UIUtils.getDisplayEmployeeDepartment(leadProfile))
            textViewScheduleType.text = String.format(resources.getString(R.string.out_bound_s),scheduleDetail.scheduleTypes?.outbounds?.size.toString())
            textViewScheduleTypeLiveLoad.text = String.format(resources.getString(R.string.live_load_s),scheduleDetail.scheduleTypes?.liveLoads?.size.toString())
            textViewScheduleTypeDrops.text = String.format(resources.getString(R.string.drops_s),scheduleDetail.scheduleTypes?.drops?.size.toString())
            textViewWorkItemsCount.text = String.format(resources.getString(R.string.total_containers_s), scheduleDetail.totalNumberOfWorkItems)
            val leadName= String.format("%s %s",leadProfile!!.firstName, leadProfile!!.lastName)
            textViewWorkItemsLeadName.text = String.format(resources.getString(R.string.lead_name),leadName)
            if (scheduleDetail.scheduleTypes?.outbounds!!.size>0 && !scheduleDetail.scheduleTypes?.outbounds!![0].startTime.isNullOrEmpty())
                textViewScheduleTypeStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleDetail.scheduleTypes?.outbounds!![0].startTime)!!.toLong())
            if (scheduleDetail.scheduleTypes?.liveLoads!!.size>0 && !scheduleDetail.scheduleTypes?.liveLoads!![0].startTime.isNullOrEmpty())
                textViewScheduleTypeLiveLoadStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleDetail.scheduleTypes?.liveLoads!![0].startTime)!!.toLong())
            if (scheduleDetail.scheduleTypes?.drops!!.size>0 && !scheduleDetail.scheduleTypes?.drops!![0].startTime.isNullOrEmpty())
                textViewScheduleTypeDropsStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleDetail.scheduleTypes?.drops!![0].startTime)!!.toLong())
            ScheduleUtils.changeStatusUIByValue(resources, VIEW_DETAILS, textViewStatus, relativeLayoutSide)

            recyclerViewLumpersImagesList.apply {
                adapter = LumperImagesAdapter(scheduleDetail.allAssignedLumpers, sharedPref,this@ViewHolder)
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
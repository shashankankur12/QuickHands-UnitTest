package com.quickhandslogistics.adapters.schedule

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.common.LumperImagesAdapter
import com.quickhandslogistics.contracts.common.LumperImagesContract
import com.quickhandslogistics.contracts.schedule.ScheduleContract
import com.quickhandslogistics.data.customerSheet.CustomerSheet
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetailData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.EMPLOYEE_DEPARTMENT_INBOUND
import com.quickhandslogistics.utils.AppConstant.Companion.EMPLOYEE_DEPARTMENT_OUTBOUND
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.sharedPref
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.UIUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.bottom_sheet_customer_sheet_fragement.*
import kotlinx.android.synthetic.main.item_schedule.view.*
import java.util.*
import kotlin.collections.ArrayList

class ScheduleAdapter(private val resources: Resources, private val context: Context, var adapterItemClickListener: ScheduleContract.View.OnAdapterItemClickListener) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private var workItemsList: ArrayList<ScheduleDetailData> = ArrayList()
    private var selectedDate: Date = Date()

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

    fun getItem(position: Int): ScheduleDetailData {
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
        private val textViewScheduleTypeUnfinished: TextView = itemView.textViewScheduleTypeUnfinished
        private val textViewScheduleTypeUnfinishedStartTime: TextView = itemView.textViewScheduleTypeUnfinishedStartTime
        private val recyclerViewLumpersImagesList: RecyclerView = itemView.recyclerViewLumpersImagesList
        private val relativeLayoutSide: RelativeLayout = itemView.relativeLayoutSide
        private val layoutCustomerSignature: ConstraintLayout = itemView.layoutCustomerSignature
        private val textViewCustomerNote: TextView = itemView.textViewCustomerNote
        private val textViewCustomerName: TextView = itemView.textViewCustomerName
        private val imageViewSignature: ImageView = itemView.imageViewSignature
        private val circleImageViewArrow: CircleImageView = itemView.circleImageViewArrow

        init {
            recyclerViewLumpersImagesList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//                addItemDecoration(OverlapDecoration())
            }
        }

        fun bind(scheduleDetail: ScheduleDetailData) {
            val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            textViewBuildingName.text = UIUtils.getSpannableText(resources.getString(R.string.department_full),"${scheduleDetail.scheduleDepartment.toLowerCase().capitalize()}s")
            if (!DateUtils.isCurrentDate(selectedDate.time) && !DateUtils.isFutureDate(selectedDate.time)) {
                if (checkAllContainerComplete(scheduleDetail.customerSheet)) {
                    textViewStatus.text = resources.getString(R.string.completed)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                    relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_completed)
                    layoutCustomerSignature.visibility=View.VISIBLE
                    setCustomerSheetDetails(scheduleDetail.customerSheet)
                } else {
                    textViewStatus.text = resources.getString(R.string.pending)
                    textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                    relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_on_hold)
                    layoutCustomerSignature.visibility=View.GONE
                }
            } else {
                textViewStatus.text = resources.getString(R.string.view_details)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                relativeLayoutSide.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
                layoutCustomerSignature.visibility=View.GONE
            }

            val allScheduleItem =ScheduleUtils.createDifferentListData(scheduleDetail)
            val allWorkItem:ArrayList<WorkItemDetail> = ArrayList()
            allWorkItem.addAll(allScheduleItem.first)
            allWorkItem.addAll(allScheduleItem.second)
            allWorkItem.addAll(allScheduleItem.third)
            allWorkItem.addAll(allScheduleItem.fourth)
            allWorkItem.addAll(allScheduleItem.fifth)

            val scheduleWorkItem=ScheduleUtils.getAllScheduleFilterList(allWorkItem)

            textViewScheduleType.text = String.format(resources.getString(R.string.out_bound_s),scheduleWorkItem.first.size)
            textViewScheduleTypeLiveLoad.text = String.format(resources.getString(R.string.live_load_s),scheduleWorkItem.second.size)
            textViewScheduleTypeDrops.text = String.format(resources.getString(R.string.drops_s),scheduleWorkItem.third.size)
            textViewScheduleTypeUnfinished.text = String.format(resources.getString(R.string.resume_header),scheduleWorkItem.fourth.size)
            textViewWorkItemsCount.text = String.format(resources.getString(R.string.total_containers_s),scheduleWorkItem.fifth)

            leadProfile?.buildingDetailData?.get(0)?.leads?.let {
                val leadName= getDepartmentLead(it, scheduleDetail.scheduleDepartment)
                textViewWorkItemsLeadName.text = String.format(resources.getString(R.string.lead_name),leadName)
            }

            if (scheduleWorkItem.first.size>0 && !scheduleWorkItem.first[0].startTime.isNullOrEmpty())
                textViewScheduleTypeStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleWorkItem.first[0].startTime)!!.toLong())
            else textViewScheduleTypeStartTime.visibility=View.GONE
            if (scheduleWorkItem.second.size>0 && !scheduleWorkItem.second[0].startTime.isNullOrEmpty())
                textViewScheduleTypeLiveLoadStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleWorkItem.second[0].startTime)!!.toLong())
            else textViewScheduleTypeLiveLoadStartTime.visibility=View.GONE
            if (scheduleWorkItem.third.size>0 && !scheduleWorkItem.third[0].startTime.isNullOrEmpty())
                textViewScheduleTypeDropsStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleWorkItem.third[0].startTime)!!.toLong())
            else textViewScheduleTypeDropsStartTime.visibility=View.GONE
            if (scheduleWorkItem.fourth.size>0 && !scheduleWorkItem.fourth[0].startTime.isNullOrEmpty())
            textViewScheduleTypeUnfinishedStartTime.text=DateUtils.convertMillisecondsToTimeString((scheduleWorkItem.fourth[0].startTime)!!.toLong())
            else textViewScheduleTypeUnfinishedStartTime.visibility=View.GONE

            val assignedLumperList = ScheduleUtils.getAssignedLumperList(scheduleDetail) as ArrayList<EmployeeData>
            circleImageViewArrow.visibility = if (assignedLumperList.size > 0) View.VISIBLE else View.GONE
            recyclerViewLumpersImagesList.apply {
                adapter = LumperImagesAdapter(assignedLumperList, sharedPref, this@ViewHolder)
            }

            if (scheduleDetail.scheduleDepartment == (EMPLOYEE_DEPARTMENT_INBOUND)) {
                textViewScheduleType.visibility = View.GONE
                textViewScheduleTypeStartTime.visibility = View.GONE
            } else if (scheduleDetail.scheduleDepartment == EMPLOYEE_DEPARTMENT_OUTBOUND) {
                textViewScheduleTypeLiveLoad.visibility = View.GONE
                textViewScheduleTypeDrops.visibility = View.GONE
                textViewScheduleTypeLiveLoadStartTime.visibility = View.GONE
                textViewScheduleTypeDropsStartTime.visibility = View.GONE
            }
            itemView.setOnClickListener(this)
        }

        private fun setCustomerSheetDetails(customerSheet: CustomerSheet?) {
            customerSheet?.let { sheetData ->
                sheetData.note?.let { textViewCustomerNote.text = it.capitalize() }
                sheetData.customerRepresentativeName?.let { textViewCustomerName.text = it.capitalize() }
                sheetData.signatureUrl?.let { Glide.with(context).load(it).into(imageViewSignature) }
            }
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

        private fun getDepartmentLead(leads: ArrayList<EmployeeData>, scheduleDepartment: String): String? {
            var leadName = ""
            leads.forEach {
                if (it.department.equals(scheduleDepartment, ignoreCase = true))
                    leadName = it.fullName!!
            }
            return leadName
        }

        private fun checkAllContainerComplete(customerSheet: CustomerSheet?): Boolean {
            var allContainerDone = false
            customerSheet?.isSigned?.let {
                allContainerDone = true
            }
            return allContainerDone
        }
    }

    fun updateList(
        scheduledData: ArrayList<ScheduleDetailData>,
        currentPageIndex: Int,
        selectedDate: Date
    ) {
        if (currentPageIndex == 1) {
            this.workItemsList.clear()
        }
        this.workItemsList.addAll(scheduledData)
        this.selectedDate = selectedDate
        notifyDataSetChanged()
    }
}
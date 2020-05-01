package com.quickhandslogistics.modified.adapters.workSheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailLumpersContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_work_item_detail_lumper_time.view.*

class WorkSheetItemDetailLumpersAdapter(
    private var onAdapterClick: WorkSheetItemDetailLumpersContract.View.OnAdapterItemClickListener
) : Adapter<WorkSheetItemDetailLumpersAdapter.WorkItemHolder>() {

    private var workItemStatus = ""
    private var lumperList = ArrayList<EmployeeData>()
    private var timingsData = LinkedHashMap<String, LumpersTimeSchedule>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_work_item_detail_lumper_time, parent, false)
        return WorkItemHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return lumperList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return lumperList[position]
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkItemHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val textViewEmployeeId: TextView = view.textViewEmployeeId
        private val textViewAddTime: TextView = view.textViewAddTime
        private val textViewWorkTime: TextView = view.textViewWorkTime
        private val textViewWaitingTime: TextView = view.textViewWaitingTime
        private val textViewBreakTime: TextView = view.textViewBreakTime

        fun bind(employeeData: EmployeeData) {
            if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl)) {
                Glide.with(context).load(employeeData.profileImageUrl)
                    .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                    .into(circleImageViewProfile)
            } else {
                Glide.with(context).clear(circleImageViewProfile);
            }

            textViewLumperName.text = String.format(
                "%s %s",
                getDefaultOrValue(employeeData.firstName).capitalize(),
                getDefaultOrValue(employeeData.lastName).capitalize()
            )

            if (StringUtils.isNullOrEmpty(employeeData.employeeId)) {
                textViewEmployeeId.visibility = View.GONE
            } else {
                textViewEmployeeId.visibility = View.VISIBLE
                textViewEmployeeId.text = String.format("(Emp ID: %s)", employeeData.employeeId)
            }

            if (timingsData.containsKey(employeeData.id)) {
                val timingDetail = timingsData[employeeData.id]
                timingDetail?.let { timingDetail ->
                    val startTime = DateUtils.convertDateStringToTime(
                        DateUtils.PATTERN_API_RESPONSE, timingDetail.startTime
                    )
                    val endTime = DateUtils.convertDateStringToTime(
                        DateUtils.PATTERN_API_RESPONSE, timingDetail.endTime
                    )
                    textViewWorkTime.text = String.format(
                        "%s - %s",
                        if (startTime.isNotEmpty()) startTime else "NA",
                        if (endTime.isNotEmpty()) endTime else "NA"
                    )

                    val waitingTime = getDefaultOrValue(timingDetail.waitingTime)
                    if (waitingTime.isNotEmpty() && waitingTime.toInt() != 0) {
                        textViewWaitingTime.text = String.format("%s Min", waitingTime)
                    } else {
                        textViewWaitingTime.text = "NA"
                    }

                    val breakTimeStart = DateUtils.convertDateStringToTime(
                        DateUtils.PATTERN_API_RESPONSE, timingDetail.breakTimeStart
                    )
                    val breakTimeEnd = DateUtils.convertDateStringToTime(
                        DateUtils.PATTERN_API_RESPONSE, timingDetail.breakTimeEnd
                    )
                    textViewBreakTime.text = String.format(
                        "%s - %s",
                        if (breakTimeStart.isNotEmpty()) breakTimeStart else "NA",
                        if (breakTimeEnd.isNotEmpty()) breakTimeEnd else "NA"
                    )
                }
            }

            changeAddButtonVisibility()
            textViewAddTime.setOnClickListener(this)
        }

        private fun changeAddButtonVisibility() {
            if (workItemStatus == AppConstant.WORK_ITEM_STATUS_IN_PROGRESS || workItemStatus == AppConstant.WORK_ITEM_STATUS_ON_HOLD) {
                textViewAddTime.visibility = View.VISIBLE
            } else {
                textViewAddTime.visibility = View.GONE
            }
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    textViewAddTime.id -> {
                        val employeeData = getItem(adapterPosition)
                        val timingData = timingsData[employeeData.id]
                        onAdapterClick.onAddTimeClick(employeeData, timingData)
                    }
                }
            }
        }
    }

    fun updateList(
        lumperList: ArrayList<EmployeeData>?,
        timingsData: LinkedHashMap<String, LumpersTimeSchedule>,
        status: String? = ""
    ) {
        this.timingsData.clear()
        this.lumperList.clear()
        lumperList?.let {
            this.lumperList.addAll(lumperList)
            this.timingsData.putAll(timingsData)
        }
        this.workItemStatus = getDefaultOrValue(status)
        notifyDataSetChanged()
    }
}
package com.quickhandslogistics.adapters.scheduleTime

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.controls.CustomTextView
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.convertUTCDateStringToMilliseconds
import com.quickhandslogistics.utils.UIUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_edit_schedule_time.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditScheduleTimeAdapter(scheduleTimeList: ArrayList<ScheduleTimeDetail>, private val onAdapterClick: EditScheduleTimeContract.View.OnAdapterItemClickListener) :
    Adapter<EditScheduleTimeAdapter.ViewHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""

    private val scheduleTimeList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var filteredScheduleTimeList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var scheduledLumpersIdsTimeMap: HashMap<String, Long> = HashMap()

    init {
        for (scheduleTime in scheduleTimeList) {
            if (!scheduleTime.reportingTimeAndDay.isNullOrEmpty()) {
                scheduledLumpersIdsTimeMap[scheduleTime.lumperInfo?.id!!] = convertUTCDateStringToMilliseconds(PATTERN_API_RESPONSE, scheduleTime.reportingTimeAndDay)
            }
        }
        this.scheduleTimeList.addAll(scheduleTimeList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_schedule_time, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredScheduleTimeList.size else scheduleTimeList.size
    }

    private fun getItem(position: Int): ScheduleTimeDetail {
        return if (searchEnabled) filteredScheduleTimeList[position] else scheduleTimeList[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: CustomTextView = view.textViewEmployeeId
        private val textViewScheduleTime: TextView = view.textViewScheduleTime
        private val textViewAddStartTime: TextView = view.textViewAddStartTime
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile
        private val imageViewCancel: ImageView = view.imageViewCancel
        private val layoutScheduleTime: RelativeLayout = view.layoutScheduleTime

        fun bind(scheduleTimeDetail: ScheduleTimeDetail) {
            scheduleTimeDetail.lumperInfo?.let { employeeData ->
                UIUtils.showEmployeeProfileImage(context, employeeData.profileImageUrl, circleImageViewProfile)
                UIUtils.updateProfileBorder(context, employeeData.isTemporaryAssigned, circleImageViewProfile)
                textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
                textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData)

                if (scheduledLumpersIdsTimeMap.containsKey(employeeData.id!!)) {
                    textViewScheduleTime.visibility = View.VISIBLE
                    textViewAddStartTime.visibility = View.GONE

                    textViewScheduleTime.text = DateUtils.convertMillisecondsToTimeString(scheduledLumpersIdsTimeMap[employeeData.id!!]!!)
                } else {
                    textViewScheduleTime.visibility = View.GONE
                    textViewAddStartTime.visibility = View.VISIBLE
                }
            }

            textViewAddStartTime.setOnClickListener(this)
            itemView.setOnClickListener(this)
            layoutScheduleTime.setOnClickListener(this)
            imageViewCancel.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    textViewAddStartTime.id -> {
                        onAdapterClick.onAddStartTimeClick(adapterPosition, 0)
                    }
                    layoutScheduleTime.id -> {
                        var timeInMillis: Long = 0
                        val scheduleTimeDetail = getItem(adapterPosition)
                        if (scheduledLumpersIdsTimeMap.containsKey(scheduleTimeDetail.lumperInfo?.id!!)) {
                            timeInMillis = scheduledLumpersIdsTimeMap[scheduleTimeDetail.lumperInfo?.id!!]!!
                        }
                        onAdapterClick.onAddStartTimeClick(adapterPosition, timeInMillis)
                    }
                    layoutScheduleTime.id -> {
                        onAdapterClick.onAddScheduleNoteClick(adapterPosition)
                    }
                    imageViewCancel.id -> {
                        onAdapterClick.onAddRemoveClick(adapterPosition, getItem(adapterPosition))
                    }
                }
            }
        }
    }

    fun isSearchEnabled(): Boolean {
        return searchEnabled
    }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            filteredScheduleTimeList.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        filteredScheduleTimeList.clear()
        if (searchTerm.isEmpty()) {
            filteredScheduleTimeList.addAll(scheduleTimeList)
        } else {
            for (data in scheduleTimeList) {
                val term = "${data.lumperInfo?.firstName} ${data.lumperInfo?.lastName}"

                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredScheduleTimeList.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun addStartTime(adapterPosition: Int, timeInMillis: Long) {
        val scheduleTimeDetail = getItem(adapterPosition)
        scheduledLumpersIdsTimeMap[scheduleTimeDetail.lumperInfo?.id!!] = timeInMillis
        scheduleTimeList[adapterPosition].reportingTimeAndDay = DateUtils.convertMillisecondsToUTCDateString(PATTERN_API_RESPONSE, timeInMillis)
        notifyDataSetChanged()
    }

    fun addStartTimetoAll(timeInMillis: Long) {
        setSearchEnabled(false)
        for (scheduleTimeDetail in scheduleTimeList) {
            scheduledLumpersIdsTimeMap[scheduleTimeDetail.lumperInfo?.id!!] = timeInMillis
            scheduleTimeDetail.reportingTimeAndDay = DateUtils.convertMillisecondsToUTCDateString(PATTERN_API_RESPONSE, timeInMillis)
        }
        notifyDataSetChanged()
    }

    fun getScheduledLumpersTimeMap(): HashMap<String, Long> {
        return scheduledLumpersIdsTimeMap
    }

    fun getScheduledTimeList(): ArrayList<ScheduleTimeDetail> {
        return scheduleTimeList
    }

    fun getLumpersList(): ArrayList<EmployeeData> {
        val employeeDataList = ArrayList<EmployeeData>()
        for (scheduleTimeDetail in scheduleTimeList) {
            employeeDataList.add(scheduleTimeDetail.lumperInfo!!)
        }
        return employeeDataList
    }

    fun addLumpersList(employeeDataList: ArrayList<EmployeeData>) {
        val oldLumpersIdsTimeMap = HashMap<String, Long>()
        oldLumpersIdsTimeMap.putAll(scheduledLumpersIdsTimeMap)

        setSearchEnabled(false)
        this.scheduleTimeList.clear()
        this.scheduledLumpersIdsTimeMap.clear()

        for (employeeData in employeeDataList) {
            val scheduleTimeDetail = ScheduleTimeDetail()
            scheduleTimeDetail.lumperInfo = employeeData
            this.scheduleTimeList.add(scheduleTimeDetail)

            if (oldLumpersIdsTimeMap.containsKey(employeeData.id!!)) {
                this.scheduledLumpersIdsTimeMap[employeeData.id!!] = oldLumpersIdsTimeMap[employeeData.id!!]!!
            }
        }

        notifyDataSetChanged()
    }

    fun removeLumpersInList(position: Int, item: ScheduleTimeDetail
    ) {
        val scheduleTimeDetail = getItem(position)
        scheduleTimeList.removeAt(position)
        if (scheduledLumpersIdsTimeMap.containsKey(scheduleTimeDetail.lumperInfo?.id!!))
        scheduledLumpersIdsTimeMap.remove(scheduleTimeDetail.lumperInfo?.id!!)
        notifyDataSetChanged()
    }


}
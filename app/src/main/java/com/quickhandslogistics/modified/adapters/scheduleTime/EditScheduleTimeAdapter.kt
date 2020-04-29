package com.quickhandslogistics.modified.adapters.scheduleTime

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_edit_schedule_time.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditScheduleTimeAdapter(
    private val scheduleTimeList: ArrayList<ScheduleTimeDetail>,
    private val onAdapterClick: EditScheduleTimeContract.View.OnAdapterItemClickListener
) : Adapter<EditScheduleTimeAdapter.WorkItemHolder>() {

    private var employeeDataList: ArrayList<EmployeeData> = ArrayList()
    private var filteredEmployeeDataList: ArrayList<EmployeeData> = ArrayList()

    private var scheduledLumpersIdsTimeMap: HashMap<String, Long> = HashMap()

    private var searchEnabled = false
    private var searchTerm = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkItemHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_edit_schedule_time, parent, false)
        return WorkItemHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredEmployeeDataList.size else employeeDataList.size
    }

    private fun getItem(position: Int): EmployeeData {
        return if (searchEnabled) filteredEmployeeDataList[position] else employeeDataList[position]
    }

    override fun onBindViewHolder(holder: WorkItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedLumpers(): HashMap<String, Long> {
        return scheduledLumpersIdsTimeMap
    }

    fun updateLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        setSearchEnabled(false)
        this.employeeDataList.clear()
        this.employeeDataList.addAll(employeeDataList)

        for (employeeData in employeeDataList) {
            for (scheduleTime in scheduleTimeList) {
                if (employeeData.id == scheduleTime.lumperInfo?.id) {
                    scheduledLumpersIdsTimeMap[employeeData.id!!] =
                        DateUtils.convertUTCDateStringToMilliseconds(
                            DateUtils.PATTERN_API_RESPONSE,
                            scheduleTime.reportingTimeAndDay
                        )
                    break
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class WorkItemHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private val textViewLumperName: TextView = view.textViewLumperName
        private val textViewEmployeeId: TextView = view.textViewEmployeeId
        private val textViewScheduleTime: TextView = view.textViewScheduleTime
        private val textViewAddStartTime: TextView = view.textViewAddStartTime
        private val circleImageViewProfile: CircleImageView = view.circleImageViewProfile

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
                ValueUtils.getDefaultOrValue(employeeData.firstName).capitalize(),
                ValueUtils.getDefaultOrValue(employeeData.lastName).capitalize()
            )

            if (StringUtils.isNullOrEmpty(employeeData.employeeId)) {
                textViewEmployeeId.visibility = View.GONE
            } else {
                textViewEmployeeId.visibility = View.VISIBLE
                textViewEmployeeId.text = String.format("(Emp ID: %s)", employeeData.employeeId)
            }

            if (scheduledLumpersIdsTimeMap.containsKey(employeeData.id!!)) {
                textViewScheduleTime.visibility = View.VISIBLE
                textViewAddStartTime.visibility = View.GONE

                textViewScheduleTime.text =
                    DateUtils.convertMillisecondsToTimeString(scheduledLumpersIdsTimeMap[employeeData.id!!]!!)
            } else {
                textViewScheduleTime.visibility = View.GONE
                textViewAddStartTime.visibility = View.VISIBLE
            }

            textViewAddStartTime.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let {
                when (view.id) {
                    itemView.id -> {
                        var timeInMillis: Long = 0
                        val employeeData = getItem(adapterPosition)
                        if (scheduledLumpersIdsTimeMap.containsKey(employeeData.id!!)) {
                            timeInMillis = scheduledLumpersIdsTimeMap[employeeData.id!!]!!
                        }
                        onAdapterClick.onAddStartTimeClick(adapterPosition, timeInMillis)
                    }
                    textViewAddStartTime.id -> {
                        onAdapterClick.onAddStartTimeClick(adapterPosition, 0)
                    }
                }
            }
        }
    }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            filteredEmployeeDataList.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        filteredEmployeeDataList.clear()
        if (searchTerm.isEmpty()) {
            filteredEmployeeDataList.addAll(employeeDataList)
        } else {
            for (data in employeeDataList) {
                val term = "${data.firstName} ${data.lastName}"

                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredEmployeeDataList.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun addStartTime(adapterPosition: Int, timeInMillis: Long) {
        val employeeData = getItem(adapterPosition)
        scheduledLumpersIdsTimeMap[employeeData.id!!] = timeInMillis
        notifyDataSetChanged()
    }

    fun addStartTimetoAll(timeInMillis: Long) {
        setSearchEnabled(false)
        for (employeeData in employeeDataList) {
            scheduledLumpersIdsTimeMap[employeeData.id!!] = timeInMillis
        }
        notifyDataSetChanged()
    }
}
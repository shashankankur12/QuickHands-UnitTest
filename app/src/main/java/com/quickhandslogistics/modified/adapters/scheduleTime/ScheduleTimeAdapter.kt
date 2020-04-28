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
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_schedule_time.view.*
import java.util.*
import kotlin.collections.ArrayList

class ScheduleTimeAdapter : Adapter<ScheduleTimeAdapter.ViewHolder>() {

    private var searchEnabled = false
    private var searchTerm = ""

    private var items: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var filteredItems: ArrayList<ScheduleTimeDetail> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_time, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return if (searchEnabled) filteredItems.size else items.size
    }

    private fun getItem(position: Int): ScheduleTimeDetail {
        return if (searchEnabled) filteredItems[position] else items[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateLumpersData(scheduleTimeDetailList: ArrayList<ScheduleTimeDetail>) {
        items.clear()
        items.addAll(scheduleTimeDetailList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        var textViewLumperName: TextView = view.textViewLumperName
        var textViewEmployeeId: TextView = view.textViewEmployeeId
        var textViewScheduleTime: TextView = view.textViewScheduleTime
        var circleImageViewProfile: CircleImageView = view.circleImageViewProfile

        fun bind(scheduleTimeDetail: ScheduleTimeDetail) {
            val employeeData = scheduleTimeDetail.lumperInfo
            employeeData?.let {
                if (!StringUtils.isNullOrEmpty(employeeData.profileImageUrl)) {
                    Glide.with(context).load(employeeData.profileImageUrl)
                        .placeholder(R.drawable.dummy).error(R.drawable.dummy)
                        .into(circleImageViewProfile)
                } else {
                    Glide.with(context).clear(circleImageViewProfile)
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
            }

            textViewScheduleTime.text = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                scheduleTimeDetail.reportingTimeAndDay
            )
        }
    }

    fun setSearchEnabled(enabled: Boolean, searchTerm: String = "") {
        this.searchEnabled = enabled
        if (!searchEnabled) {
            this.searchTerm = ""
            filteredItems.clear()
            notifyDataSetChanged()
            return
        }
        this.searchTerm = searchTerm.toLowerCase(Locale.getDefault())
        filter()
    }

    private fun filter() {
        filteredItems.clear()
        if (searchTerm.isEmpty()) {
            filteredItems.addAll(items)
        } else {
            for (data in items) {
                val term = "${data.lumperInfo?.firstName} ${data.lumperInfo?.lastName}"
                if (term.toLowerCase(Locale.getDefault()).contains(searchTerm)) {
                    filteredItems.add(data)
                }
            }
        }
        notifyDataSetChanged()
    }
}
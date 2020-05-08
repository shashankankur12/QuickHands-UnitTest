package com.quickhandslogistics.modified.controls

import android.content.res.Resources
import android.widget.TextView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant

object ScheduleUtils {

    fun getAllAssignedLumpersList(workItems: List<WorkItemDetail>?): ArrayList<EmployeeData> {
        val assignedLumpers = ArrayList<EmployeeData>()
        workItems?.let {
            for (workItem in workItems) {
                workItem.assignedLumpersList?.let { assignedLumpersList ->
                    assignedLumpers.addAll(assignedLumpersList)
                }
            }
        }
        return assignedLumpers
    }

    fun getScheduleTypeName(workItems: List<WorkItemDetail>?, scheduleTypeNames: String, scheduleType: String): String {
        var scheduleTypes = scheduleTypeNames

        workItems?.let {
            if (workItems.isNotEmpty()) {
                if (scheduleTypes.isNotEmpty()) {
                    scheduleTypes += ", "
                }
                scheduleTypes += scheduleType
            }
        }
        return scheduleTypes
    }

    fun getWorkItemTypeDisplayName(workItemType: String?, resources: Resources): String {
        var workItemTypeDisplayName = ""
        workItemType?.let {
            workItemTypeDisplayName = when (workItemType) {
                "live" -> resources.getString(R.string.string_live_loads)
                "drop" -> resources.getString(R.string.string_drops)
                else -> resources.getString(R.string.string_out_bounds)
            }
        }
        return workItemTypeDisplayName
    }

    fun getWorkItemTypeCounts(list: ArrayList<WorkItemDetail>): Triple<Int, Int, Int> {
        var liveLoadsCount = 0
        var dropsCount = 0
        var outBoundsCount = 0

        for (workItemDetail in list) {
            when (workItemDetail.workItemType) {
                "live" -> liveLoadsCount++
                "drop" -> dropsCount++
                else -> outBoundsCount++
            }
        }
        return Triple(liveLoadsCount, dropsCount, outBoundsCount)
    }

    fun showStatusTextViewByStatus(textViewStatus: TextView, status: String?, resources: Resources) {
        when (status) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED -> {
                textViewStatus.text = resources.getString(R.string.scheduled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
            }
            AppConstant.WORK_ITEM_STATUS_ON_HOLD -> {
                textViewStatus.text = resources.getString(R.string.on_hold)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
            }
            AppConstant.WORK_ITEM_STATUS_CANCELLED -> {
                textViewStatus.text = resources.getString(R.string.cancelled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
            }
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> {
                textViewStatus.text = resources.getString(R.string.in_progress)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
            }
            else -> {
                textViewStatus.text = resources.getString(R.string.completed)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
            }
        }
    }
}
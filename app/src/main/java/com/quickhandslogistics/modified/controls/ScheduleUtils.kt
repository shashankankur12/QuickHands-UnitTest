package com.quickhandslogistics.modified.controls

import android.content.res.Resources
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail

object ScheduleUtils {

    fun getAllAssignedLumpersList(
        workItems: List<WorkItemDetail>?
    ): ArrayList<EmployeeData> {
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

    fun getScheduleTypeName(
        workItems: List<WorkItemDetail>?,
        scheduleTypeNames: String,
        scheduleType: String
    ): String {
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
}
package com.quickhandslogistics.modified.controls

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
}
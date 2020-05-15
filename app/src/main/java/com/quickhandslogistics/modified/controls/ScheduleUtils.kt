package com.quickhandslogistics.modified.controls

import android.content.res.Resources
import android.widget.RelativeLayout
import android.widget.TextView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.StringUtils
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

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

    fun changeStatusUIByValue(
        resources: Resources, status: String?, textViewStatus: TextView,
        relativeLayoutSide: RelativeLayout? = null, isEditable: Boolean = false
    ) {
        when (status) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED -> {
                textViewStatus.text = resources.getString(R.string.scheduled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.WORK_ITEM_STATUS_ON_HOLD -> {
                textViewStatus.text = resources.getString(R.string.on_hold)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_on_hold)
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> {
                textViewStatus.text = resources.getString(R.string.in_progress)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_in_progress)
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.WORK_ITEM_STATUS_CANCELLED -> {
                textViewStatus.text = resources.getString(R.string.cancelled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_cancelled)
                setStatusViewEditable(false, textViewStatus)
            }
            AppConstant.WORK_ITEM_STATUS_COMPLETED -> {
                textViewStatus.text = resources.getString(R.string.completed)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_completed)
                setStatusViewEditable(false, textViewStatus)
            }
        }
    }

    private fun setStatusViewEditable(isEditable: Boolean, textViewStatus: TextView) {
        if (isEditable) {
            textViewStatus.isClickable = true
            textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0)
        } else {
            textViewStatus.isClickable = false
            textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    fun createStatusList(resources: Resources, status: String): LinkedHashMap<String, String> {
        val statusList: LinkedHashMap<String, String> = LinkedHashMap()
        when (status) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED -> {
                statusList[resources.getString(R.string.scheduled)] = AppConstant.WORK_ITEM_STATUS_SCHEDULED
                statusList[resources.getString(R.string.in_progress)] = AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
                statusList[resources.getString(R.string.on_hold)] = AppConstant.WORK_ITEM_STATUS_ON_HOLD
                statusList[resources.getString(R.string.cancelled)] = AppConstant.WORK_ITEM_STATUS_CANCELLED
            }
            AppConstant.WORK_ITEM_STATUS_ON_HOLD -> {
                statusList[resources.getString(R.string.in_progress)] = AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
                statusList[resources.getString(R.string.on_hold)] = AppConstant.WORK_ITEM_STATUS_ON_HOLD
                statusList[resources.getString(R.string.cancelled)] = AppConstant.WORK_ITEM_STATUS_CANCELLED
                statusList[resources.getString(R.string.completed)] = AppConstant.WORK_ITEM_STATUS_COMPLETED
            }
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> {
                statusList[resources.getString(R.string.in_progress)] = AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
                statusList[resources.getString(R.string.on_hold)] = AppConstant.WORK_ITEM_STATUS_ON_HOLD
                statusList[resources.getString(R.string.cancelled)] = AppConstant.WORK_ITEM_STATUS_CANCELLED
                statusList[resources.getString(R.string.completed)] = AppConstant.WORK_ITEM_STATUS_COMPLETED
            }
        }
        return statusList
    }

    fun sortEmployeesList(employeesList: ArrayList<EmployeeData>?): ArrayList<EmployeeData> {
        return if (!employeesList.isNullOrEmpty()) {
            employeesList.sortWith(Comparator { lumper1, lumper2 ->
                if (!StringUtils.isNullOrEmpty(lumper1.firstName) && !StringUtils.isNullOrEmpty(lumper2.firstName)) {
                    lumper1.firstName?.toLowerCase(Locale.getDefault())!!.compareTo(lumper2.firstName?.toLowerCase(Locale.getDefault())!!)
                } else {
                    0
                }
            })
            employeesList
        } else ArrayList()
    }
}
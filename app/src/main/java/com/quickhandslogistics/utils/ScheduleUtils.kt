package com.quickhandslogistics.utils

import android.content.res.Resources
import android.text.Spanned
import android.widget.RelativeLayout
import android.widget.TextView
import com.quickhandslogistics.R
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.scheduleTime.RequestLumpersRecord
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse
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

        if (!workItems.isNullOrEmpty()) {
            if (scheduleTypes.isNotEmpty()) {
                scheduleTypes += ", "
            }
            scheduleTypes += scheduleType
        }
        return scheduleTypes
    }

    fun getWorkItemTypeDisplayName(workItemType: String?, resources: Resources): String {
        var workItemTypeDisplayName = ""
        workItemType?.let {
            workItemTypeDisplayName = when (workItemType) {
                "live" -> resources.getString(R.string.live_loads)
                "drop" -> resources.getString(R.string.drops)
                else -> resources.getString(R.string.out_bounds)
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
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.WORK_ITEM_STATUS_COMPLETED -> {
                textViewStatus.text = resources.getString(R.string.completed)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_completed)
                setStatusViewEditable(isEditable, textViewStatus)
            }
            AppConstant.VIEW_DETAILS -> {
                textViewStatus.text = resources.getString(R.string.view_details)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                relativeLayoutSide?.setBackgroundResource(R.drawable.schedule_item_stroke_scheduled)
                setStatusViewEditable(isEditable, textViewStatus)
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

    fun sortEmployeesList(employeesList: ArrayList<EmployeeData>?, isTemporaryLumpers: Boolean = false): ArrayList<EmployeeData> {
        return if (!employeesList.isNullOrEmpty()) {
            employeesList.sortWith(Comparator { lumper1, lumper2 ->
                if (!lumper1.firstName.isNullOrEmpty() && !lumper2.firstName.isNullOrEmpty()) {
                    lumper1.firstName?.toLowerCase(Locale.getDefault())!!.compareTo(lumper2.firstName?.toLowerCase(Locale.getDefault())!!)
                } else {
                    0
                }
            })

            if (isTemporaryLumpers) {
                val iterate = employeesList.listIterator()
                while (iterate.hasNext()) {
                    val oldValue = iterate.next()
                    oldValue.isTemporaryAssigned = true
                    iterate.set(oldValue)
                }
            }
            employeesList
        } else ArrayList()
    }


    fun sortEmployeesAttendanceList(employeesList: ArrayList<LumperAttendanceData>?, isTemporaryLumpers: Boolean = false): ArrayList<LumperAttendanceData> {
        return if (!employeesList.isNullOrEmpty()) {
            employeesList.sortWith(Comparator { lumper1, lumper2 ->
                if (!lumper1.firstName.isNullOrEmpty() && !lumper2.firstName.isNullOrEmpty()) {
                    lumper1.firstName?.toLowerCase(Locale.getDefault())!!.compareTo(lumper2.firstName?.toLowerCase(Locale.getDefault())!!)
                } else {
                    0
                }
            })

            if (isTemporaryLumpers) {
                val iterate = employeesList.listIterator()
                while (iterate.hasNext()) {
                    val oldValue = iterate.next()
                    oldValue.isTemporaryAssigned = true
                    iterate.set(oldValue)
                }
            }
            employeesList
        } else ArrayList()
    }

    fun getWholeScheduleStatus(scheduleTypes: ScheduleDetail.ScheduleTypes): String {
        var scheduledCount = 0
        var inProgressCount = 0
        var onHoldCount = 0
        var cancelledCount = 0
        var completedCount = 0

        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(scheduleTypes.liveLoads!!)
        allWorkItems.addAll(scheduleTypes.outbounds!!)
        allWorkItems.addAll(scheduleTypes.drops!!)

        for (workItem in allWorkItems) {
            when (workItem.status) {
                AppConstant.WORK_ITEM_STATUS_SCHEDULED -> scheduledCount++
                AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> inProgressCount++
                AppConstant.WORK_ITEM_STATUS_ON_HOLD -> onHoldCount++
                AppConstant.WORK_ITEM_STATUS_CANCELLED -> cancelledCount++
                AppConstant.WORK_ITEM_STATUS_COMPLETED -> completedCount++
            }
        }

        return if (inProgressCount > 0 || onHoldCount > 0) {
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
        } else if (scheduledCount > 0) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED
        } else if (cancelledCount > 0 && completedCount == 0) {
            AppConstant.WORK_ITEM_STATUS_CANCELLED
        } else {
            AppConstant.WORK_ITEM_STATUS_COMPLETED
        }
    }

    fun getFilledBuildingParametersCount(buildingOps: HashMap<String, String>?): Int {
        var count = 0

        buildingOps?.let {
            for (key in buildingOps.keys) {
                val value = buildingOps[key]
                if (!value.isNullOrEmpty()) {
                    count++
                }
            }
        }

        return count
    }

    fun getBuildingParametersList(sharedPref: SharedPref): ArrayList<String> {
        val parameters = ArrayList<String>()
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        leadProfile?.buildingDetailData?.let { buildingDetailData ->
            if (!buildingDetailData.parameters.isNullOrEmpty()) {
                parameters.addAll(buildingDetailData.parameters!!)
            }
        }
        return parameters
    }

    fun getShiftDetailString(leadProfile: LeadProfileData?): String {
        var shiftName = ""
        leadProfile?.shift?.let { name ->
            shiftName = name.capitalize()
        }

        return " ${ResourceManager.getInstance().getString(R.string.dept_bold)} ${UIUtils.getDisplayEmployeeDepartment(leadProfile)}  ${ResourceManager.getInstance().getString(R.string.shift_bold)} $shiftName"
    }

    fun getOldShiftDetailString(leadProfile: LeadProfileData?): String {
        var shiftName = ""
        var shiftStartTime = ""
        var shiftEndTime = ""
        leadProfile?.shift?.let { name ->
            shiftName = name.capitalize()
            val shiftDetail = when (leadProfile.shift) {
                AppConstant.EMPLOYEE_SHIFT_MORNING -> {
                    leadProfile.buildingDetailData?.morningShift
                }
                AppConstant.EMPLOYEE_SHIFT_SWING -> {
                    leadProfile.buildingDetailData?.swingShift
                }
                AppConstant.EMPLOYEE_SHIFT_NIGHT -> {
                    leadProfile.buildingDetailData?.nightShift
                }
                else -> null
            }
            shiftDetail?.let {
                shiftStartTime = DateUtils.convertMillisecondsToTimeString(shiftDetail.startTime!!)
                shiftEndTime = DateUtils.convertMillisecondsToTimeString(shiftDetail.endTime!!)
            }
        }
        return "$shiftName ($shiftStartTime - $shiftEndTime)"
    }

     fun getSortRequestLumper(records: ArrayList<RequestLumpersRecord>): ArrayList<RequestLumpersRecord> {
        var pendingRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        var completedRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        var rejectedRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        var cancelRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        var sortedlRecords: ArrayList<RequestLumpersRecord> = ArrayList()
        records.forEach {
            when {
                it.requestStatus.equals(AppConstant.REQUEST_LUMPERS_STATUS_PENDING) -> {
                    pendingRecords.add(it)
                }
                it.requestStatus.equals(AppConstant.REQUEST_LUMPERS_STATUS_APPROVED) -> {
                    completedRecords.add(it)
                }
                it.requestStatus.equals(AppConstant.REQUEST_LUMPERS_STATUS_REJECTED) -> {
                    rejectedRecords.add(it)
                }
                it.requestStatus.equals(AppConstant.REQUEST_LUMPERS_STATUS_CANCELLED) -> {
                    cancelRecords.add(it)
                }
            }
        }

        sortedlRecords.addAll(getSortedDate(pendingRecords))
        sortedlRecords.addAll(getSortedDate(completedRecords))
        sortedlRecords.addAll(getSortedDate(rejectedRecords))
        sortedlRecords.addAll(getSortedDate(cancelRecords))
        return sortedlRecords
    }

    private fun getSortedDate(records: ArrayList<RequestLumpersRecord>): ArrayList<RequestLumpersRecord> {
        records.sortWith(Comparator { data: RequestLumpersRecord, t1: RequestLumpersRecord ->
            (data.createdAt)?.compareTo(t1.createdAt!!)!!
        })
        return records
    }

    fun calculatePercent(lumperCase: String, totalCases: String): Double {
        return (lumperCase.toDouble() / totalCases.toDouble()) * 100
    }

    fun getCancelHeaderDetails(scheduleDetails: ScheduleTimeDetail, rawString:String): Spanned? {
        var lumperName =String.format( "%s %s", scheduleDetails.lumperInfo!!.firstName,scheduleDetails.lumperInfo!!.lastName)
        var lumperScheduleTime =DateUtils.changeDateString(DateUtils.PATTERN_API_RESPONSE ,DateUtils.PATTERN_NORMAL,
            scheduleDetails.reportingTimeAndDay!!
        )
        var formetString= String.format(rawString, lumperName, lumperScheduleTime)
        return UIUtils.getSpannedText(formetString)
    }

    fun getscheduleTypeNote(workItemDetail: WorkItemDetail, resources: Resources): String {
          var noteType=  resources.getString(R.string.daily)
        if (workItemDetail.scheduleForWeek!!){
            noteType= resources.getString(R.string.weekly)
        }else if (workItemDetail.scheduleForMonth!!){
            noteType= resources.getString(R.string.monthly)
        }
        return noteType
    }
    fun scheduleTypeNotePopupTitle(workItemDetail: WorkItemDetail, resources: Resources): String {
        var noteType=  resources.getString(R.string.daily_scheduled)
        if (workItemDetail.scheduleForWeek!!){
            noteType= resources.getString(R.string.weekly_scheduled)
        }else if (workItemDetail.scheduleForMonth!!){
            noteType= resources.getString(R.string.monthly_scheduled)
        }
        return noteType
    }

    fun getGroupNoteList(workItemData: WorkSheetListAPIResponse.Data): Triple<ArrayList<String>, ArrayList<String>, ArrayList<String>>  {
         var dailyNoteList: ArrayList<String> = ArrayList()
         var weeklyNoteList: ArrayList<String> = ArrayList()
         var monthlyNoteList: ArrayList<String> = ArrayList()
         var workItemDetail: ArrayList<WorkItemDetail> = ArrayList()

        workItemData?.let{
            workItemDetail.addAll(it.cancelled!!)
            workItemDetail.addAll(it.onHold!!)
            workItemDetail.addAll(it.inProgress!!)
            workItemDetail.addAll(it.scheduled!!)
            workItemDetail.addAll(it.completed!!)
        }

        workItemDetail.forEach {
            when {
                it.scheduleForWeek!! -> {
                    if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                        if (!weeklyNoteList.contains(it.scheduleNote!!))
                            weeklyNoteList.add(it.scheduleNote!!)
                }
                it.scheduleForMonth!! -> {
                    if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                        if (!monthlyNoteList.contains(it.scheduleNote!!))
                            monthlyNoteList.add(it.scheduleNote!!)
                }
                else -> {
                    if (!it.scheduleNote.isNullOrEmpty() && !it.scheduleNote.equals("NA"))
                        if (!dailyNoteList.contains(it.scheduleNote!!))
                            dailyNoteList.add(it.scheduleNote!!)
                }
            }

        }

        return Triple(dailyNoteList,weeklyNoteList ,monthlyNoteList)
    }
}

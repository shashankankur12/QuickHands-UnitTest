package com.quickhandslogistics.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.schedule.ScheduleContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.models.schedule.ScheduleModel
import com.quickhandslogistics.utils.ScheduleUtils.getAllAssignedLumpersList
import com.quickhandslogistics.utils.ScheduleUtils.getScheduleTypeName
import com.quickhandslogistics.utils.ScheduleUtils.getWholeScheduleStatus
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.utils.ValueUtils
import java.util.*
import kotlin.collections.ArrayList

class SchedulePresenter(private var scheduleView: ScheduleContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    ScheduleContract.Presenter, ScheduleContract.Model.OnFinishedListener {

    private val scheduleModel = ScheduleModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        scheduleView == null
    }

    override fun getScheduledWorkItemsByDate(date: Date, pageIndex: Int) {
        scheduleView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        scheduleModel.fetchHeaderInfo(date, this)
        scheduleModel.fetchSchedulesByDate(date, pageIndex, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        scheduleView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            scheduleView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            scheduleView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(selectedDate: Date, scheduleListAPIResponse: ScheduleListAPIResponse, currentPageIndex: Int) {
        val workItemsList = ArrayList<ScheduleDetail>()
        scheduleListAPIResponse.data?.scheduleDetailsList?.let {
            workItemsList.addAll(it)
        }

        val iterate = workItemsList.listIterator()
        while (iterate.hasNext()) {
            val oldValue = iterate.next()
            oldValue.scheduleTypes?.let { scheduleTypes ->
                // Find a common status for all work items
                val commonStatus = getWholeScheduleStatus(scheduleTypes)
                oldValue.commonStatus = commonStatus

                // Create single name for all types of work items scheduled
                var scheduleTypeNames = ""
                scheduleTypeNames = getScheduleTypeName(scheduleTypes.liveLoads, scheduleTypeNames, resources.getString(R.string.live_loads))
                scheduleTypeNames = getScheduleTypeName(scheduleTypes.drops, scheduleTypeNames, resources.getString(R.string.drops))
                scheduleTypeNames = getScheduleTypeName(scheduleTypes.outbounds, scheduleTypeNames, resources.getString(R.string.out_bounds))

                if (scheduleTypeNames.isEmpty()) {
                    // Remove the wrong record returned from API of another date.
                    iterate.remove()
                } else {
                    oldValue.scheduleTypeNames = scheduleTypeNames
                    oldValue.allAssignedLumpers.addAll(getAllAssignedLumpersList(scheduleTypes.liveLoads))
                    oldValue.allAssignedLumpers.addAll(getAllAssignedLumpersList(scheduleTypes.drops))
                    oldValue.allAssignedLumpers.addAll(getAllAssignedLumpersList(scheduleTypes.outbounds))
                    oldValue.allAssignedLumpers = oldValue.allAssignedLumpers.distinctBy { it.id } as ArrayList<EmployeeData>
                    iterate.set(oldValue)
                }
            }
        }

        val totalPagesCount = ValueUtils.getDefaultOrValue(scheduleListAPIResponse.data?.pageCount)
        val nextPageIndex = ValueUtils.getDefaultOrValue(scheduleListAPIResponse.data?.next)

        if (workItemsList.size > 0) {
            scheduleView?.showScheduleData(selectedDate, workItemsList, totalPagesCount, nextPageIndex, currentPageIndex)
        } else {
            scheduleView?.showEmptyData()
        }

        scheduleView?.hideProgressDialog()
    }

    override fun onSuccessGetHeaderInfo(dateString: String) {
        scheduleView?.showDateString(dateString)
    }
}
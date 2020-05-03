package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.modified.models.schedule.ScheduleModel
import com.quickhandslogistics.modified.controls.ScheduleUtils.getAllAssignedLumpersList
import com.quickhandslogistics.modified.controls.ScheduleUtils.getScheduleTypeName
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class SchedulePresenter(
    private var scheduleView: ScheduleContract.View?,
    private val resources: Resources,
    sharedPref: SharedPref
) :
    ScheduleContract.Presenter, ScheduleContract.Model.OnFinishedListener {

    private val scheduleModel: ScheduleModel = ScheduleModel(sharedPref)

    override fun getScheduledWorkItemsByDate(date: Date) {
        scheduleView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        scheduleModel.fetchSchedulesByDate(date, this)
    }

    override fun onDestroy() {
        scheduleView == null
    }

    override fun onFailure(message: String) {
        scheduleView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            scheduleView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            scheduleView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(selectedDate: Date, scheduleListAPIResponse: ScheduleListAPIResponse) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        scheduleView?.showDateString(dateString)

        val workItemsList = ArrayList<ScheduleDetail>()
        scheduleListAPIResponse.data?.scheduleDetailsList?.let {
            workItemsList.addAll(it)
        }

        val iterate = workItemsList.listIterator()
        while (iterate.hasNext()) {
            val oldValue = iterate.next()
            oldValue.scheduleTypes?.let { scheduleTypes ->
                var scheduleTypeNames = ""
                scheduleTypeNames = getScheduleTypeName(
                    scheduleTypes.liveLoads, scheduleTypeNames,
                    resources.getString(R.string.string_live_loads)
                )
                scheduleTypeNames = getScheduleTypeName(
                    scheduleTypes.drops, scheduleTypeNames,
                    resources.getString(R.string.string_drops)
                )
                scheduleTypeNames = getScheduleTypeName(
                    scheduleTypes.outbounds, scheduleTypeNames,
                    resources.getString(R.string.string_out_bounds)
                )
                if (scheduleTypeNames.isEmpty()) {
                    // Remove the wrong record returned from API of another date.
                    iterate.remove()
                } else {
                    oldValue.scheduleTypeNames = scheduleTypeNames
                    oldValue.allAssignedLumpers.addAll(getAllAssignedLumpersList(scheduleTypes.liveLoads))
                    oldValue.allAssignedLumpers.addAll(getAllAssignedLumpersList(scheduleTypes.drops))
                    oldValue.allAssignedLumpers.addAll(getAllAssignedLumpersList(scheduleTypes.outbounds))
                    oldValue.allAssignedLumpers =
                        oldValue.allAssignedLumpers.distinctBy { it.id } as ArrayList<EmployeeData>
                    iterate.set(oldValue)
                }
            }
        }

        if (workItemsList.size > 0) {
            scheduleView?.showScheduleData(selectedDate, workItemsList)
        } else {
            scheduleView?.showEmptyData()
        }
        scheduleView?.fetchUnsScheduledWorkItems()
    }
}
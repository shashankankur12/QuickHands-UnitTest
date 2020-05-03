package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.controls.ScheduleUtils.getAllAssignedLumpersList
import com.quickhandslogistics.modified.controls.ScheduleUtils.getScheduleTypeName
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.data.schedule.UnScheduleListAPIResponse
import com.quickhandslogistics.modified.models.schedule.UnScheduleModel
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*
import kotlin.collections.ArrayList

class UnSchedulePresenter(
    private var unScheduleView: UnScheduleContract.View?,
    private val resources: Resources,
    sharedPref: SharedPref
) :
    UnScheduleContract.Presenter, UnScheduleContract.Model.OnFinishedListener {

    private val unScheduleModel: UnScheduleModel = UnScheduleModel(sharedPref)

    override fun getUnScheduledWorkItems(showProgressDialog: Boolean) {
        if (showProgressDialog) {
            unScheduleView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        }
        unScheduleModel.fetchUnSchedulesByDate(this)
    }

    override fun onDestroy() {
        unScheduleView = null
    }

    override fun onFailure(message: String) {
        unScheduleView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            unScheduleView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            unScheduleView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(unScheduleListAPIResponse: UnScheduleListAPIResponse) {
        val workItemsList = ArrayList<ScheduleDetail>()
        unScheduleListAPIResponse.data?.records?.day1ScheduleList?.let {
            workItemsList.addAll(it)
        }
        unScheduleListAPIResponse.data?.records?.day2ScheduleList?.let {
            workItemsList.addAll(it)
        }
        unScheduleListAPIResponse.data?.records?.day3ScheduleList?.let {
            workItemsList.addAll(it)
        }
        unScheduleListAPIResponse.data?.records?.day4ScheduleList?.let {
            workItemsList.addAll(it)
        }
        unScheduleListAPIResponse.data?.records?.day5ScheduleList?.let {
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
                oldValue.scheduleTypeNames = scheduleTypeNames
                oldValue.allAssignedLumpers.addAll(getAllAssignedLumpersList(scheduleTypes.liveLoads))
                oldValue.allAssignedLumpers.addAll(getAllAssignedLumpersList(scheduleTypes.drops))
                oldValue.allAssignedLumpers.addAll(getAllAssignedLumpersList(scheduleTypes.outbounds))
                oldValue.allAssignedLumpers =
                    oldValue.allAssignedLumpers.distinctBy { it.id } as ArrayList<EmployeeData>
                iterate.set(oldValue)
            }
        }

        workItemsList.sortWith(Comparator { workItem1, workItem2 ->
            val dateLong1 = DateUtils.getMillisecondsFromDateString(
                DateUtils.PATTERN_API_REQUEST_PARAMETER,
                workItem1?.endDateForCurrentWorkItem
            )
            val dateLong2 = DateUtils.getMillisecondsFromDateString(
                DateUtils.PATTERN_API_REQUEST_PARAMETER,
                workItem2?.endDateForCurrentWorkItem
            )
            dateLong1.compareTo(dateLong2)
        })

        if (workItemsList.size > 0) {
            unScheduleView?.showUnScheduleData(workItemsList)
        } else {
            unScheduleView?.showEmptyData()
        }
        unScheduleView?.hideProgressDialog()
    }
}

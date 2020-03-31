package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleAPIResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.models.schedule.ScheduleModel
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

    override fun onFailure(message: String) {

    }

    override fun onSuccess(selectedDate: Date, scheduleAPIResponse: ScheduleAPIResponse) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        scheduleView?.showDateString(dateString)

        val workItemsList = ArrayList<WorkItemDetail>()
        scheduleAPIResponse.data?.workItems?.liveTypeWorkItem?.let {
            workItemsList.addAll(it)
        }
        scheduleAPIResponse.data?.workItems?.dropTypeWorkItem?.let {
            workItemsList.addAll(it)
        }

        if (workItemsList.size > 0) {
            scheduleView?.showScheduleData(selectedDate, workItemsList)
        } else {
            scheduleView?.showEmptyData()
        }
        scheduleView?.fetchUnsScheduledWorkItems()
    }
}
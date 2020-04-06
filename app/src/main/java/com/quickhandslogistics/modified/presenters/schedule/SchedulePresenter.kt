package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
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
            var scheduleTypeNames = ""
            oldValue.scheduleTypes?.liveLoads?.let {
                if (it.isNotEmpty()) {
                    scheduleTypeNames = resources.getString(R.string.string_live_loads)
                }
            }

            oldValue.scheduleTypes?.drops?.let {
                if (it.isNotEmpty()) {
                    if (scheduleTypeNames.isNotEmpty()) {
                        scheduleTypeNames += ", "
                    }
                    scheduleTypeNames += resources.getString(R.string.string_drops)
                }
            }

            oldValue.scheduleTypes?.outbounds?.let {
                if (it.isNotEmpty()) {
                    if (scheduleTypeNames.isNotEmpty()) {
                        scheduleTypeNames += ", "
                    }
                    scheduleTypeNames += resources.getString(R.string.string_out_bonds)
                }
            }
            oldValue.scheduleTypeNames = scheduleTypeNames
            iterate.set(oldValue)
        }

        if (workItemsList.size > 0) {
            scheduleView?.showScheduleData(selectedDate, workItemsList)
        } else {
            scheduleView?.showEmptyData()
        }
        scheduleView?.fetchUnsScheduledWorkItems()
    }
}
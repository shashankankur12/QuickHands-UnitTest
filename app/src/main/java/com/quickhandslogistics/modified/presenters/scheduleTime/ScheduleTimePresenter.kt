package com.quickhandslogistics.modified.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.modified.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.modified.models.scheduleTime.ScheduleTimeModel
import com.quickhandslogistics.utils.DateUtils
import java.util.*

class ScheduleTimePresenter(
    private var scheduleTimeView: ScheduleTimeContract.View?,
    private val resources: Resources
) :
    ScheduleTimeContract.Presenter, ScheduleTimeContract.Model.OnFinishedListener {

    private val scheduleTimeModel: ScheduleTimeModel = ScheduleTimeModel()

    override fun getSchedulesTimeByDate(date: Date) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, date)
        scheduleTimeView?.showDateString(dateString)

        scheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        scheduleTimeModel.fetchSchedulesTimeByDate(date, this)
    }

    override fun onDestroy() {
        scheduleTimeView = null
    }

    override fun onFailure(message: String) {
        scheduleTimeView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            scheduleTimeView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            scheduleTimeView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(selectedDate: Date, getScheduleTimeAPIResponse: GetScheduleTimeAPIResponse) {
        scheduleTimeView?.hideProgressDialog()
        scheduleTimeView?.showScheduleTimeData(selectedDate, getScheduleTimeAPIResponse.data!!)
    }
}
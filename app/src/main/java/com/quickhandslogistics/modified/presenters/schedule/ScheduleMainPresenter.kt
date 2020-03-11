package com.quickhandslogistics.modified.presenters.schedule

import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.data.schedule.ScheduleMainResponse
import com.quickhandslogistics.modified.models.schedule.ScheduleMainModel
import com.quickhandslogistics.utils.DateUtils
import java.util.*

class ScheduleMainPresenter(private var scheduleMainView: ScheduleMainContract.View) :
    ScheduleMainContract.Presenter, ScheduleMainContract.Model.OnFinishedListener {

    private val scheduleMainModel: ScheduleMainModel = ScheduleMainModel()

    override fun showSchedulesByDate(date: Date) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, date)
        scheduleMainView.showDateString(dateString, date.time)
        scheduleMainModel.fetchSchedules(date, this)
    }

    override fun onFailure(message: String) {

    }

    override fun onSuccess(
        selectedDate: Date,
        scheduleMainResponse: ScheduleMainResponse
    ) {
        scheduleMainView.showScheduleData(selectedDate, scheduleMainResponse)
    }
}
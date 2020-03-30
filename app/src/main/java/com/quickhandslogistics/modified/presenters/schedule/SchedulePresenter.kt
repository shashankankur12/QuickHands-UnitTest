package com.quickhandslogistics.modified.presenters.schedule

import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.modified.models.schedule.ScheduleModel
import com.quickhandslogistics.utils.DateUtils
import java.util.*

class SchedulePresenter(private var scheduleView: ScheduleContract.View) :
    ScheduleContract.Presenter, ScheduleContract.Model.OnFinishedListener {

    private val scheduleModel: ScheduleModel = ScheduleModel()

    override fun showSchedulesByDate(date: Date) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, date)
        scheduleView.showDateString(dateString)
        scheduleModel.fetchSchedules(date, this)
    }

    override fun onFailure(message: String) {

    }

    override fun onSuccess(selectedDate: Date, scheduleDataList: ArrayList<ScheduleData>) {
        scheduleView.showScheduleData(selectedDate, scheduleDataList)
    }
}
package com.quickhandslogistics.modified.presenters.schedule

import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.modified.models.schedule.UnScheduleModel
import java.util.*

class UnSchedulePresenter(private var unScheduleView: UnScheduleContract.View) :
    UnScheduleContract.Presenter, UnScheduleContract.Model.OnFinishedListener {

    private val unScheduleModel: UnScheduleModel = UnScheduleModel()

    override fun showUnScheduleWork() {
        unScheduleModel.fetchUnScheduleWork(this)
    }

    override fun onFailure(message: String) {

    }

    override fun onSuccess(unScheduledData: ArrayList<ScheduleData>) {
        unScheduleView.showUnScheduleData(unScheduledData)

    }
}
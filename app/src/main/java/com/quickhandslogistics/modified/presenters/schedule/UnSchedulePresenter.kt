package com.quickhandslogistics.modified.presenters.schedule

import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleAPIResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.models.schedule.UnScheduleModel
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class UnSchedulePresenter(
    private var unScheduleView: UnScheduleContract.View?,
    sharedPref: SharedPref
) :
    UnScheduleContract.Presenter, UnScheduleContract.Model.OnFinishedListener {

    private val unScheduleModel: UnScheduleModel = UnScheduleModel(sharedPref)

    override fun getUnScheduledWorkItems(date: Date) {
        unScheduleModel.fetchUnSchedulesByDate(date, this)
    }

    override fun onFailure(message: String) {

    }

    override fun onSuccess(
        selectedDate: Date,
        unScheduleAPIResponse: ScheduleAPIResponse
    ) {
        //unScheduleView.showUnScheduleData(unScheduledData)

        val workItemsList = ArrayList<WorkItemDetail>()
        unScheduleAPIResponse.data?.workItems?.liveTypeWorkItem?.let {
            workItemsList.addAll(it)
        }
        unScheduleAPIResponse.data?.workItems?.dropTypeWorkItem?.let {
            workItemsList.addAll(it)
        }
        unScheduleView?.showUnScheduleData(selectedDate, workItemsList)
        unScheduleView?.hideProgressDialog()
    }
}
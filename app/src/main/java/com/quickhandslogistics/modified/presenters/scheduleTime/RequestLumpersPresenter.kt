package com.quickhandslogistics.modified.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.scheduleTime.RequestLumpersContract
import com.quickhandslogistics.modified.models.scheduleTime.RequestLumpersModel
import java.util.*

class RequestLumpersPresenter(private var requestLumpersView: RequestLumpersContract.View?, private val resources: Resources) :
    RequestLumpersContract.Presenter, RequestLumpersContract.Model.OnFinishedListener {

    private val requestLumpersModel = RequestLumpersModel()

    /** View Listeners */
    override fun onDestroy() {
        requestLumpersView = null
    }

    override fun initiateScheduleTime(
        scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, requiredLumpersCount: Int, notesDM: String, selectedDate: Date
    ) {
        requestLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        requestLumpersModel.assignScheduleTime(scheduledLumpersIdsTimeMap, notes, requiredLumpersCount, notesDM, selectedDate, this)
    }

    override fun fetchAllRequestsByDate(selectedDate: Date) {
        requestLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        requestLumpersModel.fetchAllRequestsByDate(selectedDate, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        requestLumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            requestLumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            requestLumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessScheduleTime() {
        requestLumpersView?.hideProgressDialog()
        requestLumpersView?.scheduleTimeFinished()
    }

    override fun onSuccessFetchRequests() {
        requestLumpersView?.hideProgressDialog()
        requestLumpersView?.showAllRequests()
    }
}
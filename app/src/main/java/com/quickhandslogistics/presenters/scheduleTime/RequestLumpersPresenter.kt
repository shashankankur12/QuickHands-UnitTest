package com.quickhandslogistics.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.RequestLumpersContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.scheduleTime.RequestLumpersListAPIResponse
import com.quickhandslogistics.models.scheduleTime.RequestLumpersModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils.getSortRequestLumper
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class RequestLumpersPresenter(private var requestLumpersView: RequestLumpersContract.View?, private val resources: Resources) :
    RequestLumpersContract.Presenter, RequestLumpersContract.Model.OnFinishedListener {

    private val requestLumpersModel = RequestLumpersModel()

    /** View Listeners */
    override fun onDestroy() {
        requestLumpersView = null
    }

    override fun fetchAllRequestsByDate(selectedDate: Date) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        requestLumpersView?.showHeaderInfo(dateString)

        requestLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        requestLumpersModel.fetchAllRequestsByDate(selectedDate, this)
    }

    override fun createNewRequestForLumpers(
        requiredLumperCount: String,
        notesDM: String,
        date: Date,
        noteLumper: String
    ) {
        requestLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        requestLumpersModel.createNewRequestForLumpers(requiredLumperCount, notesDM, date, noteLumper,this)
    }

    override fun cancelRequestForLumpers(requestId: String, date: Date) {
        requestLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        requestLumpersModel.cancelRequestForLumpers(requestId, date, this)
    }

    override fun updateRequestForLumpers(requestId: String, requiredLumperCount: String, notesDM: String, date: Date) {
        requestLumpersView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        requestLumpersModel.updateRequestForLumpers(requestId, requiredLumperCount, notesDM, date, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        requestLumpersView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            requestLumpersView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            requestLumpersView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        requestLumpersView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            requestLumpersView?.showLoginScreen()
        }
    }

    override fun onSuccessCancelRequest(date: Date) {
        requestLumpersView?.hideProgressDialog()
        requestLumpersView?.showSuccessDialog(resources.getString(R.string.request_cancelled_success_message), date)
    }

    override fun onSuccessFetchRequests(response: RequestLumpersListAPIResponse) {
        requestLumpersView?.hideProgressDialog()

        response.data?.records?.let { records ->
            requestLumpersView?.showAllRequests(getSortRequestLumper(records))
        }
    }

    override fun onSuccessRequest(date: Date) {
        requestLumpersView?.hideProgressDialog()
        requestLumpersView?.showSuccessDialog(resources.getString(R.string.request_placed_success_message), date)
    }
}
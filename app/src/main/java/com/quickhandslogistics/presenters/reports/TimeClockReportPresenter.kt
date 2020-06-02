package com.quickhandslogistics.presenters.reports

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.TimeClockReportContract
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.models.reports.TimeClockReportModel

class TimeClockReportPresenter(private var timeClockReportView: TimeClockReportContract.View?, private val resources: Resources) :
    TimeClockReportContract.Presenter, TimeClockReportContract.Model.OnFinishedListener {

    private val timeClockReportModel = TimeClockReportModel()

    /** View Listeners */
    override fun onDestroy() {
        timeClockReportView = null
    }

    override fun fetchLumpersList() {
        timeClockReportView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        timeClockReportModel.fetchLumpersList(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        timeClockReportView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            timeClockReportView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            timeClockReportView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: LumperListAPIResponse) {
        timeClockReportView?.hideProgressDialog()
        timeClockReportView?.showLumpersData(response.data?.permanentLumpersList!!)
    }
}
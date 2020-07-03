package com.quickhandslogistics.presenters.reports

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.LumperJobReportContract
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.data.reports.ReportResponse
import com.quickhandslogistics.models.reports.LumperJobReportModel
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class LumperJobReportPresenter(private var lumperJobReportView: LumperJobReportContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    LumperJobReportContract.Presenter, LumperJobReportContract.Model.OnFinishedListener {

    private val lumperJobHistoryModel = LumperJobReportModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        lumperJobReportView = null
    }

    override fun fetchLumpersList() {
        lumperJobReportView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumperJobHistoryModel.fetchLumpersList(this)
    }

    override fun createTimeClockReport(startDate: Date, endDate: Date, reportType: String, lumperIdsList: ArrayList<String>) {
        lumperJobReportView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumperJobHistoryModel.createTimeClockReport(startDate, endDate, reportType, lumperIdsList, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        lumperJobReportView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumperJobReportView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            lumperJobReportView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: LumperListAPIResponse) {
        lumperJobReportView?.hideProgressDialog()
        lumperJobReportView?.showLumpersData(response.data?.permanentLumpersList!!)
    }

    override fun onSuccessCreateReport(response: ReportResponse) {
        response.data?.also { reportUrl ->
            lumperJobHistoryModel.getUrlMimeType(reportUrl, this)
        } ?: run {
            lumperJobReportView?.hideProgressDialog()
        }
    }

    override fun onSuccessFetchMimeType(reportUrl: String, mimeType: String) {
        lumperJobReportView?.hideProgressDialog()
        lumperJobReportView?.showReportDownloadDialog(reportUrl, mimeType)
    }
}
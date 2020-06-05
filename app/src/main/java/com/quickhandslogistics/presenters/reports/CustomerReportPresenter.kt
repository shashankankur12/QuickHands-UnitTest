package com.quickhandslogistics.presenters.reports

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.CustomerReportContract
import com.quickhandslogistics.data.reports.ReportResponse
import com.quickhandslogistics.models.reports.CustomerReportModel
import java.util.*

class CustomerReportPresenter(private var customerReportView: CustomerReportContract.View?, private val resources: Resources) :
    CustomerReportContract.Presenter, CustomerReportContract.Model.OnFinishedListener {

    private val customerReportModel = CustomerReportModel()

    /** View Listeners */
    override fun onDestroy() {
        customerReportView = null
    }

    override fun createTimeClockReport(startDate: Date, endDate: Date, reportType: String) {
        customerReportView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        customerReportModel.createTimeClockReport(startDate, endDate, reportType, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        customerReportView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            customerReportView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            customerReportView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessCreateReport(response: ReportResponse) {
        response.data?.also { reportUrl ->
            customerReportModel.getUrlMimeType(reportUrl, this)
        } ?: run {
            customerReportView?.hideProgressDialog()
        }
    }

    override fun onSuccessFetchMimeType(reportUrl: String, mimeType: String) {
        customerReportView?.hideProgressDialog()
        customerReportView?.showReportDownloadDialog(reportUrl, mimeType)
    }
}
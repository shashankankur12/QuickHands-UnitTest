package com.quickhandslogistics.contracts.reports

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.reports.ReportResponse
import java.util.*

class CustomerReportContract {
    interface Model {
        fun createTimeClockReport(startDate: Date, endDate: Date, reportType: String, onFinishedListener: OnFinishedListener)
        fun getUrlMimeType(reportUrl: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccessCreateReport(response: ReportResponse)
            fun onSuccessFetchMimeType(reportUrl: String, mimeType: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showReportDownloadDialog(reportUrl: String, mimeType: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun createTimeClockReport(startDate: Date, endDate: Date, reportType: String)
    }
}
package com.quickhandslogistics.contracts.reports

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.data.reports.ReportResponse
import java.util.*

class LumperJobReportContract {
    interface Model {
        fun fetchLumpersList(onFinishedListener: OnFinishedListener)
        fun createTimeClockReport(startDate: Date, endDate: Date, reportType: String, lumperIdsList: ArrayList<String>, onFinishedListener: OnFinishedListener)
        fun getUrlMimeType(reportUrl: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: LumperListAPIResponse)
            fun onSuccessCreateReport(response: ReportResponse)
            fun onSuccessFetchMimeType(reportUrl: String, mimeType: String)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: ArrayList<EmployeeData>)
        fun showReportDownloadDialog(reportUrl: String, mimeType: String)

        interface OnAdapterItemClickListener {
            fun onLumperSelectionChanged()
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchLumpersList()
        fun createTimeClockReport(startDate: Date, endDate: Date, reportType: String, lumperIdsList: ArrayList<String>)
    }
}
package com.quickhandslogistics.models.reports

import android.util.Log
import com.quickhandslogistics.contracts.reports.CustomerReportContract
import com.quickhandslogistics.data.reports.ReportRequest
import com.quickhandslogistics.data.reports.ReportResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.FetchMimeType
import com.quickhandslogistics.utils.OnFetchCompleteListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CustomerReportModel : CustomerReportContract.Model {

    override fun createTimeClockReport(startDate: Date, endDate: Date, reportType: String, onFinishedListener: CustomerReportContract.Model.OnFinishedListener) {
        val startDateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, startDate)
        val endDateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, endDate)

        DataManager.getService().createCustomerReport(DataManager.getAuthToken(), startDateString, endDateString, reportType).enqueue(object : Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if (DataManager.isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessCreateReport(response.body()!!)
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e(CustomerReportModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun getUrlMimeType(reportUrl: String, onFinishedListener: CustomerReportContract.Model.OnFinishedListener) {
        FetchMimeType(reportUrl, object : OnFetchCompleteListener {
            override fun onFetchMimeType(mimeType: String) {
                onFinishedListener.onSuccessFetchMimeType(reportUrl, mimeType)
            }
        }).execute()
    }
}
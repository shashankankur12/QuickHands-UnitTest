package com.quickhandslogistics.modified.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.modified.contracts.scheduleTime.RequestLumpersContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.scheduleTime.RequestLumpersListAPIResponse
import com.quickhandslogistics.modified.data.scheduleTime.RequestLumpersRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RequestLumpersModel : RequestLumpersContract.Model {

    override fun fetchAllRequestsByDate(selectedDate: Date, onFinishedListener: RequestLumpersContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)
        DataManager.getService().getRequestLumpersList(getAuthToken(), dateString).enqueue(object : Callback<RequestLumpersListAPIResponse> {
            override fun onResponse(call: Call<RequestLumpersListAPIResponse>, response: Response<RequestLumpersListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessFetchRequests(response.body()!!)
                }
            }

            override fun onFailure(call: Call<RequestLumpersListAPIResponse>, t: Throwable) {
                Log.e(RequestLumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun createNewRequestForLumpers(requiredLumperCount: String, notesDM: String, date: Date, onFinishedListener: RequestLumpersContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, date)
        val request = RequestLumpersRequest(requiredLumperCount.toInt(), notesDM, dateString)

        DataManager.getService().createRequestLumpers(getAuthToken(), request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessRequest(date)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(RequestLumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun updateRequestForLumpers(requestId: String, requiredLumperCount: String, notesDM: String, date: Date, onFinishedListener: RequestLumpersContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, date)
        val request = RequestLumpersRequest(requiredLumperCount.toInt(), notesDM, dateString)

        DataManager.getService().updateRequestLumpers(getAuthToken(), requestId, request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessRequest(date)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(RequestLumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
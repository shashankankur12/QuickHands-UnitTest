package com.quickhandslogistics.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.schedule.GetPastFutureDateResponse
import com.quickhandslogistics.data.scheduleTime.CancelLumperRequest
import com.quickhandslogistics.data.scheduleTime.leadinfo.GetLeadInfoResponse
import com.quickhandslogistics.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeNoteRequest
import com.quickhandslogistics.models.schedule.ScheduleModel
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment.Companion.CANCEL_SCHEDULE_LUMPER
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment.Companion.EDIT_SCHEDULE_LUMPER
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ScheduleTimeModel(private val sharedPref: SharedPref) : ScheduleTimeContract.Model {

    override fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener) {
        val date = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, selectedDate)
        onFinishedListener.onSuccessGetHeaderInfo(date)
    }

    override fun fetchSchedulesTimeByDate(selectedDate: Date, onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getScheduleTimeList(getAuthToken(), dateString).enqueue(object : Callback<GetScheduleTimeAPIResponse> {
            override fun onResponse(call: Call<GetScheduleTimeAPIResponse>, response: Response<GetScheduleTimeAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(selectedDate, response.body()!!)
                }
            }

            override fun onFailure(call: Call<GetScheduleTimeAPIResponse>, t: Throwable) {
                Log.e(ScheduleTimeModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun cancelScheduleLumpers(lumperId: ArrayList<String>, date: Date, cancelReason: String?, onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, date)
        val cancelLumperRequest = CancelLumperRequest(lumperId, cancelReason)

        DataManager.getService().cancelScheduleLumper(getAuthToken(), dateString,cancelLumperRequest).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessRequest(date, CANCEL_SCHEDULE_LUMPER)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(RequestLumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
    override fun editScheduleLumpers(
        lumperId: String,
        date: Date,
        timeMilsec: Long,
        request: ScheduleTimeNoteRequest,
        onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener
    ) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, date)

        DataManager.getService().editScheduleLumper(getAuthToken(), lumperId, dateString,timeMilsec,request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessRequest(date, EDIT_SCHEDULE_LUMPER)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(RequestLumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun fetchLeadScheduleByDate(selectedDate: Date, onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getLeadWorkInfo(getAuthToken(), dateString).enqueue(object : Callback<GetLeadInfoResponse> {
            override fun onResponse(call: Call<GetLeadInfoResponse>, response: Response<GetLeadInfoResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessLeadInfo(response.body()!!)
                }
            }

            override fun onFailure(call: Call<GetLeadInfoResponse>, t: Throwable) {
                Log.e(ScheduleTimeModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun fetchPastFutureDate(onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener) {
        DataManager.getService().scheduleTimePastFutureDate(getAuthToken()).enqueue(object : Callback<GetPastFutureDateResponse> {
            override fun onResponse(call: Call<GetPastFutureDateResponse>, response: Response<GetPastFutureDateResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessPastFutureDate(response.body())
                }
            }

            override fun onFailure(call: Call<GetPastFutureDateResponse>, t: Throwable) {
                Log.e(ScheduleModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
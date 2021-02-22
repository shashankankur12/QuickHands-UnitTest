package com.quickhandslogistics.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeNoteRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment.Companion.CANCEL_SCHEDULE_LUMPER
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment.Companion.EDIT_SCHEDULE_LUMPER
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ScheduleTimeModel(private val sharedPref: SharedPref) : ScheduleTimeContract.Model {

    override fun fetchHeaderInfo(selectedDate: Date, onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

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

    override fun cancelScheduleLumpers(lumperId: String, date: Date, cancelReason: String, onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, date)

        DataManager.getService().cancelScheduleLumper(getAuthToken(), lumperId, dateString).enqueue(object : Callback<BaseResponse> {
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
}
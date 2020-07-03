package com.quickhandslogistics.models.attendance

import android.util.Log
import com.quickhandslogistics.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimeClockAttendanceModel(private val sharedPref: SharedPref) : TimeClockAttendanceContract.Model {

    override fun fetchHeaderInfo(onFinishedListener: TimeClockAttendanceContract.Model.OnFinishedListener) {
        val date = DateUtils.getCurrentDateStringByEmployeeShift(sharedPref, pattern = DateUtils.PATTERN_NORMAL)
        onFinishedListener.onSuccessGetHeaderInfo(date)
    }

    override fun fetchLumpersAttendanceList(onFinishedListener: TimeClockAttendanceContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift(sharedPref)

        DataManager.getService().getAttendanceList(getAuthToken(), dateString).enqueue(object : Callback<GetAttendanceAPIResponse> {
            override fun onResponse(call: Call<GetAttendanceAPIResponse>, response: Response<GetAttendanceAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessGetList(response.body()!!)
                }
            }

            override fun onFailure(call: Call<GetAttendanceAPIResponse>, t: Throwable) {
                Log.e(TimeClockAttendanceModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun saveLumpersAttendanceList(
        attendanceDetailList: List<AttendanceDetail>, onFinishedListener: TimeClockAttendanceContract.Model.OnFinishedListener
    ) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift(sharedPref)

        DataManager.getService().saveAttendanceDetails(getAuthToken(), dateString, attendanceDetailList).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessSaveDate()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(TimeClockAttendanceModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
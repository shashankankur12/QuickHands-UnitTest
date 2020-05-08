package com.quickhandslogistics.modified.models.attendance

import android.util.Log
import com.quickhandslogistics.modified.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimeClockAttendanceModel : TimeClockAttendanceContract.Model {

    override fun fetchLumpersAttendanceList(onFinishedListener: TimeClockAttendanceContract.Model.OnFinishedListener) {
        DataManager.getService().getAttendanceList(getAuthToken()).enqueue(object : Callback<GetAttendanceAPIResponse> {
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
        attendanceDetailList: List<AttendanceDetail>,
        onFinishedListener: TimeClockAttendanceContract.Model.OnFinishedListener
    ) {
        DataManager.getService().saveAttendanceDetails(getAuthToken(), attendanceDetailList).enqueue(object : Callback<BaseResponse> {
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
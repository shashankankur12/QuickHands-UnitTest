package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnScheduleDetailModel : UnScheduleDetailContract.Model {

    override fun fetchScheduleDetail(scheduleIdentityId: String, scheduleFromDate: String, onFinishedListener: UnScheduleDetailContract.Model.OnFinishedListener) {
        DataManager.getService().getScheduleDetail(getAuthToken(), scheduleIdentityId, scheduleFromDate)
            .enqueue(object : Callback<ScheduleDetailAPIResponse> {
                override fun onResponse(call: Call<ScheduleDetailAPIResponse>, response: Response<ScheduleDetailAPIResponse>) {
                    if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                        onFinishedListener.onSuccess(response.body()!!, scheduleFromDate)
                    }
                }

                override fun onFailure(call: Call<ScheduleDetailAPIResponse>, t: Throwable) {
                    Log.e(UnScheduleDetailModel::class.simpleName, t.localizedMessage!!)
                    onFinishedListener.onFailure()
                }
            })
    }
}
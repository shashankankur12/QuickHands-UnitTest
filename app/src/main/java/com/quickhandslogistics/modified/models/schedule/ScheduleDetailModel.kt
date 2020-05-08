package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ScheduleDetailModel : ScheduleDetailContract.Model {

    override fun fetchScheduleDetail(scheduleIdentityId: String, selectedDate: Date, onFinishedListener: ScheduleDetailContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getScheduleDetail(getAuthToken(), scheduleIdentityId, dateString)
            .enqueue(object : Callback<ScheduleDetailAPIResponse> {
                override fun onResponse(call: Call<ScheduleDetailAPIResponse>, response: Response<ScheduleDetailAPIResponse>) {
                    if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                        onFinishedListener.onSuccess(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<ScheduleDetailAPIResponse>, t: Throwable) {
                    Log.e(ScheduleDetailModel::class.simpleName, t.localizedMessage!!)
                    onFinishedListener.onFailure()
                }
            })
    }
}
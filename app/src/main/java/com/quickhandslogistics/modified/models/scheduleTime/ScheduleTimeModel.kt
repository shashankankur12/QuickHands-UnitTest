package com.quickhandslogistics.modified.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.modified.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.modified.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ScheduleTimeModel : ScheduleTimeContract.Model {

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
}
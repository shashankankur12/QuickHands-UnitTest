package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ScheduleModel : ScheduleContract.Model {

    override fun fetchSchedulesByDate(selectedDate: Date, pageIndex: Int, onFinishedListener: ScheduleContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getSchedulesList(getAuthToken(), dateString, pageIndex, AppConstant.API_PAGE_SIZE).enqueue(object : Callback<ScheduleListAPIResponse> {
            override fun onResponse(call: Call<ScheduleListAPIResponse>, response: Response<ScheduleListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(selectedDate, response.body()!!, pageIndex)
                }
            }

            override fun onFailure(call: Call<ScheduleListAPIResponse>, t: Throwable) {
                Log.e(ScheduleModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
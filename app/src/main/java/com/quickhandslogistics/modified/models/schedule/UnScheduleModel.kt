package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.schedule.UnScheduleListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnScheduleModel : UnScheduleContract.Model {

    override fun fetchUnSchedulesByDate(onFinishedListener: UnScheduleContract.Model.OnFinishedListener) {
        val call = DataManager.getService().getUnSchedulesList(getAuthToken())
        call.enqueue(object : Callback<UnScheduleListAPIResponse> {
            override fun onResponse(call: Call<UnScheduleListAPIResponse>, response: Response<UnScheduleListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<UnScheduleListAPIResponse>, t: Throwable) {
                Log.e(UnScheduleModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
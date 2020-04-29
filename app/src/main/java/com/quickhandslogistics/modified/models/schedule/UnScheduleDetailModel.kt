package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener

class UnScheduleDetailModel : UnScheduleDetailContract.Model {

    override fun fetchScheduleDetail(
        scheduleIdentityId: String, scheduleFromDate: String,
        onFinishedListener: UnScheduleDetailContract.Model.OnFinishedListener
    ) {
        DataManager.getScheduleDetail(
            scheduleIdentityId, scheduleFromDate,
            object : ResponseListener<ScheduleDetailAPIResponse> {
                override fun onSuccess(response: ScheduleDetailAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccess(response, scheduleFromDate)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(UnScheduleDetailModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
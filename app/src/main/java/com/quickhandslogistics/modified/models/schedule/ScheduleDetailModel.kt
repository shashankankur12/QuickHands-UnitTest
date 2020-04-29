package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.DateUtils
import java.util.*

class ScheduleDetailModel : ScheduleDetailContract.Model {

    override fun fetchScheduleDetail(
        scheduleIdentityId: String,
        selectedDate: Date,
        onFinishedListener: ScheduleDetailContract.Model.OnFinishedListener
    ) {
        val dateString =
            DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)
        DataManager.getScheduleDetail(
            scheduleIdentityId, dateString,
            object : ResponseListener<ScheduleDetailAPIResponse> {
                override fun onSuccess(response: ScheduleDetailAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccess(response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(ScheduleDetailModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
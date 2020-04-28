package com.quickhandslogistics.modified.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.modified.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.modified.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.DateUtils
import java.util.*

class ScheduleTimeModel : ScheduleTimeContract.Model {

    override fun fetchSchedulesTimeByDate(
        selectedDate: Date,
        onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener
    ) {
        val dateString =
            DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getScheduleTimeList(dateString,
            object : ResponseListener<GetScheduleTimeAPIResponse> {
                override fun onSuccess(response: GetScheduleTimeAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccess(selectedDate, response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(ScheduleTimeModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
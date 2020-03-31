package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleAPIResponse
import com.quickhandslogistics.modified.models.LoginModel
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class ScheduleModel(private val sharedPref: SharedPref) : ScheduleContract.Model {

    override fun fetchSchedulesByDate(
        selectedDate: Date,
        onFinishedListener: ScheduleContract.Model.OnFinishedListener
    ) {
        val dateString =
            DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)
        val buildingId = sharedPref.getString("")

        DataManager.getSchedulesList(
            dateString,
            "ec591484-af69-48a5-aa53-e343686770a5",
            true,
            object : ResponseListener<ScheduleAPIResponse> {
                override fun onSuccess(response: ScheduleAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccess(selectedDate, response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(LoginModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
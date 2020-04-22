package com.quickhandslogistics.modified.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class ScheduleTimeModel(private val sharedPref: SharedPref) : ScheduleTimeContract.Model {

    override fun fetchSchedulesTimeByDate(
        selectedDate: Date,
        onFinishedListener: ScheduleTimeContract.Model.OnFinishedListener
    ) {
        val dateString =
            DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)
        val buildingId = sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID)

        DataManager.getAllLumpersData(
            object : ResponseListener<AllLumpersResponse> {
                override fun onSuccess(response: AllLumpersResponse) {
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
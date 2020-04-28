package com.quickhandslogistics.modified.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.modified.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.scheduleTime.LumperScheduleTimeData
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*
import kotlin.collections.ArrayList

class EditScheduleTimeModel(private val sharedPref: SharedPref) :
    EditScheduleTimeContract.Model {

    override fun fetchLumpersList(onFinishedListener: EditScheduleTimeContract.Model.OnFinishedListener) {
        DataManager.getAllLumpersData(object : ResponseListener<AllLumpersResponse> {
            override fun onSuccess(response: AllLumpersResponse) {
                if (response.success) {
                    onFinishedListener.onSuccessFetchLumpers(response)
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(EditScheduleTimeModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }

    override fun assignScheduleTime(
        scheduledLumpersIdsTimeMap: HashMap<String, Long>,
        notes: String, requiredLumpersCount: Int, notesDM: String, selectedDate: Date,
        onFinishedListener: EditScheduleTimeContract.Model.OnFinishedListener
    ) {
        val dateString =
            DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)
        val lumpersData: ArrayList<LumperScheduleTimeData> = ArrayList()
        for (employeeId in scheduledLumpersIdsTimeMap.keys) {
            val timestamp = scheduledLumpersIdsTimeMap[employeeId]!! / 1000
            lumpersData.add(LumperScheduleTimeData(timestamp, employeeId))
        }
        val request =
            ScheduleTimeRequest(lumpersData, notes, requiredLumpersCount, notesDM, dateString)
        DataManager.saveScheduleTimeDetail(
            request, object : ResponseListener<BaseResponse> {
                override fun onSuccess(response: BaseResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccessScheduleTime()
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(EditScheduleTimeModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
package com.quickhandslogistics.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.scheduleTime.LumperScheduleTimeData
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class EditScheduleTimeModel : EditScheduleTimeContract.Model {

    override fun assignScheduleTime(
        scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String,
        selectedDate: Date, onFinishedListener: EditScheduleTimeContract.Model.OnFinishedListener
    ) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        val lumpersData: ArrayList<LumperScheduleTimeData> = ArrayList()
        for (employeeId in scheduledLumpersIdsTimeMap.keys) {
            val timestamp = scheduledLumpersIdsTimeMap[employeeId]!!
            lumpersData.add(LumperScheduleTimeData(timestamp, employeeId))
        }

        val request = ScheduleTimeRequest(lumpersData, notes, dateString)

        DataManager.getService().saveScheduleTimeDetails(getAuthToken(), request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessScheduleTime()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(EditScheduleTimeModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
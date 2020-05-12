package com.quickhandslogistics.modified.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.modified.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.modified.data.scheduleTime.LumperScheduleTimeData
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class EditScheduleTimeModel : EditScheduleTimeContract.Model {

    override fun fetchLumpersList(onFinishedListener: EditScheduleTimeContract.Model.OnFinishedListener) {
        DataManager.getService().getAllLumpersData(getAuthToken(), 1, AppConstant.API_PAGE_SIZE).enqueue(object : Callback<LumperListAPIResponse> {
            override fun onResponse(call: Call<LumperListAPIResponse>, response: Response<LumperListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessFetchLumpers(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LumperListAPIResponse>, t: Throwable) {
                Log.e(EditScheduleTimeModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun assignScheduleTime(
        scheduledLumpersIdsTimeMap: HashMap<String, Long>,
        notes: String, requiredLumpersCount: Int, notesDM: String, selectedDate: Date,
        onFinishedListener: EditScheduleTimeContract.Model.OnFinishedListener
    ) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        val lumpersData: ArrayList<LumperScheduleTimeData> = ArrayList()
        for (employeeId in scheduledLumpersIdsTimeMap.keys) {
            val timestamp = scheduledLumpersIdsTimeMap[employeeId]!! / 1000
            lumpersData.add(LumperScheduleTimeData(timestamp, employeeId))
        }

        val request = ScheduleTimeRequest(lumpersData, notes, requiredLumpersCount, notesDM, dateString)

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
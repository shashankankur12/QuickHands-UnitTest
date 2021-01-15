package com.quickhandslogistics.models.workSheet

import android.util.Log
import com.quickhandslogistics.contracts.workSheet.AddLumperTimeWorkSheetItemContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.workSheet.TimingDetails
import com.quickhandslogistics.data.workSheet.UpdateLumperTimeRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLumperTimeWorkSheetItemModel : AddLumperTimeWorkSheetItemContract.Model {

    override fun saveLumperTimings(
        id: String,
        workItemId: String,
        selectedStartTime: Long,
        selectedEndTime: Long,
        selectedBreakInTime: Long,
        selectedBreakOutTime: Long,
        waitingTime: String,
        partWorkDone: Int,
        onFinishedListener: AddLumperTimeWorkSheetItemContract.Model.OnFinishedListener
    ) {
        val waitingTimeInt = if (waitingTime.isNotEmpty()) waitingTime.toInt() else 0
        val timingDetail = TimingDetails()
        if (selectedStartTime > 0) timingDetail.startTime = selectedStartTime
        if (selectedEndTime > 0) timingDetail.endTime = selectedEndTime
        if (selectedBreakInTime > 0) timingDetail.breakTimeStart = selectedBreakInTime
        if (selectedBreakOutTime > 0) timingDetail.breakTimeEnd = selectedBreakOutTime
        if (waitingTimeInt > 0) timingDetail.waitingTime = waitingTimeInt


        val request = UpdateLumperTimeRequest(id, workItemId, timingDetail, partWorkDone)
        DataManager.getService().updateLumperTimeInWorkItem(getAuthToken(), request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(AddLumperTimeWorkSheetItemModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
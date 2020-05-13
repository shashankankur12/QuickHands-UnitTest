package com.quickhandslogistics.modified.models.workSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.workSheet.AddLumperTimeWorkSheetItemContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.workSheet.TimingDetails
import com.quickhandslogistics.modified.data.workSheet.UpdateLumperTimeRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLumperTimeWorkSheetItemModel : AddLumperTimeWorkSheetItemContract.Model {

    override fun saveLumperTimings(
        id: String, workItemId: String, selectedStartTime: Long, selectedEndTime: Long, selectedBreakInTime: Long,
        selectedBreakOutTime: Long, waitingTime: String, onFinishedListener: AddLumperTimeWorkSheetItemContract.Model.OnFinishedListener
    ) {
        val waitingTimeInt = if (waitingTime.isNotEmpty()) waitingTime.toInt() else 0
        val timingDetail = TimingDetails()
        if (selectedStartTime > 0) timingDetail.startTime = selectedStartTime / 1000
        if (selectedEndTime > 0) timingDetail.endTime = selectedEndTime / 1000
        if (selectedBreakInTime > 0) timingDetail.breakTimeStart = selectedBreakInTime / 1000
        if (selectedBreakOutTime > 0) timingDetail.breakTimeEnd = selectedBreakOutTime / 1000
        if (waitingTimeInt > 0) timingDetail.waitingTime = waitingTimeInt

        val request = UpdateLumperTimeRequest(id, workItemId, timingDetail)
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
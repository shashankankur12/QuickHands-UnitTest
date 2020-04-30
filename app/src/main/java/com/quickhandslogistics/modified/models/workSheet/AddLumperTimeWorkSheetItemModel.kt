package com.quickhandslogistics.modified.models.workSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.workSheet.AddLumperTimeWorkSheetItemContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.workSheet.TimingDetails
import com.quickhandslogistics.modified.data.workSheet.UpdateLumperTimeRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.SharedPref

class AddLumperTimeWorkSheetItemModel(private val sharedPref: SharedPref) :
    AddLumperTimeWorkSheetItemContract.Model {

    override fun saveLumperTimings(
        id: String, workItemId: String, selectedStartTime: Long, selectedEndTime: Long,
        selectedBreakInTime: Long, selectedBreakOutTime: Long, waitingTime: String,
        onFinishedListener: AddLumperTimeWorkSheetItemContract.Model.OnFinishedListener
    ) {
        val waitingTimeInt = if (waitingTime.isNotEmpty()) waitingTime.toInt() else 0
        val timingDetail = TimingDetails()
        if (selectedStartTime > 0) timingDetail.startTime = selectedStartTime / 1000
        if (selectedEndTime > 0) timingDetail.endTime = selectedEndTime / 1000
        if (selectedBreakInTime > 0) timingDetail.breakTimeStart = selectedBreakInTime / 1000
        if (selectedBreakOutTime > 0) timingDetail.breakTimeEnd = selectedBreakOutTime / 1000
        if (waitingTimeInt > 0) timingDetail.waitingTime = waitingTimeInt

        val request = UpdateLumperTimeRequest(id, workItemId, timingDetail)
        DataManager.updateLumperTimeInWorkItem(request,
            object : ResponseListener<BaseResponse> {
                override fun onSuccess(response: BaseResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccess()
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(WorkSheetItemDetailModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
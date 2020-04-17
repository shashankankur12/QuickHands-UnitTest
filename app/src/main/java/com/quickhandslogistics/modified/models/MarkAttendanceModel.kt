package com.quickhandslogistics.modified.models

import android.util.Log
import com.quickhandslogistics.modified.contracts.MarkAttendanceContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener

class MarkAttendanceModel : MarkAttendanceContract.Model {

    override fun fetchLumpersAttendanceList(onFinishedListener: MarkAttendanceContract.Model.OnFinishedListener) {
        DataManager.getLumpersAttendanceList(object : ResponseListener<GetAttendanceAPIResponse> {
            override fun onSuccess(response: GetAttendanceAPIResponse) {
                if (response.success) {
                    onFinishedListener.onSuccessGetList(response)
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(MarkAttendanceModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }

    override fun saveLumpersAttendanceList(
        attendanceDetailList: List<AttendanceDetail>,
        onFinishedListener: MarkAttendanceContract.Model.OnFinishedListener
    ) {
        DataManager.saveLumpersAttendanceList(attendanceDetailList, object : ResponseListener<BaseResponse> {
            override fun onSuccess(response: BaseResponse) {
                if (response.success) {
                    onFinishedListener.onSuccessSaveDate()
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(MarkAttendanceModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }
}
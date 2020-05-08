package com.quickhandslogistics.modified.models.lumperSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.modified.data.lumperSheet.SubmitLumperSheetRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.DateUtils
import java.util.*

class LumperSheetModel : LumperSheetContract.Model {

    override fun fetchLumperSheetList(selectedDate: Date, onFinishedListener: LumperSheetContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)
        DataManager.getLumperSheetList(dateString, object : ResponseListener<LumperSheetListAPIResponse> {
            override fun onSuccess(response: LumperSheetListAPIResponse) {
                if (response.success) {
                    onFinishedListener.onSuccess(response, selectedDate)
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(LumperSheetModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }

    override fun submitLumperSheet(selectedDate: Date, onFinishedListener: LumperSheetContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)
        val request = SubmitLumperSheetRequest(dateString)
        DataManager.submitLumperSheet(request, object : ResponseListener<BaseResponse> {
            override fun onSuccess(response: BaseResponse) {
                if (response.success) {
                    onFinishedListener.onSuccessSubmitLumperSheet()
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(LumperWorkDetailModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }
}
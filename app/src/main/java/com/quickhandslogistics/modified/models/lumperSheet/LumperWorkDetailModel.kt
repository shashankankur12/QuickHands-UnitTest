package com.quickhandslogistics.modified.models.lumperSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.DateUtils
import java.io.File
import java.util.*

class LumperWorkDetailModel : LumperWorkDetailContract.Model {

    override fun fetchLumperWorkDetails(lumperId:String, selectedDate: Date, onFinishedListener: LumperWorkDetailContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getLumperWorkDetails(lumperId, dateString, object : ResponseListener<LumperWorkDetailAPIResponse> {
            override fun onSuccess(response: LumperWorkDetailAPIResponse) {
                if (response.success) {
                    onFinishedListener.onSuccess(response)
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

    override fun saveLumperSignature(
        lumperId: String, date: Date, signatureFilePath: String,
        onFinishedListener: LumperWorkDetailContract.Model.OnFinishedListener
    ) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, date)
        val file = File(signatureFilePath)
        DataManager.saveLumperSigntaure(
            lumperId, dateString, file,
            object : ResponseListener<BaseResponse> {
                override fun onSuccess(response: BaseResponse) {
                    if (response.success) {
                        // Delete the temporary saved file
                        file.delete()

                        onFinishedListener.onSuccessSaveLumperSignature(lumperId, date)
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
package com.quickhandslogistics.models.lumperSheet

import android.util.Log
import com.quickhandslogistics.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.models.attendance.TimeClockAttendanceModel
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.createMultiPartBody
import com.quickhandslogistics.network.DataManager.createRequestBody
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class LumperWorkDetailModel : LumperWorkDetailContract.Model {

    override fun fetchLumperWorkDetails(lumperId: String, selectedDate: Date, onFinishedListener: LumperWorkDetailContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getLumperWorkDetail(getAuthToken(), dateString, lumperId).enqueue(object : Callback<LumperWorkDetailAPIResponse> {
            override fun onResponse(call: Call<LumperWorkDetailAPIResponse>, response: Response<LumperWorkDetailAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LumperWorkDetailAPIResponse>, t: Throwable) {
                Log.e(LumperWorkDetailModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun saveLumperSignature(
        lumperId: String, date: Date, signatureFilePath: String, onFinishedListener: LumperWorkDetailContract.Model.OnFinishedListener
    ) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, date)

        val lumperIdRequestBody = createRequestBody(lumperId)
        val dayRequestBody = createRequestBody(dateString)

        val signatureFile = File(signatureFilePath)
        val signatureMultiPartBody = createMultiPartBody(signatureFile, "signature")

        DataManager.getService().saveLumperSignature(getAuthToken(), dayRequestBody, lumperIdRequestBody, signatureMultiPartBody)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                        // Delete the temporary saved file
                        signatureFile.delete()

                        onFinishedListener.onSuccessSaveLumperSignature(lumperId, date)
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Log.e(LumperWorkDetailModel::class.simpleName, t.localizedMessage!!)
                    onFinishedListener.onFailure()
                }
            })
    }

    override fun saveLumpersAttendanceList(
        attendanceDetailList: List<AttendanceDetail>, onFinishedListener: LumperWorkDetailContract.Model.OnFinishedListener
    ) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift()

        DataManager.getService().saveAttendanceDetails(getAuthToken(), dateString, attendanceDetailList).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessSaveDate()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(TimeClockAttendanceModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
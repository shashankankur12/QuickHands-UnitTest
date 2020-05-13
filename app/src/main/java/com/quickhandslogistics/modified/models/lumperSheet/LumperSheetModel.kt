package com.quickhandslogistics.modified.models.lumperSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.modified.data.lumperSheet.SubmitLumperSheetRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LumperSheetModel : LumperSheetContract.Model {

    override fun fetchLumperSheetList(selectedDate: Date, onFinishedListener: LumperSheetContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate)

        DataManager.getService().getLumperSheetList(getAuthToken(), dateString).enqueue(object : Callback<LumperSheetListAPIResponse> {
            override fun onResponse(call: Call<LumperSheetListAPIResponse>, response: Response<LumperSheetListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!, selectedDate)
                }
            }

            override fun onFailure(call: Call<LumperSheetListAPIResponse>, t: Throwable) {
                Log.e(LumperSheetModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun submitLumperSheet(selectedDate: Date, onFinishedListener: LumperSheetContract.Model.OnFinishedListener) {
        val request = SubmitLumperSheetRequest(DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDate))

        DataManager.getService().submitLumperSheet(getAuthToken(), request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessSubmitLumperSheet()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(LumperSheetModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
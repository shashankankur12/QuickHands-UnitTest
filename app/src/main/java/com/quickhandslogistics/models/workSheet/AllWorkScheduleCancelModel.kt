package com.quickhandslogistics.models.workSheet

import android.util.Log
import com.quickhandslogistics.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.common.AllLumpersResponse
import com.quickhandslogistics.data.workSheet.CancelAllSchedulesRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AllWorkScheduleCancelModel(private val sharedPref: SharedPref) : AllWorkScheduleCancelContract.Model {

    override fun fetchLumpersList(onFinishedListener: AllWorkScheduleCancelContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift(sharedPref)

        DataManager.getService().getPresentLumpersList(getAuthToken(), dateString).enqueue(object : Callback<AllLumpersResponse> {
            override fun onResponse(call: Call<AllLumpersResponse>, response: Response<AllLumpersResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessFetchLumpers(response.body()!!)
                }
            }

            override fun onFailure(call: Call<AllLumpersResponse>, t: Throwable) {
                Log.e(AllWorkScheduleCancelModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun cancelAllWorkSchedules(
        selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String, onFinishedListener: AllWorkScheduleCancelContract.Model.OnFinishedListener
    ) {
        val request = CancelAllSchedulesRequest(selectedLumperIdsList, notesQHL, notesCustomer)
        val day = DateUtils.getCurrentDateStringByEmployeeShift(sharedPref)

        DataManager.getService().cancelAllSchedules(getAuthToken(), day, request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessCancelWorkSchedules()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(AllWorkScheduleCancelModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
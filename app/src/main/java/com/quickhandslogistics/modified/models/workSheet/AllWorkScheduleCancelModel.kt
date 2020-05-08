package com.quickhandslogistics.modified.models.workSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.workSheet.CancelAllSchedulesRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AllWorkScheduleCancelModel : AllWorkScheduleCancelContract.Model {

    override fun fetchLumpersList(onFinishedListener: AllWorkScheduleCancelContract.Model.OnFinishedListener) {
        DataManager.getService().getAllLumpersData(getAuthToken()).enqueue(object : Callback<AllLumpersResponse> {
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
        selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String,
        onFinishedListener: AllWorkScheduleCancelContract.Model.OnFinishedListener
    ) {
        val request = CancelAllSchedulesRequest(selectedLumperIdsList, notesQHL, notesCustomer)
        val day = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, Date())
        DataManager.cancelAllSchedules(day, request, object : ResponseListener<BaseResponse> {
            override fun onSuccess(response: BaseResponse) {
                if (response.success) {
                    onFinishedListener.onSuccessCancelWorkSchedules()
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(AllWorkScheduleCancelModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }
}
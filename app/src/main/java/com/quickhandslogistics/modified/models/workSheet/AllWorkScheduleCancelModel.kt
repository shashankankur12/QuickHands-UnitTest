package com.quickhandslogistics.modified.models.workSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.workSheet.CancelAllSchedulesRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class AllWorkScheduleCancelModel(private val sharedPref: SharedPref) :
    AllWorkScheduleCancelContract.Model {

    override fun fetchLumpersList(onFinishedListener: AllWorkScheduleCancelContract.Model.OnFinishedListener) {
        DataManager.getAllLumpersData(object : ResponseListener<AllLumpersResponse> {
            override fun onSuccess(response: AllLumpersResponse) {
                if (response.success) {
                    onFinishedListener.onSuccessFetchLumpers(response)
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

    override fun cancelAllWorkSchedules(
        selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String,
        onFinishedListener: AllWorkScheduleCancelContract.Model.OnFinishedListener
    ) {
        val request = CancelAllSchedulesRequest(selectedLumperIdsList, notesQHL, notesCustomer)
        val day = DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, Date())
        DataManager.cancelAllSchedules(
            day, request,
            object : ResponseListener<BaseResponse> {
                override fun onSuccess(response: BaseResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccessCancelWorkSchedules()
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(
                            AllWorkScheduleCancelModel::class.simpleName, error.localizedMessage!!
                        )
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
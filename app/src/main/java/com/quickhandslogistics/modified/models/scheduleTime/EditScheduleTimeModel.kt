package com.quickhandslogistics.modified.models.scheduleTime

import android.util.Log
import com.quickhandslogistics.modified.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.schedule.AssignLumpersRequest
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class EditScheduleTimeModel(private val sharedPref: SharedPref) :
    EditScheduleTimeContract.Model {

    override fun fetchLumpersList(onFinishedListener: EditScheduleTimeContract.Model.OnFinishedListener) {
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
                    Log.e(EditScheduleTimeModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }

    override fun assignScheduleTime(
        workItemId: String,
        workItemType: String,
        selectedLumperIdsList: ArrayList<String>,
        onFinishedListener: EditScheduleTimeContract.Model.OnFinishedListener
    ) {
        val assignLumperRequest = AssignLumpersRequest(
            sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID),
            workItemType, selectedLumperIdsList
        )
        DataManager.assignLumpers(
            workItemId,
            assignLumperRequest,
            object : ResponseListener<BaseResponse> {
                override fun onSuccess(response: BaseResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccessScheduleTime()
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(EditScheduleTimeModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
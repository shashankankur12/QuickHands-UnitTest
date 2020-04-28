package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.AddWorkItemLumpersContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.schedule.AssignLumpersRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class AddWorkItemLumpersModel(private val sharedPref: SharedPref) :
    AddWorkItemLumpersContract.Model {

    override fun fetchLumpersList(onFinishedListener: AddWorkItemLumpersContract.Model.OnFinishedListener) {
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
                    Log.e(AddWorkItemLumpersModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }

    override fun assignLumpersList(
        workItemId: String,
        workItemType: String,
        selectedLumperIdsList: ArrayList<String>,
        onFinishedListener: AddWorkItemLumpersContract.Model.OnFinishedListener
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
                        onFinishedListener.onSuccessAssignLumpers()
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(AddWorkItemLumpersModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
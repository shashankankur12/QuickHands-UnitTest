package com.quickhandslogistics.modified.models.workSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.SharedPref

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
        selectedLumperIdsList: ArrayList<String>,
        onFinishedListener: AllWorkScheduleCancelContract.Model.OnFinishedListener
    ) {
        /*val assignLumperRequest = AssignLumpersRequest(
            sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID),
            workItemType, selectedLumperIdsList
        )
        DataManager.assignLumpers(
            workItemId,
            assignLumperRequest,
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
                        Log.e(AllWorkScheduleCancelModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })*/
    }
}
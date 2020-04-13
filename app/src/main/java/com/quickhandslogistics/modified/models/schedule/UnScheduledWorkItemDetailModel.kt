package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.UnScheduledWorkItemDetailContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.schedule.ChangeWorkItemScheduleStatusRequest
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class UnScheduledWorkItemDetailModel(private val sharedPref: SharedPref) :
    UnScheduledWorkItemDetailContract.Model {

    override fun fetchWorkItemDetail(
        workItemId: String,
        onFinishedListener: UnScheduledWorkItemDetailContract.Model.OnFinishedListener
    ) {
        DataManager.getWorkItemDetail(workItemId,
            object : ResponseListener<WorkItemDetailAPIResponse> {
                override fun onSuccess(response: WorkItemDetailAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccess(response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(
                            UnScheduledWorkItemDetailModel::class.simpleName,
                            error.localizedMessage!!
                        )
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }

    override fun changeWorkItemStatus(
        workItemId: String, workItemType: String,
        onFinishedListener: UnScheduledWorkItemDetailContract.Model.OnFinishedListener
    ) {
        val request = ChangeWorkItemScheduleStatusRequest(workItemType)
        DataManager.changeWorkItemScheduleStatus(
            sharedPref.getString(AppConstant.PREFERENCE_BUILDING_ID),
            workItemId, request,
            object : ResponseListener<BaseResponse> {
                override fun onSuccess(response: BaseResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccessChangeStatus()
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(
                            UnScheduledWorkItemDetailModel::class.simpleName,
                            error.localizedMessage!!
                        )
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
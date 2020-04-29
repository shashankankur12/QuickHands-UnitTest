package com.quickhandslogistics.modified.models.workSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.modified.data.workSheet.ChangeStatusRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener

class WorkSheetItemDetailModel : WorkSheetItemDetailContract.Model {

    override fun fetchWorkItemDetail(
        workItemId: String,
        onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener
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
                            WorkSheetItemDetailModel::class.simpleName,
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
        workItemId: String, status: String,
        onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener
    ) {
        val request = ChangeStatusRequest(status)
        DataManager.changeWorkItemStatus(workItemId, request,
            object : ResponseListener<BaseResponse> {
                override fun onSuccess(response: BaseResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccessChangeStatus(workItemId)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(WorkSheetItemDetailModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
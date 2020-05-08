package com.quickhandslogistics.modified.models.workSheet

import android.util.Log
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.modified.data.workSheet.ChangeStatusRequest
import com.quickhandslogistics.modified.data.workSheet.UpdateNotesRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.network.ResponseListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkSheetItemDetailModel : WorkSheetItemDetailContract.Model {

    override fun fetchWorkItemDetail(workItemId: String, onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener) {
        DataManager.getService().getWorkItemDetail(getAuthToken(), workItemId).enqueue(object : Callback<WorkItemDetailAPIResponse> {
            override fun onResponse(call: Call<WorkItemDetailAPIResponse>, response: Response<WorkItemDetailAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<WorkItemDetailAPIResponse>, t: Throwable) {
                Log.e(WorkSheetItemDetailModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun changeWorkItemStatus(workItemId: String, status: String, onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener) {
        val request = ChangeStatusRequest(status)
        DataManager.changeWorkItemStatus(workItemId, request, object : ResponseListener<BaseResponse> {
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

    override fun updateWorkItemNotes(
        workItemId: String, notesQHLCustomer: String, notesQHL: String,
        onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener
    ) {
        val request = UpdateNotesRequest(notesQHL, notesQHLCustomer)
        DataManager.updateWorkItemNotes(workItemId, request, object : ResponseListener<BaseResponse> {
            override fun onSuccess(response: BaseResponse) {
                if (response.success) {
                    onFinishedListener.onSuccessUpdateNotes(workItemId)
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
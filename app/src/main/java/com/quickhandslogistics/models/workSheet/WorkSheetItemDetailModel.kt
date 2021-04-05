package com.quickhandslogistics.models.workSheet

import android.util.Log
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.data.workSheet.ChangeStatusRequest
import com.quickhandslogistics.data.workSheet.UpdateNotesRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkSheetItemDetailModel : WorkSheetItemDetailContract.Model {

    override fun fetchWorkItemDetail(workItemId: String, onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener) {
        DataManager.getService().getWorkItemContainerDetail(getAuthToken(), workItemId).enqueue(object : Callback<WorkItemDetailAPIResponse> {
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

        DataManager.getService().changeWorkItemStatus(getAuthToken(), workItemId, request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessChangeStatus(workItemId)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(WorkSheetItemDetailModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun updateWorkItemNotes(
        workItemId: String, notesQHLCustomer: String, notesQHL: String, onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener
    ) {
        val request = UpdateNotesRequest(notesQHL, notesQHLCustomer)

        DataManager.getService().updateWorkItemNotes(getAuthToken(), workItemId, request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessUpdateNotes(workItemId)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(WorkSheetItemDetailModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun removeLumper(lumperId: String, workItemId: String, onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener) {
//        DataManager.getService().updateWorkItemNotes(getAuthToken(), workItemId, lumperid).enqueue(object : Callback<BaseResponse> {
//            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
//                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
//                    onFinishedListener.onSuccessChangeStatus(workItemId)
//                }
//            }
//
//            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
//                Log.e(WorkSheetItemDetailModel::class.simpleName, t.localizedMessage!!)
//                onFinishedListener.onFailure()
//            }
//        })
    }
}
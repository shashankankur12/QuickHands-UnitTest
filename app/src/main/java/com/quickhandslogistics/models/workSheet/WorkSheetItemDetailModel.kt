package com.quickhandslogistics.models.workSheet

import android.util.Log
import com.quickhandslogistics.contracts.workSheet.UploadImageResponse
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.schedule.AssignLumpersRequest
import com.quickhandslogistics.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.data.workSheet.ChangeStatusRequest
import com.quickhandslogistics.data.workSheet.UpdateNotesRequest
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.DateUtils
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

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

    override fun changeWorkItemStatus(workItemId: String, status: String, selectedDate: Date?, selectedTime: Long?, onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener) {
        val dateString = selectedDate?.let { DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, it) }
        val timeString = selectedTime?.let { it }

        val request = ChangeStatusRequest(status,timeString, dateString)

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
        workItemId: String,
        notesQHLCustomer: String,
        notesQHL: String,
        noteImageArrayList: ArrayList<String>,
        onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener
    ) {
        val request = UpdateNotesRequest(notesQHL, notesQHLCustomer, noteImageArrayList)

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

    override fun removeLumper(lumperIds: ArrayList<String>, tempLumperIds: ArrayList<String>, workItemId: String, onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener) {

        val request = AssignLumpersRequest(lumperIds, tempLumperIds)
        DataManager.getService().removeLumperFromWork(getAuthToken(), workItemId, request).enqueue(object : Callback<BaseResponse> {
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

    override fun uploadeNoteImage(fileName: MultipartBody.Part, onFinishedListener: WorkSheetItemDetailContract.Model.OnFinishedListener) {
        DataManager.getService().uploadImage(getAuthToken(), fileName ).enqueue(object : Callback<UploadImageResponse> {
            override fun onResponse(call: Call<UploadImageResponse>, response: Response<UploadImageResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessUploadImage(response.body())
                }
            }

            override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                Log.e(WorkSheetItemDetailModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.ScheduledWorkItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduledWorkItemDetailModel : ScheduledWorkItemDetailContract.Model {

    override fun fetchWorkItemDetail(workItemId: String, onFinishedListener: ScheduledWorkItemDetailContract.Model.OnFinishedListener) {
        DataManager.getService().getWorkItemDetail(getAuthToken(), workItemId).enqueue(object : Callback<WorkItemDetailAPIResponse> {
            override fun onResponse(call: Call<WorkItemDetailAPIResponse>, response: Response<WorkItemDetailAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<WorkItemDetailAPIResponse>, t: Throwable) {
                Log.e(ScheduledWorkItemDetailModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
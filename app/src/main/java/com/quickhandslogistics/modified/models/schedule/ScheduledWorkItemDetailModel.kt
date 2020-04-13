package com.quickhandslogistics.modified.models.schedule

import android.util.Log
import com.quickhandslogistics.modified.contracts.schedule.ScheduledWorkItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener

class ScheduledWorkItemDetailModel : ScheduledWorkItemDetailContract.Model {

    override fun fetchWorkItemDetail(
        workItemId: String,
        onFinishedListener: ScheduledWorkItemDetailContract.Model.OnFinishedListener
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
                            ScheduledWorkItemDetailModel::class.simpleName,
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
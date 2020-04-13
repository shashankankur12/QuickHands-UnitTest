package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse

class UnScheduledWorkItemDetailContract {
    interface Model {
        fun fetchWorkItemDetail(workItemId: String, onFinishedListener: OnFinishedListener)
        fun changeWorkItemStatus(
            workItemId: String,
            workItemType: String,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessChangeStatus()
            fun onSuccess(response: WorkItemDetailAPIResponse)
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun workItemStatusChanged()
        fun showWorkItemDetail(workItemDetail: WorkItemDetail)
    }

    interface Presenter {
        fun fetchWorkItemDetail(workItemId: String)
        fun changeWorkItemStatus(workItemId: String, workItemType: String)
        fun onDestroy()
    }
}
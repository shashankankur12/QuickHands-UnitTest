package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse

class ScheduledWorkItemDetailContract {
    interface Model {
        fun fetchWorkItemDetail(workItemId: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: WorkItemDetailAPIResponse)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showWorkItemDetail(workItemDetail: WorkItemDetail)
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchWorkItemDetail(workItemId: String)
    }
}
package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class UnScheduledWorkItemDetailContract {
    interface Model {
        fun changeWorkItemStatus(
            workItemId: String,
            workItemType: String,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessChangeStatus()
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun workItemStatusChanged()
    }

    interface Presenter {
        fun changeWorkItemStatus(workItemId: String, workItemType: String)
        fun onDestroy()
    }
}
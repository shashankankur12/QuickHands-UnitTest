package com.quickhandslogistics.modified.contracts.workSheet

import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse

class WorkSheetItemDetailContract {
    interface Model {
        fun fetchWorkItemDetail(workItemId: String, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(response: WorkItemDetailAPIResponse)
        }
    }

    interface View {
        fun hideProgressDialog()
        fun showProgressDialog(message: String)
        fun showAPIErrorMessage(message: String)
        fun showWorkItemDetail(workItemDetail: WorkItemDetail)

        interface OnAdapterItemClickListener {
            fun onSelectStatus(status: String)
        }

        interface OnFragmentInteractionListener {
            fun fetchWorkItemDetail()
        }
    }

    interface Presenter {
        fun fetchWorkItemDetail(workItemId: String)
        fun onDestroy()
    }
}